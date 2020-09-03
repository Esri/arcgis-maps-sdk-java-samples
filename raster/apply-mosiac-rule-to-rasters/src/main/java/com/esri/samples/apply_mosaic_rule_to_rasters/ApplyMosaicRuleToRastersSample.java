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
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;

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
      stackPane.getChildren().addAll(mapView);

    } catch (Exception e) {
      // on any error, display the stack trace
      e.printStackTrace();
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
