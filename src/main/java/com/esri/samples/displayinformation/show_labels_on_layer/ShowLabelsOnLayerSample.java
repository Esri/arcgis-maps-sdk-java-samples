/*
 * Copyright 2018 Esri.
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
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import com.esri.arcgisruntime.arcgisservices.LabelDefinition;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.TextSymbol;

public class ShowLabelsOnLayerSample extends Application {

  private MapView mapView;

  @Override
  public void start(Stage stage) {

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

      // create a map view and set a map
      mapView = new MapView();
      ArcGISMap map = new ArcGISMap(Basemap.createLightGrayCanvas());
      mapView.setMap(map);

      // create a feature layer from an online feature service of US Highways
      String serviceUrl = "http://sampleserver6.arcgisonline.com/arcgis/rest/services/USA/MapServer/1";
      ServiceFeatureTable serviceFeatureTable = new ServiceFeatureTable(serviceUrl);
      FeatureLayer featureLayer = new FeatureLayer(serviceFeatureTable);
      map.getOperationalLayers().add(featureLayer);

      // zoom to the layer when it's done loading
      featureLayer.addDoneLoadingListener(() -> {
        if (featureLayer.getLoadStatus() == LoadStatus.LOADED) {
          mapView.setViewpointGeometryAsync(featureLayer.getFullExtent());
        } else {
          new Alert(Alert.AlertType.ERROR, featureLayer.getLoadError().getMessage()).show();
        }
      });

      // use large blue text with a yellow halo for the labels
      TextSymbol textSymbol = new TextSymbol();
      textSymbol.setSize(20);
      textSymbol.setColor(0xFF0000FF);
      textSymbol.setHaloColor(0xFFFFFF00);
      textSymbol.setHaloWidth(2);

      // construct the label definition json
      JsonObject json = new JsonObject();
      // prepend 'I - ' (for Interstate) to the route number for the label
      JsonObject expressionInfo = new JsonObject();
      expressionInfo.add("expression", new JsonPrimitive("'I -' + $feature.rte_num1"));
      json.add("labelExpressionInfo", expressionInfo);
      // position the label above and along the direction of the road
      json.add("labelPlacement", new JsonPrimitive("esriServerLinePlacementAboveAlong"));
      // only show labels on the interstate highways (others have an empty rte_num1 attribute)
      json.add("where", new JsonPrimitive("rte_num1 <> ' '"));
      // set the text symbol as the label symbol
      json.add("symbol", new JsonParser().parse(textSymbol.toJson()));

      // create a label definition from the JSON string
      LabelDefinition labelDefinition = LabelDefinition.fromJson(json.toString());
      // add the definition to the feature layer and enable labels on it
      featureLayer.getLabelDefinitions().add(labelDefinition);
      featureLayer.setLabelsEnabled(true);

      // add the map view to stack pane
      stackPane.getChildren().add(mapView);

    } catch (Exception e) {
      // on any error, display stack trace
      e.printStackTrace();
    }
  }

  /**
   * Stops and releases all resources used in application.
   */
  @Override
  public void stop() {

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
