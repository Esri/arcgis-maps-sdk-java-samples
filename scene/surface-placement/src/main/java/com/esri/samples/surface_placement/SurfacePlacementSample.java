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

import java.util.Arrays;

import com.esri.arcgisruntime.layers.ArcGISSceneLayer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
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
      fxScene.getStylesheets().add(getClass().getResource("/surface_placement/style.css").toExternalForm());

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
      sceneView.setViewpointCamera(new Camera(48.3889, -4.4595, 80, 330, 90, 0));

      // add base surface for elevation data
      Surface surface = new Surface();
      ArcGISTiledElevationSource elevationSource = new ArcGISTiledElevationSource(
          "https://elevation3d.arcgis.com/arcgis/rest/services/WorldElevation3D/Terrain3D/ImageServer");
      surface.getElevationSources().add(elevationSource);
      scene.setBaseSurface(surface);

      // add a scene layer
      final String buildings = "http://tiles.arcgis.com/tiles/P3ePLMYs2RVChkJx/arcgis/rest/services/Buildings_Brest/SceneServer/layers/0";
      ArcGISSceneLayer sceneLayer = new ArcGISSceneLayer(buildings);
      scene.getOperationalLayers().add(sceneLayer);

      // create overlays with surface placement types
      GraphicsOverlay drapedBillboardedOverlay = new GraphicsOverlay();
      drapedBillboardedOverlay.getSceneProperties().setSurfacePlacement(SurfacePlacement.DRAPED_BILLBOARDED);

      GraphicsOverlay drapedFlatOverlay = new GraphicsOverlay();
      drapedFlatOverlay.getSceneProperties().setSurfacePlacement(SurfacePlacement.DRAPED_FLAT);

      GraphicsOverlay relativeOverlay = new GraphicsOverlay();
      relativeOverlay.getSceneProperties().setSurfacePlacement(SurfacePlacement.RELATIVE);

      GraphicsOverlay absoluteOverlay = new GraphicsOverlay();
      absoluteOverlay.getSceneProperties().setSurfacePlacement(SurfacePlacement.ABSOLUTE);

      // create point for graphic location
      Point point = new Point(-4.4609257, 48.3903965, 70, SpatialReferences.getWgs84());

      // create a red triangle symbol
      SimpleMarkerSymbol triangleSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.TRIANGLE, 0xFFFF0000, 10);

      // create a text symbol for each elevation mode
      TextSymbol drapedBillboardedText =
          new TextSymbol(10, "DRAPED BILLBOARDED", 0xFFFFFFFF, HorizontalAlignment.LEFT, VerticalAlignment.MIDDLE);
      drapedBillboardedText.setOffsetX(20);

      TextSymbol drapedFlatText =
          new TextSymbol(10, "DRAPED FLAT", 0xFFFFFFFF, HorizontalAlignment.LEFT, VerticalAlignment.MIDDLE);
      drapedFlatText.setOffsetX(20);

      TextSymbol relativeText =
          new TextSymbol(10, "RELATIVE", 0xFFFFFFFF, HorizontalAlignment.LEFT, VerticalAlignment.MIDDLE);
      relativeText.setOffsetX(20);

      TextSymbol absoluteText =
          new TextSymbol(10, "ABSOLUTE", 0xFFFFFFFF, HorizontalAlignment.LEFT, VerticalAlignment.MIDDLE);
      absoluteText.setOffsetX(20);

      // add the point graphic and text graphic to the corresponding graphics overlay
      drapedBillboardedOverlay.getGraphics().add(new Graphic(point, triangleSymbol));
      drapedBillboardedOverlay.getGraphics().add(new Graphic(point, drapedBillboardedText));

      drapedFlatOverlay.getGraphics().add(new Graphic(point, triangleSymbol));
      drapedFlatOverlay.getGraphics().add(new Graphic(point, drapedFlatText));

      relativeOverlay.getGraphics().add(new Graphic(point, triangleSymbol));
      relativeOverlay.getGraphics().add(new Graphic(point, relativeText));

      absoluteOverlay.getGraphics().add(new Graphic(point, triangleSymbol));
      absoluteOverlay.getGraphics().add(new Graphic(point, absoluteText));

      sceneView.getGraphicsOverlays().addAll(Arrays.asList(drapedBillboardedOverlay, relativeOverlay, absoluteOverlay));

      // create radio buttons to toggle between billboarded and flat draped surface placement modes
      ToggleGroup toggleGroup = new ToggleGroup();
      Label toggleGroupLabel = new Label("Toggle draped mode:");

      RadioButton drapedBillboardedRadioButton = new RadioButton("Draped Billboarded");
      drapedBillboardedRadioButton.setToggleGroup(toggleGroup);
      drapedBillboardedRadioButton.setSelected(true);
      drapedBillboardedRadioButton.setUserData(drapedBillboardedOverlay);

      RadioButton drapedFlatRadioButton = new RadioButton("Draped Flat");
      drapedFlatRadioButton.setToggleGroup(toggleGroup);
      drapedFlatRadioButton.setUserData(drapedFlatOverlay);

      toggleGroup.selectedToggleProperty().addListener((observableValue, oldToggle, newToggle) -> {
        if (toggleGroup.getSelectedToggle() != null) {
          sceneView.getGraphicsOverlays().remove(oldToggle.getUserData());
          sceneView.getGraphicsOverlays().add((GraphicsOverlay) toggleGroup.getSelectedToggle().getUserData());
        }
      });

      // create a controls area for the radio buttons
      VBox controlsVBox = new VBox(6);
      controlsVBox.setBackground(
          new Background(new BackgroundFill(Paint.valueOf("rgba(0,0,0,0.3)"), CornerRadii.EMPTY, Insets.EMPTY)));
      controlsVBox.setPadding(new Insets(10.0));
      controlsVBox.setMaxSize(210, 90);
      controlsVBox.getStyleClass().add("panel-region");
      controlsVBox.getChildren()
          .addAll(toggleGroupLabel, drapedBillboardedRadioButton, drapedFlatRadioButton);

      // add the scene view and control box to the stack pane
      stackPane.getChildren().addAll(sceneView, controlsVBox);
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
