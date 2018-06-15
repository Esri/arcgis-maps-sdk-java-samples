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

package com.esri.samples.featurelayers.statistical_query;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.data.StatisticDefinition;
import com.esri.arcgisruntime.data.StatisticRecord;
import com.esri.arcgisruntime.data.StatisticType;
import com.esri.arcgisruntime.data.StatisticsQueryParameters;
import com.esri.arcgisruntime.data.StatisticsQueryResult;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.MapView;

public class StatisticalQuerySample extends Application {

  private MapView mapView;

  @Override
  public void start(Stage stage) {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);
      scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

      // set title, size, and add scene to stage
      stage.setTitle("Statistical Query Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // create and set a map with the streets vector basemap
      final ArcGISMap map = new ArcGISMap(Basemap.createStreetsVector());
      mapView = new MapView();
      mapView.setMap(map);

      // create a service feature table using the URL
      String featureServiceURL = "https://sampleserver6.arcgisonline" +
        ".com/arcgis/rest/services/SampleWorldCities/MapServer/0";
      final ServiceFeatureTable featureTable = new ServiceFeatureTable(featureServiceURL);

      // create a feature layer from the service feature table and add it to the map
      FeatureLayer featureLayer = new FeatureLayer(featureTable);
      map.getOperationalLayers().add(featureLayer);

      // create toggle buttons for the query filters
      CheckBox currentExtentFilterToggle = new CheckBox("Only cities in current extent");
      CheckBox populationFilterToggle = new CheckBox("Only cities greater than 5M");

      // create static definitions for all the statistics we want to query on
      // here we specify all statistic types for the "POP" (population) field
      List<StatisticDefinition> statisticDefinitions = Stream.of(StatisticType.values())
        .map(type -> new StatisticDefinition("POP", type, null))
        .collect(Collectors.toList());

      // create a button to perform the statistical query
      Button queryButton = new Button("Get Statistics");
      queryButton.setOnAction(e -> {
        // create statistics query parameters with the definitions
        StatisticsQueryParameters statisticsQueryParameters = new StatisticsQueryParameters(statisticDefinitions);

        if (currentExtentFilterToggle.isSelected()) {
          // set the query geometry to the current visible area
          statisticsQueryParameters.setGeometry(mapView.getVisibleArea());
          // set the spatial relationship to intersects (the default)
          statisticsQueryParameters.setSpatialRelationship(QueryParameters.SpatialRelationship.INTERSECTS);
        }

        if (populationFilterToggle.isSelected()) {
          // set an attribute filter to only retrieve large cities (rank = 1)
          statisticsQueryParameters.setWhereClause("POP_RANK = 1");
        }

        // execute the query
        ListenableFuture<StatisticsQueryResult> query = featureTable.queryStatisticsAsync(statisticsQueryParameters);
        query.addDoneListener(() -> {
          try {
            StatisticsQueryResult result = query.get();
            StringBuilder statistics = new StringBuilder();
            for (Iterator<StatisticRecord> it = result.iterator(); it.hasNext();) {
              StatisticRecord record = it.next();
              record.getStatistics().forEach((key, value) -> statistics.append("\n").append(key).append(": ").append(value));
            }
            Alert alert = new Alert(Alert.AlertType.INFORMATION, statistics.toString());
            alert.setHeaderText("Statistics");
            alert.show();
          } catch (ExecutionException | InterruptedException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage());
            alert.show();
          }
        });
      });

      // layout the controls
      VBox controlsBox = new VBox(6);
      controlsBox.setAlignment(Pos.CENTER);
      controlsBox.setMaxSize(220, Double.MIN_VALUE);
      controlsBox.getStyleClass().add("panel-region");
      currentExtentFilterToggle.setMaxWidth(Double.MAX_VALUE);
      populationFilterToggle.setMaxWidth(Double.MAX_VALUE);
      queryButton.setMaxWidth(Double.MAX_VALUE);
      controlsBox.getChildren().addAll(currentExtentFilterToggle, populationFilterToggle, queryButton);

      // add the map view and control panel to stack pane
      stackPane.getChildren().addAll(mapView, controlsBox);
      StackPane.setAlignment(controlsBox, Pos.TOP_LEFT);
      StackPane.setMargin(controlsBox, new Insets(10, 0, 0, 10));

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
