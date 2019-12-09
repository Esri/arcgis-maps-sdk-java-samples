/*
 * Copyright 2019 Esri.
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
package com.esri.samples.feature_layers;

import java.nio.charset.StandardCharsets;

import com.esri.arcgisruntime.arcgisservices.LabelDefinition;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.SubtypeFeatureLayer;
import com.esri.arcgisruntime.layers.SubtypeSublayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.Renderer;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;
import com.esri.arcgisruntime.symbology.Symbol;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import org.apache.commons.io.IOUtils;

public class DisplaySubtypeFeatureLayerController {
  
  @FXML private MapView mapView;
  @FXML private Label currentMapScaleLabel;
  @FXML private Label minScaleLabel;
  @FXML private CheckBox sublayerVisibilityCheckbox;
  private SubtypeSublayer sublayer;
  private Renderer originalRenderer;
  private Renderer alternativeRenderer;
  
  public void initialize() {

    try {

      // create a map with streets night vector basemap and add it to the map view
      ArcGISMap map = new ArcGISMap();
      map.setBasemap(Basemap.createStreetsNightVector());
      mapView.setMap(map);
      // display the current map scale 
      mapView.addMapScaleChangedListener(mapScaleChangedEvent ->
        currentMapScaleLabel.setText("Current map scale: 1:" + Math.round(mapView.getMapScale())));
      
      // set the viewpoint to Naperville, Illinois
      Viewpoint initialViewpoint = new Viewpoint(new Envelope(-9812691.11079696, 5128687.20710657,
        -9812377.9447607, 5128865.36767282, SpatialReferences.getWebMercator()));
      map.setInitialViewpoint(initialViewpoint);

      // create a subtype feature layer from the service feature table, and add it to the map
      ServiceFeatureTable serviceFeatureTable = new ServiceFeatureTable("https://sampleserver7.arcgisonline" +
        ".com/arcgis/rest/services/UtilityNetwork/NapervilleElectric/FeatureServer/100");
      SubtypeFeatureLayer subtypeFeatureLayer = new SubtypeFeatureLayer(serviceFeatureTable);
      map.getOperationalLayers().add(subtypeFeatureLayer);
      
      // access the json required for sublayer label definitions
      final String json = IOUtils.toString(getClass().getResourceAsStream("/label_definition.json"), StandardCharsets.UTF_8);

      // load the subtype feature layer
      subtypeFeatureLayer.loadAsync();
      subtypeFeatureLayer.addDoneLoadingListener(() -> {
        
        // get the Street Light sublayer and define its labels
        sublayer = subtypeFeatureLayer.getSublayerWithSubtypeName("Street Light");
        sublayer.setLabelsEnabled(true);
        sublayer.getLabelDefinitions().add(LabelDefinition.fromJson(json));

        // get the original renderer of the sublayer (white and black circular icon)
        originalRenderer = sublayer.getRenderer();
        // create a custom renderer for the sublayer (light pink diamond symbol)
        Symbol symbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.DIAMOND, 0xfff58f84, 20);
        alternativeRenderer = new SimpleRenderer(symbol);
      });

    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
    }
  }

  /**
   * Sets the minimum scale of the labels for the sublayer.
   */
  @FXML
  private void handleMinScaleButtonClicked() {
    sublayer.setMinScale(mapView.getMapScale());
    minScaleLabel.setText("Sublayer min scale: 1:" + Math.round(sublayer.getMinScale()));
  }

  /**
   * Sets the visibility of the sublayer.
   */
  @FXML
  private void handleSublayerVisibility() {
      sublayer.setVisible(sublayerVisibilityCheckbox.isSelected());
  }
  
  /**
   * Sets the renderer of the sublayer to its original format (a white and black circular icon). 
   */
  @FXML
  private void handleOriginalRendererButtonClicked() {
    sublayer.setRenderer(originalRenderer);
  }

  /**
   * Sets the renderer of the sublayer to that of a pink diamond symbol.
   */
  @FXML
  private void handleAlternativeRendererButtonClicked() {
    sublayer.setRenderer(alternativeRenderer);
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
