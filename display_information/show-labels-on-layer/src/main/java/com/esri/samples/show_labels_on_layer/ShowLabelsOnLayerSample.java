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

package com.esri.samples.show_labels_on_layer;

import java.util.Arrays;

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
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
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

      // set the initial viewpoint near the center of the US
      mapView.setViewpointCenterAsync(new Point(-10846309.950860, 4683272.219411, SpatialReferences.getWebMercator()), 20000000);

      // create a feature layer from an online feature service of US Congressional Districts
      String serviceUrl = "https://services.arcgis.com/P3ePLMYs2RVChkJx/arcgis/rest/services/USA_116th_Congressional_Districts/FeatureServer/0";
      ServiceFeatureTable serviceFeatureTable = new ServiceFeatureTable(serviceUrl);
      FeatureLayer featureLayer = new FeatureLayer(serviceFeatureTable);

      // add the feature layer to the map
      map.getOperationalLayers().add(featureLayer);

      // show alert if layer fails to load
      featureLayer.addDoneLoadingListener(() -> {
        if (featureLayer.getLoadStatus() != LoadStatus.LOADED) {
          new Alert(Alert.AlertType.ERROR, "Error loading Feature Layer.").show();
        }
      });

      // use red text with white halo for republican district labels
      TextSymbol republicanTextSymbol = new TextSymbol();
      republicanTextSymbol.setSize(10);
      republicanTextSymbol.setColor(0xFFFF0000);
      republicanTextSymbol.setHaloColor(0xFFFFFFFF);
      republicanTextSymbol.setHaloWidth(2);

      // use blue text with white halo for democrat district labels
      TextSymbol democratTextSymbol = new TextSymbol();
      democratTextSymbol.setSize(10);
      democratTextSymbol.setColor(0xFF0000FF);
      democratTextSymbol.setHaloColor(0xFFFFFFFF);
      democratTextSymbol.setHaloWidth(2);

      // construct a json label definition
      JsonObject json = new JsonObject();
      // use a custom label expression combining some of the feature's fields
      JsonObject expressionInfo = new JsonObject();
      expressionInfo.add("expression", new JsonPrimitive("$feature.NAME + \" (\" + left($feature.PARTY,1) + \")\\nDistrict \" + $feature.CDFIPS"));
      json.add("labelExpressionInfo", expressionInfo);
      // position the label in the center of the feature
      json.add("labelPlacement", new JsonPrimitive("esriServerPolygonPlacementAlwaysHorizontal"));

      // create a copy of the json with a custom where clause and symbol only for republican districts
      JsonObject republicanJson = json.deepCopy();
      republicanJson.add("where", new JsonPrimitive("PARTY = 'Republican'"));
      republicanJson.add("symbol", new JsonParser().parse(republicanTextSymbol.toJson()));

      // create a copy of the json with a custom where clause and symbol only for democrat districts
      JsonObject democratJson = json.deepCopy();
      democratJson.add("where", new JsonPrimitive("PARTY = 'Democrat'"));
      democratJson.add("symbol", new JsonParser().parse(democratTextSymbol.toJson()));

      // create label definitions from the JSON strings
      LabelDefinition republicanLabelDefinition = LabelDefinition.fromJson(republicanJson.toString());
      LabelDefinition democratLabelDefinition = LabelDefinition.fromJson(democratJson.toString());

      // add the definitions to the feature layer
      featureLayer.getLabelDefinitions().addAll(Arrays.asList(republicanLabelDefinition, democratLabelDefinition));
      featureLayer.getLabelDefinitions().add(democratLabelDefinition);

      // enable labels
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
