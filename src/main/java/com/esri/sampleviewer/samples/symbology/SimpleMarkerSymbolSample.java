/*
 * Copyright 2015 Esri.
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

package com.esri.sampleviewer.samples.symbology;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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

/**
 * This sample demonstrates how to add a SimpleMarkerSymbol to a Graphic and
 * display it using a GraphicsOverlay.
 * <p>
 * Adding a {@link Graphic} to a {@link GraphicsOverlay} will make it visible to
 * the user as long as the GraphicsOverlay is set to the MapView and the Graphic
 * has a symbol.
 * <h4>How it Works</h4>
 * 
 * First a GraphicsOverlay needs to be created and added using the
 * {@link MapView#getGraphicsOverlays} method. Then a Graphic can be created
 * using a {@link Point} and a {@link SimpleMarkerSymbol}. Lastly the Graphic
 * needs to be added to the GraphicsOverlay, {@link GraphicsOverlay#getGraphics}
 * .
 */
public class SimpleMarkerSymbolSample extends Application {

  private MapView mapView;

  private static final String SAMPLES_THEME_PATH =
      "../resources/SamplesTheme.css";

  @Override
  public void start(Stage stage) throws Exception {

    // create stack pane and application scene
    StackPane stackPane = new StackPane();
    Scene scene = new Scene(stackPane);
    scene.getStylesheets().add(getClass().getResource(SAMPLES_THEME_PATH)
        .toExternalForm());

    // size the stage, add a title, and set scene to stage
    stage.setTitle("Simple Marker Symbol Sample");
    stage.setWidth(800);
    stage.setHeight(700);
    stage.setScene(scene);
    stage.show();

    // create a control panel
    VBox vBoxControl = new VBox(6);
    vBoxControl.setMaxSize(250, 190);
    vBoxControl.getStyleClass().add("panel-region");

    // create sample label and description
    Label descriptionLabel = new Label("Sample Description");
    descriptionLabel.getStyleClass().add("panel-label");
    TextArea description = new TextArea("This sample shows how to add a "
        + "Simple Marker Symbol to a Graphic and display it using a "
        + "Graphics Overlay.");
    description.setWrapText(true);
    description.autosize();
    description.setEditable(false);

    // add sample label and description to the control panel
    vBoxControl.getChildren().addAll(descriptionLabel, description);
    try {

      // create map with imagery basemap
      final Map map = new Map(Basemap.createImagery());

      // create spatial reference for WGS 1948
      final SpatialReference webMercator = SpatialReferences.getWebMercator();

      // create a initial viewpoint with a center point and scale
      Point point = new Point(-226773, 6550477, webMercator);
      Viewpoint viewpoint = new Viewpoint(point, 7500);

      // set initial view point to the map
      map.setInitialViewpoint(viewpoint);

      // create a view and set map to it
      mapView = new MapView();
      mapView.setMap(map);

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

      // add the map view and control box to stack pane
      stackPane.getChildren().addAll(mapView, vBoxControl);
      StackPane.setAlignment(vBoxControl, Pos.TOP_LEFT);
      StackPane.setMargin(vBoxControl, new Insets(10, 0, 0, 10));
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
