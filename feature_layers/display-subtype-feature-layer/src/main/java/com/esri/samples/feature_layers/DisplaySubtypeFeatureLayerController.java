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

import java.io.File;
import java.util.Objects;
import java.util.Scanner;

import com.esri.arcgisruntime.arcgisservices.LabelDefinition;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.SubtypeFeatureLayer;
import com.esri.arcgisruntime.layers.SubtypeSublayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.symbology.Renderer;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;
import com.esri.arcgisruntime.symbology.Symbol;
import javafx.fxml.FXML;

import com.esri.arcgisruntime.mapping.view.MapView;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;

public class DisplaySubtypeFeatureLayerController {

  @FXML
  private ToggleButton toggleRendererButton;
  @FXML
  private MapView mapView;
  @FXML
  private Label currentMapScaleLabel;
  @FXML
  private Label minScaleLabel;
  @FXML
  private CheckBox checkBox;
  private SubtypeSublayer sublayer;
  private Renderer originalRenderer;


  public void initialize() {

    try {

      // create a map with streets night vector basemap and add it to the map view
      ArcGISMap map = new ArcGISMap();
      map.setBasemap(Basemap.createStreetsNightVector());
      mapView.setMap(map);

      mapView.addMapScaleChangedListener(mapScaleChangedEvent ->
        currentMapScaleLabel.setText("Current Map Scale: 1:" + Math.round(mapView.getMapScale())));

      // set the viewpoint to Naperville
      Viewpoint initialViewpoint = new Viewpoint(new Envelope(-9812691.11079696, 5128687.20710657,
        -9812377.9447607, 5128865.36767282, SpatialReferences.getWebMercator()));
      map.setInitialViewpoint(initialViewpoint);

      // access the json required for the sublayer label definitions
      File jsonFile = new File(System.getProperty("data.dir"),
        Objects.requireNonNull(getClass().getClassLoader().getResource("label_definition.json")).getFile());
      final String json;
      // read in the complete file as a string
      try (Scanner scanner = new Scanner(jsonFile)) {
        json = scanner.useDelimiter("\\A").next();
      }

      // create a subtype feature layer from the service feature table, and add it to the map
      final String serviceFeatureTableUrl = "https://sampleserver7.arcgisonline" +
        ".com/arcgis/rest/services/UtilityNetwork/NapervilleElectric/FeatureServer/100";
      ServiceFeatureTable serviceFeatureTable = new ServiceFeatureTable(serviceFeatureTableUrl);
      SubtypeFeatureLayer subtypeFeatureLayer = new SubtypeFeatureLayer(serviceFeatureTable);
      map.getOperationalLayers().add(subtypeFeatureLayer);

      subtypeFeatureLayer.loadAsync();
      subtypeFeatureLayer.addDoneLoadingListener(() -> {

        // get the Street Light sublayer and define its labels
        sublayer = subtypeFeatureLayer.getSublayerWithSubtypeName("Street Light");
        sublayer.setLabelsEnabled(true);
        sublayer.getLabelDefinitions().add(LabelDefinition.fromJson(json));

        // get the original renderer of the sublayer
        originalRenderer = sublayer.getRenderer();
      });

    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
    }
  }

  /**
   * Sets the visibility of the sublayer.
   */
  @FXML
  private void controlSublayerVisibility() {
    sublayer.setVisible(checkBox.isSelected());
  }
  
  /**
   * Switches between a renderer with a pink diamond symbol and the sublayer's original renderer.
   */
  @FXML
  private void handleToggleRendererButtonClicked() {
    if (toggleRendererButton.isSelected()) {
      Symbol symbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.DIAMOND, 0xfff58f84, 20);
      Renderer sublayerRenderer = new SimpleRenderer(symbol);
      sublayer.setRenderer(sublayerRenderer);
    } else {
      sublayer.setRenderer(originalRenderer);
    }
  }

  /**
   * Sets the minimum scale of the labels for the sublayer.
   */
  @FXML
  private void handleMinScaleButtonClicked() {
    sublayer.setMinScale(mapView.getMapScale());
    minScaleLabel.setText("Sublayer labelling min scale: 1:" + Math.round(sublayer.getMinScale()));
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
