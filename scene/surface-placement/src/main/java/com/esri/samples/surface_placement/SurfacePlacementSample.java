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

package com.esri.samples.surface_placement;

import java.util.Arrays;

import com.esri.arcgisruntime.layers.ArcGISSceneLayer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
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
      sceneView.setViewpointCamera(new Camera(48.3889, -4.4595, 90, 330, 90, 0));

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

      GraphicsOverlay relativeToSceneOverlay = new GraphicsOverlay();
      relativeToSceneOverlay.getSceneProperties().setSurfacePlacement(SurfacePlacement.RELATIVE_TO_SCENE);

      // create points for graphic locations
      Point surfaceRelatedPoint = new Point(-4.4609257, 48.3903965, 70, SpatialReferences.getWgs84());
      Point sceneRelatedPoint = new Point(-4.4610562, 48.3902727, 70, SpatialReferences.getWgs84());

      // create a red triangle symbol
      SimpleMarkerSymbol triangleSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.TRIANGLE, 0xFFFF0000, 12);

      // create a text symbol for each surface placement type
      TextSymbol drapedBillboardedText =
          new TextSymbol(14, "DRAPED BILLBOARDED", 0xFF0000CC, HorizontalAlignment.LEFT, VerticalAlignment.MIDDLE);
      drapedBillboardedText.setOffsetX(20);

      TextSymbol drapedFlatText =
          new TextSymbol(14, "DRAPED FLAT", 0xFF0000CC, HorizontalAlignment.LEFT, VerticalAlignment.MIDDLE);
      drapedFlatText.setOffsetX(20);

      TextSymbol relativeText =
          new TextSymbol(14, "RELATIVE", 0xFF0000CC, HorizontalAlignment.LEFT, VerticalAlignment.MIDDLE);
      relativeText.setOffsetX(20);

      TextSymbol absoluteText =
          new TextSymbol(14, "ABSOLUTE", 0xFF0000CC, HorizontalAlignment.LEFT, VerticalAlignment.MIDDLE);
      absoluteText.setOffsetX(20);

      TextSymbol relativeToSceneText =
        new TextSymbol(14, "RELATIVE TO SCENE", 0xFF0000CC, HorizontalAlignment.RIGHT, VerticalAlignment.MIDDLE);
      relativeToSceneText.setOffsetX(-20);

      // add the point graphic and text graphic to the corresponding graphics overlay
      drapedBillboardedOverlay.getGraphics().add(new Graphic(surfaceRelatedPoint, triangleSymbol));
      drapedBillboardedOverlay.getGraphics().add(new Graphic(surfaceRelatedPoint, drapedBillboardedText));

      drapedFlatOverlay.getGraphics().add(new Graphic(surfaceRelatedPoint, triangleSymbol));
      drapedFlatOverlay.getGraphics().add(new Graphic(surfaceRelatedPoint, drapedFlatText));

      relativeOverlay.getGraphics().add(new Graphic(surfaceRelatedPoint, triangleSymbol));
      relativeOverlay.getGraphics().add(new Graphic(surfaceRelatedPoint, relativeText));

      absoluteOverlay.getGraphics().add(new Graphic(surfaceRelatedPoint, triangleSymbol));
      absoluteOverlay.getGraphics().add(new Graphic(surfaceRelatedPoint, absoluteText));

      relativeToSceneOverlay.getGraphics().add(new Graphic(sceneRelatedPoint, triangleSymbol));
      relativeToSceneOverlay.getGraphics().add(new Graphic(sceneRelatedPoint, relativeToSceneText));

      // add graphics overlays to the scene view
      sceneView.getGraphicsOverlays().addAll(Arrays.asList(drapedBillboardedOverlay, relativeOverlay, absoluteOverlay, relativeToSceneOverlay));

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

      // create slider to adjust the z-value
      Slider zValueSlider = new Slider(0, 150, 70);
      Label zValueSliderLabel = new Label("Z-Value:");
      zValueSlider.setShowTickMarks(true);
      zValueSlider.setShowTickLabels(true);
      zValueSlider.setMajorTickUnit(50);
      zValueSlider.setOrientation(Orientation.VERTICAL);

      zValueSlider.valueProperty().addListener((observableValue, oldValue, newValue) -> {

        // get z-value from slider
        Double zValueFromSlider = zValueSlider.getValue();

        // create new points with updated z-value
        Point newSurfaceRelatedPoint = new Point(surfaceRelatedPoint.getX(), surfaceRelatedPoint.getY(), zValueFromSlider, surfaceRelatedPoint.getSpatialReference());
        Point newSceneRelatedPoint = new Point(sceneRelatedPoint.getX(), sceneRelatedPoint.getY(), zValueFromSlider, sceneRelatedPoint.getSpatialReference());

        // update the geometry of each of the existing graphics (both the symbol and label) to the relevant new point
        drapedBillboardedOverlay.getGraphics().get(0).setGeometry(newSurfaceRelatedPoint);
        drapedBillboardedOverlay.getGraphics().get(1).setGeometry(newSurfaceRelatedPoint);

        drapedFlatOverlay.getGraphics().get(0).setGeometry(newSurfaceRelatedPoint);
        drapedFlatOverlay.getGraphics().get(1).setGeometry(newSurfaceRelatedPoint);

        relativeOverlay.getGraphics().get(0).setGeometry(newSurfaceRelatedPoint);
        relativeOverlay.getGraphics().get(1).setGeometry(newSurfaceRelatedPoint);

        absoluteOverlay.getGraphics().get(0).setGeometry(newSurfaceRelatedPoint);
        absoluteOverlay.getGraphics().get(1).setGeometry(newSurfaceRelatedPoint);

        relativeToSceneOverlay.getGraphics().get(0).setGeometry(newSceneRelatedPoint);
        relativeToSceneOverlay.getGraphics().get(1).setGeometry(newSceneRelatedPoint);

      });

      // create a controls area for the z-value slider
      VBox sliderVBox = new VBox();
      sliderVBox.setBackground(
        new Background(new BackgroundFill(Paint.valueOf("rgba(0,0,0,0.5)"), CornerRadii.EMPTY, Insets.EMPTY)));
      sliderVBox.setPadding(new Insets(10.0));
      sliderVBox.setMaxSize(70, 200);
      sliderVBox.getStyleClass().add("panel-region");
      sliderVBox.getChildren()
        .addAll(zValueSliderLabel, zValueSlider);

      // add the scene view, radio buttons control box and slider control box to the stack pane
      stackPane.getChildren().addAll(sceneView, controlsVBox, sliderVBox);
      StackPane.setAlignment(controlsVBox, Pos.TOP_LEFT);
      StackPane.setMargin(controlsVBox, new Insets(10, 0, 0, 10));
      StackPane.setAlignment(sliderVBox, Pos.TOP_RIGHT);
      StackPane.setMargin(sliderVBox, new Insets(10, 10, 0, 0));

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
