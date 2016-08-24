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

package com.esri.samples.symbology;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;

public class SimpleMarkerSymbolSample extends Application {

  private MapView mapView;

  @Override
  public void start(Stage stage) throws Exception {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);

      // size the stage, add a title, and set scene to stage
      stage.setTitle("Simple Marker Symbol Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // create ArcGISMap with imagery basemap
      final ArcGISMap map = new ArcGISMap(Basemap.createImagery());

      // create spatial reference for WGS 1948
      final SpatialReference webMercator = SpatialReferences.getWebMercator();

      // create a initial viewpoint with a center point and scale
      Point point = new Point(-226773, 6550477, webMercator);
      Viewpoint viewpoint = new Viewpoint(point, 7500);

      // set initial view point to the ArcGISMap
      map.setInitialViewpoint(viewpoint);

      // create a view and set ArcGISMap to it
      mapView = new MapView();
      mapView.setMap(map);

      // create new graphics overlay and add it to the mapview
      GraphicsOverlay graphicsOverlay = new GraphicsOverlay();
      mapView.getGraphicsOverlays().add(graphicsOverlay);

      // create a red (0xFFFF0000) simple marker symbol
      SimpleMarkerSymbol symbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, 0xFFFF0000, 12);

      // create a new graphic with a our point and symbol
      Graphic graphic = new Graphic(point, symbol);
      graphicsOverlay.getGraphics().add(graphic);

      // add the map view and control box to stack pane
      stackPane.getChildren().add(mapView);
    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
    }
  }

  /**
   * Stops and releases all resources used in application.
   */
  @Override
  public void stop() throws Exception {

    // release resources when the application closes
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

    Application.launch(args);
  }

}
