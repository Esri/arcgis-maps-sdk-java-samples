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

package com.esri.samples.surface_placement;

import com.esri.arcgisruntime.layers.ArcGISSceneLayer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.ArcGISTiledElevationSource;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Surface;
import com.esri.arcgisruntime.mapping.view.Camera;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.LayerSceneProperties.SurfacePlacement;
import com.esri.arcgisruntime.mapping.view.SceneView;
import com.esri.arcgisruntime.symbology.DistanceCompositeSceneSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.TextSymbol;
import com.esri.arcgisruntime.symbology.TextSymbol.HorizontalAlignment;
import com.esri.arcgisruntime.symbology.TextSymbol.VerticalAlignment;

public class SurfacePlacementSample extends Application {

  private SceneView sceneView;

  @Override
  public void start(Stage stage) {

    try {

      // create stack pane and JavaFX app scene
      StackPane stackPane = new StackPane();
      Scene fxScene = new Scene(stackPane);
      fxScene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

      // set title, size, and add JavaFX scene to stage
      stage.setTitle("Surface Placement Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(fxScene);
      stage.show();

      // create a scene and add a basemap to it
      ArcGISScene scene = new ArcGISScene(Basemap.Type.IMAGERY);

      // add the SceneView to the stack pane
      sceneView = new SceneView();
      sceneView.setArcGISScene(scene);

      // add a camera and initial camera position
      sceneView.setViewpointCamera(new Camera(53.05, -4.01, 1115, 299, 88, 0));

      // add base surface for elevation data
      Surface surface = new Surface();
      ArcGISTiledElevationSource elevationSource = new ArcGISTiledElevationSource(
          "http://elevation3d.arcgis.com/arcgis/rest/services/WorldElevation3D/Terrain3D/ImageServer");
      surface.getElevationSources().add(elevationSource);
      scene.setBaseSurface(surface);

      // create a scenelayer from an online service and add it to the scene
      ArcGISSceneLayer sceneLayer = new ArcGISSceneLayer("URL");
      scene.getOperationalLayers().add(sceneLayer);

      // create a graphics overlay and add it to the sceneview
      GraphicsOverlay graphicsOverlay = new GraphicsOverlay();
      sceneView.getGraphicsOverlays().add(graphicsOverlay);

      // create point for graphic location
      Point point = new Point(-4.04, 53.06, 1000, SpatialReferences.getWgs84());

      // create a red (0xFFFF0000) triangle symbol
      SimpleMarkerSymbol triangleSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.TRIANGLE, 0xFFFF0000, 10);

      // create a text symbol for each elevation mode
      TextSymbol textSymbol = new TextSymbol(10, "", 0xFFFFFFFF, HorizontalAlignment.LEFT, VerticalAlignment.MIDDLE);
      textSymbol.setOffsetX(20);

      // create a distance composite scene symbol to hold the triangle symbol and text symbol
      DistanceCompositeSceneSymbol distanceCompositeSceneSymbol = new DistanceCompositeSceneSymbol();
      distanceCompositeSceneSymbol.getRangeCollection().add(new DistanceCompositeSceneSymbol.Range(textSymbol, 0, 0));
      distanceCompositeSceneSymbol.getRangeCollection()
          .add(new DistanceCompositeSceneSymbol.Range(triangleSymbol, 0, 0));

      // create a graphic using the point and distance composite symbol, and add it the overlay
      Graphic graphic = new Graphic(point, distanceCompositeSceneSymbol);
      graphicsOverlay.getGraphics().add(graphic);

      // create a ComboBox for selecting the surface placement mode
      ComboBox<SurfacePlacement> surfacePlacementComboBox = new ComboBox<>();
      surfacePlacementComboBox.getItems().addAll(SurfacePlacement.values());

      // on selection, change the surface placement mode and update symbol text
      surfacePlacementComboBox.getSelectionModel().selectedItemProperty()
          .addListener(((observable, oldValue, newValue) -> {
            if (newValue != null) {
              graphicsOverlay.getSceneProperties().setSurfacePlacement(newValue);
              textSymbol.setText(newValue.name());
            }
          }));

      // select the current surface placement mode
      surfacePlacementComboBox.getSelectionModel().select(graphicsOverlay.getSceneProperties().getSurfacePlacement());

      // and the scene view and control box to the stack pane
      stackPane.getChildren().addAll(sceneView, surfacePlacementComboBox);
      StackPane.setAlignment(surfacePlacementComboBox, Pos.TOP_LEFT);
      StackPane.setMargin(surfacePlacementComboBox, new Insets(10));

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

    if (sceneView != null) {
      sceneView.dispose();
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
