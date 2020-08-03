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

package com.esri.samples.display_layer_view_state;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import com.esri.arcgisruntime.ArcGISRuntimeException;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.layers.Layer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.LayerViewStatus;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.portal.Portal;
import com.esri.arcgisruntime.portal.PortalItem;

public class DisplayLayerViewStateSample extends Application {

  private MapView mapView;
  private FeatureLayer featureLayer;
  private Label layerViewStatusLabel;
  private Button loadLayerButton;
  private Button hideLayerButton;
  private VBox controlsVBox;

  @Override
  public void start(Stage stage) {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);
      scene.getStylesheets().add(getClass().getResource("/display_layer_view_state/style.css").toExternalForm());

      // set title, size, and add scene to stage
      stage.setTitle("Display Layer View State Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // create an ArcGISMap with the topographic basemap
      ArcGISMap map = new ArcGISMap(Basemap.createTopographic());

      // create a map view and set the ArcGISMap to it
      mapView = new MapView();
      mapView.setMap(map);

      // set the initial viewpoint for the map view
      mapView.setViewpoint(new Viewpoint(new Point(-11e6, 45e5, SpatialReferences.getWebMercator()), 40000000));

      // create a control panel
      controlsVBox = new VBox();
      controlsVBox.getStyleClass().add("panel-region");

      // create a label to display the view status and add to the control panel
      layerViewStatusLabel = new Label("Click button to load feature layer\n ");
      layerViewStatusLabel.getStyleClass().add("panel-label");
      controlsVBox.getChildren().add(layerViewStatusLabel);

      // create a listener that fires every time a layer's view status has changed
      mapView.addLayerViewStateChangedListener(statusChangeEvent -> {

        // get the layer whose state has changed
        Layer layer = statusChangeEvent.getLayer();
        // only update the status if the layer is the feature layer
        if (layer != featureLayer) {
          return ;
        }
        // get the layer's view status and display the status
        EnumSet<LayerViewStatus> layerViewStatus = statusChangeEvent.getLayerViewStatus();
        displayViewStateText(layerViewStatus);

        // if there is an error or warning, display the message as an alert
        ArcGISRuntimeException error = statusChangeEvent.getError();
        if (error != null){
          Throwable cause = error.getCause();
          String message = (cause != null) ? cause.toString() : error.toString();
          Alert alert = new Alert(Alert.AlertType.ERROR, message);
          alert.show();
        }
      });

      // create buttons to toggle the visibility of the feature layer
      loadLayerButton = new Button("Load Layer");
      loadLayerButton.getStyleClass().add("panel-button");
      hideLayerButton = new Button("Hide Layer");
      hideLayerButton.getStyleClass().add("panel-button");
      // initially add show layer button to control panel
      controlsVBox.getChildren().add(loadLayerButton);

      // create a listener for clicks on the showLayerButton
      loadLayerButton.setOnAction(event -> {

        // if the feature layer already exists and is hidden, toggle it's visibility to visible
        if (featureLayer != null && !featureLayer.isVisible()) {
          featureLayer.setVisible(true);
        } else {
          // create a feature layer from a portal item
          final PortalItem portalItem = new PortalItem(new Portal("https://runtime.maps.arcgis.com/"),
            "b8f4033069f141729ffb298b7418b653");
          featureLayer = new FeatureLayer(portalItem, 0);
          //  set a minimum and a maximum scale for the visibility of the feature layer
          featureLayer.setMinScale(40000000);
          featureLayer.setMaxScale(40000000 / 10);
          // add the feature layer to the map
          map.getOperationalLayers().add(featureLayer);
        }

        // toggle the visibility of the buttons
        displayHideButton();
      });

      // create a listener for clicks on the hideLayerButton to toggle visibility of the feature layer
      hideLayerButton.setOnAction(event -> {

        if (featureLayer == null) return;

        if (featureLayer.isVisible()) {
          featureLayer.setVisible(false);
          displayLoadButton();
        }
        else {
          featureLayer.setVisible(true);
          displayHideButton();
        }
      });

      // add the map view and control panel to the stack pane
      stackPane.getChildren().addAll(mapView, controlsVBox);
      StackPane.setAlignment(controlsVBox, Pos.TOP_CENTER);
      StackPane.setMargin(controlsVBox, new Insets(10, 0, 0, 0));
    } catch (Exception e) {
      // on any error, display the stack trace
      e.printStackTrace();
    }
  }

  /**
   * Formats and displays all relevant layer view status flags
   *
   * @param layerViewStatus to display
   */
  public void displayViewStateText(EnumSet<LayerViewStatus> layerViewStatus){
    List<String> stringList = new ArrayList<>();

    if (layerViewStatus.contains(LayerViewStatus.ACTIVE)) {
      stringList.add("Active");
    }
    if (layerViewStatus.contains(LayerViewStatus.ERROR)) {
      stringList.add("Error");
    }
    if (layerViewStatus.contains(LayerViewStatus.LOADING)) {
      stringList.add("Loading");
    }
    if (layerViewStatus.contains(LayerViewStatus.NOT_VISIBLE)) {
      stringList.add("Not Visible");
    }
    if (layerViewStatus.contains(LayerViewStatus.OUT_OF_SCALE)) {
      stringList.add("Out of Scale");
    }
    if (layerViewStatus.contains(LayerViewStatus.WARNING)) {
      stringList.add("Warning");
    }

    layerViewStatusLabel.setText("Current view status:\n" + String.join(", ", stringList));
    layerViewStatusLabel.setVisible(true);
  }

  /**
   * Display show button
   */
  public void displayLoadButton(){
    controlsVBox.getChildren().remove(hideLayerButton);
    controlsVBox.getChildren().add(loadLayerButton);
  }

  /**
   * Display hide button
   */
  public void displayHideButton(){
    controlsVBox.getChildren().remove(loadLayerButton);
    controlsVBox.getChildren().add(hideLayerButton);
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

    Application.launch(args);
  }

}
