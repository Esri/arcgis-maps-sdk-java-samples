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

package com.esri.sampleviewer.samples.graphicsoverlay;

import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Map;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.RgbColor;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;

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

/**
 * This sample demonstrates how to render Graphics using a SimpleRenderer.
 * <h4>How it Works</h4>
 * 
 * A {@link SimpleRenderer} is created using the {@link SimpleMarkerSymbol} and
 * is set to a {@link GraphicsOverlay}. Three Points are then created and each
 * attached to a {@link Graphic} which is then added to the GraphicsOverlay. The
 * GraphicsOverlay is set to the MapView, so the Graphics are visible. All
 * Graphics are then automatically styled with the same symbol that was defined
 * in the renderer.
 */
public class AddGraphicsWithRenderer extends Application {

  private MapView mapView;

  @Override
  public void start(Stage stage) throws Exception {

    // create stack pane and application scene
    StackPane stackPane = new StackPane();
    Scene scene = new Scene(stackPane);
    scene.getStylesheets().add(getClass()
        .getResource("../resources/SamplesTheme.css").toExternalForm());

    // set title, size, and add scene to stage
    stage.setTitle("Add Graphics with Renderer Sample");
    stage.setWidth(700);
    stage.setHeight(800);
    stage.setScene(scene);
    stage.show();

    // create a control panel
    VBox vBoxControl = new VBox(6);
    vBoxControl.setMaxSize(240, 120);
    vBoxControl.getStyleClass().add("panel-region");

    // create sample description
    Label descriptionLabel = new Label("Sample Description:");
    descriptionLabel.getStyleClass().add("panel-label");
    TextArea description = new TextArea(
        "This sample shows how to create Graphics using a Simple Renderer.");
    description.autosize();
    description.setWrapText(true);
    description.setEditable(false);

    // add label and sample description to the control panel
    vBoxControl.getChildren().addAll(descriptionLabel, description);
    try {

      // create spatial reference
      SpatialReference spatialReference = SpatialReferences.getWgs84();

      // create points
      Point topPoint = new Point(-110.828, 44.460, spatialReference);
      Point middlePoint = new Point(-110.829, 44.461, spatialReference);
      Point bottomPoint = new Point(-110.829, 44.462, spatialReference);

      // create a envelope to use for the map view
      Envelope envelope = new Envelope(topPoint, bottomPoint);

      // create a map with the basemap imagery with labels
      Map map = new Map(Basemap.createImageryWithLabels());

      // create a view for this map and set the map to it
      mapView = new MapView();
      mapView.setMap(map);

      // set the viewpoint to the map view with padding
      mapView.setViewpointGeometryWithPaddingAsync(envelope, 400);

      // create a new graphics overlay and add it to the map view
      GraphicsOverlay graphicOverlay = new GraphicsOverlay();
      mapView.getGraphicsOverlays().add(graphicOverlay);

      // create a red cross simple marker symbol
      RgbColor red = new RgbColor(255, 0, 0, 255);
      SimpleMarkerSymbol crossSymbol =
          new SimpleMarkerSymbol(red, 12, SimpleMarkerSymbol.Style.CROSS);
      SimpleRenderer renderer = new SimpleRenderer(crossSymbol);

      // set the graphic overlay's renderer
      graphicOverlay.setRenderer(renderer);

      // create graphic from points
      Graphic topGraphic = new Graphic(topPoint);
      Graphic middleGraphic = new Graphic(middlePoint);
      Graphic bottomGraphic = new Graphic(bottomPoint);

      // add graphics to the graphic overlay
      graphicOverlay.getGraphics().add(topGraphic);
      graphicOverlay.getGraphics().add(middleGraphic);
      graphicOverlay.getGraphics().add(bottomGraphic);

      // add the map view and control panel to stack pane
      stackPane.getChildren().addAll(mapView, vBoxControl);
      StackPane.setAlignment(vBoxControl, Pos.TOP_LEFT);
      StackPane.setMargin(vBoxControl, new Insets(10, 0, 0, 10));
    } catch (Exception e) {
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
