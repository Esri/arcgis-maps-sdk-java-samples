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

package com.esri.samples.raster.stretch_renderer;

import java.io.File;
import java.util.Arrays;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;

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

  @FXML private MapView mapView;
  @FXML private ComboBox<String> stretchTypeComboBox;
  @FXML private Slider factorSlider;
  @FXML private Slider minSlider;
  @FXML private Slider maxSlider;

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

    // set defaults
    stretchTypeComboBox.getItems().setAll("MinMax", "PercentClip", "StdDeviation");
    stretchTypeComboBox.getSelectionModel().select("MinMax");

    // add listeners
    factorSlider.valueChangingProperty().addListener(o -> {
      if (!factorSlider.isValueChanging()) {
        updateRenderer();
      }
    });
    minSlider.valueChangingProperty().addListener(o -> {
      if (!minSlider.isValueChanging()) {
        updateRenderer();
      }
    });
    maxSlider.valueChangingProperty().addListener(o -> {
      if (!maxSlider.isValueChanging()) {
        updateRenderer();
      }
    });

    updateRenderer();
  }

  /**
   * Updates the raster layer renderer according to the chosen property values.
   */
  public void updateRenderer() {

    minSlider.setDisable(false);
    maxSlider.setDisable(false);
    factorSlider.setDisable(true);

    //[DocRef: Name=Working_With_Maps-Add_Raster_Data-Stretch_Renderer-Java
    double min = minSlider.getValue();
    double max = maxSlider.getValue();

    StretchParameters stretchParameters;
    switch (stretchTypeComboBox.getSelectionModel().getSelectedItem()) {
      case "MinMax":
        stretchParameters = new MinMaxStretchParameters(Arrays.asList(min), Arrays.asList(max));
        break;
      case "PercentClip":
        stretchParameters = new PercentClipStretchParameters(min, max);
        break;
      default:
        minSlider.setDisable(true);
        maxSlider.setDisable(true);
        factorSlider.setDisable(false);
        stretchParameters = new StandardDeviationStretchParameters(factorSlider.getValue());
    }

    // create blend renderer
    StretchRenderer stretchRenderer = new StretchRenderer(stretchParameters, null, true, null);

    rasterLayer.setRasterRenderer(stretchRenderer);
    //[DocRef: Name=Working_With_Maps-Add_Raster_Data-Stretch_Renderer-Java
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
