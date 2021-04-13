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

package com.esri.samples.display_device_location_with_nmea_data_sources;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.esri.arcgisruntime.location.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geometry.PolylineBuilder;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.location.LocationDataSource.LocationChangedListener;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;

import javafx.util.Duration;
import org.apache.commons.io.IOUtils;

public class DisplayDeviceLocationWithNmeaDataSourcesController {

  @FXML private MapView mapView;
  private NmeaLocationDataSource nmeaLocationDataSource;
  private LocationDisplay locationDisplay;
  private List<NmeaSatelliteInfo> satelliteInfos;
  private int count = 0;
  @FXML private Label satelliteCount;
  @FXML private Label systemInfo;
  @FXML private Label satelliteID;
  @FXML private Button startButton;
  @FXML private Button resetButton;

  public void initialize() {

    try {

      // authentication with an API key or named user is required to access basemaps and other location services
      String yourAPIKey = System.getProperty("apiKey");
      ArcGISRuntimeEnvironment.setApiKey(yourAPIKey);

      // create a map with the dark gray basemap style
      ArcGISMap map = new ArcGISMap(BasemapStyle.ARCGIS_NAVIGATION);

      // create a map view and set the map to it
      mapView.setMap(map);

      // set a viewpoint on the map view centered on Los Angeles, California
      mapView.setViewpoint(new Viewpoint(new Point(-117.191, 34.0306, SpatialReferences.getWgs84()), 100000));
      // disable mapview interaction, the location display will automatically center on the mock device location
      mapView.setEnableMousePan(false);
      mapView.setEnableKeyboardNavigation(false);

      // create a new NMEA location data source
      nmeaLocationDataSource = new NmeaLocationDataSource(SpatialReferences.getWgs84());
      locationDisplay = mapView.getLocationDisplay();
      locationDisplay.setLocationDataSource(nmeaLocationDataSource);
      locationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.RECENTER);

      start();

    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
    }
  }

  @FXML
  private void start () {

    // enable receiving NMEA location data from external device
    nmeaLocationDataSource.startAsync();
    // display the user's location
    locationDisplay.startAsync();

    // load simulated NMEA sentences for sample
    File simulatedNmeaDataFile = new File(System.getProperty("data.dir"), "./samples-data/redlands/Redlands.nmea");
    if (simulatedNmeaDataFile.exists()) {

      try  {
        // read the nmea file contents
        List<String> lines = Files.readAllLines(simulatedNmeaDataFile.toPath(), StandardCharsets.UTF_8);

        List<String> sortedLines = new ArrayList<>();

        lines.forEach(line -> {
          if (line.contains("$GPGGA")) {
            sortedLines.add(line + "\n");
          } else {
            int index = sortedLines.size() - 1;
            sortedLines.add(index, line + "\n" );
          }

        });

        retrieveSatelliteInformation();

        Timeline timeline = new Timeline();
        timeline.setCycleCount(-1); // loop count
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(250), event -> {
          String nmeaLine = lines.get(count);
          nmeaLocationDataSource.pushData(sortedLines.get(count++).getBytes(StandardCharsets.UTF_8)); // post increment step
//          System.out.println(nmeaLocationDataSource.addSatellitesChangedListener(););
//          count++;
//          System.out.println(count);
//          System.out.println(nmeaLine);



//          nmeaLocationDataSource.addLocationChangedListener(locationChangedEvent -> {
//            LocationDataSource.LocationChangedEvent mLocationChangedEvent = locationChangedEvent;
//
//            NmeaLocationDataSource.NmeaLocation location = (NmeaLocationDataSource.NmeaLocation) mLocationChangedEvent.getLocation();
//
//            List<NmeaSatelliteInfo> satelliteInfos = location.getSatellites();
////            System.out.println("Satellite info" + satelliteInfos.get(0).getId());
//
//          });

//          System.out.println("Lines " + nmeaLine);
//          System.out.println("Sorted lines " + sortedLines.get(count));


          if (count == sortedLines.size()) count = 0;

        }));
        timeline.play();



        System.out.println("Sorted lines " + sortedLines);
        System.out.println("Lines " + lines);



//        nmeaLocationDataSource.pushData(lines.get(1).getBytes(StandardCharsets.UTF_8));


//        lines.forEach(line -> {
//          nmeaLocationDataSource.pushData(line.getBytes(StandardCharsets.UTF_8));
//
//        });
//        nmeaData =  lines.collect(Collectors.toList());
//        System.out.println(nmeaData.size());

//        nmeaLocationDataSource.pushData(nmeaData.get(1).getBytes(StandardCharsets.UTF_8));
      } catch (Exception e) {
        e.printStackTrace();
      }

//      String content = Files.readString(simulatedNmeaDataFile.toPath(), StandardCharsets.UTF_8);
//      List<String> splitStrings = Arrays.asList(content.split("\n").clone());
//      splitStrings.forEach(line -> {
//        if (line.startsWith("$GPGGA")) {
//          nmeaData.add(line + "\n");
//        } else {
//          nmeaData.set(nmeaData.size() - 1, (line + "\n"));
//        }
//
//        System.out.println(nmeaData);
//      });

//      System.out.println(Arrays.toString(content.getBytes(StandardCharsets.UTF_8)));
//
//      nmeaLocationDataSource.pushData(content.getBytes(StandardCharsets.UTF_8));
//

    } else {
      new Alert(Alert.AlertType.ERROR, "File not found").show();
    }

  }

  private void retrieveSatelliteInformation() {

    HashSet<Integer> uniqueValues = new HashSet<>();

    nmeaLocationDataSource.addSatellitesChangedListener(satellitesChangedEvent -> {
      // get satellite information from the nmea location data source every time the satellites change
      satelliteInfos = satellitesChangedEvent.getSatelliteInfos();

      satelliteCount.setText("Satellite count: " + satelliteInfos.size());

      for (NmeaSatelliteInfo satInfo : satelliteInfos) {

        // collect unique satellite ids
        uniqueValues.add(satInfo.getId());
        // sort the ids numerically
        List<Integer> sorted = new ArrayList<>(uniqueValues);
        Collections.sort(sorted);
        // display the satellite system and id information
        systemInfo.setText("System: " + satInfo.getSystem());
        satelliteID.setText("Satellite IDs " + sorted);
      }
    });


  }


  /**
   * Disposes application resources.
   */
  void terminate() {
    if (mapView != null) {
      mapView.dispose();
    }
  }


}
