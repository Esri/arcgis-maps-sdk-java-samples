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

package com.esri.samples.colormap_renderer;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.layers.RasterLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.raster.ColormapRenderer;
import com.esri.arcgisruntime.raster.Raster;

public class ColormapRendererSample extends Application {

  private MapView mapView;
  private RasterLayer rasterLayer; // keeps loadable in scope to avoid garbage collection

  @Override
  public void start(Stage stage) {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);

      // set title, size, and add scene to stage
      stage.setTitle("Colormap Renderer Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // create a raster from a local raster file
      Raster raster = new Raster(new File(System.getProperty("data.dir"), "./samples-data/raster/ShastaBW.tif").getAbsolutePath());

      // create a raster layer
      rasterLayer = new RasterLayer(raster);

      // create a Map with imagery basemap
      ArcGISMap map = new ArcGISMap(Basemap.createImagery());

      // add the map to a map view
      mapView = new MapView();
      mapView.setMap(map);

      // add the raster as an operational layer
      map.getOperationalLayers().add(rasterLayer);

      // create a color map where values 0-149 are red (0xFFFF0000) and 150-250 are yellow (0xFFFFFF00)
      List<Integer> colors = IntStream.range(0, 250)
          .boxed()
          .map(i -> i < 150 ? 0xFFFF0000 : 0xFFFFFF00)
          .collect(Collectors.toList());

      // create a colormap renderer
      ColormapRenderer colormapRenderer = new ColormapRenderer(colors);

      // set the colormap renderer on the raster layer
      rasterLayer.setRasterRenderer(colormapRenderer);

      // set viewpoint on the raster
      rasterLayer.addDoneLoadingListener(() -> {
        if (map.getLoadStatus() == LoadStatus.LOADED) {
          mapView.setViewpointGeometryAsync(rasterLayer.getFullExtent(), 150);
        } else {
          Alert alert = new Alert(Alert.AlertType.ERROR, "Raster Layer Failed to Load!");
          alert.show();
        }
      });

      // add the map view to stack pane
      stackPane.getChildren().addAll(mapView);
    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
    }
  }

  /**
   * Stops and releases all resources used in application.
   */
  @Override
  public void stop() {

    if (mapView != null) {
      mapView.dispose();
    }
  }

  /**
   * Opens and runs application.
   *
   * @param args arguments passed to this application
   */
  public static void main(String[] args) {

    Application.launch(args);
  }

}
