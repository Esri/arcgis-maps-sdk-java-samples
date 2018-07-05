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

package com.esri.samples.mapview.display_layer_view_state_status;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.ArcGISMapImageLayer;
import com.esri.arcgisruntime.layers.ArcGISTiledLayer;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.layers.Layer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.MapView;

public class DisplayLayerViewStateStatusSample extends Application {

  private MapView mapView;

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
  public void start(Stage stage) {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);
      scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

      // set title, size, and add scene to stage
      stage.setTitle("Display Layer View State Status Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // create a control panel
      VBox controlsVBox = new VBox(6);
      controlsVBox.setBackground(new Background(new BackgroundFill(Paint.valueOf("rgba(0,0,0,0.3)"), CornerRadii.EMPTY,
          Insets.EMPTY)));
      controlsVBox.setPadding(new Insets(10.0));
      controlsVBox.setMaxSize(220, 90);
      controlsVBox.getStyleClass().add("panel-region");

      // create labels to display the view status of each layer
      Label worldTimeZonesLabel = new Label("World Time Zones: ");
      Label censusLabel = new Label("Census: ");
      Label facilitiesLabel = new Label("Facilities: ");

      worldTimeZonesLabel.getStyleClass().add("panel-label");
      censusLabel.getStyleClass().add("panel-label");
      facilitiesLabel.getStyleClass().add("panel-label");

      // add labels to the control panel
      controlsVBox.getChildren().addAll(worldTimeZonesLabel, censusLabel, facilitiesLabel);

      // create three layers to add to the ArcGISMap
      final ArcGISTiledLayer tiledLayer = new ArcGISTiledLayer(SERVICE_TIME_ZONES);

      // this layer will be OUT_OF_SCALE outside of its set min/max scales
      final ArcGISMapImageLayer imageLayer = new ArcGISMapImageLayer(SERVICE_CENSUS);
      imageLayer.setMinScale(MIN_SCALE);
      imageLayer.setMaxScale(MIN_SCALE / 10);

      // creating a layer from a service feature table
      final ServiceFeatureTable featureTable = new ServiceFeatureTable(SERVICE_RECREATION);
      final FeatureLayer featureLayer = new FeatureLayer(featureTable);

      // adding layers to the ArcGISMaps' layer list
      final ArcGISMap map = new ArcGISMap();
      map.getOperationalLayers().add(tiledLayer);
      map.getOperationalLayers().add(imageLayer);
      map.getOperationalLayers().add(featureLayer);

      // create a view and set ArcGISMap to it
      mapView = new MapView();
      mapView.setMap(map);

      // set viewpoint
      mapView.setViewpoint(new Viewpoint(new Point(-11e6, 45e5, SpatialReferences.getWebMercator()), MIN_SCALE));

      // fires every time a layers' view status has changed
      mapView.addLayerViewStateChangedListener(e -> {
        // holds the label that needs to be changed
        Layer layer = e.getLayer();

        String viewStatus = e.getLayerViewStatus().iterator().next().toString();
        final int layerIndex = map.getOperationalLayers().indexOf(layer);

        // finding and updating label that needs to be changed
        switch (layerIndex) {
          case TILED_LAYER:
            worldTimeZonesLabel.setText("World Time Zones: " + viewStatus);
            break;
          case IMAGE_LAYER:
            censusLabel.setText("Census: " + viewStatus);
            break;
          case FEATURE_LAYER:
            facilitiesLabel.setText("Facilities: " + viewStatus);
        }
      });

      // add the map view and control panel to stack pane
      stackPane.getChildren().addAll(mapView, controlsVBox);
      StackPane.setAlignment(controlsVBox, Pos.TOP_LEFT);
      StackPane.setMargin(controlsVBox, new Insets(10, 0, 0, 10));
    } catch (Exception e) {
      // on any error, display the stack trace
      e.printStackTrace();
    }
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
