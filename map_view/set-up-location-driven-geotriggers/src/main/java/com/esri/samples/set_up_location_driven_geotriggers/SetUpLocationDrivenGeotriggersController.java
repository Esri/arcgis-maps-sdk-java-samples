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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

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
import com.esri.arcgisruntime.location.LocationDataSource;
import com.esri.arcgisruntime.location.NmeaLocationDataSource;
import com.esri.arcgisruntime.location.NmeaSatelliteInfo;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;

public class SetUpLocationDrivenGeotriggersController {

  @FXML private MapView mapView;
  @FXML private Label satelliteCount;
  @FXML private Label satelliteID;
  @FXML private Button startButton;
  @FXML private Button stopButton;
  @FXML private Label systemInfo;

  private int count = 0;
  private NmeaLocationDataSource nmeaLocationDataSource;
  private List<NmeaSatelliteInfo> nmeaSatelliteInfo;

  public void initialize() {

    try {

      // authentication with an API key or named user is required to access basemaps and other location services
      String yourAPIKey = System.getProperty("apiKey");
      ArcGISRuntimeEnvironment.setApiKey(yourAPIKey);

      // create a map with the navigation basemap style and set it to the map view
      ArcGISMap map = new ArcGISMap(BasemapStyle.ARCGIS_NAVIGATION);
      mapView.setMap(map);

      // set a viewpoint on the map view centered on Redlands, California
      mapView.setViewpoint(new Viewpoint(new Point(-117.191, 34.0306, SpatialReferences.getWgs84()), 100000));

      // create a new NMEA location data source
      nmeaLocationDataSource = new NmeaLocationDataSource(SpatialReferences.getWgs84());

      // set the NMEA location data source onto the map view's location display
      LocationDisplay locationDisplay = mapView.getLocationDisplay();
      locationDisplay.setLocationDataSource(nmeaLocationDataSource);
      locationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.RECENTER);

      // disable map view interaction, the location display will automatically center on the mock device location
      mapView.setEnableMousePan(false);
      mapView.setEnableKeyboardNavigation(false);
      stopButton.setDisable(true);

    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
    }
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
   * Obtains NMEA satellite information from the NMEA location data source, and displays satellite information on the app.
   */
  private void setupSatelliteChangedListener() {

    HashSet<Integer> uniqueSatelliteIds = new HashSet<>();

    nmeaLocationDataSource.addSatellitesChangedListener(satellitesChangedEvent -> {

      // get satellite information from the NMEA location data source every time the satellites change
      nmeaSatelliteInfo = satellitesChangedEvent.getSatelliteInfos();
      // set the text of the satellite count label
      satelliteCount.setText("Satellite count: " + nmeaSatelliteInfo.size());

      for (NmeaSatelliteInfo satInfo : nmeaSatelliteInfo) {
        // collect unique satellite ids
        uniqueSatelliteIds.add(satInfo.getId());
        // sort the ids numerically
        List<Integer> sortedIds = new ArrayList<>(uniqueSatelliteIds);
        Collections.sort(sortedIds);
        // display the satellite system and id information
        systemInfo.setText("System: " + satInfo.getSystem());
        satelliteID.setText("Satellite IDs: " + sortedIds);
      }
    });
  }

  /**
   * Stops displaying the mock data location and receiving location data.
   */
  @FXML
  private void stop() {

    // stop receiving and displaying location data
    nmeaLocationDataSource.stop();

  }

  /**
   * Disposes application resources.
   */
  void terminate() {
    if (mapView != null) {
      nmeaLocationDataSource.stop();
      mapView.dispose();
    }
  }

}
