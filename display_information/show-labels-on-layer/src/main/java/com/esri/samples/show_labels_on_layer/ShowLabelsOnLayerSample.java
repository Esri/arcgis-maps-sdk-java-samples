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
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.arcgisservices.LabelDefinition;
import com.esri.arcgisruntime.arcgisservices.LabelingPlacement;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.labeling.ArcadeLabelExpression;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.ColorUtil;
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

      // authentication with an API key or named user is required to access basemaps and other location services
      String yourAPIKey = System.getProperty("apiKey");
      ArcGISRuntimeEnvironment.setApiKey(yourAPIKey);

      // create a map with the light gray basemap style
      ArcGISMap map = new ArcGISMap(BasemapStyle.ARCGIS_LIGHT_GRAY);

      // create a map view and set the map to it
      mapView = new MapView();
      mapView.setMap(map);

      // set the initial viewpoint near the center of the US
      mapView.setViewpointCenterAsync(new Point(-10846309.950860, 4683272.219411, SpatialReferences.getWebMercator()), 20000000);

      // create a feature layer from an online feature service of US Congressional Districts
      String serviceUrl = "https://services.arcgis.com/P3ePLMYs2RVChkJx/arcgis/rest/services/USA_116th_Congressional_Districts/FeatureServer/0";
      var serviceFeatureTable = new ServiceFeatureTable(serviceUrl);
      var featureLayer = new FeatureLayer(serviceFeatureTable);

      // add the feature layer to the map
      map.getOperationalLayers().add(featureLayer);

      // show alert if layer fails to load
      featureLayer.addDoneLoadingListener(() -> {
        if (featureLayer.getLoadStatus() != LoadStatus.LOADED) {
          new Alert(Alert.AlertType.ERROR, "Error loading Feature Layer.").show();
        }
      });

      // create label definitions for each party
      LabelDefinition republicanLabelDefinition = makeLabelDefinition("Republican", Color.RED);
      LabelDefinition democratLabelDefinition = makeLabelDefinition("Democrat", Color.BLUE);

      // enable labels on the feature layer
      featureLayer.setLabelsEnabled(true);

      // add the definitions to the feature layer
      featureLayer.getLabelDefinitions().addAll(Arrays.asList(republicanLabelDefinition, democratLabelDefinition));

      // add the map view to stack pane
      stackPane.getChildren().add(mapView);

    } catch (Exception e) {
      // on any error, display stack trace
      e.printStackTrace();
    }
  }

  /**
   * Creates a label definition for a given party (field value) and color to populate a text symbol with.
   *
   * @param party the name of the party to be passed into the label definition's WHERE clause
   * @param color the color to be passed into the text symbol
   *
   * @return label definition created from the given arcade expression
   */
  private LabelDefinition makeLabelDefinition(String party, Color color) {

    // create text symbol for styling the label
    TextSymbol textSymbol = new TextSymbol();
    textSymbol.setSize(12);
    textSymbol.setColor(ColorUtil.colorToArgb(color));
    textSymbol.setHaloColor(0xFFFFFFFF);
    textSymbol.setHaloWidth(2);

    // create a label definition with an Arcade expression script
    var arcadeLabelExpression =
      new ArcadeLabelExpression("$feature.NAME + \" (\" + left($feature.PARTY,1) + \")\\nDistrict \" + $feature.CDFIPS");
    var labelDefinition = new LabelDefinition(arcadeLabelExpression, textSymbol);
    labelDefinition.setPlacement(LabelingPlacement.POLYGON_ALWAYS_HORIZONTAL);
    labelDefinition.setWhereClause(String.format("PARTY = '%s'", party));

    return labelDefinition;

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
