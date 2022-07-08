
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
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.data.ArcGISFeature;
import com.esri.arcgisruntime.data.ContingencyConstraintViolation;
import com.esri.arcgisruntime.data.ContingentCodedValue;
import com.esri.arcgisruntime.data.ContingentRangeValue;
import com.esri.arcgisruntime.data.ContingentValue;
import com.esri.arcgisruntime.data.ContingentValuesResult;
import com.esri.arcgisruntime.data.Feature;
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
import com.esri.arcgisruntime.symbology.SimpleRenderer;
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
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class AddFeaturesWithContingentValuesController {
  
  @FXML private MapView mapView;
  @FXML private ComboBox<String> statusComboBox;
  @FXML private ComboBox<String> protectionComboBox;
  @FXML private Label bufferLimitLabel;
  @FXML private Slider bufferSlider;
  @FXML private Button saveButton;
  @FXML private Button discardButton;

  private ArcGISFeature newFeature;
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
      var vectorTiledLayer = new ArcGISVectorTiledLayer(new File(System.getProperty("data.dir"), "./samples-data/FillmoreTopographicMap.vtpk").getAbsolutePath());
      // create a new basemap with the vector tiled layer and create a new map from it
      var basemap = new Basemap(vectorTiledLayer);
      ArcGISMap map = new ArcGISMap(basemap);

      // set the map to the map view
      mapView.setMap(map);

      // create a graphics overlay to display the nest buffer exclusion area
      graphicsOverlay = new GraphicsOverlay();
      var bufferSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.FORWARD_DIAGONAL, ColorUtil.colorToArgb(Color.RED), new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, ColorUtil.colorToArgb(Color.BLACK), 2));
      graphicsOverlay.setRenderer(new SimpleRenderer(bufferSymbol));
      mapView.getGraphicsOverlays().add(graphicsOverlay);

      geodatabase = new Geodatabase(new File(System.getProperty("data.dir"),
        "./samples-data/ContingentValuesBirdNests.geodatabase").getAbsolutePath());

      geodatabase.addDoneLoadingListener(() -> {
        if (geodatabase.getLoadStatus() == LoadStatus.LOADED) {
          // get the "BirdsNests" geodatabase feature table from the geodatabase
          geodatabaseFeatureTable = geodatabase.getGeodatabaseFeatureTable("BirdNests");
          var featureLayer = new FeatureLayer(geodatabaseFeatureTable);
          map.getOperationalLayers().add(featureLayer);
          featureLayer.addDoneLoadingListener(() -> {
            if (featureLayer.getLoadStatus() == LoadStatus.LOADED) {
              mapView.setViewpointGeometryAsync(featureLayer.getFullExtent(), 50);
              // queries the features in the feature table and applies a buffer to them
              queryAndBufferFeatures();
            }
          });
        } else if (geodatabase.getLoadStatus() == LoadStatus.FAILED_TO_LOAD) {
          new Alert(Alert.AlertType.ERROR, "Geodatabase failed to load").show();
        }
      });
      // load the geodatabase
      geodatabase.loadAsync();

      // when the map view is clicked, add a new feature 
      mapView.setOnMouseClicked(event -> {
        // check that the primary mouse button was clicked
        if (event.isStillSincePress() && event.getButton() == MouseButton.PRIMARY) {

          // create a point from where the user clicked
          Point2D point = new Point2D(event.getX(), event.getY());
          // create a map point from a point
          Point mapPoint = mapView.screenToLocation(point);

          // create a new empty feature to define attributes for
          newFeature = (ArcGISFeature) geodatabaseFeatureTable.createFeature();
          // get the normalized geometry for the clicked location and use it as the feature's geometry
          Point normalizedMapPoint = (Point) GeometryEngine.normalizeCentralMeridian(mapPoint);
          newFeature.setGeometry(normalizedMapPoint);
          // add the feature to the table
          var feature = geodatabaseFeatureTable.addFeatureAsync(newFeature);
          // update the feature to get the updated objectid - a temporary ID is used before the feature is added
          feature.addDoneListener(newFeature::refresh);
          System.out.println(geodatabaseFeatureTable.getTotalFeatureCount());

        }
      });
      
    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
    }
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

  /**
   * Gets contingent values
   */
  private List<String> getContingentValues(String field, String fieldGroupName) {

    // create an empty list to return valid contingent values
    List<String> contingentValuesNamesList = new ArrayList<>();

    // instantiate a dictionary containing all possible values for a field in the context of the
    // contingent field groups it participates in
    ContingentValuesResult contingentValuesResult = geodatabaseFeatureTable.getContingentValues(newFeature, field);

    var contingentValueList = contingentValuesResult.getContingentValuesByFieldGroup().get(fieldGroupName);

    // loop through the contingent values
    for (ContingentValue contingentValue : contingentValueList) {

      // contingent coded values are contingent values defined from a coded value domain
      // there are often multiple results returned by the ContingentValuesResult
      if (contingentValue instanceof ContingentCodedValue) {
        contingentValuesNamesList.add(((ContingentCodedValue) contingentValue).getCodedValue().getName());
      } else if (contingentValue instanceof ContingentRangeValue) {
        contingentValuesNamesList.add(((ContingentRangeValue) contingentValue).getMinValue().toString());
        contingentValuesNamesList.add(((ContingentRangeValue) contingentValue).getMaxValue().toString());
      }
    }

    return contingentValuesNamesList;
  }

  /**
   * Validate contingent values
   */
  private boolean areContingentValuesValid() {
    int numberOfViolations = 0;

    List<ContingencyConstraintViolation> contingencyConstraintViolations = geodatabaseFeatureTable.validateContingencyConstraints(newFeature);
    numberOfViolations = contingencyConstraintViolations.size();

    // if the number of contingency constraint violations is zero, the attribute map satisfies all contingencies

    if (numberOfViolations == 0) {
      return true;
    } else {
      List<String> fieldGroupsNames = new ArrayList<>();
      contingencyConstraintViolations.forEach(contingencyConstraintViolation -> {
        fieldGroupsNames.add(contingencyConstraintViolation.getFieldGroup().getName());
      });
      System.out.println(fieldGroupsNames);
      return false;

    }
  }

  /**
   * Create new nest
   */
  @FXML
  private void createNewNest() {
    // once the attribute map is filled and validated, save the feature to the geodatabase feature table
    var update = geodatabaseFeatureTable.updateFeatureAsync(newFeature);
    update.addDoneListener(() -> {
      queryAndBufferFeatures();
      newFeature = null;
    });
  }


  /**
   * Discards the nearly created feature
   */
  @FXML
  private void discardFeature() {
    // delete the newly created feature from the geodatabase feature table
    var delete = geodatabaseFeatureTable.deleteFeatureAsync(newFeature);
    delete.addDoneListener(() -> {
      newFeature = null;
    });
  }


  /**
   * Stops and releases all resources used in application.
   */
  void terminate() {

    if (mapView != null) {
      mapView.dispose();
    }
  }
}
