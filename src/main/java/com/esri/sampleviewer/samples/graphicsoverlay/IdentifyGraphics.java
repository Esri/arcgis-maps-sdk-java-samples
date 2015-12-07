/*
 * Copyright 2015 Esri. Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package com.esri.sampleviewer.samples.graphicsoverlay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.BasemapType;
import com.esri.arcgisruntime.mapping.Map;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.RgbColor;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.UniqueValue;
import com.esri.arcgisruntime.symbology.UniqueValueRenderer;

/**
 * This sample demonstrates how to identify Graphics at a given screen point.
 * <h4>How it Works</h4>
 * 
 * A new {@link GraphicsOverlay} is created and added to the MapView using the
 * {@link MapView#getGraphicsOverlays} method. A set of {@link Graphic}s are
 * then added to this GraphicsOverlay and can be identified by using the
 * {@link MapView#identifyGraphicsOverlays} method.
 * <p>
 * A ListenableFuture needs to be a class level field because it could get
 * garbage collected right after being set.
 */
public class IdentifyGraphics extends Application {

  private MapView mapView;
  private ListenableFuture<List<Graphic>> identifyGraphics;

  private static final String SAMPLES_THEME_PATH =
      "../resources/SamplesTheme.css";

  @Override
  public void start(Stage stage) throws Exception {

    // create stack pane and application scene
    StackPane stackPane = new StackPane();
    Scene scene = new Scene(stackPane);
    scene.getStylesheets().add(getClass().getResource(SAMPLES_THEME_PATH)
        .toExternalForm());

    // set title, size, and add scene to stage
    stage.setTitle("Identify Graphics Sample");
    stage.setWidth(800);
    stage.setHeight(700);
    stage.setScene(scene);
    stage.show();

    // create a control panel
    VBox vBoxControl = new VBox(6);
    vBoxControl.setMaxSize(250, 190);
    vBoxControl.getStyleClass().add("panel-region");

    // create sample description
    Label descriptionLabel = new Label("Sample Description:");
    descriptionLabel.getStyleClass().add("panel-label");
    TextArea description = new TextArea(
        "This sample shows how to Identify a Graphic. To identify, select a Graphic.");
    description.setWrapText(true);
    description.autosize();
    description.setEditable(false);

    // add label and sample description to the control panel
    vBoxControl.getChildren().addAll(descriptionLabel, description);
    try {

      // create a map with basemap light gray canvas
      final Map map =
          new Map(BasemapType.LIGHT_GRAY_CANVAS, 56.075844, -2.681572, 13);

      // create a map view and set the map to it
      mapView = new MapView();
      mapView.setMap(map);

      // create a graphics overlay
      final GraphicsOverlay graphicsOverlay = new GraphicsOverlay();

      // add graphics overlay to the map view
      mapView.getGraphicsOverlays().add(graphicsOverlay);

      // create and add graphics to graphics overlay
      createRenderedGraphics(graphicsOverlay);

      mapView.setOnMouseClicked(e -> {
        if (e.getButton() == MouseButton.PRIMARY) {
          // create a point from location clicked
          Point2D mapViewPoint = new Point2D(e.getX(), e.getY());

          // identify any graphics that were selected 
          identifyGraphics = mapView.identifyGraphicsOverlay(graphicsOverlay,
              mapViewPoint, 10, 1);

          //displays dialog for graphics
          identifyGraphics.addDoneListener(() -> {
            Platform.runLater(() -> createGraphicDialog());
          });
        }
      });

      // add the map view and control panel to stack pane
      stackPane.getChildren().addAll(mapView, vBoxControl);
      StackPane.setAlignment(vBoxControl, Pos.TOP_LEFT);
      StackPane.setMargin(vBoxControl, new Insets(10, 0, 0, 10));
    } catch (Exception e) {
      // on any error, print stack trace
      e.printStackTrace();
    }
  }

  /**
   * Creates four different Graphics and renders them to the GrapicsOverlay.
   * 
   * @param graphicsOverlay holds graphic information for map
   */
  private void createRenderedGraphics(GraphicsOverlay graphicsOverlay) {

    // create a unique value renderer
    UniqueValueRenderer uniqueValueRenderer = new UniqueValueRenderer();
    uniqueValueRenderer.getFieldNames().add("SYMBOL");

    // the unique value below will need to be passed a list of objects
    // create list of red circle points to render
    List<Object> appliedSymbolList = new ArrayList<>();
    appliedSymbolList.add("Red Circle Symbol");
    SimpleMarkerSymbol markerSymbol = new SimpleMarkerSymbol(
        new RgbColor(255, 0, 0, 255), 10, SimpleMarkerSymbol.Style.CIRCLE);
    UniqueValue uniqueValue = new UniqueValue("Symbol", "Red Circle Symbol",
        markerSymbol, appliedSymbolList);
    uniqueValueRenderer.getUniqueValues().add(uniqueValue);

    // create list of purple triangle points to render
    appliedSymbolList = new ArrayList<>();
    appliedSymbolList.add("Purple Triangle Symbol");
    markerSymbol = new SimpleMarkerSymbol(new RgbColor(128, 0, 128, 255), 10,
        SimpleMarkerSymbol.Style.TRIANGLE);
    uniqueValue = new UniqueValue("Symbol", "Purple Triangle Symbol",
        markerSymbol, appliedSymbolList);
    uniqueValueRenderer.getUniqueValues().add(uniqueValue);

    // create list of green cross points to render
    appliedSymbolList = new ArrayList<>();
    appliedSymbolList.add("Green Cross Symbol");
    markerSymbol = new SimpleMarkerSymbol(new RgbColor(0, 255, 0, 255), 10,
        SimpleMarkerSymbol.Style.CROSS);
    uniqueValue = new UniqueValue("Symbol", "Green Cross Symbol", markerSymbol,
        appliedSymbolList);
    uniqueValueRenderer.getUniqueValues().add(uniqueValue);

    // create list of blue diamond points to render
    appliedSymbolList = new ArrayList<>();
    appliedSymbolList.add("Blue Diamond Symbol");
    markerSymbol = new SimpleMarkerSymbol(new RgbColor(0, 0, 255, 255), 10,
        SimpleMarkerSymbol.Style.DIAMOND);
    uniqueValue = new UniqueValue("Symbol", "Blue Diamond Symbol", markerSymbol,
        appliedSymbolList);
    uniqueValueRenderer.getUniqueValues().add(uniqueValue);

    // create four graphics and add them to the graphics overlay
    SpatialReference spatialReference = SpatialReferences.getWgs84();
    Graphic redGraphic = new Graphic(new Point(-2.641, 56.077,
        spatialReference));
    redGraphic.getAttributes().put("SYMBOL", "Red Circle Symbol");

    Graphic purpleGraphic = new Graphic(new Point(-2.669, 56.058,
        spatialReference));
    purpleGraphic.getAttributes().put("SYMBOL", "Purple Triangle Symbol");

    Graphic greenGraphic = new Graphic(new Point(-2.688, 56.062,
        spatialReference));
    greenGraphic.getAttributes().put("SYMBOL", "Green Cross Symbol");

    Graphic blueGraphic = new Graphic(new Point(-2.720, 56.073,
        spatialReference));
    blueGraphic.getAttributes().put("SYMBOL", "Blue Diamond Symbol");

    graphicsOverlay.getGraphics().addAll(
        Arrays.asList(redGraphic, purpleGraphic, greenGraphic, blueGraphic));

    // apply the renderer to the graphics overlay
    graphicsOverlay.setRenderer(uniqueValueRenderer);
  }

  /**
   * Finds the description for the first Graphic that was selected and displays
   * that description in a dialog box.
   */
  private void createGraphicDialog() {

    try {
      // get list of graphics that were selected
      List<Graphic> graphics = identifyGraphics.get();

      if (!graphics.isEmpty()) {
        // get only the first graphic description
        String description =
            graphics.get(0).getAttributes().get("SYMBOL").toString();

        // create and show a alert dialog box
        Alert dialog = new Alert(AlertType.INFORMATION);
        dialog.setHeaderText("SYMBOL");
        dialog.setContentText(description);
        dialog.showAndWait();
      }
    } catch (Exception e) {
      // on any error, display the stack trace
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
