/*
 * Copyright 2016 Esri.
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

package com.esri.samples.raster.rgb_renderer;

import java.io.File;
import java.util.Arrays;

import com.esri.arcgisruntime.layers.RasterLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.raster.*;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;

public class RgbRendererController {

  @FXML private MapView mapView;
  @FXML private ComboBox<String> stretchTypeComboBox;
  @FXML private Spinner<Integer> factorSpinner;
  @FXML private Spinner<Integer> minPercentSpinner;
  @FXML private Spinner<Integer> maxPercentSpinner;
  @FXML private Spinner<Integer> minRedSpinner;
  @FXML private Spinner<Integer> minGreenSpinner;
  @FXML private Spinner<Integer> minBlueSpinner;
  @FXML private Spinner<Integer> maxRedSpinner;
  @FXML private Spinner<Integer> maxGreenSpinner;
  @FXML private Spinner<Integer> maxBlueSpinner;

  private RasterLayer rasterLayer;

  public void initialize() {

    // create rasters
    Raster raster = new Raster(new File("./samples-data/raster/Shasta.tif").getAbsolutePath());

    // create a raster layer
    rasterLayer = new RasterLayer(raster);

    // create a basemap from the raster layer
    Basemap basemap = new Basemap(rasterLayer);
    ArcGISMap map = new ArcGISMap(basemap);

    // set the map to the map view
    mapView.setMap(map);

    // set defaults
    stretchTypeComboBox.getItems().setAll("MinMax", "PercentClip", "StdDeviation");
    stretchTypeComboBox.getSelectionModel().select("MinMax");

    // bindings
    BooleanBinding minMaxStretchBinding = Bindings.createBooleanBinding(() ->
        !"MinMax".equals(stretchTypeComboBox.getSelectionModel().getSelectedItem()), stretchTypeComboBox
        .getSelectionModel().selectedItemProperty());
    minRedSpinner.disableProperty().bind(minMaxStretchBinding);
    minGreenSpinner.disableProperty().bind(minMaxStretchBinding);
    minBlueSpinner.disableProperty().bind(minMaxStretchBinding);
    maxRedSpinner.disableProperty().bind(minMaxStretchBinding);
    maxGreenSpinner.disableProperty().bind(minMaxStretchBinding);
    maxBlueSpinner.disableProperty().bind(minMaxStretchBinding);
    BooleanBinding percentClipStretchBinding = Bindings.createBooleanBinding(() ->
        !"PercentClip".equals(stretchTypeComboBox.getSelectionModel().getSelectedItem()), stretchTypeComboBox
        .getSelectionModel().selectedItemProperty());
    minPercentSpinner.disableProperty().bind(percentClipStretchBinding);
    maxPercentSpinner.disableProperty().bind(percentClipStretchBinding);
    BooleanBinding stdDeviationStretchBinding = Bindings.createBooleanBinding(() ->
        !"StdDeviation".equals(stretchTypeComboBox.getSelectionModel().getSelectedItem()), stretchTypeComboBox
        .getSelectionModel().selectedItemProperty());
   factorSpinner.disableProperty().bind(stdDeviationStretchBinding);

    // add listeners
    factorSpinner.valueProperty().addListener(o -> updateRenderer());
    minPercentSpinner.valueProperty().addListener(o -> updateRenderer());
    maxPercentSpinner.valueProperty().addListener(o -> updateRenderer());
    minRedSpinner.valueProperty().addListener(o -> updateRenderer());
    minGreenSpinner.valueProperty().addListener(o -> updateRenderer());
    minBlueSpinner.valueProperty().addListener(o -> updateRenderer());
    maxRedSpinner.valueProperty().addListener(o -> updateRenderer());
    maxGreenSpinner.valueProperty().addListener(o -> updateRenderer());
    maxBlueSpinner.valueProperty().addListener(o -> updateRenderer());

    updateRenderer();
  }

  /**
   * Updates the raster layer renderer according to the chosen property values.
   */
  public void updateRenderer() {

    double minP = minPercentSpinner.getValue();
    double maxP = maxPercentSpinner.getValue();
    double minR = minRedSpinner.getValue();
    double minG = minGreenSpinner.getValue();
    double minB = minBlueSpinner.getValue();
    double maxR = maxRedSpinner.getValue();
    double maxG = maxGreenSpinner.getValue();
    double maxB = maxBlueSpinner.getValue();

    StretchParameters stretchParameters;
    switch (stretchTypeComboBox.getSelectionModel().getSelectedItem()) {
      case "MinMax": stretchParameters = new MinMaxStretchParameters(Arrays.asList(minR, minG, minB), Arrays.asList
          (maxR, maxG, maxB)); break;
      case "PercentClip":stretchParameters = new PercentClipStretchParameters(minP, maxP); break;
      default:
        stretchParameters = new StandardDeviationStretchParameters(factorSpinner.getValue());
    }

    // create rgb renderer
    RGBRenderer rgbRenderer = new RGBRenderer(stretchParameters, Arrays.asList(0, 1, 2), null, true);

    rasterLayer.setRasterRenderer(rgbRenderer);
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
