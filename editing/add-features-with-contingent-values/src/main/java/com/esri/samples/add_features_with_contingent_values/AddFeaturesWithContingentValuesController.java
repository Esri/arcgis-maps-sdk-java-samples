
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
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
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
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class AddFeaturesWithContingentValuesController {

  @FXML
  private MapView mapView;
  @FXML
  private ComboBox<CodedValue> statusComboBox;
  @FXML
  private ComboBox<CodedValue> protectionComboBox;
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

      // display the name of the coded values in the attribute combo boxes
      protectionComboBox.setButtonCell(new CodedValueListCell());
      protectionComboBox.setCellFactory(c -> new CodedValueListCell());
      statusComboBox.setButtonCell(new CodedValueListCell());
      statusComboBox.setCellFactory(c -> new CodedValueListCell());

      // create a new vector tile package from a vector tile package path
      var vectorTiledLayer = new ArcGISVectorTiledLayer(new File(System.getProperty("data.dir"), "./samples-data" +
        "/FillmoreTopographicMap.vtpk").getAbsolutePath());
      // create a new basemap with the vector tiled layer and create a new map from it
      var basemap = new Basemap(vectorTiledLayer);
      ArcGISMap map = new ArcGISMap(basemap);

      // set the map to the map view
      mapView.setMap(map);
      mapView.setDisable(true);

      // create a graphics overlay to display the nest buffer exclusion area
      graphicsOverlay = new GraphicsOverlay();
      var bufferSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.FORWARD_DIAGONAL,
        ColorUtil.colorToArgb(Color.RED), new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID,
        ColorUtil.colorToArgb(Color.BLACK), 2));
      graphicsOverlay.setRenderer(new SimpleRenderer(bufferSymbol));

      var graphic = new Graphic();
      var symbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, ColorUtil.colorToArgb(Color.BLACK), 11);
      graphic.setSymbol(symbol);
      graphicsOverlay.getGraphics().add(graphic);

      mapView.getGraphicsOverlays().add(graphicsOverlay);

      geodatabase = new Geodatabase(new File(System.getProperty("data.dir"),
        "./samples-data/ContingentValuesBirdNests.geodatabase").getAbsolutePath());

      // wait for the geodatabase to finish loading and check it has loaded
      geodatabase.addDoneLoadingListener(() -> {
        if (geodatabase.getLoadStatus() == LoadStatus.LOADED) {

          // get the BirdNests geodatabase feature table, and wait for it to load
          geodatabaseFeatureTable = geodatabase.getGeodatabaseFeatureTable("BirdNests");

          geodatabaseFeatureTable.addDoneLoadingListener(() -> {
            if (geodatabaseFeatureTable.getLoadStatus() == LoadStatus.LOADED) {

              // create a new feature that matches the schema of the geodatabase feature table
              newFeature = (ArcGISFeature) geodatabaseFeatureTable.createFeature();

              featureLayer = new FeatureLayer(geodatabaseFeatureTable);
              map.getOperationalLayers().add(featureLayer);
              // when the feature layer has loaded, set the viewpoint on the map to its full extent, and add buffers to
              // the features
              featureLayer.addDoneLoadingListener(() -> {
                if (featureLayer.getLoadStatus() == LoadStatus.LOADED) {
                  mapView.setViewpointGeometryAsync(featureLayer.getFullExtent(), 50);
                  // queries the features in the feature table and applies a buffer to them
                  queryAndBufferFeatures();
                  getAndLoadContingentDefinitionAndSetStatus();
                  // finally, prompt the user 
                  new Alert(Alert.AlertType.INFORMATION, "Click on the map to add a feature, and click save to " +
                    "save the chosen attributes to it").show();
                  mapView.setDisable(false);
                }
              });

            } else if (geodatabase.getLoadStatus() == LoadStatus.FAILED_TO_LOAD) {
              new Alert(Alert.AlertType.ERROR, "Geodatabase failed to load").show();
            }
          });

          // load the geodatabase feature table
          geodatabaseFeatureTable.loadAsync();
        }
      });
      // load the geodatabase
      geodatabase.loadAsync();

      // set up mouse clicked listener
      mapView.setOnMouseClicked(event -> {
        if (event.isStillSincePress() && event.getButton() == MouseButton.PRIMARY) {
          vBox.setDisable(false);

          // if the newFeature object is null, create a new feature and set its attributes from the already populated UI
          if (newFeature == null) {
            newFeature = (ArcGISFeature) geodatabaseFeatureTable.createFeature();
            newFeature.getAttributes().put("Status", statusComboBox.getSelectionModel().getSelectedItem().getCode());
            newFeature.getAttributes().put("Protection", protectionComboBox.getSelectionModel().getSelectedItem().getCode());
            newFeature.getAttributes().put("BufferSize", (int) bufferSlider.getValue());
          }
          
          // create a point from where the user clicked
          Point2D point = new Point2D(event.getX(), event.getY());
          // create a map point from a point
          Point mapPoint = mapView.screenToLocation(point);
          // get the normalized geometry for the clicked location and use it as the feature's geometry
          Point normalizedMapPoint = (Point) GeometryEngine.normalizeCentralMeridian(mapPoint);
          newFeature.setGeometry(normalizedMapPoint);
          graphic.setGeometry(newFeature.getGeometry());
        }

      });


    } catch (Exception e) {
      // on any error, display the stack trace
      e.printStackTrace();
    }
  }

  private void getAndLoadContingentDefinitionAndSetStatus() {


    // get and load the table's contingent values definition. this must be loaded after the table has loaded
    definition = geodatabaseFeatureTable.getContingentValuesDefinition();
    definition.addDoneLoadingListener(() -> {
      
        // check the contingent values definition has field groups (if the list is empty, there are no 
        // contingent values defined for this table
        if (definition.getLoadStatus() == LoadStatus.LOADED && !definition.getFieldGroups().isEmpty()) {

          CodedValueDomain statusCodedValueDomain =
            (CodedValueDomain) geodatabaseFeatureTable.getField("Status").getDomain();

          // printing out the possible values for status
          for (CodedValue codedValue : statusCodedValueDomain.getCodedValues()) {
            // requires some mapping of object code back to string
            statusComboBox.getItems().add(codedValue);
          }
            
          // once the combo box has been populated with coded values, select the first one
          if (statusComboBox.getItems().size() == statusCodedValueDomain.getCodedValues().size()) {
            statusComboBox.getSelectionModel().selectFirst();
          }
          
          // check the contingent values are valid
          contingentValuesAreValid();

        }
    });

    definition.loadAsync();
  }

  /**
   * Handles interaction with the protection combo box.
   */
  @FXML
  private void handleProtectionComboBox() {

    // put the value chosen from the protection combobox as the attribute for the new feature
    if (protectionComboBox.getSelectionModel().getSelectedItem() != null) {

      newFeature.getAttributes().put("Protection", protectionComboBox.getSelectionModel().getSelectedItem().getCode());

      // get contingent values for the buffer size field
      ContingentValuesResult bufferContingentValues = geodatabaseFeatureTable.getContingentValues(newFeature, 
        "BufferSize");
      // get contingent range values for the buffer size field group
      ContingentRangeValue bufferRangeValue =
        (ContingentRangeValue) bufferContingentValues.getContingentValuesByFieldGroup()
        .get("BufferSizeFieldGroup").get(0);

      // get the minimum and maximum value from the buffer contingent range values
      var minValue = (Integer) bufferRangeValue.getMinValue();
      var maxValue = (Integer) bufferRangeValue.getMaxValue();

      // set the min and max value of the buffer slider depending on the returned value of the contingent range value
      bufferSlider.setMin(minValue);
      bufferSlider.setMax(maxValue);
      bufferSlider.setValue(minValue);
      newFeature.getAttributes().put("BufferSize", (int) bufferSlider.getValue());

    }
  }

  /**
   * Handles interaction with the status combo box.
   */
  @FXML
  private void handleStatusComboBox() {

    // clear the protection attribute combobox of values
    protectionComboBox.getItems().clear();

    // add "Status" attribute to the new feature: this will allow the related contingent value in the
    // "Protected" field to be detected
    newFeature.getAttributes().put("Status", statusComboBox.getSelectionModel().getSelectedItem().getCode());

    // get contingent values for the protection field
    ContingentValuesResult protectionContingentValues = geodatabaseFeatureTable.getContingentValues(newFeature,
      "Protection");

    List<ContingentValue> protectionFieldGroupValues =
      protectionContingentValues.getContingentValuesByFieldGroup().get("ProtectionFieldGroup");
    protectionFieldGroupValues.forEach(contingentValue -> {
      var contingentCodedValue = (ContingentCodedValue) contingentValue; // cast to required contingent value type
      // add contingent coded values to the protection attribute combobox
      protectionComboBox.getItems().add(contingentCodedValue.getCodedValue());
    });

    if (protectionComboBox.getItems().size() == protectionFieldGroupValues.size()) {
      protectionComboBox.getSelectionModel().selectFirst();
      System.out.println("selects protection combobox");
    }
  }

  /**
   *
   */
  @FXML
  private void handleBufferSlider() {

    // set the initial attribute and the text to the min of the contingent range value
    newFeature.getAttributes().put("BufferSize", (int) Math.round(bufferSlider.getValue())); // TODO demo for review
    System.out.println(Math.round(bufferSlider.getValue()));

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
   * Validate contingent values
   */
  private boolean contingentValuesAreValid() {

    List<ContingencyConstraintViolation> contingencyConstraintViolations = geodatabaseFeatureTable
      .validateContingencyConstraints(newFeature);
    int numberOfViolations = contingencyConstraintViolations.size();

    // if the number of contingency constraint violations is zero, the attribute map satisfies all contingencies
    if (numberOfViolations == 0) {
      return true;
    } else {
      List<String> fieldGroupsNames = new ArrayList<>();
      contingencyConstraintViolations.forEach(contingencyConstraintViolation -> {
        fieldGroupsNames.add(contingencyConstraintViolation.getFieldGroup().getName());
      });
      String errorType = contingencyConstraintViolations.get(0).getType().toString();
      new Alert(Alert.AlertType.ERROR,
        errorType + "! " + numberOfViolations + " violations found in: " + Arrays.toString(fieldGroupsNames.toArray())).show();
      
      return false;
    }
  }

  /**
   * Create new nest
   */
  @FXML
  private void handleSaveButton() {
    
    if (contingentValuesAreValid()) {
      System.out.println("Contingent Values are valid");
    }
    // add the empty feature to the table
    geodatabaseFeatureTable.addFeatureAsync(newFeature).addDoneListener(() -> {

      newFeature.refresh();

      System.out.println("Buffer size attribute " + newFeature.getAttributes().get("BufferSize"));
      System.out.println("Protection attribute " + newFeature.getAttributes().get("Protection"));
      System.out.println("Status attribute " + newFeature.getAttributes().get("Status"));

      System.out.println(geodatabaseFeatureTable.getTotalFeatureCount());
      queryAndBufferFeatures();

      newFeature = null;
      
      vBox.setDisable(true);
      
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

    vBox.setDisable(true);
    geodatabase.close();


  }


  /**
   * Stops and releases all resources used in application.
   */
  void terminate() {

    if (mapView != null) {
      mapView.dispose();
      geodatabase.close();
    }
  }
}
