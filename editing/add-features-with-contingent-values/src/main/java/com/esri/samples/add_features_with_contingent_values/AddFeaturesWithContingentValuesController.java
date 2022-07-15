
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


package com.esri.samples.add_features_with_contingent_values;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.ArcGISFeature;
import com.esri.arcgisruntime.data.CodedValue;
import com.esri.arcgisruntime.data.CodedValueDomain;
import com.esri.arcgisruntime.data.ContingencyConstraintViolation;
import com.esri.arcgisruntime.data.ContingentCodedValue;
import com.esri.arcgisruntime.data.ContingentRangeValue;
import com.esri.arcgisruntime.data.ContingentValue;
import com.esri.arcgisruntime.data.ContingentValuesDefinition;
import com.esri.arcgisruntime.data.ContingentValuesResult;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.Field;
import com.esri.arcgisruntime.data.FieldGroup;
import com.esri.arcgisruntime.data.Geodatabase;
import com.esri.arcgisruntime.data.GeodatabaseFeatureTable;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.layers.ArcGISVectorTiledLayer;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.ColorUtil;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;
import com.esri.arcgisruntime.symbology.Symbol;
import com.esri.arcgisruntime.symbology.SymbolStyle;
import com.esri.arcgisruntime.symbology.UniqueValueRenderer;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class AddFeaturesWithContingentValuesController {

  @FXML
  private MapView mapView;
  @FXML
  private ComboBox<String> statusComboBox;
  @FXML
  private ComboBox<String> protectionComboBox;
  @FXML
  private Label bufferLimitLabel;
  @FXML
  private Slider bufferSlider;
  @FXML
  private Button saveButton;
  @FXML
  private Button discardButton;
  @FXML
  private VBox vBox;

  private ArcGISFeature newFeature;
  private ContingentValuesDefinition definition;
  private FeatureLayer featureLayer;
  private Geodatabase geodatabase;
  private GeodatabaseFeatureTable geodatabaseFeatureTable;
  private GraphicsOverlay graphicsOverlay;

  /**
   * Called after FXML loads. Sets up map.
   */
  public void initialize() {

    try {

      // authentication with an API key or named user is required to access basemaps and other location services
      String yourAPIKey = System.getProperty("apiKey");
      ArcGISRuntimeEnvironment.setApiKey(yourAPIKey);

      // create a new vector tile package from a vector tile package path
      var vectorTiledLayer = new ArcGISVectorTiledLayer(new File(System.getProperty("data.dir"), "./samples-data" +
        "/FillmoreTopographicMap.vtpk").getAbsolutePath());
      // create a new basemap with the vector tiled layer and create a new map from it
      var basemap = new Basemap(vectorTiledLayer);
      ArcGISMap map = new ArcGISMap(basemap);

      // set the map to the map view
      mapView.setMap(map);

      // create a graphics overlay to display the nest buffer exclusion area
      graphicsOverlay = new GraphicsOverlay();
      var bufferSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.FORWARD_DIAGONAL,
        ColorUtil.colorToArgb(Color.RED), new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID,
        ColorUtil.colorToArgb(Color.BLACK), 2));
      graphicsOverlay.setRenderer(new SimpleRenderer(bufferSymbol));
      mapView.getGraphicsOverlays().add(graphicsOverlay);

      geodatabase = new Geodatabase(new File(System.getProperty("data.dir"),
        "./samples-data/ContingentValuesBirdNests.geodatabase").getAbsolutePath());

      geodatabase.addDoneLoadingListener(() -> {

        if (geodatabase.getLoadStatus() == LoadStatus.LOADED) {

          geodatabaseFeatureTable = geodatabase.getGeodatabaseFeatureTable("BirdNests");
          geodatabaseFeatureTable.addDoneLoadingListener(() -> {

            newFeature = (ArcGISFeature) geodatabaseFeatureTable.createFeature();
            
            System.out.println(geodatabaseFeatureTable.getTotalFeatureCount());

            loadContingentDefinition();

            featureLayer = new FeatureLayer(geodatabaseFeatureTable);

            featureLayer.addDoneLoadingListener(() -> {
              if (featureLayer.getLoadStatus() == LoadStatus.LOADED) {

                mapView.setViewpointGeometryAsync(featureLayer.getFullExtent(), 50);
                // queries the features in the feature table and applies a buffer to them
                queryAndBufferFeatures();
              }
            });

            map.getOperationalLayers().add(featureLayer);
          });
          geodatabaseFeatureTable.loadAsync();

          
        } else if (geodatabase.getLoadStatus() == LoadStatus.FAILED_TO_LOAD) {
          new Alert(Alert.AlertType.ERROR, "Geodatabase failed to load").show();
        }
      });

      // load the geodatabase
      geodatabase.loadAsync();
      
//      // when the map view is clicked, add a new feature 
//      mapView.setOnMouseClicked(event -> {
//        // check that the primary mouse button was clicked
//        if (event.isStillSincePress() && event.getButton() == MouseButton.PRIMARY) {
//
//          vBox.setDisable(false);
//          // create a point from where the user clicked
//          Point2D point = new Point2D(event.getX(), event.getY());
//          // create a map point from a point
//          Point mapPoint = mapView.screenToLocation(point);
//          
//          // get the normalized geometry for the clicked location and use it as the feature's geometry
//          Point normalizedMapPoint = (Point) GeometryEngine.normalizeCentralMeridian(mapPoint);
//          newFeature.setGeometry(normalizedMapPoint);
//          
//        }
//      });


    } catch (Exception e) {
      // on any error, display the stack trace
      e.printStackTrace();
    }

  }

  private void loadContingentDefinition() {

    definition = geodatabaseFeatureTable.getContingentValuesDefinition();
    definition.loadAsync();
    definition.addDoneLoadingListener(() -> {
      
      if (definition.getLoadStatus() == LoadStatus.LOADED) {

        definition.getFieldGroups().forEach(fieldGroup -> {
          System.out.println("Name: " + fieldGroup.getName());
          System.out.println("Fields: " + fieldGroup.getFields());
          System.out.println("First contingency value of field group : " + fieldGroup.getContingencies().get(0).getValues());
        });

        System.out.println("Name of first field group: " + definition.getFieldGroups().get(0).getName());
//        var protectionFieldGroup = definition.getFieldGroups().get(0);
//
//        CodedValueDomain protectionDomain = (CodedValueDomain)geodatabaseFeatureTable.getField("Protection").getDomain();
//        for (CodedValue codedValue : protectionDomain.getCodedValues()) {
//          System.out.println(codedValue.getName()); // this gets endangered/not endangered back but doesn't follow the design
//        }


        handleMouseClick();

        CodedValueDomain statusCodedValueDomain =
          (CodedValueDomain) geodatabaseFeatureTable.getField("Status").getDomain();

        // printing out the possible values for status
        for (CodedValue codedValue : statusCodedValueDomain.getCodedValues()) {
          statusComboBox.getItems().add(codedValue.getName());
        }

        // handle protection contingent value
        var valuesResult = geodatabaseFeatureTable.getContingentValues(newFeature, "Protection");
//
//      System.out.println(valuesResult.getContingentValuesByFieldGroup().values());
//
//      var values = valuesResult.getContingentValuesByFieldGroup();
//
//      for (String valueName : values.keySet()) {
//        System.out.println("value name " + valueName);
//
//        for (ContingentValue contingentValue : values.get(valueName)) {
//
//          ContingentCodedValue codedValue = (ContingentCodedValue) contingentValue;
//
//          System.out.println("possible species : " + codedValue.getCodedValue().getCode());
        var values = valuesResult.getContingentValuesByFieldGroup().get("ProtectionFieldGroup");

        System.out.println(valuesResult.getContingentValuesAllGroups().size());
        System.out.println(values.size());

        // add coded values of the "protection" contingent value to the combobox
        for (ContingentValue contingentValue : values) {
          ContingentCodedValue codedValue = (ContingentCodedValue) contingentValue;

          System.out.println(codedValue);

          protectionComboBox.getItems().add(codedValue.getCodedValue().getName());
        }

        var contingentValueResult = geodatabaseFeatureTable.getContingentValues(newFeature, "BufferSize");

        var bufferSizeGroupContingentValues = (ContingentRangeValue) contingentValueResult.getContingentValuesByFieldGroup().get("BufferSizeFieldGroup");
        System.out.println(bufferSizeGroupContingentValues);

        var minValue = (int) bufferSizeGroupContingentValues.getMinValue();
        var maxValue = (int) bufferSizeGroupContingentValues.getMaxValue();

        System.out.println(minValue);
        
      }
      


    });


  }

  /**
   * HAndle mouse click
   */
  private void handleMouseClick() {

    mapView.setOnMouseClicked(event -> {

      // check that the primary mouse button was clicked
      if (event.isStillSincePress() && event.getButton() == MouseButton.PRIMARY) {

        vBox.setDisable(false);
        // create a point from where the user clicked
        Point2D point = new Point2D(event.getX(), event.getY());
        // create a map point from a point
        Point mapPoint = mapView.screenToLocation(point);

        // get the normalized geometry for the clicked location and use it as the feature's geometry
        Point normalizedMapPoint = (Point) GeometryEngine.normalizeCentralMeridian(mapPoint);
        newFeature.setGeometry(normalizedMapPoint);
      }

    });

  }

  /**
   * Handles protection combobox selection
   */
  @FXML
  private void handleProtectionCombobox() {

    newFeature.getAttributes().put("Protection", protectionComboBox.getSelectionModel().getSelectedItem());

  }

  /**
   * Handles status combobox selection
   */
  @FXML
  private void handleStatusCombobox() {

    newFeature.getAttributes().put("Status", statusComboBox.getSelectionModel().getSelectedItem());

    System.out.println(newFeature.getAttributes().get("Status"));

  }

  /**
   * Queries and buffers features
   */
  private void queryAndBufferFeatures() {

    // get all the features that have buffer size greater than zero
    QueryParameters parameters = new QueryParameters();
    parameters.setWhereClause("BufferSize > 0");
    var results = geodatabaseFeatureTable.queryFeaturesAsync(parameters);

    results.addDoneListener(() -> {
      try {
        // clear the existing buffer graphics
        graphicsOverlay.getGraphics().clear();

        var featureQueryResult = results.get();
        for (Feature feature : featureQueryResult) {

          Integer bufferDistance = (Integer) feature.getAttributes().get("BufferSize");
          Geometry buffer = GeometryEngine.buffer(feature.getGeometry(), bufferDistance);
          graphicsOverlay.getGraphics().add(new Graphic(buffer));

        }

      } catch (InterruptedException | ExecutionException e) {
        e.printStackTrace();
      }
    });
  }


//
//  /**
//   * Validate contingent values
//   */
//  private boolean areContingentValuesValid() {
//    int numberOfViolations = 0;
//
//    List<ContingencyConstraintViolation> contingencyConstraintViolations = geodatabaseFeatureTable
//    .validateContingencyConstraints(newFeature);
//    numberOfViolations = contingencyConstraintViolations.size();
//
//    // if the number of contingency constraint violations is zero, the attribute map satisfies all contingencies
//
//    if (numberOfViolations == 0) {
//      return true;
//    } else {
//      List<String> fieldGroupsNames = new ArrayList<>();
//      contingencyConstraintViolations.forEach(contingencyConstraintViolation -> {
//        fieldGroupsNames.add(contingencyConstraintViolation.getFieldGroup().getName());
//      });
//      System.out.println(fieldGroupsNames);
//      return false;
//
//    }
//  }

  /**
   * Create new nest
   */
  @FXML
  private void createNewNest() {

    // add the feature to the table
    var feature = geodatabaseFeatureTable.addFeatureAsync(newFeature);
    // update the feature to get the updated objectid - a temporary ID is used before the feature is added
    feature.addDoneListener(newFeature::refresh);
    System.out.println(geodatabaseFeatureTable.getTotalFeatureCount());


    // once the attribute map is filled and validated, save the feature to the geodatabase feature table
    var update = geodatabaseFeatureTable.updateFeatureAsync(newFeature);
    update.addDoneListener(() -> {
//      queryAndBufferFeatures();
      newFeature = null;
    });
  }


  /**
   * Discards the nearly created feature
   */
  @FXML
  private void discardFeature() {

    // delete the newly created feature from the geodatabase feature table
//    var delete = geodatabaseFeatureTable.deleteFeatureAsync(newFeature);
//    delete.addDoneListener(() -> {
//      newFeature = null;
//    });

    geodatabase.close();

    geodatabase.loadAsync();
    geodatabase.addDoneLoadingListener(() -> {
      System.out.println("geodatabase loaded");
      loadContingentDefinition();
    });

  }


  /**
   * Stops and releases all resources used in application.
   */
  void terminate() {

    if (mapView != null) {
      mapView.dispose();
//      geodatabase.close();
    }
  }
}
