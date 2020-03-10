/*
 * Copyright 2020 Esri.
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

package com.esri.samples.perform_valve_isolation_trace;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.paint.Color;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.ArcGISFeature;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.ColorUtil;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;
import com.esri.arcgisruntime.utilitynetworks.UtilityAssetGroup;
import com.esri.arcgisruntime.utilitynetworks.UtilityAssetType;
import com.esri.arcgisruntime.utilitynetworks.UtilityCategory;
import com.esri.arcgisruntime.utilitynetworks.UtilityCategoryComparison;
import com.esri.arcgisruntime.utilitynetworks.UtilityCategoryComparisonOperator;
import com.esri.arcgisruntime.utilitynetworks.UtilityDomainNetwork;
import com.esri.arcgisruntime.utilitynetworks.UtilityElement;
import com.esri.arcgisruntime.utilitynetworks.UtilityElementTraceResult;
import com.esri.arcgisruntime.utilitynetworks.UtilityNetwork;
import com.esri.arcgisruntime.utilitynetworks.UtilityNetworkDefinition;
import com.esri.arcgisruntime.utilitynetworks.UtilityNetworkSource;
import com.esri.arcgisruntime.utilitynetworks.UtilityTier;
import com.esri.arcgisruntime.utilitynetworks.UtilityTraceConfiguration;
import com.esri.arcgisruntime.utilitynetworks.UtilityTraceFilter;
import com.esri.arcgisruntime.utilitynetworks.UtilityTraceParameters;
import com.esri.arcgisruntime.utilitynetworks.UtilityTraceResult;
import com.esri.arcgisruntime.utilitynetworks.UtilityTraceType;

public class PerformValveIsolationTraceController {

  @FXML private MapView mapView;
  @FXML private ProgressIndicator progressIndicator;
  @FXML private ComboBox<UtilityCategory> categorySelectionComboBox;
  @FXML private Button traceButton;
  @FXML private Label statusLabel;
  @FXML private CheckBox includeIsolatedFeaturesCheckbox;

  private UtilityNetwork utilityNetwork;
  private UtilityTraceConfiguration traceConfiguration;
  private UtilityElement startingLocation;

  public void initialize() {
    try {

      // create a basemap and set it to the map view
      ArcGISMap map = new ArcGISMap(Basemap.createStreetsNightVector());
      mapView.setMap(map);

      // load the utility network data from the feature service and create feature layers
      String featureServiceURL =
              "https://sampleserver7.arcgisonline.com/arcgis/rest/services/UtilityNetwork/NapervilleGas/FeatureServer";

      ServiceFeatureTable distributionLineFeatureTable = new ServiceFeatureTable(featureServiceURL + "/3");
      FeatureLayer distributionLineLayer = new FeatureLayer(distributionLineFeatureTable);

      ServiceFeatureTable deviceFeatureTable = new ServiceFeatureTable(featureServiceURL + "/0");
      FeatureLayer deviceLayer = new FeatureLayer(deviceFeatureTable);

      // add the feature layers to the map
      map.getOperationalLayers().addAll(Arrays.asList(distributionLineLayer, deviceLayer));

      // create a graphics overlay for the starting location and add it to the map view
      GraphicsOverlay startingLocationGraphicsOverlay = new GraphicsOverlay();
      mapView.getGraphicsOverlays().add(startingLocationGraphicsOverlay);

      // create and apply renderers for the starting point graphics overlay
      SimpleMarkerSymbol startingPointSymbol =
              new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CROSS, ColorUtil.colorToArgb(Color.LIGHTGREEN), 25);
      startingLocationGraphicsOverlay.setRenderer(new SimpleRenderer(startingPointSymbol));

      // create and load the utility network
      utilityNetwork = new UtilityNetwork(featureServiceURL, map);
      utilityNetwork.loadAsync();
      utilityNetwork.addDoneLoadingListener(() -> {
        if (utilityNetwork.getLoadStatus() == LoadStatus.LOADED) {

          // get a trace configuration from a tier
          UtilityNetworkDefinition networkDefinition = utilityNetwork.getDefinition();
          UtilityDomainNetwork domainNetwork = networkDefinition.getDomainNetwork("Pipeline");
          UtilityTier tier = domainNetwork.getTier("Pipe Distribution System");
          traceConfiguration = tier.getTraceConfiguration();

          // create a trace filter
          traceConfiguration.setFilter(new UtilityTraceFilter());

          // get a default starting location
          UtilityNetworkSource networkSource = networkDefinition.getNetworkSource("Gas Device");
          UtilityAssetGroup assetGroup = networkSource.getAssetGroup("Meter");
          UtilityAssetType assetType = assetGroup.getAssetType("Customer");
          startingLocation = utilityNetwork.createElement(assetType, UUID.fromString("98A06E95-70BE-43E7-91B7-E34C9D3CB9FF"));

          // get the first feature for the starting location, and get its geometry
          ListenableFuture<List<ArcGISFeature>> elementFeaturesFuture =
                  utilityNetwork.fetchFeaturesForElementsAsync(Collections.singletonList(startingLocation));

          elementFeaturesFuture.addDoneListener(() -> {
            try {
              List<ArcGISFeature> startingLocationFeatures = elementFeaturesFuture.get();

              if (!startingLocationFeatures.isEmpty()) {
                Geometry startingLocationGeometry = startingLocationFeatures.get(0).getGeometry();

                if (startingLocationGeometry instanceof Point){
                  Point startingLocationGeometryPoint = (Point) startingLocationFeatures.get(0).getGeometry();

                // create a graphic for the starting location and add it to the graphics overlay
                Graphic startingLocationGraphic = new Graphic(startingLocationGeometry, startingPointSymbol);
                startingLocationGraphicsOverlay.getGraphics().add(startingLocationGraphic);

                // set the map's viewpoint to the starting location
                mapView.setViewpointAsync(new Viewpoint(startingLocationGeometryPoint, 3000));

                // build the choice list for categories populated with the 'Name' property of each 'UtilityCategory' in the 'UtilityNetworkDefinition'
                categorySelectionComboBox.getItems().addAll(networkDefinition.getCategories());
                categorySelectionComboBox.getSelectionModel().select(0);
                categorySelectionComboBox.setCellFactory(param -> new UtilityCategoryListCell());
                categorySelectionComboBox.setButtonCell(new UtilityCategoryListCell());

                // enable the UI
                enableUI();

                // hide the progress indicator
                progressIndicator.setVisible(false);

                // update the status text
                statusLabel.setText("Utility network loaded. Ready to perform trace...");
                }

              } else {
                new Alert(Alert.AlertType.ERROR, "Error getting starting location geometry!").show();
              }
            } catch (ExecutionException | InterruptedException e) {
              new Alert(Alert.AlertType.ERROR, "Error getting starting location feature!").show();
            }
          });

        } else {
          new Alert(Alert.AlertType.ERROR, "Error loading Utility Network.").show();
        }
      });
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @FXML
  private void handleTraceClick() {
    try {
      // clear previous selection from the layers
      mapView.getMap().getOperationalLayers().forEach(layer -> {
        if (layer instanceof FeatureLayer) {
          ((FeatureLayer) layer).clearSelection();
        }
      });

      // show the progress indicator and update the status text
      progressIndicator.setVisible(true);
      statusLabel.setText("Running isolation trace...");

      // disable the UI
      traceButton.setDisable(true);
      categorySelectionComboBox.setDisable(true);
      includeIsolatedFeaturesCheckbox.setDisable(true);

      // get the selected utility category
      if (categorySelectionComboBox.getSelectionModel().getSelectedItem() != null) {
        UtilityCategory selectedCategory = categorySelectionComboBox.getSelectionModel().getSelectedItem();
        // create a category comparison for the trace
        // NOTE: UtilityNetworkAttributeComparison or UtilityCategoryComparison with Operator.DoesNotExists
        // can also be used. These conditions can be joined with either UtilityTraceOrCondition or UtilityTraceAndCondition.
        UtilityCategoryComparison categoryComparison = new UtilityCategoryComparison(selectedCategory, UtilityCategoryComparisonOperator.EXISTS);
        // set the category comparison to the barriers of the configuration's trace filter
        traceConfiguration.getFilter().setBarriers(categoryComparison);
      }

      // set the configuration to include or leave out isolated features
      traceConfiguration.setIncludeIsolatedFeatures(includeIsolatedFeaturesCheckbox.isSelected());

      // build parameters for the isolation trace
      UtilityTraceParameters traceParameters = new UtilityTraceParameters(UtilityTraceType.ISOLATION, Collections.singletonList(startingLocation));
      traceParameters.setTraceConfiguration(traceConfiguration);

      // run the trace and get the result
      ListenableFuture<List<UtilityTraceResult>> utilityTraceResultsFuture = utilityNetwork.traceAsync(traceParameters);
      utilityTraceResultsFuture.addDoneListener(() -> {
        try {
          List<UtilityTraceResult> utilityTraceResults = utilityTraceResultsFuture.get();

          if (utilityTraceResults.get(0) instanceof UtilityElementTraceResult) {
            UtilityElementTraceResult utilityElementTraceResult = (UtilityElementTraceResult) utilityTraceResults.get(0);

            if (!utilityElementTraceResult.getElements().isEmpty()) {

              // iterate through the map's feature layers
              mapView.getMap().getOperationalLayers().forEach(layer -> {
                if (layer instanceof FeatureLayer) {

                  // create query parameters to find features whose network source name matches the layer's feature
                  // table name
                  QueryParameters queryParameters = new QueryParameters();
                  utilityElementTraceResult.getElements().forEach(utilityElement -> {

                    String networkSourceName = utilityElement.getNetworkSource().getName();
                    String featureTableName = ((FeatureLayer) layer).getFeatureTable().getTableName();

                    if (networkSourceName.equals(featureTableName)) {
                      queryParameters.getObjectIds().add(utilityElement.getObjectId());
                    }
                  });

                  // select features that match the query
                  ListenableFuture<FeatureQueryResult> featureQueryResultListenableFuture =
                          ((FeatureLayer) layer).selectFeaturesAsync(queryParameters, FeatureLayer.SelectionMode.NEW);

                  // wait for the selection to finish
                  featureQueryResultListenableFuture.addDoneListener(() -> {
                    // update the status text, enable the buttons and hide the progress indicator
                    statusLabel.setText("Isolation trace completed.");
                    enableUI();
                  });
                }
              });
            }
          } else {
            statusLabel.setText("Trace failed.");
            new Alert(Alert.AlertType.ERROR, "Trace result not a utility element.").show();
            enableUI();
          }

        } catch (Exception e) {
          statusLabel.setText("Trace failed.");
          new Alert(Alert.AlertType.ERROR, "Error getting isolation trace result.").show();
          enableUI();
        }
      });

    } catch (Exception e) {
      new Alert(Alert.AlertType.ERROR, "Error performing isolation trace.").show();
      enableUI();
    }
  }

  /**
   * Enables the UI and hides the progress indicator.
   */
  private void enableUI() {
    progressIndicator.setVisible(false);
    traceButton.setDisable(false);
    categorySelectionComboBox.setDisable(false);
    includeIsolatedFeaturesCheckbox.setDisable(false);
  }

  /**
   * Stops and releases all resources used in application.
   */
  public void terminate() {

    if (mapView != null) {
      mapView.dispose();
    }
  }
}
