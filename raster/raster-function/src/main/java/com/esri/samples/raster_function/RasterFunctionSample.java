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

package com.esri.samples.raster_function;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.layers.RasterLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.raster.ImageServiceRaster;
import com.esri.arcgisruntime.raster.Raster;
import com.esri.arcgisruntime.raster.RasterFunction;
import com.esri.arcgisruntime.raster.RasterFunctionArguments;

public class RasterFunctionSample extends Application {

  private ImageServiceRaster imageServiceRaster; // keep loadables in scope to avoid garbage collection
  private MapView mapView;

  @Override
  public void start(Stage stage) {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);

      // set title, size, and add scene to stage
      stage.setTitle("Raster Function Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // authentication with an API key or named user is required to access basemaps and other location services
      String yourAPIKey = System.getProperty("apiKey");
      ArcGISRuntimeEnvironment.setApiKey(yourAPIKey);

      // create a map with a basemap style
      ArcGISMap map = new ArcGISMap(BasemapStyle.ARCGIS_DARK_GRAY);

      // create a map view and set its map
      mapView = new MapView();
      mapView.setMap(map);

      // create an image service raster from an online raster service
      imageServiceRaster = new ImageServiceRaster("https://sampleserver6.arcgisonline" +
          ".com/arcgis/rest/services/NLCDLandCover2001/ImageServer");
      imageServiceRaster.loadAsync();
      imageServiceRaster.addDoneLoadingListener(() -> {

          if (imageServiceRaster.getLoadStatus() == LoadStatus.LOADED) {
            // create raster function from local json file
            File jsonFile = new File(System.getProperty("data.dir"), "./samples-data/raster/hillshade_simplified.json");
            try (Scanner scanner = new Scanner(jsonFile)) {
              // read in the complete file as a string
              String json = scanner.useDelimiter("\\A").next();
              RasterFunction rasterFunction = RasterFunction.fromJson(json);
              RasterFunctionArguments arguments = rasterFunction.getArguments();
              // apply the raster function
              arguments.setRaster(arguments.getRasterNames().get(0), imageServiceRaster);
              // create a new raster from the function definition
              Raster raster = new Raster(rasterFunction);
              // create raster layer and add to map as operational layer
              RasterLayer hillshadeLayer = new RasterLayer(raster);
              // add the hillshade raster layer to the map
              map.getOperationalLayers().add(hillshadeLayer);
              hillshadeLayer.addDoneLoadingListener(() -> {
                if (hillshadeLayer.getLoadStatus() == LoadStatus.LOADED) {
                  // set viewpoint on the raster
                  mapView.setViewpointGeometryAsync(hillshadeLayer.getFullExtent(), 150);
                } else {
                  Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to load the hillshade raster layer");
                  alert.show();
                }
              });
            } catch (FileNotFoundException e) {
              Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to locate raster function json");
              alert.show();
            }
          } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to load image service raster");
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
