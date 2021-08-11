/*
 * Copyright 2021 Esri
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.esri.samples.set_up_location_driven_geotriggers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geotriggers.*;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.location.*;
import com.esri.arcgisruntime.portal.Portal;
import com.esri.arcgisruntime.portal.PortalItem;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.util.Duration;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;
import org.apache.commons.io.IOUtils;

public class SetUpLocationDrivenGeotriggersController {

  @FXML private MapView mapView;
  @FXML private Label satelliteCount;
  @FXML private Label satelliteID;
  @FXML private Button startButton;
  @FXML private Button stopButton;
  @FXML private Label systemInfo;

  private int count = 0;
  private LocationGeotriggerFeed locationGeotriggerFeed;
  private SimulatedLocationDataSource simulatedLocationDataSource;
  private List<NmeaSatelliteInfo> nmeaSatelliteInfo;

  public void initialize() {

    try {

      // authentication with an API key or named user is required to access basemaps and other location services
      String yourAPIKey = System.getProperty("apiKey");
      ArcGISRuntimeEnvironment.setApiKey(yourAPIKey);

      // create a map with a predefined tile basemap, feature styles and labels in the Santa Barbara Botanic Garden and set it to the map view
      Portal portal = new Portal("https://www.arcgis.com/");
      ArcGISMap map = new ArcGISMap(new PortalItem(portal, "6ab0e91dc39e478cae4f408e1a36a308"));
      mapView.setMap(map);

      // create service feature tables to add geotrigger monitors for later
      ServiceFeatureTable gardenSectionFeatureTable = new ServiceFeatureTable(new PortalItem(portal,"1ba816341ea04243832136379b8951d9"), 0);
      ServiceFeatureTable gardenPOIFeatureTable = new ServiceFeatureTable(new PortalItem(portal,"7c6280c290c34ae8aeb6b5c4ec841167"), 0);

      // create geotriggers for each of the service feature tables
      createGeotriggerMonitor(gardenSectionFeatureTable, 0.0, "Section Geotrigger");
      createGeotriggerMonitor(gardenPOIFeatureTable, 0.0, "POI Geotrigger");

      // access the json of the walking route points
      String polylineData = IOUtils.toString(getClass().getResourceAsStream(
        "/set_up_location_driven_geotriggers/polyline_data.json"), StandardCharsets.UTF_8);
      // create a polyline from the location points
      Polyline locations = (Polyline) Geometry.fromJson(polylineData, SpatialReferences.getWgs84());


      // create a new simulated location data source to replicate the path walked around the garden
      simulatedLocationDataSource = new SimulatedLocationDataSource();
      // set the location of the simulated location data source with simulation parameters to set a consistent velocity
      simulatedLocationDataSource.setLocations(
        locations, new SimulationParameters(Calendar.getInstance(), 5.0, 0.0, 0.0 ));

      // configure the map view's location display to follow the simulated location data source
      LocationDisplay locationDisplay = mapView.getLocationDisplay();
      locationDisplay.setLocationDataSource(simulatedLocationDataSource);
      locationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.RECENTER);
      locationDisplay.setInitialZoomScale(1000);

      // disable map view interaction, the location display will automatically center on the mock device location
      mapView.setEnableMousePan(false);
      mapView.setEnableKeyboardNavigation(false);

      // start the location display when the map is loaded
      map.addDoneLoadingListener(() -> {
        if (map.getLoadStatus() == LoadStatus.LOADED) {

          // start the location display
          locationDisplay.startAsync();

          locationGeotriggerFeed = new LocationGeotriggerFeed(simulatedLocationDataSource);

        } else {
          new Alert(Alert.AlertType.ERROR, "Map failed to load: " + map.getLoadError().getCause().getMessage()).show();
        }
      });



    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
    }
  }

  /**
   * Creates a geotrigger monitor for a given service feature table
   * @param serviceFeatureTable
   * @param bufferSize
   * @param geotriggerName
   */
  private void createGeotriggerMonitor(ServiceFeatureTable serviceFeatureTable, double bufferSize, String geotriggerName) {

    FeatureFenceParameters featureFenceParameters = new FeatureFenceParameters(serviceFeatureTable, bufferSize);
    // define an arcade expression that returns the value for the "name" field of the feaeture that triggered the monitor
    FenceGeotrigger fenceGeotrigger = new FenceGeotrigger(locationGeotriggerFeed, FenceRuleType.ENTER_OR_EXIT, featureFenceParameters);
    GeotriggerMonitor geotriggerMonitor = new GeotriggerMonitor(fenceGeotrigger);

    connect(geotriggerMonitor, geotriggerNotification, handleGeotriggerNotification);

    geotriggerMonitor.startAsync();

  }

  /**
   * Initializes the location data source, reads the mock data NMEA sentences, and displays location updates from that file
   * on the location display. Data is pushed to the data source using a timeline to simulate live updates, as they would
   * appear if using real-time data from a GPS dongle.
   */
  @FXML
  private void start() {

    // prepare the mock data NMEA sentences
    File simulatedNmeaDataFile = new File(System.getProperty("data.dir"), "./samples-data/redlands/Redlands.nmea");
    if (simulatedNmeaDataFile.exists()) {

      try {
        // read the nmea file contents using a buffered reader and store the mock data sentences in a list
        BufferedReader bufferedReader = new BufferedReader(new FileReader(simulatedNmeaDataFile.getPath()));
        // add carriage return for NMEA location data source parser
        List<String> nmeaSentences = bufferedReader.lines().map(nmeaSentence -> nmeaSentence + "\n").collect(Collectors.toList());

        // close the stream and release resources
        bufferedReader.close();

        LocationDataSource.StatusChangedListener listener = new LocationDataSource.StatusChangedListener() {

          // create a new timeline to simulate a stream of NMEA data
          final Timeline timeline = new Timeline();

          @Override
          public void statusChanged(LocationDataSource.StatusChangedEvent statusChangedEvent) {
            // check that the location data source has started
            if (statusChangedEvent.getStatus() == LocationDataSource.Status.STARTED) {

              // add a satellite changed listener to the NMEA location data source and display satellite information on the app
              setupSatelliteChangedListener();

              timeline.setCycleCount(-1); // loop count
              // push the mock data NMEA sentences into the data source every 250 ms
              timeline.getKeyFrames().add(new KeyFrame(Duration.millis(250), event -> {

                // note: you can also use real-time NMEA sentences obtained via a GPS dongle
                nmeaLocationDataSource.pushData(nmeaSentences.get(count++).getBytes(StandardCharsets.UTF_8)); // post increment step
                // reset the count after the last data point is reached
                if (count == nmeaSentences.size()) {
                  count = 0;
                }

              }));

              // start the timeline
              timeline.play();
              // handle UI interactions
              startButton.setDisable(true);
              stopButton.setDisable(false);

              // stop the timeline and remove the status changed listener when the location data source has stopped
            } if (statusChangedEvent.getStatus() == LocationDataSource.Status.STOPPED) {

              timeline.stop();
              nmeaLocationDataSource.removeStatusChangedListener(this);
              // handle UI interactions
              stopButton.setDisable(true);
              startButton.setDisable(false);

            }
          }
        };

        // initialize the location data source and prepare to begin receiving location updates when data is pushed. As
        // updates are received, they will be displayed on the map
        nmeaLocationDataSource.addStatusChangedListener(listener);
        nmeaLocationDataSource.startAsync();

      } catch (Exception e) {
        new Alert(Alert.AlertType.ERROR, e.getCause().getMessage()).show();
        e.printStackTrace();
      }

    } else {
      new Alert(Alert.AlertType.ERROR, "File not found").show();
    }

  }

  /**
   * Disposes application resources.
   */
  void terminate() {
    if (mapView != null) {
      simulatedLocationDataSource.stop();
      mapView.dispose();
    }
  }

}
