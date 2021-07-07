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
import com.esri.arcgisruntime.data.OgcFeatureCollectionTable;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.ColorUtil;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import com.esri.arcgisruntime.mapping.view.GeoView;

import java.util.ArrayList;

public class QueryWithCqlFiltersController {

  @FXML private MapView mapView;
  @FXML private ProgressIndicator progressIndicator;
  @FXML private ComboBox<String> comboBox;
  @FXML private TextField textField;
  @FXML private DatePicker startDatePicker;
  @FXML private DatePicker endDatePicker;
  @FXML private Button applyQueryButton;
  @FXML private Label featureNumberLabel;
  ObservableList<String> cqlQueryList;

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

      comboBox.getItems().addAll(cqlQueries());

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
          var simpleRenderer = new SimpleRenderer(new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, ColorUtil.colorToArgb(Color.BLUE), 3));
          featureLayer.setRenderer(simpleRenderer);

          // add the layer to the map
          map.getOperationalLayers().add(featureLayer);

          // zoom to a small area within the dataset by default
          Envelope datasetExtent = ogcFeatureCollectionTable.getExtent();
          if (datasetExtent != null && !datasetExtent.isEmpty()) {
            mapView.setViewpointGeometryAsync(
              new Envelope(datasetExtent.getCenter(), datasetExtent.getWidth() / 3, datasetExtent.getHeight() / 3));
          }

        } else {
          // show an alert dialog if there is a loading failure
          new Alert(Alert.AlertType.ERROR, "Failed to load OGC Feature Collection Table: " +
            ogcFeatureCollectionTable.getLoadError().getCause().getMessage()).show();
        }
      });

      // once the map view navigation has completed, query the OGC API feature table for
      // additional features within the new visible extent.
      mapView.addViewpointChangedListener(e -> {
        if (!e.getSource().isNavigating()) {
          System.out.println("is navigating!");

          // get the current extent
          Envelope currentExtent = mapView.getVisibleArea().getExtent();

          // create a query based on the current visible extent
          QueryParameters visibleExtentQuery = new QueryParameters();
          visibleExtentQuery.setGeometry(currentExtent);
          visibleExtentQuery.setSpatialRelationship(QueryParameters.SpatialRelationship.INTERSECTS);
          // set a limit of 5000 on the number of returned features per request, the default on some services
          // could be as low as 10
          visibleExtentQuery.setMaxFeatures(3000);

          try {
            // populate the table with the query, leaving existing table entries intact
            // setting the outfields parameter to null requests all fields
            ogcFeatureCollectionTable.populateFromServiceAsync(visibleExtentQuery, false, null).addDoneListener(() -> {
              progressIndicator.setVisible(false);
              System.out.println("populated");
            });

          } catch (Exception exception) {
            exception.printStackTrace();
            new Alert(Alert.AlertType.ERROR, exception.getMessage()).show();
          }
        }
      });

    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
    }
  }

  private ObservableList<String> cqlQueries() {

    cqlQueryList = FXCollections.observableArrayList();

    // sample query 1: query for features with an F_CODE attribute property of "AP010".
    cqlQueryList.add("F_CODE = 'AP010'"); // cql-text query
    cqlQueryList.add("{ \"eq\" : [ { \"property\" : \"F_CODE\" }, \"AP010\" ] }"); // cql-json query

    // sample query 2: cql-text query for features with an F_CODE attribute property similar to "AQ".
    cqlQueryList.add("F_CODE LIKE 'AQ%'");

    // sample query 3: use cql-json to combine "before" and "eq" operators with the logical "and" operator
    cqlQueryList.add("{\"and\":[{\"eq\":[{ \"property\" : \"F_CODE\" }, \"AP010\"]},{ \"before\":" +
      "[{ \"property\" : \"ZI001_SDV\"},\"2013-01-01\"]}]}");

    return cqlQueryList;

  }

  // TODO
  @FXML
  private void query() {



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
