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

package com.esri.samples.scale_bar;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.UnitSystem;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.toolkit.Scalebar;

public class ScaleBarSample extends Application {

  private MapView mapView;

  @Override
  public void start(Stage stage) {

    // create stack pane and application scene
    StackPane stackPane = new StackPane();
    Scene scene = new Scene(stackPane);

    // set title, size and scene to stage
    stage.setTitle("Scale Bar Sample");
    stage.setWidth(800);
    stage.setHeight(700);
    stage.setScene(scene);
    stage.show();

    // authentication with an API key or named user is required to access basemaps and other location services
    String yourAPIKey = System.getProperty("apiKey");
    ArcGISRuntimeEnvironment.setApiKey(yourAPIKey);

    // create a map with a basemap style
    ArcGISMap map = new ArcGISMap(BasemapStyle.ARCGIS_IMAGERY_STANDARD);

    // create a map view and set its map
    mapView = new MapView();
    mapView.setMap(map);

    // set a viewpoint on the map view
    mapView.setViewpoint(new Viewpoint(64.1405, -16.2426, 9000));

    // create a scale bar for the map view
    Scalebar scaleBar = new Scalebar(mapView);

    // specify skin style for the scale bar
    scaleBar.setSkinStyle(Scalebar.SkinStyle.GRADUATED_LINE);

    // set the unit system (default is METRIC)
    scaleBar.setUnitSystem(UnitSystem.IMPERIAL);

    // to enhance visibility of the scale bar, by making background transparent
    Color transparentWhite = new Color(1, 1, 1, 0.7);
    scaleBar.setBackground(new Background(new BackgroundFill(transparentWhite, new CornerRadii(5), Insets.EMPTY)));

    // add the map view and scale bar to stack pane
    stackPane.getChildren().addAll(mapView, scaleBar);

    // set position of scale bar
    StackPane.setAlignment(scaleBar, Pos.BOTTOM_CENTER);
    // give padding to scale bar
    StackPane.setMargin(scaleBar, new Insets(0, 0, 50, 0));
  }

  // Stops and releases all resources used in application
  @Override
  public void stop() {
    if (mapView != null) {
      mapView.dispose();
    }
  }

  // Opens and runs application
  public static void main(String[] args) {
    Application.launch(args);
  }
}
