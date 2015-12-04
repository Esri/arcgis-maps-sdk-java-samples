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

package com.esri.sampleviewer.samples.editing;

import java.util.List;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.datasource.FeatureQueryResult;
import com.esri.arcgisruntime.datasource.QueryParameters;
import com.esri.arcgisruntime.datasource.QueryParameters.SpatialRelationship;
import com.esri.arcgisruntime.datasource.arcgis.FeatureEditResult;
import com.esri.arcgisruntime.datasource.arcgis.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.Polygon;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.layers.FeatureLayer.SelectionMode;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Map;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.Viewpoint;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * This sample demonstrates how to update a <@Feature> from a
 * <@ServiceFeatureTable> using a combo box. How it works: a ServiceFeatureTable
 * is created from a URL holding the <@FeatureLayer>, this is then added a
 * <@Map> and displayed on a <@MapView>. When the user selects a Feature they
 * will be able to change it's typdamage attribute by making a selection in the
 * ComboBox. From here the ServiceFeatureTable is updated, which will then
 * update the selected Feature icon. Lastly, this update will be saved and be
 * able to persist beyond this session.
 */
public class UpdateAttributes extends Application {

  private MapView mapView;

  private FeatureQueryResult selectedFeatures;
  private FeatureLayer featureLayer;
  private ServiceFeatureTable featureTable;
  private ComboBox<String> comboBox;

  private static final String FEATURE_LAYER_URL =
      "http://sampleserver6.arcgisonline.com/arcgis/rest/services/DamageAssessment/FeatureServer/0";
  private static final String SAMPLES_THEME_PATH =
      "../resources/SamplesTheme.css";

  @Override
  public void start(Stage stage) throws Exception {

    // create stack pane and application scene
    StackPane stackPane = new StackPane();
    Scene scene = new Scene(stackPane);
    scene.getStylesheets().add(getClass().getResource(SAMPLES_THEME_PATH)
        .toExternalForm());

    // size the stage, add a title, and set scene to stage
    stage.setTitle("Update Attributes Sample");
    stage.setHeight(700);
    stage.setWidth(800);
    stage.setScene(scene);
    stage.show();

    // create a control panel
    VBox vBoxControl = new VBox(6);
    vBoxControl.setMaxSize(240, 120);
    vBoxControl.getStyleClass().add("panel-region");

    // create sample label and description
    Label descriptionLabel = new Label("Sample Description");
    descriptionLabel.getStyleClass().add("panel-label");

    TextArea description = new TextArea(
        "This sample shows how to update\n"
            + "feature attributes. Click on a feature\n"
            + "to select it, then use the combo box\n"
            + "to update the typdamage attribute.");
    description.setEditable(false);
    description.setMinSize(210, 80);

    // create list of damage types
    ObservableList<String> damageList = FXCollections.observableArrayList();
    damageList.add("Destroyed");
    damageList.add("Inaccessible");
    damageList.add("Major");
    damageList.add("Minor");
    damageList.add("Affected");

    Label typeDamageLabel = new Label("Select type damage:");
    typeDamageLabel.getStyleClass().add("panel-label");

    // create combo box
    comboBox = new ComboBox<>(damageList);

    // set size, tooltip and disable values for the combo box
    comboBox.setPrefSize(200, 10);
    comboBox.setTooltip(new Tooltip("Type of Damage"));
    comboBox.setDisable(true);

    // handle type damage selection
    comboBox.showingProperty().addListener((obs, wasShowing, isShowing) -> {
      if (!isShowing) {
        // update the selected feature
        updateAttributes();
      }
    });

    // add sample label and description and comboBox to the control panel
    vBoxControl.getChildren().addAll(descriptionLabel, description,
        typeDamageLabel, comboBox);

    try {
      // create spatial reference for point
      SpatialReference spatialReference = SpatialReferences.getWebMercator();

      // create a initial viewpoint with a point and scale
      Point pointLondon = new Point(-036773, 6710477, spatialReference);
      Viewpoint viewpoint = new Viewpoint(pointLondon, 200000);

      // create a map with streets basemap
      Map map = new Map(Basemap.createStreets());

      // set viewpoint to map
      map.setInitialViewpoint(viewpoint);

      // create view for this map
      mapView = new MapView();

      // create service feature table from URL
      featureTable = new ServiceFeatureTable(FEATURE_LAYER_URL);
      featureTable.getOutFields().add("*"); // gets all fields from table

      // create a feature layer from table
      featureLayer = new FeatureLayer(featureTable);

      // enable combobox once the layer is loaded
      featureLayer.addDoneLoadingListener(() -> comboBox.setDisable(false));

      // add the layer to the map
      map.getOperationalLayers().add(featureLayer);

      mapView.setOnMouseClicked(e -> {
        // accept only primary mouse click
        if (e.getButton() == MouseButton.PRIMARY) {
          // create point from where user clicked
          Point2D point = new Point2D(e.getX(), e.getY());

          // create map point from point
          Point mapPoint = mapView.screenToLocation(point);

          // select feature that user clicked
          selectFeatures(mapPoint);
        }
      });
      // set map to be displayed in view
      mapView.setMap(map);

      // add the map view and control box to stack pane
      stackPane.getChildren().addAll(mapView, vBoxControl);
      StackPane.setAlignment(vBoxControl, Pos.TOP_LEFT);
      StackPane.setMargin(vBoxControl, new Insets(10, 0, 0, 10));

    } catch (Exception e) {
      // on any error, display the stack trace
      e.printStackTrace();
    }
  }

  /**
   * Selects features around the map point
   * 
   * @param mapPoint x,y-coordinate pair
   */
  private void selectFeatures(Point mapPoint) {

    // create a buffer for the mapPoint
    double distance = mapView.getUnitsPerPixel() * 10;
    Polygon pointBuffer = GeometryEngine.buffer(mapPoint, distance);

    // create a query from pointBuffer
    QueryParameters queryParams = new QueryParameters();
    queryParams.setGeometry(pointBuffer);
    queryParams.setSpatialRelationship(SpatialRelationship.WITHIN);
    queryParams.getOutFields().add("*");

    // select the features based on the query
    ListenableFuture<FeatureQueryResult> queryFeatures = featureLayer
        .selectFeatures(queryParams,
            SelectionMode.NEW);

    try {
      // get selected features from the result
      selectedFeatures = queryFeatures.get();

    } catch (Exception e) {
      // on any error, display the stack trace
      e.printStackTrace();
    }
  }

  /**
   * Applies changes to the feature, Service Feature Table and on the server;
   * 
   * @param featureTable holds all Feature data
   */
  private void updateAttributes() {

    if (selectedFeatures != null) {

      // apply damage type to the selected features
      selectedFeatures.forEach(feature -> {
        feature.getAttributes().put("typdamage", comboBox.getValue());

        // update feature in the feature table
        final ListenableFuture<Boolean> mapViewResult = featureTable
            .updateFeatureAsync(feature);

        try {
          // if successful, update change to the server
          if (mapViewResult.get().booleanValue()) {
            // apply change to the server
            final ListenableFuture<List<FeatureEditResult>> serverResult =
                featureTable.applyEditsAsync();
            // check if server result successful
            if (!serverResult.get().get(0).hasCompletedWithErrors()) {
              System.out.println("Feature successfully updated");
            }
          }
        } catch (Exception e) {
          // on any error, display the stack trace
          e.printStackTrace();
        }
      });
      // Clear the selected features
      featureLayer.clearSelection();
      selectedFeatures = null;
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
