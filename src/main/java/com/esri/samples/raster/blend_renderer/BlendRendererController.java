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

package com.esri.samples.raster.blend_renderer;

import java.io.File;
import java.util.Arrays;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;

import com.esri.arcgisruntime.layers.RasterLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.raster.BlendRenderer;
import com.esri.arcgisruntime.raster.ColorRamp;
import com.esri.arcgisruntime.raster.Raster;
import com.esri.arcgisruntime.raster.SlopeType;

public class BlendRendererController {

  public MapView mapView;
  public ComboBox<SlopeType> slopeTypeComboBox;
  public ComboBox<ColorRamp.PresetType> colorRampComboBox;
  public Slider azimuthSlider;
  public Slider altitudeSlider;

  private RasterLayer rasterLayer;
  private Raster imageryRaster;
  private Raster elevationRaster;

  public void initialize() {

    // create rasters
    imageryRaster = new Raster(new File("./samples-data/raster/Shasta.tif").getAbsolutePath());
    elevationRaster = new Raster(new File("./samples-data/raster/Shasta_Elevation.tif").getAbsolutePath());

    // create a raster layer
    rasterLayer = new RasterLayer(imageryRaster);

    // create a basemap from the raster layer
    Basemap basemap = new Basemap(rasterLayer);
    ArcGISMap map = new ArcGISMap(basemap);

    // set the map to the map view
    mapView.setMap(map);

    // set defaults
    colorRampComboBox.getItems().setAll(ColorRamp.PresetType.values());
    colorRampComboBox.getSelectionModel().select(ColorRamp.PresetType.NONE);
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

    ColorRamp colorRamp = colorRampComboBox.getSelectionModel().getSelectedItem() != ColorRamp.PresetType.NONE?
        new ColorRamp(colorRampComboBox.getSelectionModel().getSelectedItem(), 800) : null;

    // if color ramp is not NONE, color the hillshade elevation raster instead of using satellite imagery raster color
    rasterLayer = colorRamp != null ? new RasterLayer(elevationRaster) : new RasterLayer(imageryRaster);

    mapView.getMap().setBasemap(new Basemap(rasterLayer));

    // create blend renderer
    BlendRenderer blendRenderer = new BlendRenderer(elevationRaster, Arrays.asList(9.0), Arrays.asList(255.0), null,
        null, null, null, colorRamp, altitudeSlider.getValue(), azimuthSlider
        .getValue(), 1, slopeTypeComboBox.getSelectionModel().getSelectedItem(), 1, 1, 8);

    rasterLayer.setRasterRenderer(blendRenderer);
  }

  void terminate() {

    if (mapView != null) {
      mapView.dispose();
    }
  }

}
