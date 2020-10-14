/*
 * Copyright 2020 Esri.
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

package com.esri.samples.apply_mosaic_rule_to_rasters;

import java.util.Arrays;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Color;
import javafx.scene.control.Label;

import com.esri.arcgisruntime.layers.RasterLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.DrawStatus;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.raster.ImageServiceRaster;
import com.esri.arcgisruntime.raster.MosaicRule;
import com.esri.arcgisruntime.raster.MosaicMethod;
import com.esri.arcgisruntime.raster.MosaicOperation;

public class ApplyMosaicRuleToRastersSample extends Application {

  private MapView mapView;
  private MosaicRule mosaicRule;
  private RasterLayer rasterLayer; // keep loadable in scope to avoid garbage collection

  @Override
  public void start(Stage stage) {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);

      // set title, size, and add scene to stage
      stage.setTitle("Apply Mosaic Rule To Rasters Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // add a progress indicator to show the scene is loading
      ProgressIndicator progressIndicator = new ProgressIndicator(ProgressIndicator.INDETERMINATE_PROGRESS);

      // create a label
      Label mosaicRuleLabel = new Label("Choose a mosaic rule: ");
      mosaicRuleLabel.setTextFill(Color.WHITE);

      // create a combo box
      ComboBox<String> comboBox = new ComboBox<>();
      comboBox.setMaxWidth(Double.MAX_VALUE);

      // set up the control panel UI
      VBox controlsVBox = new VBox(6);
      controlsVBox.setBackground(new Background(new BackgroundFill(Paint.valueOf("rgba(0, 0, 0, 0.3)"),
        CornerRadii.EMPTY, Insets.EMPTY)));
      controlsVBox.setPadding(new Insets(10.0));
      controlsVBox.setMaxSize(260, 50);
      controlsVBox.setVisible(false);
      // add the label and combo box to the control panel
      controlsVBox.getChildren().addAll(mosaicRuleLabel, comboBox);

      // create a map view
      mapView = new MapView();

      // add draw status listener to the map view
      mapView.addDrawStatusChangedListener (drawStatusChangedEvent -> {
        //show progress indicator while map is drawing
        if (drawStatusChangedEvent.getDrawStatus() == DrawStatus.IN_PROGRESS) {
          progressIndicator.setVisible(true);
        }
        else {
          progressIndicator.setVisible(false);
        }
      });
      // create an ArcGISMap map
      ArcGISMap map = new ArcGISMap(Basemap.createLightGrayCanvasVector());
      // set the ArcGISMap to the map view
      mapView.setMap(map);

      // create a raster layer from the image service raster
      String imageServiceURL = "https://sampleserver7.arcgisonline.com/arcgis/rest/services/amberg_germany/ImageServer";
      ImageServiceRaster imageServiceRaster = new ImageServiceRaster(imageServiceURL);
      rasterLayer = new RasterLayer(imageServiceRaster);

      // add raster layer as an operational layer to the map
      map.getOperationalLayers().add(rasterLayer);

      // listen for the raster layer to finish loading
      rasterLayer.addDoneLoadingListener(() -> {
        if (rasterLayer.getLoadStatus() == LoadStatus.LOADED) {
          // when loaded, set map view's viewpoint to the image service raster's center
          mapView.setViewpoint(new Viewpoint(imageServiceRaster.getServiceInfo().getFullExtent().getCenter(), 25000.0));

          // enable UI interaction once raster layer has loaded
          controlsVBox.setVisible(true);

          // add the mosaic rules to the combo box
          comboBox.getItems().addAll("Default", "Northwest", "Center", "By attribute", "Lock raster");

          // set the default combo box value
          comboBox.setValue("Default");

          // create a mosaic rule for the image service raster if one does not already exist
          if (imageServiceRaster.getMosaicRule() == null) {
            mosaicRule = new MosaicRule();
            imageServiceRaster.setMosaicRule(mosaicRule);
          }
        } else {
          // show alert if raster layer fails to load.
          new Alert(Alert.AlertType.ERROR, "Error loading raster layer.").show();
        }
      });

      // set the mosaic rule of the image service raster based on rule chosen from the combo box
      comboBox.getSelectionModel().selectedItemProperty().addListener(e -> {
        // create a new mosaic rule
        mosaicRule = new MosaicRule();

        switch (comboBox.getSelectionModel().getSelectedItem()) {
          case "Default":
            mosaicRule.setMosaicMethod(MosaicMethod.NONE);
            break;
          case "Northwest":
            mosaicRule.setMosaicMethod(MosaicMethod.NORTHWEST);
            mosaicRule.setMosaicOperation(MosaicOperation.FIRST);
            break;
          case "Center":
            mosaicRule.setMosaicMethod(MosaicMethod.CENTER);
            mosaicRule.setMosaicOperation(MosaicOperation.BLEND);
            break;
          case "By attribute":
            mosaicRule.setMosaicMethod(MosaicMethod.ATTRIBUTE);
            mosaicRule.setSortField("OBJECTID");
            break;
          case "Lock raster":
            mosaicRule.setMosaicMethod(MosaicMethod.LOCK_RASTER);
            mosaicRule.getLockRasterIds().clear();
            mosaicRule.getLockRasterIds().addAll(Arrays.asList(1L, 7L, 12L));
            break;
        }
        // set the mosaic rule of the image service raster
        imageServiceRaster.setMosaicRule(mosaicRule);
      });

      // add the map view, control panel and progress indicator to the stack pane
      stackPane.getChildren().addAll(mapView,controlsVBox, progressIndicator);
      StackPane.setAlignment(controlsVBox, Pos.TOP_LEFT);
      StackPane.setMargin(controlsVBox, new Insets(10, 0, 0, 10));
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
