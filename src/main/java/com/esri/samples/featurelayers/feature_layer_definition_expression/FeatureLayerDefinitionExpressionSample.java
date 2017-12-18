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

package com.esri.samples.featurelayers.feature_layer_definition_expression;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.MapView;

public class FeatureLayerDefinitionExpressionSample extends Application {

  private MapView mapView;
  private FeatureLayer featureLayer;

  private final static String FEATURE_SERVICE_URL =
      "http://sampleserver6.arcgisonline.com/arcgis/rest/services/SF311/FeatureServer/0";

  @Override
  public void start(Stage stage) throws Exception {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);
      scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

      // set title, size, and add scene to stage
      stage.setTitle("Feature Layer Definition Expression Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // create renderer toggle switch
      ToggleButton definitionSwitch = new ToggleButton();
      definitionSwitch.setText("expression");

      // set the definition expression
      definitionSwitch.selectedProperty().addListener((observable, oldValue, newValue) -> {
        if (definitionSwitch.isSelected()) {
          featureLayer.setDefinitionExpression("req_Type = 'Tree Maintenance or Damage'");
        } else {
          // reset the definition expression
          featureLayer.setDefinitionExpression("");
        }
      });

      // create service feature table
      final ServiceFeatureTable featureTable = new ServiceFeatureTable(FEATURE_SERVICE_URL);

      // create feature layer from service feature table
      featureLayer = new FeatureLayer(featureTable);

      // create a ArcGISMap using the basemap topographic
      final ArcGISMap map = new ArcGISMap(Basemap.createTopographic());

      // add the feature layer to the ArcGISMap
      map.getOperationalLayers().add(featureLayer);

      // create a view for this ArcGISMap and set map to it
      mapView = new MapView();
      mapView.setMap(map);

      // starting location for sample
      Point startPoint = new Point(-13630845, 4544861, SpatialReferences.getWebMercator());

      // set the viewpoint for the map view
      mapView.setViewpointCenterAsync(startPoint, 150000);

      // add the map view and control panel to stack pane
      stackPane.getChildren().addAll(mapView, definitionSwitch);
      StackPane.setAlignment(definitionSwitch, Pos.TOP_LEFT);
      StackPane.setMargin(definitionSwitch, new Insets(10, 0, 0, 10));

    } catch (Exception e) {
      // on any error, display the stack trace
      e.printStackTrace();
    }
  }

  /**
   * Stops and releases all resources used in application.
   */
  @Override
  public void stop() throws Exception {

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
