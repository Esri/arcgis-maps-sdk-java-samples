/*
 * Copyright 2016 Esri.
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

//[DocRef: Name=Get_Started-First_Map_App-First_Application-Java
package com.esri.samples.map.display_map;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

//[DocRef: Name=Get_Started-First_Map_App-Imports-Java
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.MapView;
//[DocRef: Name=Get_Started-First_Map_App-Imports-Java

//[DocRef: Name=Get_Started-First_Map_App-Variable-Java
public class DisplayMapSample extends Application {

  private MapView mapView;
  //[DocRef: Name=Get_Started-First_Map_App-Variable-Java

  @Override
  public void start(Stage stage) throws Exception {

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

      //[DocRef: Name=Get_Started-First_Map_App-DisplayMap-Java
      // create a ArcGISMap with the a Basemap instance with an Imagery base
      // layer
      ArcGISMap map = new ArcGISMap(Basemap.createImagery());

      // set the map to be displayed in this view
      mapView = new MapView();
      mapView.setMap(map);

      // add the map view to stack pane
      stackPane.getChildren().addAll(mapView);
      //[DocRef: Name=Get_Started-First_Map_App-DisplayMap-Java
    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
    }
  }

  //[DocRef: Name=Get_Started-First_Map_App-Dispose-Java
  /**
   * Stops and releases all resources used in application.
   */
  @Override
  public void stop() throws Exception {

    if (mapView != null) {
      mapView.dispose();
    }
  }
  //[DocRef: Name=Get_Started-First_Map_App-Dispose-Java

  /**
   * Opens and runs application.
   *
   * @param args arguments passed to this application
   */
  public static void main(String[] args) {

    Application.launch(args);
  }

}
//[DocRef: Name=Get_Started-First_Map_App-First_Application-Java
