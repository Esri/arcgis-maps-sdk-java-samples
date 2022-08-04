/*
 * Copyright 2022 Esri.
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

package com.esri.samples.create_mobile_geodatabase;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.data.Field;
import com.esri.arcgisruntime.data.FieldDescription;
import com.esri.arcgisruntime.data.Geodatabase;
import com.esri.arcgisruntime.data.GeodatabaseFeatureTable;
import com.esri.arcgisruntime.data.TableDescription;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.GeometryType;
import com.esri.arcgisruntime.geometry.Multipoint;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.data.QueryParameters;

public class CreateMobileGeodatabaseSample extends Application{

  private MapView mapView;
  private Geodatabase geodatabase;
  private GeodatabaseFeatureTable geodatabaseFeatureTable;
  private Path geodatabasePath;
  private Label label;
  private Button viewTableButton, createGeodatabaseButton, closeGeodatabaseButton;
  private VBox vBoxControls;
  private Stage tableStage;
  private boolean isTableWindowOpen;

  @Override
  public void start(Stage stage) {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);

      // set title, size, and add scene to stage
      stage.setTitle("Create Mobile Geodatabase");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // authentication with an API key or named user is required to access base maps and other location services
      String yourAPIKey = System.getProperty("apiKey");
      ArcGISRuntimeEnvironment.setApiKey(yourAPIKey);

      // create a map with the topographic imagery basemap style
      ArcGISMap map = new ArcGISMap(BasemapStyle.ARCGIS_TOPOGRAPHIC);

      // create a map view and set the map to it
      mapView = new MapView();
      mapView.setMap(map);

      // create a point located at Redlands, CA to be used as the viewpoint for the map
      var point = new Point(-117.195800, 34.056295, SpatialReferences.getWgs84());
      mapView.setViewpointCenterAsync(point, 10000);

      // create vbox, label, and button UI components
      setUpControlsVBox();

      // create a graphics overlay to display the input points
      var graphicsOverlay = new GraphicsOverlay();
      mapView.getGraphicsOverlays().add(graphicsOverlay);

      // create a graphic to add the simple marker symbol in the graphics overlay
      var simpleMarkerSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, 0xFF000000, 10);
      var graphic = new Graphic();
      graphic.setSymbol(simpleMarkerSymbol);
      graphicsOverlay.getGraphics().add(graphic);

      // keep track of the points added by the user
      List<Point> inputs = new ArrayList<>();

      // create a point from where the user clicked and add it to the geodatabase feature table
      mapView.setOnMouseClicked(e -> {
        if(createGeodatabaseButton.isDisabled() && !isTableWindowOpen) {
          if (e.isStillSincePress() && e.getButton() == MouseButton.PRIMARY) {
            // create 2D point from pointer location
            var point2D = new Point2D(e.getX(), e.getY());

            // create a map point from 2D point
            Point mapPoint = mapView.screenToLocation(point2D);

            // the map point should be normalized to the central meridian when wrapping around a map, so its value stays within the coordinate system of the map view
            Point normalizedMapPoint = (Point) GeometryEngine.normalizeCentralMeridian(mapPoint);

            // add a point where the user clicks on the map and update the inputs graphic geometry
            inputs.add(normalizedMapPoint);
            Multipoint inputsGeometry = new Multipoint(new PointCollection(inputs));
            graphic.setGeometry(inputsGeometry);

            if (inputs.size() > 0) {
              // set up the feature attributes
              Map<String, Object> featureAttributes = new HashMap<>(Map.of("collection_timestamp", Calendar.getInstance().getTime().toString()));
              // create a new feature at the map point
              var feature = geodatabaseFeatureTable.createFeature(featureAttributes, normalizedMapPoint);
              // add the feature to the feature table
              var addFeatureFuture = geodatabaseFeatureTable.addFeatureAsync(feature);
              addFeatureFuture.addDoneListener(() -> {
                try {
                  addFeatureFuture.get();
                  // update the total feature count on screen if feature was added successfully
                  label.setText("Number of features added: " + geodatabaseFeatureTable.getTotalFeatureCount());

                } catch (InterruptedException | ExecutionException ex) {
                  throw new RuntimeException(ex);
                }
              });
            }
          }
        }
        else {
          // close table if user clicks on the map without adding a new feature
          handleTableWindowVisibility();
        }
      });

      // create geodatabase and feature layer from geodatabase feature table descriptions
      createGeodatabaseButton.setOnAction(e -> {

        // get the path for the geodatabase file
        geodatabasePath = Paths.get(System.getProperty("user.dir") + "/LocationHistory.geodatabase");
        try {
          Files.deleteIfExists(geodatabasePath);
        } catch (IOException ioException) {
          ioException.printStackTrace();
        }

        // create geodatabase from the specified mobile geodatabase file path
        var geodatabaseFuture = Geodatabase.createAsync(geodatabasePath.toString());
        geodatabaseFuture.addDoneListener(() -> {
          try {
            // get the instance of the mobile geodatabase
            geodatabase = geodatabaseFuture.get();

            // create a table description to store features as map points and set non-required properties to false
            var tableDescription = new TableDescription("LocationHistory", SpatialReferences.getWgs84(),
              GeometryType.POINT);

            // Set up the fields for the table. FieldType.OID is the primary key of the SQLite table.
            var fieldDescriptionOID = new FieldDescription("oid", Field.Type.OID);
            var fieldDescriptionText = new FieldDescription("collection_timestamp", Field.Type.TEXT);
            tableDescription.getFieldDescriptions().addAll(List.of(fieldDescriptionOID, fieldDescriptionText));

            // add a new table to the geodatabase feature table by creating one from the table description
            var geodatabaseFeatureTableFuture = geodatabase.createTableAsync(tableDescription);

            // set up the map view to display the feature layer using the loaded [tableFuture] geodatabase feature table
            geodatabaseFeatureTableFuture.addDoneListener(() -> {
              try {
                // get the result of the loaded "LocationHistory" table
                geodatabaseFeatureTable = geodatabaseFeatureTableFuture.get();
                // create a feature layer for the map using the GeodatabaseFeatureTable
                var featureLayer = new FeatureLayer(geodatabaseFeatureTable);
                mapView.getMap().getOperationalLayers().add(featureLayer);
                createGeodatabaseButton.setDisable(true);
                closeGeodatabaseButton.setDisable(false);
                viewTableButton.setDisable(false);
                mapView.setDisable(false);
                label.setText("Click map to add features");

              } catch (Exception exception) {
                new Alert(Alert.AlertType.ERROR, "Failed to get feature table result").show();
                exception.printStackTrace();
              }
            });

          } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Failed to get geodatabase result").show();
            ex.printStackTrace();          }
        });
      });

      closeGeodatabaseButton.setOnAction(event -> {
        // close geodatabase and show information alert
        geodatabase.close();
        Alert dialog = new Alert(Alert.AlertType.INFORMATION, "Mobile geodatabase has been closed and stored " +
          "in the following directory: \n\n" + geodatabasePath);
        dialog.initOwner(mapView.getScene().getWindow());
        dialog.setHeaderText(null);
        dialog.setTitle(("Information"));
        dialog.showAndWait();
        // handle UI
        viewTableButton.setDisable(true);
        closeGeodatabaseButton.setDisable(true);
        createGeodatabaseButton.setDisable(true);
        label.setText("Restart sample to begin again");
        graphicsOverlay.getGraphics().clear();
        map.getOperationalLayers().clear();
        mapView.setDisable(true);
      });

      viewTableButton.setOnAction(e -> {
        handleTableWindowVisibility();
        displayTable(stage);
      });

      // add the map view and UI elements to the stack pane
      stackPane.getChildren().addAll(mapView, vBoxControls);
      StackPane.setAlignment(vBoxControls, Pos.TOP_LEFT);
      StackPane.setMargin(vBoxControls, new Insets(10, 0, 0, 10));

    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
    }
  }

  /**
   * Display a new window with the table of features stored in the geodatabase feature table
   */
  private void displayTable(Stage stage) {

    // create observable list of type GeoFeature to store the geodatabase features
    final ObservableList<FeatureAttributeField> fieldData = FXCollections.observableArrayList();

    // query all the features loaded to the table
    var queryResultFuture = geodatabaseFeatureTable.queryFeaturesAsync(new QueryParameters());
    queryResultFuture.addDoneListener(() -> {
      try {
        var queryResults = queryResultFuture.get();
        queryResults.forEach(feature ->
          // add features to the observable list
          fieldData.add(new FeatureAttributeField(feature.getAttributes().get("oid").toString(), feature.getAttributes().get("collection_timestamp").toString())));

        // create and set up a new table view to display the features in a table
        TableView<FeatureAttributeField> table = new TableView<>();
        table.setEditable(false);
        table.setVisible(true);

        // create two table columns and add them to the table view
        TableColumn<FeatureAttributeField, String> oidCol = new TableColumn<>("OID");
        TableColumn<FeatureAttributeField, String> timeCol = new TableColumn<>("COLLECTION TIMESTAMP");
        table.getColumns().add(oidCol);
        table.getColumns().add(timeCol);

        // associate data to the table columns referencing the fields in the GeoFeature class
        oidCol.setCellValueFactory(new PropertyValueFactory<>("oid"));
        timeCol.setCellValueFactory(new PropertyValueFactory<>("timestamp"));

        // add data to the table view
        table.setItems(fieldData);

        // create a StackPane, Scene, and Stage for displaying the table view in a new window
        var pane = new StackPane();
        pane.getChildren().add(table);
        var scene = new Scene(pane, 220, 230);

        // set up stage properties before display
        tableStage = new Stage();
        tableStage.setTitle("Features");
        tableStage.setScene(scene);
        tableStage.setX(stage.getX() + 200);
        tableStage.setY(stage.getY() + 100);
        tableStage.show();
        isTableWindowOpen = true; // to control window behaviour

      } catch (InterruptedException | ExecutionException e) {
        throw new RuntimeException(e);
      }
    });
  }

  /**
   * Handles visibility of the table window.
   */
  private void handleTableWindowVisibility() {

    if (tableStage != null) {
      if (!tableStage.isShowing()) {
        isTableWindowOpen = false;
      }
    }

    if (isTableWindowOpen && tableStage != null) {
      // close table window if open
      tableStage.close();
      isTableWindowOpen = false;
    }
  }

  /**
   * Creates UI with two buttons and a label component.
   */
  private void setUpControlsVBox() {

    // create label to show number of added features
    label = new Label("Create mobile geodatabase");
    label.setStyle("-fx-text-fill: white");

    // button to create the geodatabase
    createGeodatabaseButton = new Button("Create Geodatabase");
    createGeodatabaseButton.setAlignment(Pos.CENTER);
    //createGeodatabaseButton.setDisable(false);

    // button to save and close the geodatabase
    closeGeodatabaseButton = new Button("Close Geodatabase");
    closeGeodatabaseButton.setAlignment(Pos.CENTER);
    closeGeodatabaseButton.setDisable(true);

    // button to show geodatabase feature table contents
    viewTableButton = new Button("View Table");
    viewTableButton.setAlignment(Pos.CENTER);
    viewTableButton.setDisable(true);

    // create and configure a VBox
    vBoxControls = new VBox(10);
    vBoxControls.setBackground(new Background(new BackgroundFill(Paint.valueOf("rgba(0,0,0,0.7)"),
      CornerRadii.EMPTY, Insets.EMPTY)));
    vBoxControls.setPadding(new Insets(10.0));
    vBoxControls.setMaxSize(185, 100);
    vBoxControls.setDisable(false);

    // add the label and button to the VBos
    vBoxControls.getChildren().addAll(label, createGeodatabaseButton, closeGeodatabaseButton, viewTableButton);
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
