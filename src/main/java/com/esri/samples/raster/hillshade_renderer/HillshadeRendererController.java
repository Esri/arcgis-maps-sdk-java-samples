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

package com.esri.samples.raster.hillshade_renderer;

import java.io.File;
import java.util.Arrays;

import com.esri.arcgisruntime.layers.RasterLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.raster.*;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;

public class HillshadeRendererController {

  public MapView mapView;
  public ComboBox<SlopeType> slopeTypeComboBox;
  public ComboBox<ColorRamp.PresetType> colorRampComboBox;
  public Slider azimuthSlider;
  public Slider altitudeSlider;

  private RasterLayer rasterLayer;

  public void initialize() {

    // create rasters
    Raster raster = new Raster(new File("./samples-data/raster/srtm.tiff").getAbsolutePath());

    // create a raster layer
    rasterLayer = new RasterLayer(raster);

    // create a basemap from the raster layer
    Basemap basemap = new Basemap(rasterLayer);
    ArcGISMap map = new ArcGISMap(basemap);

    // set the map to the map view
    mapView.setMap(map);

    // set defaults
    slopeTypeComboBox.getItems().setAll(SlopeType.values());
    slopeTypeComboBox.getSelectionModel().select(SlopeType.NONE);

    // add listeners
    altitudeSlider.valueChangingProperty().addListener(o -> {
      if (!altitudeSlider.isValueChanging()) {
        updateRenderer();
      }
    });
    azimuthSlider.valueChangingProperty().addListener(o -> {
      if (!azimuthSlider.isValueChanging()) {
        updateRenderer();
      }
    });

    updateRenderer();
  }

  /**
   * Updates the raster layer renderer according to the chosen property values.
   */
  public void updateRenderer() {

    // create blend renderer
    HillshadeRenderer hillshadeRenderer = new HillshadeRenderer(altitudeSlider.getValue(), azimuthSlider.getValue(),
        0.000016, slopeTypeComboBox.getSelectionModel().getSelectedItem(), 1, 1, 8);

    rasterLayer.setRasterRenderer(hillshadeRenderer);
  }

  void terminate() {

    if (mapView != null) {
      mapView.dispose();
    }
  }

}
