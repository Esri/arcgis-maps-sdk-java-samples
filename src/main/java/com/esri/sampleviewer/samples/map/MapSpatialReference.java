/* Copyright 2015 Esri.
 
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
 
    http://www.apache.org/licenses/LICENSE-2.0
 
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
limitations under the License.  */

package com.esri.sampleviewer.samples.map;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.layers.ArcGISMapImageLayer;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Map;
import com.esri.arcgisruntime.mapping.view.MapView;

/**
 * This sample demonstrates how to create a Map with a Spatial Reference and
 * apply a Basemap to it.
 * <p>
 * A {@link SpatialReference} ensures that spatial data from different layers or
 * sources can be integrated for accurate viewing.
 * <p>
 * A {@link Basemap} is beneath all other layers on a {@link Map} and is used to
 * provide visual reference for all other layers.
 * <p>
 * The {@link ArcGISMapImageLayer} will re-project itself to a Map's
 * SpatialReference, meaning it will align itself to the Map. Note, not all
 * layer types can be re-projected and will fail to draw if their
 * SpatialReference doesn't match that of the Maps.
 * <h4>How it Works</h4>
 * 
 * A Map is create by suppling a SpatialReference. An ArcGISMapImageLayer is
 * also created from a URL,which is used to create a Basemap. The
 * {@link Map#setBasemap} method allows us to set the Basemap to the Map. Then
 * the Map is set to the {@link MapView} so that it's layers are displayed.
 */
public class MapSpatialReference extends Application {

  private MapView mapView;

  private static final String FEATURE_SERVICE_URL =
      "http://sampleserver6.arcgisonline.com/arcgis/rest/services/SampleWorldCities/MapServer";
  private static final String SAMPLES_THEME_PATH =
      "../resources/SamplesTheme.css";

  @Override
  public void start(Stage stage) throws Exception {

    // create stack pane and application scene
    StackPane stackPane = new StackPane();
    Scene scene = new Scene(stackPane);
    scene.getStylesheets().add(getClass().getResource(SAMPLES_THEME_PATH)
        .toExternalForm());

    // set title, size, and add scene to stage
    stage.setTitle("Map Spatial Reference Sample");
    stage.setWidth(800);
    stage.setHeight(700);
    stage.setScene(scene);
    stage.show();

    // create a control panel
    VBox vBoxControl = new VBox(6);
    vBoxControl.setMaxSize(260, 190);
    vBoxControl.getStyleClass().add("panel-region");

    // create sample description
    Label descriptionLabel = new Label("Sample Description:");
    descriptionLabel.getStyleClass().add("panel-label");
    TextArea description = new TextArea("This sample shows how to create a "
        + "Map with a Spatial Reference and apply a Basemap to it.");
    description.setWrapText(true);
    description.autosize();
    description.setEditable(false);

    // add label and sample description to the control panel
    vBoxControl.getChildren().addAll(descriptionLabel, description);
    try {

      // create a map with a spatial reference
      final Map map = new Map(SpatialReference.create(54024));

      // create a map image layer from url
      final ArcGISMapImageLayer mapImageLayer =
          new ArcGISMapImageLayer(FEATURE_SERVICE_URL);

      // create basemap from the map image layer
      Basemap basemap = new Basemap(mapImageLayer);

      // add the basemap to the map
      map.setBasemap(basemap);

      // create a view and set map to it
      mapView = new MapView();
      mapView.setMap(map);

      // add the map view and control panel to stack pane
      stackPane.getChildren().addAll(mapView, vBoxControl);
      StackPane.setAlignment(vBoxControl, Pos.TOP_LEFT);
      StackPane.setMargin(vBoxControl, new Insets(10, 0, 0, 10));
    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
    }
  }

  /**
   * Stops and releases all resources used in application.
   *
   * @throws Exception if security manager doesn't allow JVM to exit with
   *           current status
   */
  @Override
  public void stop() throws Exception {

    if (mapView != null) {
      mapView.dispose();
    }
    Platform.exit();
    System.exit(0);
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
