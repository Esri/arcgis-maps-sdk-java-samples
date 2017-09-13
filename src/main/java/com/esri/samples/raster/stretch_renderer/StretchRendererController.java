/*
 * Copyright 2017 Esri.
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

package com.esri.samples.raster.stretch_renderer;

import java.io.File;
import java.util.Collections;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.layout.HBox;

import org.controlsfx.control.RangeSlider;

import com.esri.arcgisruntime.layers.RasterLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.raster.MinMaxStretchParameters;
import com.esri.arcgisruntime.raster.PercentClipStretchParameters;
import com.esri.arcgisruntime.raster.Raster;
import com.esri.arcgisruntime.raster.StandardDeviationStretchParameters;
import com.esri.arcgisruntime.raster.StretchParameters;
import com.esri.arcgisruntime.raster.StretchRenderer;

public class StretchRendererController {

  @FXML private HBox stdDeviationGroup;
  @FXML private HBox minMaxGroup;
  @FXML private HBox percentClipGroup;
  @FXML private MapView mapView;
  @FXML private ComboBox<String> stretchTypeComboBox;
  @FXML private Spinner<Integer> factorSpinner;
  @FXML private Slider minPercentSlider;
  @FXML private Slider maxPercentSlider;
  @FXML private RangeSlider minMaxSlider;

  private RasterLayer rasterLayer;

  public void initialize() {

    // create raster
    Raster raster = new Raster(new File("./samples-data/raster/ShastaBW.tif").getAbsolutePath());

    // create a raster layer
    rasterLayer = new RasterLayer(raster);

    // create a basemap from the raster layer
    Basemap basemap = new Basemap(rasterLayer);
    ArcGISMap map = new ArcGISMap(basemap);

    // set the map to the map view
    mapView.setMap(map);

    // set stretch types
    stretchTypeComboBox.getItems().addAll("Min Max", "Percent Clip", "Std Deviation");

    // hide options based on selected stretch type
    stretchTypeComboBox.getSelectionModel().selectedItemProperty().addListener(e -> {
      String type = stretchTypeComboBox.getSelectionModel().getSelectedItem();
      minMaxGroup.setVisible("Min Max".equals(type));
      percentClipGroup.setVisible("Percent Clip".equals(type));
      stdDeviationGroup.setVisible("Std Deviation".equals(type));
    });

    // set up sliders to match constraint min + max <= 100
    minPercentSlider.valueProperty().addListener(e -> {
      if (minPercentSlider.getValue() + maxPercentSlider.getValue() > 100) {
        maxPercentSlider.setValue(100 - minPercentSlider.getValue());
      }
    });
    maxPercentSlider.valueProperty().addListener(e -> {
      if (minPercentSlider.getValue() + maxPercentSlider.getValue() > 100) {
        minPercentSlider.setValue(100 - maxPercentSlider.getValue());
      }
    });

    stretchTypeComboBox.getSelectionModel().select(0);

    updateRenderer();
  }

  /**
   * Updates the raster layer renderer according to the chosen property values.
   */
  public void updateRenderer() {

    StretchParameters stretchParameters;
    switch (stretchTypeComboBox.getSelectionModel().getSelectedItem()) {
      case "Min Max":
        stretchParameters = new MinMaxStretchParameters(Collections.singletonList(minMaxSlider.getLowValue()),
            Collections.singletonList(minMaxSlider.getHighValue()));
        break;
      case "Percent Clip":
        stretchParameters = new PercentClipStretchParameters(minPercentSlider.getValue(), maxPercentSlider.getValue());
        break;
      default:
        stretchParameters = new StandardDeviationStretchParameters(factorSpinner.getValue());
    }

    // create blend renderer
    StretchRenderer stretchRenderer = new StretchRenderer(stretchParameters, null, true, null);
    rasterLayer.setRasterRenderer(stretchRenderer);
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
