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

package com.esri.samples.change_atmosphere_effect;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.ArcGISTiledElevationSource;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.Surface;
import com.esri.arcgisruntime.mapping.view.AtmosphereEffect;
import com.esri.arcgisruntime.mapping.view.SceneView;
import com.esri.arcgisruntime.mapping.view.Camera;

public class ChangeAtmosphereEffectSample extends Application {

  private SceneView sceneView;

  @Override
  public void start(Stage stage) {

    try {
      // create stack pane and JavaFX app scene
      StackPane stackPane = new StackPane();
      Scene fxScene = new Scene(stackPane);
      fxScene.getStylesheets().add(getClass().getResource("/change_atmosphere_effect/style.css").toExternalForm());

      // set title, size, and add JavaFX scene to stage
      stage.setTitle("Change Atmosphere Effect Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(fxScene);
      stage.show();

      // authentication with an API key or named user is required to access basemaps and other location services
      String yourAPIKey = System.getProperty("apiKey");
      ArcGISRuntimeEnvironment.setApiKey(yourAPIKey);

      // create a scene with a basemap style
      ArcGISScene scene = new ArcGISScene(BasemapStyle.ARCGIS_IMAGERY);

      // set the scene to a scene view
      sceneView = new SceneView();
      sceneView.setArcGISScene(scene);

      // add base surface for elevation data
      Surface surface = new Surface();
      ArcGISTiledElevationSource elevationSource = new ArcGISTiledElevationSource(
              "https://elevation3d.arcgis.com/arcgis/rest/services/WorldElevation3D/Terrain3D/ImageServer");
      surface.getElevationSources().add(elevationSource);
      scene.setBaseSurface(surface);

      // add a camera and initial camera position
      Camera camera = new Camera(64.416919, -14.483728, 100, 318, 105, 0);
      sceneView.setViewpointCamera(camera);

      // create combo box for the atmosphere effects
      ComboBox<AtmosphereEffect> comboBox = new ComboBox<>();
      comboBox.getItems().addAll(AtmosphereEffect.NONE,
        AtmosphereEffect.REALISTIC, AtmosphereEffect.HORIZON_ONLY);

      // show the name of the atmosphere effects in the combo box
      comboBox.setConverter(new ComboBoxStringConverter());
      comboBox.setCellFactory(comboBox2 -> new AtmosphereEffectListCell());

      // bind the scene view atmosphere effect to the value chosen from the combo box
      comboBox.valueProperty().bindBidirectional(sceneView.atmosphereEffectProperty());

      // add scene view and control panel to the stack pane
      stackPane.getChildren().addAll(sceneView, comboBox);
      StackPane.setAlignment(comboBox, Pos.TOP_RIGHT);
      StackPane.setMargin(comboBox, new Insets(10, 10, 0, 0));
    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
    }
  }

  /**
   * Converts the AtmosphereEffect values to strings to display in the open ComboBox.
   */
  private class AtmosphereEffectListCell extends ListCell<AtmosphereEffect> {

    @Override
    protected void updateItem(AtmosphereEffect atmosphereMode, boolean empty) {

      super.updateItem(atmosphereMode, empty);
      if (atmosphereMode != null) {
        if (atmosphereMode == AtmosphereEffect.NONE) setText("None");
        else if (atmosphereMode == AtmosphereEffect.REALISTIC) setText("Realistic");
        else if (atmosphereMode == AtmosphereEffect.HORIZON_ONLY) setText("Horizon only");
      }
    }
  }

  /**
   * Converts the AtmosphereEffect values to strings to display in the ComboBox.
   */
  private class ComboBoxStringConverter extends StringConverter<AtmosphereEffect> {

    @Override
    public String toString(AtmosphereEffect atmosphereEffect) {
      if (atmosphereEffect != null) {
        if (atmosphereEffect == AtmosphereEffect.NONE) return "None";
        else if (atmosphereEffect == AtmosphereEffect.REALISTIC) return "Realistic";
        else if (atmosphereEffect == AtmosphereEffect.HORIZON_ONLY) return "Horizon only";
      }
      return "";
    }

    @Override
    public AtmosphereEffect fromString(String string) {
      return null;
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
