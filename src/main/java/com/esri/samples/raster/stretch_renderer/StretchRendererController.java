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
import java.util.Arrays;
import java.util.Collections;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
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
  @FXML private Slider mmMinSlider;
  @FXML private Slider mmMaxSlider;
  @FXML private Slider pcMinSlider;
  @FXML private Slider pcMaxSlider;
  @FXML private SimpleBooleanProperty minMaxActive;
  @FXML private SimpleBooleanProperty stdDeviationActive;
  @FXML private SimpleBooleanProperty percentClipActive;

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

    // disable controls for properties that don't apply to the current stretch type
    minMaxActive.bind(Bindings.createBooleanBinding(() -> "MinMax".equals(stretchTypeComboBox
        .getSelectionModel().getSelectedItem()), stretchTypeComboBox.getSelectionModel().selectedItemProperty()));
    stdDeviationActive.bind(Bindings.createBooleanBinding(() -> "StdDeviation".equals(stretchTypeComboBox
        .getSelectionModel().getSelectedItem()), stretchTypeComboBox.getSelectionModel().selectedItemProperty()));
    percentClipActive.bind(Bindings.createBooleanBinding(() -> "PercentClip".equals(stretchTypeComboBox
        .getSelectionModel().getSelectedItem()), stretchTypeComboBox.getSelectionModel().selectedItemProperty()));

    // round slider values to nearest integer
    factorSlider.valueProperty().addListener(o -> factorSlider.setValue(Math.round(factorSlider.getValue())));
    mmMinSlider.valueProperty().addListener(o -> mmMinSlider.setValue(Math.round(mmMinSlider.getValue())));
    mmMaxSlider.valueProperty().addListener(o -> mmMaxSlider.setValue(Math.round(mmMaxSlider.getValue())));
    pcMinSlider.valueProperty().addListener(o -> pcMinSlider.setValue(Math.round(pcMinSlider.getValue())));
    pcMaxSlider.valueProperty().addListener(o -> pcMaxSlider.setValue(Math.round(pcMaxSlider.getValue())));

    // add listeners to sliders to update rendering
    ChangeListener<Boolean> sliderChangingListener = (obs, wasChanging, isChanging) -> {
      if (!isChanging) updateRenderer();
    };
    factorSlider.valueChangingProperty().addListener(sliderChangingListener);
    mmMinSlider.valueChangingProperty().addListener(sliderChangingListener);
    mmMaxSlider.valueChangingProperty().addListener(sliderChangingListener);
    pcMinSlider.valueChangingProperty().addListener(sliderChangingListener);
    pcMaxSlider.valueChangingProperty().addListener(sliderChangingListener);

    // constrain min + max <= 100 when type is Percent Clip
    pcMaxSlider.maxProperty().bind(Bindings.createDoubleBinding(() -> "PercentClip".equals(stretchTypeComboBox
            .getSelectionModel().getSelectedItem()) ? 100 - pcMinSlider.getValue() : 100, pcMinSlider.valueProperty(),
        stretchTypeComboBox.getSelectionModel().selectedItemProperty()));

    updateRenderer();
  }

  /**
   * Updates the raster layer renderer according to the chosen property values.
   */
  public void updateRenderer() {

    StretchParameters stretchParameters;
    switch (stretchTypeComboBox.getSelectionModel().getSelectedItem()) {
      case "MinMax": stretchParameters = new MinMaxStretchParameters(Collections.singletonList(mmMinSlider.getValue()),
          Collections.singletonList(mmMaxSlider.getValue()));
        break;
      case "PercentClip":
        stretchParameters = new PercentClipStretchParameters(pcMinSlider.getValue(), pcMaxSlider.getValue());
        break;
      default:
        stretchParameters = new StandardDeviationStretchParameters(factorSlider.getValue());
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
