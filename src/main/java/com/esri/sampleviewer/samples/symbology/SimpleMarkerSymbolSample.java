/* Copyright 2015 Esri.
 
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
 
    http://www.apache.org/licenses/LICENSE-2.0
 
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
limitations under the License.  */

package com.esri.sampleviewer.samples.symbology;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Map;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.Viewpoint;
import com.esri.arcgisruntime.symbology.Color;
import com.esri.arcgisruntime.symbology.RgbColor;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * This sample adds a <@Graphic> to a <@GraphicsOverlay> using a
 * <@SimpleMarkerSymbol>. How it works, a <@Point> geometry is created from some
 * known coordinates, a SimpleMarkerSymbol is constructed, and both are set on a
 * graphic. The graphic is added to a graphics overlay in the <@MapView> so that
 * it is visible.
 */
public class SimpleMarkerSymbolSample extends Application {

  private MapView mapView;

  @Override
  public void start(Stage stage) throws Exception {
    // create border pane and application scene
    BorderPane borderPane = new BorderPane();
    Scene scene = new Scene(borderPane);

    // size the stage, add a title, and set scene to stage
    stage.setTitle("Simple Marker Symbol Sample");
    stage.setHeight(700);
    stage.setWidth(800);
    stage.setScene(scene);
    stage.show();

    try {
      // create SpatialReference for points
      SpatialReference webMercator = SpatialReferences.getWebMercator();

      // create an initial viewpoint with a point and scale
      Point point = new Point(-226773, 6550477, webMercator);
      Viewpoint viewpoint = new Viewpoint(point, 7500);

      // create a view for this map
      mapView = new MapView();

      // create map with imagery basemap
      Map map = new Map(Basemap.createImagery());

      // set initial map view point
      map.setInitialViewpoint(viewpoint);

      // set map to be displayed in the mapview
      mapView.setMap(map);

      // place map in the center of the border pane
      borderPane.setCenter(mapView);

      // create new graphics overlay and add it to the mapview
      GraphicsOverlay graphicsOverlay = new GraphicsOverlay();
      mapView.getGraphicsOverlays().add(graphicsOverlay);

      // create a simple marker symbol
      Color redColor = new RgbColor(255, 0, 0, 255); // red, fully opaque
      SimpleMarkerSymbol symbol = new SimpleMarkerSymbol(redColor, 12,
          SimpleMarkerSymbol.Style.CIRCLE);

      // create a new graphic with a our point and symbol
      Graphic graphic = new Graphic(point, symbol);
      graphicsOverlay.getGraphics().add(graphic);

    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
    }
  }

  /**
   * Stops and releases all resources used in application.
   * 
   * @throws Exception if security manager doesn't allow JVM to exit with
   *           current status
   */
  @Override
  public void stop() throws Exception {
    // release resources when the application closes
    if (mapView != null) {
      mapView.dispose();
    }
    Platform.exit();
    System.exit(0);
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
