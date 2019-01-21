/*
 * Copyright 2019 Esri.
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

package com.esri.samples.scene.choose_camera_controller;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.ArcGISTiledElevationSource;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Surface;
import com.esri.arcgisruntime.mapping.view.GlobeCameraController;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.LayerSceneProperties;
import com.esri.arcgisruntime.mapping.view.OrbitGeoElementCameraController;
import com.esri.arcgisruntime.mapping.view.SceneView;
import com.esri.arcgisruntime.symbology.ModelSceneSymbol;

import com.esri.arcgisruntime.symbology.SceneSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSceneSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import javafx.application.Application;
import javafx.fxml.FXML;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;


import java.io.File;

public class ChooseCameraControllerSample extends Application {

  private OrbitGeoElementCameraController orbitCameraController;
  private SceneView sceneView;
  private Label cameraModeLabel;
  private Button fixCameraToPlaneButton;
  private Button freeCameraModeButton;

  @Override
  public void start(Stage stage) throws Exception {

    try {

      // create stack pane and JavaFX app scene
      StackPane stackPane = new StackPane();
      Scene fxScene = new Scene(stackPane);
      fxScene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

      // set title, size, and add JavaFX scene to stage
      stage.setTitle("Change Atmosphere Effect Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(fxScene);
      stage.show();

      // create a scene and add a basemap to it
      ArcGISScene scene = new ArcGISScene();
      scene.setBasemap(Basemap.createImagery());

      // set the scene to a scene view
      sceneView = new SceneView();
      sceneView.setArcGISScene(scene);

      // add base surface for elevation data
      Surface surface = new Surface();
      ArcGISTiledElevationSource elevationSource = new ArcGISTiledElevationSource("http://elevation3d.arcgis" +
              ".com/arcgis/rest/services/WorldElevation3D/Terrain3D/ImageServer");
      surface.getElevationSources().add(elevationSource);
      scene.setBaseSurface(surface);

      // create a graphics overlay for the scene
      GraphicsOverlay sceneGraphicsOverlay = new GraphicsOverlay();
      sceneGraphicsOverlay.getSceneProperties().setSurfacePlacement(LayerSceneProperties.SurfacePlacement.ABSOLUTE);
      sceneView.getGraphicsOverlays().add(sceneGraphicsOverlay);

      // create a graphic with a SimpleMarkerSceneSymbol to add to the scene
      SimpleMarkerSceneSymbol symbol = new SimpleMarkerSceneSymbol(SimpleMarkerSceneSymbol.Style.DIAMOND, 0xFFEFA70D, 50, 50, 50,
              SceneSymbol.AnchorPosition.CENTER);
      Graphic floatingSymbol = new Graphic(new Point(-116.851151, 37.563834, 1670, SpatialReferences.getWgs84()), symbol);
      sceneGraphicsOverlay.getGraphics().add(floatingSymbol);

      // instantiate a new camera controller which orbits the graphic at a certain distance
      orbitCameraController = new OrbitGeoElementCameraController(floatingSymbol, 500.0);
      orbitCameraController.setCameraPitchOffset(85);
      orbitCameraController.setCameraHeadingOffset(120);
      // set the orbit camera controller to the scene view
      sceneView.setCameraController(orbitCameraController);

      // instantiate a new label to display what camera controller is active
      Label cameraModeLabel = new Label("Orbit Camera is active");
      cameraModeLabel.setPadding(new Insets(0, 0, 130, 0));

      // instantiate control buttons to choose what camera controller is active
      Button orbitCameraControllerButton = new Button("Orbit Camera Controller");
      orbitCameraControllerButton.setMaxWidth(Double.MAX_VALUE);
      orbitCameraControllerButton.setDisable(true);
      Button globeCameraControllerButton = new Button ("Globe Camera Controller");
      globeCameraControllerButton.setMaxWidth(Double.MAX_VALUE);

      // instantiate a control panel
      VBox controlsVBox = new VBox();
      controlsVBox.setBackground(new Background(new BackgroundFill(Paint.valueOf("rgba(0, 0, 0, 0.3)"),
              CornerRadii.EMPTY, Insets.EMPTY)));
      controlsVBox.setPadding(new Insets(10.0));
      controlsVBox.setMaxSize(260, 75);
      controlsVBox.getStyleClass().add("panel-region");
      // add buttons to the control panel
      controlsVBox.getChildren().addAll(orbitCameraControllerButton, globeCameraControllerButton);

      // set the camera to OrbitGeoElementCameraController
      orbitCameraControllerButton.setOnAction(event -> {
        sceneView.setCameraController(orbitCameraController);
        orbitCameraControllerButton.setDisable(true);
        globeCameraControllerButton.setDisable(false);
        cameraModeLabel.setText("Orbit Camera is active");
      });

      // set the camera to GlobeCameraController
      globeCameraControllerButton.setOnAction(event -> {
        sceneView.setCameraController(new GlobeCameraController());
        globeCameraControllerButton.setDisable(true);
        orbitCameraControllerButton.setDisable(false);
        cameraModeLabel.setText("Globe Camera is active");
      });

      // add scene view, label and control panel to the stack pane
      stackPane.getChildren().addAll(sceneView, cameraModeLabel, controlsVBox);
      StackPane.setAlignment(controlsVBox, Pos.BOTTOM_CENTER);
      StackPane.setAlignment(cameraModeLabel, Pos.BOTTOM_CENTER);
      StackPane.setMargin(controlsVBox, new Insets(0, 0, 50, 10));

    } catch (Exception e) {
      // on any exception, print the stack trace
      e.printStackTrace();
    }
  }

  /**
   * Stops and releases all resources used in application.
   */
  @Override
  public void stop() {
    // release resources when the application closes
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







