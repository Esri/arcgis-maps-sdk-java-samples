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

package com.esri.samples.displayinformation.show_labels_on_layer;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import com.esri.arcgisruntime.arcgisservices.LabelDefinition;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.TextSymbol;

public class ShowLabelsOnLayerSample extends Application {

  private MapView mapView;

  @Override
  public void start(Stage stage) throws Exception {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);

      // size the stage, add a title, and set scene to stage
      stage.setTitle("Show Labels on Layer Sample");
      stage.setHeight(700);
      stage.setWidth(800);
      stage.setScene(scene);
      stage.show();

      // create a view for this ArcGISMap
      mapView = new MapView();
      ArcGISMap map = new ArcGISMap(Basemap.createOceans());
      mapView.setMap(map);

      String serviceUrl = "http://sampleserver6.arcgisonline.com/arcgis/rest/services/USA/MapServer/1";
      ServiceFeatureTable serviceFeatureTable = new ServiceFeatureTable(serviceUrl);
      FeatureLayer featureLayer = new FeatureLayer(serviceFeatureTable);
      featureLayer.setRenderingMode(FeatureLayer.RenderingMode.AUTOMATIC);

      TextSymbol textSymbol = new TextSymbol();
      textSymbol.setSize(20);
      textSymbol.setColor(0xFF0000FF);
      JsonObject json = new JsonObject();
      JsonObject expressionInfo = new JsonObject();
      expressionInfo.add("expression", new JsonPrimitive("$feature.rte_num1"));
      expressionInfo.add("labelPlacement", new JsonPrimitive("esriServerLinePlacementBelowStart"));
      expressionInfo.add("symbol", new JsonParser().parse(textSymbol.toJson()));
      json.add("labelExpressionInfo", expressionInfo);
      LabelDefinition labelDefinition = LabelDefinition.fromJson(json.toString());
      featureLayer.getLabelDefinitions().add(labelDefinition);
      featureLayer.setLabelsEnabled(true);

      featureLayer.loadAsync();

      map.getOperationalLayers().add(featureLayer);

      // add the map view to stack pane
      stackPane.getChildren().add(mapView);

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
