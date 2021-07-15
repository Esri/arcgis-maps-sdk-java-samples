/*
 * Copyright 2021 Esri.
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

package com.esri.samples.query_with_cql_filters;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.OgcFeatureCollectionTable;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.TimeExtent;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.ColorUtil;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class QueryWithCqlFiltersController {

  @FXML private Button applyQueryButton;
  @FXML private Button revertToInitialQueryButton;
  @FXML private CheckBox timeExtentCheckBox;
  @FXML private ComboBox<String> comboBox;
  @FXML private DatePicker endDatePicker;
  @FXML private DatePicker startDatePicker;
  @FXML private Label featureNumberLabel;
  @FXML private MapView mapView;
  @FXML private ProgressIndicator progressIndicator;
  @FXML private TextField textField;

  private Envelope datasetExtent;
  private OgcFeatureCollectionTable ogcFeatureCollectionTable; // keep loadable in scope to avoid garbage collection

  public void initialize() {

    try {

      // authentication with an API key or named user is required to access basemaps and other location services
      String yourAPIKey = System.getProperty("apiKey");
      ArcGISRuntimeEnvironment.setApiKey(yourAPIKey);

      // create a map with the topographic basemap style
      ArcGISMap map = new ArcGISMap(BasemapStyle.ARCGIS_TOPOGRAPHIC);

      // set the map to the mapview
      mapView.setMap(map);

      // prepare UI for interaction
      populateUiWithCqlQueriesAndDateValues();

      // define strings for the service URL and collection id
      // note that the service defines the collection id which can be accessed via OgcFeatureCollectionInfo.getCollectionId().
      String serviceUrl = "https://demo.ldproxy.net/daraa";
      String collectionId = "TransportationGroundCrv";

      // create an OGC feature collection table from the service url and collection id
      ogcFeatureCollectionTable = new OgcFeatureCollectionTable(serviceUrl, collectionId);

      // set the feature request mode to manual
      // in this mode, the table must be manually populated - panning and zooming won't request features automatically
      ogcFeatureCollectionTable.setFeatureRequestMode(ServiceFeatureTable.FeatureRequestMode.MANUAL_CACHE);

      // load the table
      ogcFeatureCollectionTable.loadAsync();

      // ensure the feature collection table has loaded successfully before creating a feature layer from it to display on the map
      ogcFeatureCollectionTable.addDoneLoadingListener(() -> {
        if (ogcFeatureCollectionTable.getLoadStatus() == LoadStatus.LOADED) {

          // create a feature layer and set a renderer to it to visualize the OGC API features
          var featureLayer = new FeatureLayer(ogcFeatureCollectionTable);
          var simpleRenderer = new SimpleRenderer(new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, ColorUtil.colorToArgb(Color.DARKMAGENTA), 3));
          featureLayer.setRenderer(simpleRenderer);

          // add the layer to the map
          map.getOperationalLayers().add(featureLayer);

          datasetExtent = ogcFeatureCollectionTable.getExtent();

          // display result of query on the map
          setInitialQueryOnOgcFeatureTable();

        } else {
          // show an alert dialog if there is a loading failure
          new Alert(Alert.AlertType.ERROR, "Failed to load OGC Feature Collection Table: " +
            ogcFeatureCollectionTable.getLoadError().getCause().getMessage()).show();
        }
      });

    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
    }
  }

  /**
   * Gets the total extent of the OGC feature collection table, and uses a basic query parameters to return 3000 features
   * from the service.
   */
  @FXML
  private void setInitialQueryOnOgcFeatureTable() {

    progressIndicator.setVisible(true);

    // zoom to the dataset extent
    if (datasetExtent != null && !datasetExtent.isEmpty()) {
      mapView.setViewpointGeometryAsync(datasetExtent);
    }

    QueryParameters ogcFeatureExtentQueryParameters = new QueryParameters();
    // create a query based on the current visible extent
    ogcFeatureExtentQueryParameters.setGeometry(datasetExtent);
    // set a limit of 3000 on the number of returned features per request, the default on some services could be as low as 10
    ogcFeatureExtentQueryParameters.setMaxFeatures(3000);

    try {
      // populate and load the table with the query parameters
      // set the clearCache parameter to true to clear existing table entries, set the outfields parameter to null to request all fields
      ogcFeatureCollectionTable.populateFromServiceAsync(ogcFeatureExtentQueryParameters, true, null).addDoneListener(() -> {
        progressIndicator.setVisible(false);
        applyQueryButton.setDisable(false);
        // display number of features returned
        featureNumberLabel.setText("Query returned: " + ogcFeatureCollectionTable.getTotalFeatureCount() + " features");
      });

      // handle UI
      revertToInitialQueryButton.setDisable(true);
      // reset combo box and show original prompt text
      comboBox.getSelectionModel().clearSelection();
      comboBox.setButtonCell(new ListCell<>() {
        @Override
        protected void updateItem(String item, boolean empty) {
          setText(item);
        }
      });

    } catch (Exception exception) {
      exception.printStackTrace();
      new Alert(Alert.AlertType.ERROR, exception.getMessage()).show();
    }
  }

  /**
   * Populates features from provided query parameters, and displays the result on the map.
   */
  @FXML
  private void query() {

    QueryParameters cqlQueryParameters = new QueryParameters();
    // set the query parameter's where clause with the CQL query in the combo box
    cqlQueryParameters.setWhereClause(comboBox.getSelectionModel().getSelectedItem());
    // set the max features to the number entered in the text field
    cqlQueryParameters.setMaxFeatures(Integer.parseInt(textField.getText()));

    // if the time extent checkbox is selected, retrieve the date selected from the date picker and set it to the
    // query parameters time extent
    if (timeExtentCheckBox.isSelected()) {
      // get the value selected from the date picker
      LocalDate startDate = startDatePicker.getValue();
      LocalDate endDate = endDatePicker.getValue();

      // convert the value into a calendar
      Calendar start = new Calendar.Builder().setDate(startDate.getYear(), startDate.getMonthValue(), startDate.getDayOfMonth()).build();
      Calendar end = new Calendar.Builder().setDate(endDate.getYear(), endDate.getMonthValue(), endDate.getDayOfMonth()).build();

      // set the query parameters time extent
      cqlQueryParameters.setTimeExtent(new TimeExtent(start, end));
    }

    // populate and load the table with the query parameters
    // set the clearCache parameter to true to clear existing table entries and set the outfields parameter to null requests all fields
    ListenableFuture<FeatureQueryResult> result = ogcFeatureCollectionTable.populateFromServiceAsync(cqlQueryParameters, true, null);
    result.addDoneListener(() -> {

      // display number of features returned
      featureNumberLabel.setText("Query returned: " + ogcFeatureCollectionTable.getTotalFeatureCount() + " features");

      try {
        // create a new list to store returned geometries in
        List<Geometry> featureGeometryList = new ArrayList<>();

        // iterate through each result to get its geometry and add it to the geometry list
        result.get().iterator().forEachRemaining(feature -> {
          featureGeometryList.add(feature.getGeometry());
          feature.getGeometry();
        });

        if (!featureGeometryList.isEmpty()) {
          // zoom to the total extent of the geometries returned by the query
          var totalExtent = GeometryEngine.combineExtents(featureGeometryList);
          mapView.setViewpointGeometryAsync(totalExtent, 20);
        }

      } catch (InterruptedException | ExecutionException e) {
        e.printStackTrace();
      }

      revertToInitialQueryButton.setDisable(false);

    });

  }

  /**
   * Handles user interaction with the date picker to ensure the end date time is not earlier than the start date.
   */
  @FXML
  private void handleDatePickerInteraction() {

    if (startDatePicker.getValue().toEpochDay() > endDatePicker.getValue().toEpochDay()) {
      endDatePicker.setValue(startDatePicker.getValue());
    }

  }

  /**
   * Handle checkbox interaction to control if the date picker is enabled or not.
   */
  @FXML
  private void handleCheckBoxInteraction() {

    startDatePicker.setDisable(!timeExtentCheckBox.isSelected());
    endDatePicker.setDisable(!timeExtentCheckBox.isSelected());

  }

  /**
   * Gets the total feature count from the OGC feature collection table and displays the number on the label.
   */
  private void setFeatureNumberLabelText() {

    // display number of features returned
    featureNumberLabel.setText("Query returned: " + ogcFeatureCollectionTable.getTotalFeatureCount() + " features");

  }

  /**
   * Add CQL query examples relevant to the sample data to the combobox and set the date picker to dates
   * relevant to the data.
   */
  private void populateUiWithCqlQueriesAndDateValues() {

    ObservableList<String> cqlQueryList = FXCollections.observableArrayList();

    // sample query 1: query for features with an F_CODE attribute property of "AP010".
    cqlQueryList.add("F_CODE = 'AP010'"); // cql-text query
    cqlQueryList.add("{ \"eq\" : [ { \"property\" : \"F_CODE\" }, \"AP010\" ] }"); // cql-json query

    // sample query 2: cql-text query for features with an F_CODE attribute property similar to "AQ".
    cqlQueryList.add("F_CODE LIKE 'AQ%'");

    // sample query 3: use cql-json to combine "before" and "eq" operators with the logical "and" operator
    cqlQueryList.add("{\"and\":[{\"eq\":[{ \"property\" : \"F_CODE\" }, \"AP010\"]},{ \"before\":" +
      "[{ \"property\" : \"ZI001_SDV\"},\"2013-01-01\"]}]}");

    // add sample CQL queries to the UI combobox
    comboBox.getItems().addAll(cqlQueryList);

    // set the date picker to a date relevant to the data
    startDatePicker.setValue(LocalDate.of(2011, 6, 13));
    endDatePicker.setValue(LocalDate.of(2012, 1, 7));

  }

  /**
   * Disposes application resources.
   */
  void terminate() {

    if (mapView != null) {
      mapView.dispose();
    }
  }

}
