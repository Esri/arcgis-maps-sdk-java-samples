/*
 * Copyright 2015 Esri.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.esri.sampleviewer.samples.featurelayers;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.datasource.Feature;
import com.esri.arcgisruntime.datasource.FeatureQueryResult;
import com.esri.arcgisruntime.datasource.QueryParameters;
import com.esri.arcgisruntime.datasource.arcgis.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Map;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.RgbColor;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * This sample demonstrates how to query a FeatureLayer via a
 * ServiceFeatureTable.
 * <h4>How it Works</h4>
 * 
 * {@link QueryParameters} are created from the text that was entered by the
 * user. The {@link ServiceFeatureTable#queryFeaturesAsync} method takes those
 * parameters and searches through the {@link ServiceFeatureTable} for a match.
 * <h4>Implementation Requirements</h4>
 * 
 * ListenableFuture needs to be a class level field because it could get garbage
 * collected right after being set. Meaning that the addDoneListener method will
 * never be called.
 */
public class FeatureLayerQuery extends Application {

  private Alert dialog;

  private MapView mapView;
  private FeatureLayer featureLayer;
  private ServiceFeatureTable featureTable;
  private Point startPoint;
  private ListenableFuture<FeatureQueryResult> tableQueryResult;

  private static final String SERVICE_FEATURE_URL =
      "https://sampleserver6.arcgisonline.com/arcgis/rest/services/USA/MapServer/2";
  private static final int SCALE = 100000000;

  @Override
  public void start(Stage stage) throws Exception {

    // create stack pane and application scene
    StackPane stackPane = new StackPane();
    Scene scene = new Scene(stackPane);
    scene.getStylesheets().add(getClass()
        .getResource("../resources/SamplesTheme.css").toExternalForm());

    // set title, size, and add scene to stage
    stage.setTitle("Feature Layer Query Sample");
    stage.setWidth(800);
    stage.setHeight(700);
    stage.setScene(scene);
    stage.show();

    // create a control panel
    VBox vBoxControl = new VBox(6);
    vBoxControl.setMaxSize(240, 270);
    vBoxControl.getStyleClass().add("panel-region");

    // create sample description
    Label descriptionLabel = new Label("Sample Description:");
    descriptionLabel.getStyleClass().add("panel-label");
    TextArea description = new TextArea(
        "This sample shows how to query a Feature Layer via a Service Feature "
            + "Table.\nEnter the full name of a US state and hit submit. If found "
            + "the view will zoom to that state.");
    description.setWrapText(true);
    description.autosize();
    description.setEditable(false);

    // create area for searching
    Label searchLabel = new Label("Search for a State.");
    searchLabel.getStyleClass().add("panel-label");
    TextField searchField = new TextField();
    Button searchButton = new Button("Submit");
    HBox searchBox = new HBox(5);
    searchBox.getChildren().addAll(searchField, searchButton);
    searchBox.setDisable(true);

    // create dialog to display alert information
    dialog = new Alert(AlertType.WARNING);

    // search for the state that was entered
    searchButton.setOnAction(e -> {
      // clear the selection of the feature
      featureLayer.clearSelection();
      String stateText = searchField.getText();

      if (stateText.trim().length() > 0) {
        searchForState(stateText);
      } else {
        dialog.setContentText("State Not Found! Add a valid state name.");
        dialog.showAndWait();
        mapView.setViewpointCenterWithScaleAsync(startPoint, SCALE);
      }
    });

    // add labels, sample description, and search box to the control panel
    vBoxControl.getChildren().addAll(descriptionLabel, description, searchLabel,
        searchBox);
    try {

      // create a starting point for the view
      startPoint =
          new Point(-11000000, 5000000, SpatialReferences.getWebMercator());

      // set fill color for the US states
      SimpleLineSymbol lineSymbol = new SimpleLineSymbol(
          SimpleLineSymbol.Style.SOLID, new RgbColor(0, 0, 0, 255), 1, 0.6f);
      SimpleFillSymbol fillSymbol =
          new SimpleFillSymbol(new RgbColor(255, 204, 0, 255),
              SimpleFillSymbol.Style.SOLID, lineSymbol, 0.5f);

      // create a service feature table
      featureTable = new ServiceFeatureTable(SERVICE_FEATURE_URL);

      // create a feature layer from table
      featureLayer = new FeatureLayer(featureTable);
      featureLayer.setOpacity(0.8f);

      // enable search once the feature layer is loaded
      featureLayer.addDoneLoadingListener(() -> searchBox.setDisable(false));

      // set renderer for feature layer
      featureLayer.setRenderer(new SimpleRenderer(fillSymbol));

      // create a map with basemap topographic
      final Map map = new Map(Basemap.createTopographic());

      // add feature layer to operational layers
      map.getOperationalLayers().add(featureLayer);

      // create a view for this map
      mapView = new MapView();
      mapView.setMap(map);

      // set viewpoint to the start point
      mapView.setViewpointCenterWithScaleAsync(startPoint, SCALE);

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
   * Searches for a US state inside the current ServiceFeatureTable.
   * 
   * @param state a US state that is being searched
   */
  private void searchForState(String state) {

    // create a query for the state that was entered
    QueryParameters query = new QueryParameters();
    query
        .setWhereClause("upper(STATE_NAME) LIKE '" + state.toUpperCase() + "'");
    query.getOutFields().add("*");

    // search for the state feature in the feature table
    tableQueryResult = featureTable.queryFeaturesAsync(query);

    tableQueryResult.addDoneListener(() -> {
      try {
        // get the result from the query
        FeatureQueryResult result = tableQueryResult.get();
        // if a state feature was found
        if (result.iterator().hasNext()) {
          // get state feature and zoom to it
          Feature feature = result.iterator().next();
          Envelope envelope = feature.getGeometry().getExtent();
          mapView.setViewpointGeometryWithPaddingAsync(envelope, 200);

          // set the state feature to be selected
          featureLayer.selectFeature(feature);
        } else {
          Platform.runLater(() -> {
            dialog.setContentText("State Not Found! Add a valid state name.");
            dialog.showAndWait();
            mapView.setViewpointCenterWithScaleAsync(startPoint, SCALE);
          });
        }
      } catch (Exception e) {
        // on any error, display the stack trace
        e.printStackTrace();
      }
    });
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
