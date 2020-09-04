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

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
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
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.raster.ImageServiceRaster;
import com.esri.arcgisruntime.raster.MosaicRule;
import com.esri.arcgisruntime.raster.MosaicMethod;
import com.esri.arcgisruntime.raster.MosaicOperation;

import java.util.Arrays;
import java.util.List;

public class ApplyMosaicRuleToRastersSample extends Application {

  private MapView mapView;
  private ImageServiceRaster imageServiceRaster;
  private ComboBox<String> comboBox;
  private MosaicRule mosaicRule;

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

      // create a map with the topographic vector basemap
      ArcGISMap map = new ArcGISMap(Basemap.createTopographicVector());

      // create a map view and set the ArcGISMap to it
      mapView = new MapView();
      mapView.setMap(map);

      // set up the control panel UI
      VBox controlsVBox = new VBox(6);
      controlsVBox.setBackground(new Background(new BackgroundFill(Paint.valueOf("rgba(0, 0, 0, 0.3)"),
        CornerRadii.EMPTY, Insets.EMPTY)));
      controlsVBox.setPadding(new Insets(10.0));
      controlsVBox.setMaxSize(260,50);
      controlsVBox.setVisible(false);

      // create a label
      Label mosaicRuleLabel = new Label ("Choose a mosaic rule for the image service: ");
      mosaicRuleLabel.setTextFill(Color.WHITE);

      // create a combo box
      comboBox = new ComboBox<>();
      comboBox.setMaxWidth(Double.MAX_VALUE);

      // add the label and combo box to the control panel
      controlsVBox.getChildren().addAll(mosaicRuleLabel, comboBox);

      // create an image service raster from a url for an image service
      imageServiceRaster = new ImageServiceRaster(imageServiceURL);

      // create a raster layer from the image service raster
      RasterLayer rasterLayer = new RasterLayer(imageServiceRaster);

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

          // check if a mosaic rule exists. If not, create one
          if (imageServiceRaster.getMosaicRule() == null) {
            mosaicRule = new MosaicRule();
            imageServiceRaster.setMosaicRule(mosaicRule);
          }
          // set the mosaic rule based on rule chosen from the combo box
          comboBox.getSelectionModel().selectedItemProperty().addListener(e -> {
            setMosaicRule(comboBox.getSelectionModel().getSelectedItem());
          });
        } else {
          // show alert if raster layer fails to load.
          new Alert(Alert.AlertType.ERROR, "Error loading raster layer.").show();
        }
      });

      // add the map view and the control panel to the stack pane
      stackPane.getChildren().addAll(mapView, controlsVBox);
      StackPane.setAlignment(controlsVBox, Pos.TOP_RIGHT);
      StackPane.setMargin(controlsVBox, new Insets(10, 0, 0, 10));

    } catch (Exception e) {
      // on any error, display the stack trace
      e.printStackTrace();
    }
  }

  /**
   * Applies one of the predefined mosaic rules to the image service raster.
   *
   * @param mosaicMethod one of "Default", "Northwest", "Center", "By attribute", and "Lock raster"
   */
  private void setMosaicRule(String mosaicMethod) {

    if (mosaicMethod == "Default") {
      mosaicRule = new MosaicRule();
      mosaicRule.setMosaicMethod(MosaicMethod.NONE);
    }

    if (mosaicMethod == "Northwest") {
      mosaicRule = new MosaicRule();
      mosaicRule.setMosaicMethod(MosaicMethod.NORTHWEST);
      mosaicRule.setMosaicOperation(MosaicOperation.FIRST);
    }
    if (mosaicMethod == "Center") {
      mosaicRule = new MosaicRule();
      mosaicRule.setMosaicMethod(MosaicMethod.CENTER);
      mosaicRule.setMosaicOperation(MosaicOperation.BLEND);
    }
    if (mosaicMethod == "By attribute") {
      mosaicRule = new MosaicRule();
      mosaicRule.setMosaicMethod(MosaicMethod.ATTRIBUTE);
      mosaicRule.setSortField("OBJECTID");
    }
    if (mosaicMethod == "Lock raster") {
      mosaicRule = new MosaicRule();
      mosaicRule.setMosaicMethod(MosaicMethod.LOCK_RASTER);
      mosaicRule.getLockRasterIds().clear();
      mosaicRule.getLockRasterIds().addAll(Arrays.asList(Long.valueOf(1), Long.valueOf(7), Long.valueOf(12)));
    }
    imageServiceRaster.setMosaicRule(mosaicRule);
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
