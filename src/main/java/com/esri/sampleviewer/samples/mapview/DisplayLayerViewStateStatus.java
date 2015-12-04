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

package com.esri.sampleviewer.samples.mapview;

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

import com.esri.arcgisruntime.datasource.arcgis.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.ArcGISMapImageLayer;
import com.esri.arcgisruntime.layers.ArcGISTiledLayer;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.layers.Layer;
import com.esri.arcgisruntime.mapping.Map;
import com.esri.arcgisruntime.mapping.view.LayerViewStatus;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.Viewpoint;

/**
 * This sample demonstrates how to get the view status of a Layer from a Map.
 * <p>
 * A {@link LayerViewStatus} can be:
 * <li>ACTIVE
 * <li>ERROR
 * <li>LOADING
 * <li>NOT_VISIBLE
 * <li>OUT_OF_SCALE
 * <li>UNKNOWN</li>
 * <p>
 * The LayerViewStatus can be useful in identifying if a user has zoomed to a
 * certain level of scale that isn't visible in a particular layer.
 * <h4>How it Works</h4>
 * 
 * A {@link ArcGISMapImageLayer} is created from a URL and its minimum and
 * maximum scale is set using the {@link ArcGISMapImageLayer#setMinScale} and
 * {@link ArcGISMapImageLayer#setMaxScale} methods. This layer is then added to
 * the map, {@link Map#getOperationalLayers()}, and once the Map is added to
 * {@link MapView#setMap}, {@link Layer}s of the Map will begin to load and
 * become ACTIVE.
 * <p>
 * The {@link MapView#addLayerViewStateChangedListener} fires an event every
 * time one of the Layers' view status changes.
 */
public class DisplayLayerViewStateStatus extends Application {

  private MapView mapView;

  private String[] viewStatusList;

  private static final int MIN_SCALE = 40000000;
  private static final int TILED_LAYER = 0;
  private static final int IMAGE_LAYER = 1;
  private static final int FEATURE_LAYER = 2;

  private static final String SERVICE_TIME_ZONES =
      "http://sampleserver6.arcgisonline.com/arcgis/rest/services/WorldTimeZones/MapServer";
  private static final String SERVICE_CENSUS =
      "http://sampleserver6.arcgisonline.com/arcgis/rest/services/Census/MapServer";
  private static final String SERVICE_RECREATION =
      "http://sampleserver6.arcgisonline.com/arcgis/rest/services/Recreation/FeatureServer/0";

  @Override
  public void start(Stage stage) throws Exception {

    // create stack pane and application scene
    StackPane stackPane = new StackPane();
    Scene scene = new Scene(stackPane);
    scene.getStylesheets().add(getClass()
        .getResource("../resources/SamplesTheme.css").toExternalForm());

    // set title, size, and add scene to stage
    stage.setTitle("Display Layer View State Status Sample");
    stage.setWidth(700);
    stage.setHeight(800);
    stage.setScene(scene);
    stage.show();

    // create a control panel
    VBox vBoxControl = new VBox(6);
    vBoxControl.setMaxSize(250, 270);
    vBoxControl.getStyleClass().add("panel-region");

    // create sample description
    Label descriptionLabel = new Label("Sample Description:");
    descriptionLabel.getStyleClass().add("panel-label");
    TextArea description = new TextArea(
        "This sample shows how to get the view status of a Layer from a Map. " +
            "Zoom in / out to see the census layer's view status change " +
            "between ACTIVE and OUT_OF_SCALE.");
    description.setWrapText(true);
    description.autosize();
    description.setEditable(false);

    // create labels to display the view status of each layer
    Label worldTimeZonesLabel = new Label("World Time Zones: ");
    Label censusLabel = new Label("Census: ");
    Label facilitiesLabel = new Label("Facilities: ");

    worldTimeZonesLabel.getStyleClass().add("panel-label");
    censusLabel.getStyleClass().add("panel-label");
    facilitiesLabel.getStyleClass().add("panel-label");

    // add labels and sample description to the control panel
    vBoxControl.getChildren().addAll(descriptionLabel, description,
        worldTimeZonesLabel, censusLabel, facilitiesLabel);
    try {

      // create three layers to add to the map
      final ArcGISTiledLayer tiledLayer = new ArcGISTiledLayer(
          SERVICE_TIME_ZONES);

      final ArcGISMapImageLayer imageLayer = new ArcGISMapImageLayer(
          SERVICE_CENSUS);
      // setting the scales at which this layer can be viewed
      imageLayer.setMinScale(MIN_SCALE);
      imageLayer.setMaxScale(MIN_SCALE / 10);

      // creating a layer from a service feature table
      final ServiceFeatureTable featureTable = new ServiceFeatureTable(
          SERVICE_RECREATION);
      final FeatureLayer featureLayer = new FeatureLayer(featureTable);

      // adding layers to the maps' layer list
      final Map map = new Map();
      map.getOperationalLayers().add(tiledLayer);
      map.getOperationalLayers().add(imageLayer);
      map.getOperationalLayers().add(featureLayer);

      // create a view and set map to it
      mapView = new MapView();
      mapView.setMap(map);

      // a point where the map view will zoom to, -11e6 same as -11 x 10^6
      mapView.setViewpoint(new Viewpoint(
          new Point(-11e6, 45e5, SpatialReferences.getWebMercator()),
          MIN_SCALE));

      // create a list to hold the view status of all three layers
      viewStatusList = new String[3];

      // fires every time a layers' view status has changed
      mapView.addLayerViewStateChangedListener(e -> {
        // holds the label that needs to be changed
        Label changedLabel;
        Layer layer = e.getLayer();

        String viewStatus = e.getLayerViewStatus().iterator().next().toString();
        final int layerIndex = map.getOperationalLayers().indexOf(layer);

        // finding and updating label that needs to be changed
        switch (layerIndex) {
          case TILED_LAYER:
            viewStatusList[TILED_LAYER] = "World Time Zones: " + viewStatus;
            changedLabel = worldTimeZonesLabel;
            break;
          case IMAGE_LAYER:
            viewStatusList[IMAGE_LAYER] = "Census: " + viewStatus;
            changedLabel = censusLabel;
            break;
          case FEATURE_LAYER:
            viewStatusList[FEATURE_LAYER] = "Facilities: " + viewStatus;
            changedLabel = facilitiesLabel;
            break;
          default:
            System.out.println("Indexing Error: " + layer.getName()
                + " not found in map's operational layers list.");
            changedLabel = new Label();
        }

        // updates view status to corresponding label
        Platform.runLater(() -> changedLabel.setText(
            viewStatusList[layerIndex]));
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
