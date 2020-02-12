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

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.ColorUtil;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;
import com.esri.arcgisruntime.symbology.UniqueValueRenderer;
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
      // set the viewpoint to a subsection of the utility network
      mapView.setViewpointAsync(new Viewpoint(
              new Envelope(-9815722.9878701176, 5129758.599748333, -9815208.5110788979, 5130098.2317954693,
                      SpatialReferences.getWebMercator())));

      // load the utility network data from the feature service and create feature layers
      String featureServiceURL =
              "https://sampleserver7.arcgisonline.com/arcgis/rest/services/UtilityNetwork/NapervilleElectric/FeatureServer";

      ServiceFeatureTable distributionLineFeatureTable = new ServiceFeatureTable(featureServiceURL + "/115");
      FeatureLayer distributionLineLayer = new FeatureLayer(distributionLineFeatureTable);

      ServiceFeatureTable electricDeviceFeatureTable = new ServiceFeatureTable(featureServiceURL + "/100");
      FeatureLayer electricDeviceLayer = new FeatureLayer(electricDeviceFeatureTable);

      // create and apply a renderer for the electric distribution lines feature layer
      UniqueValueRenderer.UniqueValue mediumVoltageValue = new UniqueValueRenderer.UniqueValue("N/A", "Medium Voltage",
              new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, ColorUtil.colorToArgb(Color.DARKCYAN), 3),
              Collections.singletonList(5));
      UniqueValueRenderer.UniqueValue lowVoltageValue = new UniqueValueRenderer.UniqueValue("N/A", "Low Voltage",
              new SimpleLineSymbol(SimpleLineSymbol.Style.DASH, ColorUtil.colorToArgb(Color.DARKCYAN), 3),
              Collections.singletonList(3));
      distributionLineLayer.setRenderer(new UniqueValueRenderer(Collections.singletonList("ASSETGROUP"),
              Arrays.asList(mediumVoltageValue, lowVoltageValue), "", new SimpleLineSymbol()));

      // add the feature layers to the map
      map.getOperationalLayers().addAll(Arrays.asList(distributionLineLayer, electricDeviceLayer));

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
          UtilityDomainNetwork domainNetwork = networkDefinition.getDomainNetwork("ElectricDistribution");
          UtilityTier tier = domainNetwork.getTier("Medium Voltage Radial");
          traceConfiguration = tier.getTraceConfiguration();

          // get a default starting location
          UtilityNetworkSource networkSource = networkDefinition.getNetworkSource("Electric Distribution Device");
          UtilityAssetGroup assetGroup = networkSource.getAssetGroup("Street Light");
          UtilityAssetType assetType = assetGroup.getAssetType("Municipal");
          startingLocation = utilityNetwork.createElement(assetType, UUID.fromString("925A7696-46F4-48A6-BF27-D1AEC900D63E"));

          // create a graphic for the starting location and add it to the graphics overlay
          Graphic startingLocationGraphic = new Graphic(new Point(-9815456.2591999993, 5129967.1691000015, SpatialReferences.getWebMercator()));
          startingLocationGraphicsOverlay.getGraphics().add(startingLocationGraphic);

          // build the choice list for categories populated with the 'Name' property of each 'UtilityCategory' in the 'UtilityNetworkDefinition'
          categorySelectionComboBox.getItems().addAll(networkDefinition.getCategories());
          categorySelectionComboBox.getSelectionModel().select(0);
          categorySelectionComboBox.setCellFactory(param -> new UtilityCategoryListCell());
          categorySelectionComboBox.setButtonCell(new UtilityCategoryListCell());

          // enable the UI
          enableButtonInteraction();

          // hide the progress indicator
          progressIndicator.setVisible(false);

          // update the status text
          statusLabel.setText("Utility network loaded. Ready to perform trace...");

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
      statusLabel.setText("Running trace trace...");

      // disable the UI
      traceButton.setDisable(true);
      categorySelectionComboBox.setDisable(true);

      // get the selected utility category
      if (categorySelectionComboBox.getSelectionModel().getSelectedItem() != null) {
        UtilityCategory selectedCategory = categorySelectionComboBox.getSelectionModel().getSelectedItem();
        // create a category comparison for the trace
        // NOTE: UtilityNetworkAttributeComparison or UtilityCategoryComparison with Operator.DoesNotExists
        // can also be used. These conditions can be joined with either UtilityTraceOrCondition or UtilityTraceAndCondition.
        UtilityCategoryComparison categoryComparison = new UtilityCategoryComparison(selectedCategory, UtilityCategoryComparisonOperator.EXISTS);
        // set the category comparison to the barriers of the configuration's trace filter
//        traceConfiguration.getFilter().setBarriers(categoryComparison);
      }

      // set the configuration to include or leave out isolated features
//      traceConfiguration.setIncludeIsolatedFeatures(includeIsolatedFeaturesCheckbox.isSelected());

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
                    statusLabel.setText("Trace completed.");
                    enableButtonInteraction();
                    progressIndicator.setVisible(false);
                    enableButtonInteraction();
                  });
                }
              });
            }
          } else {
            statusLabel.setText("Trace failed.");
            progressIndicator.setVisible(false);
            new Alert(Alert.AlertType.ERROR, "Trace result not a utility element.").show();
            enableButtonInteraction();
          }

        } catch (Exception e) {
          statusLabel.setText("Trace failed.");
          progressIndicator.setVisible(false);
          // Note: this sample server may return a generic message in some circumstances when incompatible trace
          // parameters are specified
          if (e.getMessage().contains("-2147208935")) {
            new Alert(Alert.AlertType.ERROR, "Cannot run trace with the provided parameters.").show();
          } else {
            new Alert(Alert.AlertType.ERROR, "Error running utility network trace.").show();
          }
          enableButtonInteraction();
        }
      });

    } catch (Exception e) {
      new Alert(Alert.AlertType.ERROR, "Error performing valve isolation trace.").show();
    }
  }

  private void enableButtonInteraction() {
    traceButton.setDisable(false);
    categorySelectionComboBox.setDisable(false);
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
