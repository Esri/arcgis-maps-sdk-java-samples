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
import java.util.List;
import java.util.Optional;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.mapping.BasemapType;
import com.esri.arcgisruntime.mapping.Map;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.RgbColor;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.Symbol;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * This sample demonstrates how to update the Geometry and attributes of a
 * Graphic.
 * <h4>How it Works</h4>
 * 
 * A {@link Graphic} is selected using the
 * {@link MapView#identifyGraphicsOverlay} method. The Geometry of a Graphic can
 * then be changed by using the {@link Graphic#setGeometry} method. The
 * {@link Symbol} of a Graphic can also be changed by using the
 * {@link Graphic#setSymbol} method. Lastly, the attributes of a Graphic can be
 * set by using the {@link Graphic#getAttributes} method and setting the new
 * value to the corresponding key.
 * <h4>Implementation Requirements</h4>
 * 
 * ListenableFuture needs to be a class level field because it could get garbage
 * collected right after being set. Meaning that the addDoneListener method will
 * never be called.
 */
public class UpdateGraphics extends Application {

  private boolean isUpdateLocationActive;
  private List<SimpleMarkerSymbol> markers;

  private MapView mapView;
  private Graphic selectedGraphic;
  private GraphicsOverlay graphicsOverlay;
  private ListenableFuture<List<Graphic>> identifyGraphics;

  @Override
  public void start(Stage stage) throws Exception {

    // create stack pane and application scene
    StackPane stackPane = new StackPane();
    Scene scene = new Scene(stackPane);
    scene.getStylesheets().add(getClass()
        .getResource("../resources/SamplesTheme.css").toExternalForm());

    // set title, size, and add scene to stage
    stage.setTitle("Update Graphics Sample");
    stage.setWidth(700);
    stage.setHeight(800);
    stage.setScene(scene);
    stage.show();

    // create a control panel
    VBox vBoxControl = new VBox(6);
    vBoxControl.setMaxSize(240, 400);
    vBoxControl.getStyleClass().add("panel-region");

    // create sample description
    Label descriptionLabel = new Label("Sample Description:");
    descriptionLabel.getStyleClass().add("panel-label");
    TextArea description = new TextArea(
        "This sample shows how to update the location, symbol, and attributes"
            + " of a Graphic. First select a Graphic, to change its location hit"
            + " the update location button and click anywhere on the map. To "
            + "update its description click the update description. To update "
            + "its symbol just select a symbol from drop down box.");
    description.setWrapText(true);
    description.autosize();
    description.setMinHeight(250);
    description.setEditable(false);

    // create buttons for user interaction
    Button updateLocationButton = new Button("Update Graphic Location");
    Button updateDescriptionButton = new Button("Update Graphic Description");
    updateLocationButton.setMaxWidth(Double.MAX_VALUE);
    updateDescriptionButton.setMaxWidth(Double.MAX_VALUE);
    updateLocationButton.setDisable(true);
    updateDescriptionButton.setDisable(true);

    // when clicked allow user to move graphic's location
    updateLocationButton.setOnAction(e -> {
      if (selectedGraphic.isSelected()) {
        isUpdateLocationActive = true;
      }
    });

    updateDescriptionButton.setOnAction(e -> {
      if (selectedGraphic.isSelected()) {
        // get attributes from selected graphic
        java.util.Map<String, Object> attributes =
            selectedGraphic.getAttributes();

        // create input dialog
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(attributes.get("NAME").toString());
        dialog.setGraphic(null);
        dialog.setHeaderText(attributes.get("DESCRIPTION").toString());
        dialog.setContentText("New Description");

        // set the graphic's description is text entered
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(text -> {
          if (!text.isEmpty()) {
            attributes.put("DESCRIPTION", text);
          }
        });
      }
    });

    // create section for combo box
    Label symbolLabel = new Label("Update Graphic Symbol");
    symbolLabel.getStyleClass().add("panel-label");
    ComboBox<String> symbolBox = new ComboBox<>();
    symbolBox.getItems().addAll("CIRCLE", "TRIANGLE", "CROSS", "DIAMOND");
    symbolBox.getSelectionModel().selectFirst();
    symbolBox.setMaxWidth(Double.MAX_VALUE);
    symbolBox.setDisable(true);

    // set the symbol of the graphic
    symbolBox.showingProperty().addListener((obs, wasShowing, isShowing) -> {
      if (selectedGraphic.isSelected() && !isShowing) {
        selectedGraphic.setSymbol(
            markers.get(symbolBox.getSelectionModel().getSelectedIndex()));
      }
    });

    // add labels, sample description, and buttons to the control panel
    vBoxControl.getChildren().addAll(descriptionLabel, description,
        updateLocationButton, updateDescriptionButton, symbolLabel, symbolBox);
    try {

      // create a graphics overlay
      graphicsOverlay = new GraphicsOverlay();
      selectedGraphic = new Graphic();

      // create a map with basemap light gray canvas
      Map map =
          new Map(BasemapType.LIGHT_GRAY_CANVAS, 56.075844, -2.681572, 13);

      // enable buttons when map view is done loading
      map.addDoneLoadingListener(() -> {
        symbolBox.setDisable(false);
        updateLocationButton.setDisable(false);
        updateDescriptionButton.setDisable(false);
      });

      // create a map view and set the map to it
      mapView = new MapView();
      mapView.setMap(map);

      // add graphics overlay to the map view
      mapView.getGraphicsOverlays().add(graphicsOverlay);

      // create default graphics for graphics overlay
      createGraphics();

      mapView.setOnMouseClicked(e -> {
        if (e.getButton() == MouseButton.PRIMARY) {
          // clear any selected graphic
          graphicsOverlay.clearSelection();

          // create a point from location clicked
          Point2D mapViewPoint = new Point2D(e.getX(), e.getY());

          if (isUpdateLocationActive) {
            // add new location to selected graphic
            Point mapPoint = mapView.screenToLocation(mapViewPoint);
            selectedGraphic.setGeometry(mapPoint);
            isUpdateLocationActive = false;
          } else {
            // identify the graphic that was selected
            identifyGraphics = mapView.identifyGraphicsOverlay(graphicsOverlay,
                mapViewPoint, 10, 2);

            identifyGraphics.addDoneListener(() -> {
              try {
                if (!identifyGraphics.get().isEmpty()) {
                  // store the selected graphic
                  selectedGraphic = identifyGraphics.get().get(0);
                  selectedGraphic.setSelected(true);
                }
              } catch (Exception x) {
                // on any error, display the stack trace
                x.printStackTrace();
              }
            });
          }
        }
      });

      // add the map view and control panel to stack pane
      stackPane.getChildren().addAll(mapView, vBoxControl);
      StackPane.setAlignment(vBoxControl, Pos.TOP_LEFT);
      StackPane.setMargin(vBoxControl, new Insets(10, 0, 0, 10));
    } catch (Exception e) {
      // on any error, display the stack trace
      e.printStackTrace();
    }
  }

  /**
   * Creates four Graphics with a location, a symbol, and two attributes. Then
   * adds those Graphics to the GraphicsOverlay.
   */
  private void createGraphics() {

    Graphic graphic;
    // create spatial reference for the points
    SpatialReference spatialReference = SpatialReference.create(4326);

    // create points to place markers
    List<Point> points = new ArrayList<>();
    points.add(new Point(-2.641, 56.077, spatialReference));
    points.add(new Point(-2.669, 56.058, spatialReference));
    points.add(new Point(-2.718, 56.060, spatialReference));
    points.add(new Point(-2.720, 56.073, spatialReference));

    // create simple marker symbols for the points
    markers = new ArrayList<>();
    markers.add(new SimpleMarkerSymbol(new RgbColor(255, 0, 0, 255), 10,
        SimpleMarkerSymbol.Style.CIRCLE));
    markers.add(new SimpleMarkerSymbol(new RgbColor(128, 0, 128, 255), 10,
        SimpleMarkerSymbol.Style.TRIANGLE));
    markers.add(new SimpleMarkerSymbol(new RgbColor(0, 255, 0, 255), 10,
        SimpleMarkerSymbol.Style.CROSS));
    markers.add(new SimpleMarkerSymbol(new RgbColor(0, 0, 255, 255), 10,
        SimpleMarkerSymbol.Style.DIAMOND));

    // create a list of names for graphics
    List<String> names = new ArrayList<>();
    names.add("LAMB");
    names.add("CANTY BAY");
    names.add("NORTH BERWICK");
    names.add("FIDRA");

    // create a list of descriptions for graphics
    List<String> descriptions = new ArrayList<>();
    descriptions.add("Just oppsite of Bass Rock.");
    descriptions.add("100m long and 50m wide.");
    descriptions.add("Lighthouse in northern section.");
    descriptions.add("Also known as Barley Farmstead.");

    // create four graphics with attributes and add to graphics overlay
    for (int i = 0; i < 4; i++) {
      graphic = new Graphic(points.get(i), markers.get(i));
      graphic.getAttributes().put("NAME", names.get(i));
      graphic.getAttributes().put("DESCRIPTION", descriptions.get(i));
      graphicsOverlay.getGraphics().add(graphic);
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
