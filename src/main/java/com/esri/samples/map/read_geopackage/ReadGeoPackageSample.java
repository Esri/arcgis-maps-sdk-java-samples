/*
 * Copyright 2018 Esri.
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

package com.esri.samples.map.read_geopackage;

import java.io.File;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import com.esri.arcgisruntime.data.GeoPackage;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.layers.Layer;
import com.esri.arcgisruntime.layers.RasterLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.MapView;

public class ReadGeoPackageSample extends Application {

  private MapView mapView;

  @Override
  public void start(Stage stage) throws Exception {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);
      scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

      // set title, size, and add scene to stage
      stage.setTitle("Read GeoPackage Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // create a map and add it to the map view
      final ArcGISMap map = new ArcGISMap(Basemap.Type.STREETS, 39.7294, -104.8319, 11);
      mapView = new MapView();
      mapView.setMap(map);

      // create two list views, one showing the layers in the map,
      // the other, showing the layers in the geoPackage not yet added to the map
      ListView<Layer> mapLayers = new ListView<>();
      ListView<Layer> geoPackageLayers = new ListView<>();

      // create labels for the lists
      Label mapLayersLabel = new Label("Map layers");
      Label geoPackageLayersLabel = new Label("GeoPackage layers (not in the map)");
      mapLayersLabel.getStyleClass().add("panel-label");
      geoPackageLayersLabel.getStyleClass().add("panel-label");

      // create a control panel
      VBox vBoxControl = new VBox(6);
      vBoxControl.setMaxSize(250, 260);
      vBoxControl.getStyleClass().add("panel-region");

      // add labels and lists to the control panel
      vBoxControl.getChildren().addAll(mapLayersLabel, mapLayers, geoPackageLayersLabel, geoPackageLayers);

      // create a cell factory to show the layer names in the list view
      Callback<ListView<Layer>, ListCell<Layer>> cellFactory = list -> new ListCell<Layer>() {

        @Override
        protected void updateItem(Layer layer, boolean bln) {

          super.updateItem(layer, bln);
          if (layer != null) {
            if (layer instanceof FeatureLayer) {
              FeatureLayer featureLayer = (FeatureLayer) layer;
              setText(featureLayer.getName());
            } else if (layer instanceof RasterLayer) {
              RasterLayer rasterLayer = (RasterLayer) layer;
              // use the raster file name if the raster layer name is empty
              String path = rasterLayer.getRaster().getPath();
              setText(rasterLayer.getName().isEmpty() ? path.substring(path.lastIndexOf('/') + 1) : rasterLayer.getName());
            }
          } else {
            setText(null);
          }
        }

      };

      mapLayers.setCellFactory(cellFactory);
      geoPackageLayers.setCellFactory(cellFactory);

      // when you click on a layer in the geopackage layers list view, add it to the map
      geoPackageLayers.setOnMouseClicked(e -> {
        if (e.isStillSincePress() && e.getButton() == MouseButton.PRIMARY) {
          // get selected layer
          Layer layer = geoPackageLayers.getSelectionModel().getSelectedItem();
          if (layer != null) {
            // add it to the map and the top of the map layers list
            map.getOperationalLayers().add(layer);
            mapLayers.getItems().add(0, layer);
            // remove it from the geoPackage layers list
            geoPackageLayers.getItems().remove(layer);
          }
        }
      });

      // when you click on a layer in the map layers list view, remove it from the map
      mapLayers.setOnMouseClicked(e -> {
        if (e.isStillSincePress() && e.getButton() == MouseButton.PRIMARY) {
          // get selected layer
          Layer layer = mapLayers.getSelectionModel().getSelectedItem();
          if (layer != null) {
            // remove it from the map and the map layers list
            map.getOperationalLayers().remove(layer);
            mapLayers.getItems().remove(layer);
            // add it back to the geoPackage layers list
            geoPackageLayers.getItems().add(layer);
          }
        }
      });

      // read the raster and feature layers from the geoPackage and show them in the list view
      File geoPackageFile = new File("./samples-data/auroraCO/AuroraCO.gpkg");
      GeoPackage geoPackage = new GeoPackage(geoPackageFile.getAbsolutePath());
      geoPackage.loadAsync();
      geoPackage.addDoneLoadingListener(() -> {
        geoPackage.getGeoPackageRasters().forEach(raster -> {
          RasterLayer rasterLayer = new RasterLayer(raster);
          rasterLayer.loadAsync();
          // make the raster layer semi-transparent so we can see layers below it
          rasterLayer.setOpacity(0.5f);
          geoPackageLayers.getItems().add(rasterLayer);
        });
        geoPackage.getGeoPackageFeatureTables().forEach(table -> {
          FeatureLayer featureLayer = new FeatureLayer(table);
          featureLayer.loadAsync();
          geoPackageLayers.getItems().add(featureLayer);
        });
      });

      // add the map view and control box to stack pane
      stackPane.getChildren().addAll(mapView, vBoxControl);
      StackPane.setAlignment(vBoxControl, Pos.TOP_LEFT);
      StackPane.setMargin(vBoxControl, new Insets(10, 0, 0, 10));
    } catch (Exception e) {
      // on any error, display the stack trace
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
