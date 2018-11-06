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

package com.esri.samples.scene.change_atmosphere_effect;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.ArcGISTiledElevationSource;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Surface;
import com.esri.arcgisruntime.mapping.view.AtmosphereEffect;
import com.esri.arcgisruntime.mapping.view.SceneView;
import com.esri.arcgisruntime.mapping.view.Camera;

public class ChangeAtmosphereEffectSample extends Application {

  private SceneView sceneView;
  private static final String ELEVATION_IMAGE_SERVICE =
      "http://elevation3d.arcgis.com/arcgis/rest/services/WorldElevation3D/Terrain3D/ImageServer";

  @Override
  public void start(Stage stage) {

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

      // create a control panel
      VBox controlsVBox = new VBox(6);
      controlsVBox.setBackground(new Background(new BackgroundFill(Paint.valueOf("rgba(0, 0, 0, 0.3)"), CornerRadii.EMPTY, Insets.EMPTY)));
      controlsVBox.setPadding(new Insets(10.0));
      controlsVBox.setMaxSize(260, 110);
      controlsVBox.getStyleClass().add("panel-region");

      // create buttons for interaction
      Button noAtmosphereButton = new Button("No Atmosphere Effect");
      Button realisticAtmosphereButton = new Button ("Realistic Atmosphere Effect");
      Button horizonAtmosphereButton = new Button ("Horizon Only Atmosphere Effect");
      noAtmosphereButton.setMaxWidth(Double.MAX_VALUE);
      realisticAtmosphereButton.setMaxWidth(Double.MAX_VALUE);
      horizonAtmosphereButton.setMaxWidth(Double.MAX_VALUE);

      noAtmosphereButton.setOnAction(event -> {
        // set atmosphere effect to none
        sceneView.setAtmosphereEffect(AtmosphereEffect.NONE);
      });

      realisticAtmosphereButton.setOnAction(event -> {
        // set atmosphere effect to realistic
        sceneView.setAtmosphereEffect(AtmosphereEffect.REALISTIC);
      });

      horizonAtmosphereButton.setOnAction(event -> {
        // set atmosphere effect to horizon only
        sceneView.setAtmosphereEffect(AtmosphereEffect.HORIZON_ONLY);
      });

      // add controls to the user interface pane
      controlsVBox.getChildren().addAll(noAtmosphereButton, realisticAtmosphereButton, horizonAtmosphereButton);

      // create a scene and add a basemap to it
      ArcGISScene scene = new ArcGISScene();
      scene.setBasemap(Basemap.createImagery());

      // add the scene view and control panel to stack pane
      sceneView = new SceneView();
      sceneView.setArcGISScene(scene);
      stackPane.getChildren().addAll(sceneView, controlsVBox);
      stackPane.setAlignment(controlsVBox, Pos.TOP_RIGHT);
      stackPane.setMargin(controlsVBox, new Insets(10, 0, 0, 10));

      // add base surface for elevation data
      Surface surface = new Surface();
      surface.getElevationSources().add(new ArcGISTiledElevationSource(ELEVATION_IMAGE_SERVICE));
      scene.setBaseSurface(surface);

      // add a camera and initial camera position
      Camera camera = new Camera(64.416919, -14.483728, 100, 318, 105, 0);
      sceneView.setViewpointCamera(camera);

    } catch (Exception e) {
      // on any error, display the stack trace.
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
