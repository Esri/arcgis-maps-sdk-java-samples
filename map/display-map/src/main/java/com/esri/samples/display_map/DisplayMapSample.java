/*
 * Copyright 2017 Esri.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.esri.samples.display_map;

import com.esri.arcgisruntime.layers.OpenStreetMapLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.MobileMapPackage;
import com.esri.arcgisruntime.portal.Portal;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.view.MapView;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DisplayMapSample extends Application {

  private MapView mapView;

  @Override
  public void start(Stage stage) {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);

      // set title, size, and add scene to stage
      stage.setTitle("Display Map Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      OpenStreetMapLayer osm = new OpenStreetMapLayer();
      osm.loadAsync();
      osm.addDoneLoadingListener(()-> {
        System.out.println("osm loaded " + osm.getLoadStatus());
      });

      Basemap basemap = new Basemap(osm);

      // authentication with an API key or named user is required to access basemaps and other location services
      String yourAPIKey = System.getProperty("apiKey");
      ArcGISRuntimeEnvironment.setApiKey(yourAPIKey);


      ArcGISMap map = new ArcGISMap(BasemapStyle.ARCGIS_IMAGERY_STANDARD);
      //ArcGISMap map = new ArcGISMap(basemap);

      // create a map view and set the map to it
      mapView = new MapView();
      mapView.setMap(map);



      // add the map view to the stack pane
      stackPane.getChildren().addAll(mapView);
    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
    }
  }

  /**
   * Stops and releases all resources used in application.
   */
  @Override
  public void stop() {

    if (mapView != null) {
      mapView.dispose();
    }
  }

  /**
   * Opens and runs application.
   *
   * @param args arguments passed to this application
   */
  public static void main(String[] args) {
    Path path = Paths.get("/Users/mark8487/.arcgis/200.0.0-3570/jniLibs/MACOS/x64/libruntimecore.dylib");

    System.out.println(" .arcgis check : " + Files.exists(path));


    //System.loadLibrary("/Users/mark8487/.arcgis/200.0.0-3570/jniLibs/MACOS/x64/libruntimecore.dylib");
    //System.loadLibrary("jniLibs/MACOS/x64/libruntimecore.dylib");

    //ArcGISRuntimeEnvironment.setInstallDirectory("/Users/mark8487/.arcgis/200.0.0-3586");
    ArcGISRuntimeEnvironment.setInstallDirectory("/Users/mark8487/.arcgis/100.15.0-3600");
    Application.launch(args);
  }

}
