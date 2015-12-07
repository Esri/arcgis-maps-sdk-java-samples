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

import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Map;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.Color;
import com.esri.arcgisruntime.symbology.RgbColor;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;

/**
 * This sample demonstrates how to create a SimpleRenderer and add it to a
 * GraphicsOverlay.
 * <p>
 * This {@link SimpleRenderer}, when added to the {@link GraphicsOverlay}, will
 * use its {@link SimpleMarkerSymbol} to display any {@link Graphic}s that don't
 * already have a symbol set. A Renderer will not override a symbol that is
 * manually set to a Graphic.
 * <p>
 * SimpleRenderers can also be defined on a {@link FeatureLayer}.
 * <h4>How it Works</h4>
 * 
 * First a GraphicsOverlay needs to be created and added using the
 * {@link MapView#getGraphicsOverlays} method. Now a SimpleRenderer can be
 * created using a SimpleMarkerSymbol and set to the GraphicsOverlay using the
 * {@link GraphicsOverlay#setRenderer} method.
 * <p>
 * There is no need to set a symbol on a Graphic, the symbol from the renderer
 * is used.
 */
public class SimpleRendererSample extends Application {

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
    stage.setTitle("Simple Renderer Sample");
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
    TextArea description = new TextArea("This sample shows how to create a "
        + "Simple Renderer and add it to a Graphics Overlay. Graphics applied "
        + "to the Graphics Overlay will automatically render with the same "
        + "symbol.");
    description.setWrapText(true);
    description.autosize();
    description.setEditable(false);

    // add sample label and description to the control panel
    vBoxControl.getChildren().addAll(descriptionLabel, description);
    try {

      // create a map with the imagery basemap
      final Map map = new Map(Basemap.createImageryWithLabels());

      // create a view and set map to it
      mapView = new MapView();
      mapView.setMap(map);

      // create SpatialReference for points
      final SpatialReference spatialReference = SpatialReferences.getWgs84();

      // create points for displaying graphics
      Point oldFaithfullPoint = new Point(-110.828140, 44.460458,
          spatialReference);
      Point cascadeGeyserPoint = new Point(-110.829004, 44.462438,
          spatialReference);
      Point plumeGeyserPoint = new Point(-110.829381, 44.462735,
          spatialReference);

      // create initial viewpoint using an envelope
      Envelope envelope = new Envelope(oldFaithfullPoint, plumeGeyserPoint);

      // set viewpoint on mapview with padding
      mapView.setViewpointGeometryWithPaddingAsync(envelope, 300.0);

      // create a graphics overlay and add it to the mapview
      GraphicsOverlay graphicsOverlay = new GraphicsOverlay();
      mapView.getGraphicsOverlays().add(graphicsOverlay);

      // create a simple symbol for use in a simple renderer
      Color color = new RgbColor(255, 0, 0, 255); // red, fully opaque
      SimpleMarkerSymbol symbol = new SimpleMarkerSymbol(color, 12,
          SimpleMarkerSymbol.Style.CROSS);
      SimpleRenderer renderer = new SimpleRenderer(symbol);

      // apply the renderer to the graphics overlay 
      graphicsOverlay.setRenderer(renderer);

      // create graphics from the location points. 
      Graphic oldFaithfullGraphic = new Graphic(oldFaithfullPoint);

      Graphic cascadeGeyserGraphic = new Graphic(cascadeGeyserPoint);
      Graphic plumeGeyserGraphic = new Graphic(plumeGeyserPoint);
      graphicsOverlay.getGraphics().add(oldFaithfullGraphic);
      graphicsOverlay.getGraphics().add(cascadeGeyserGraphic);
      graphicsOverlay.getGraphics().add(plumeGeyserGraphic);

      // add the map view and control box to stack pane
      stackPane.getChildren().addAll(mapView, vBoxControl);
      StackPane.setAlignment(vBoxControl, Pos.TOP_LEFT);
      StackPane.setMargin(vBoxControl, new Insets(10, 0, 0, 10));
    } catch (Exception e) {
      // on any error, display stack trace
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
