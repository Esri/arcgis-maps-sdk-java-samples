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

package com.esri.samples.realistic_light_and_shadows;

import com.esri.arcgisruntime.mapping.view.LightingMode;
import javafx.fxml.FXML;
import com.esri.arcgisruntime.layers.ArcGISSceneLayer;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.util.StringConverter;

import java.util.Calendar;
import java.util.GregorianCalendar;

import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.ArcGISTiledElevationSource;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Surface;
import com.esri.arcgisruntime.mapping.view.AtmosphereEffect;
import com.esri.arcgisruntime.mapping.view.SceneView;
import com.esri.arcgisruntime.mapping.view.Camera;

public class RealisticLightAndShadowsController {

  @FXML
  private SceneView sceneView;
  @FXML
  private Label time;
  @FXML
  private Slider timeSlider;
  @FXML
  private Button noSunButton;
  @FXML
  private Button sunOnlyButton;
  @FXML
  private Button sunAndShadowsButton;
  private Surface surface;
  private Calendar calendar;

  public void initialize() {
    try {

      // create a scene and add a basemap to it
      ArcGISScene scene = new ArcGISScene();
      scene.setBasemap(Basemap.createTopographic());

      // add the SceneView to the stack pane
      sceneView.setArcGISScene(scene);

      // add base surface for elevation data
      surface = new Surface();
      ArcGISTiledElevationSource elevationSource = new ArcGISTiledElevationSource("http://elevation3d.arcgis" +
        ".com/arcgis/rest/services/WorldElevation3D/Terrain3D/ImageServer");
      surface.getElevationSources().add(elevationSource);
      scene.setBaseSurface(surface);

      // add a scene layer
      final String buildings = "http://tiles.arcgis.com/tiles/P3ePLMYs2RVChkJx/arcgis/rest/services/DevA_BuildingShells" +
        "/SceneServer/layers/0";
      ArcGISSceneLayer sceneLayer = new ArcGISSceneLayer(buildings);
      scene.getOperationalLayers().add(sceneLayer);

      // add a camera and initial camera position
      Camera camera = new Camera( 45.54605153789073,-122.69033380511073, 941.0002111233771, 162.58544227544266, 60.0, 0.0);
      sceneView.setViewpointCamera(camera);

      // set atmosphere effect to realistic
      sceneView.setAtmosphereEffect(AtmosphereEffect.REALISTIC);

      //set the time label on the control panel
      setTimeLabel();

      // set the slider to display tick labels as time strings
      setSliderLabels();

      //update the atmosphere effect based on the button clicked
      noSunButton.setOnAction(event -> sceneView.setSunLighting(LightingMode.NO_LIGHT));
      sunOnlyButton.setOnAction(event -> sceneView.setSunLighting(LightingMode.LIGHT));
      sunAndShadowsButton.setOnAction(event -> sceneView.setSunLighting(LightingMode.LIGHT_AND_SHADOWS));

    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
    }
  }

  /**
   * Update the hour of the day based on the value of the slider.
   */
  @FXML
  public void changeTimeOfDay() {
    // when the slider changes, update the hour of the day based on the value of the slider
    timeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {

        // get value from the slider
        Double timeFromSlider = timeSlider.getValue();
        // to get minutes from timer value, split double to the two values after decimal place
        String timeAsString = timeFromSlider.toString();

        // get the hour value from the slider
        int hourFromSlider = timeFromSlider.intValue();

        if (timeAsString.length() > 4) {
          String subString = timeAsString.substring(3, 5);

          int minutes = Integer.valueOf(subString);
          // convert figures into minutes
          float actualMinutes = ((float) minutes / (float) 100) * (float) 60;
          // round into an integer
          int minuteFromSlider = Math.round(actualMinutes);

          // set the calendar for given hour and minute from slider value
          calendar.set(2018, 7, 10, hourFromSlider, minuteFromSlider);

        } else {
          calendar.set(2018, 7, 10, hourFromSlider, 00);
        }

        // update label to reflect current date and time
        String dynamicDateAndTime = calendar.getTime().toString();
        String dynamicDateAndTimeTidied = dynamicDateAndTime.substring(0, 16);
        time.setText(dynamicDateAndTimeTidied);

        // set the sun time to calendar
        sceneView.setSunTime(calendar);
      }
    );
  }

  /**
   * Set labels to display on the slider.
   */
  private void setSliderLabels() {

    timeSlider.setLabelFormatter(new StringConverter<Double>() {

      @Override
      public String toString(Double object) {

        if (object == 4) return "4am";
        if (object == 8) return "8am";
        if (object == 12) return "Midday";
        if (object == 16) return "4pm";
        if (object == 20) return "8pm";

        return "Midnight";
      }

      @Override
      public Double fromString(String string) {
        return null;
      }
    });
  }

  /**
   * Set the time to display in a label.
   */
  public void setTimeLabel() {

    // set a new calendar and add a date and time
    calendar = new GregorianCalendar(2018, 7, 10, 12, 00, 0);
    sceneView.setSunTime(calendar);

    // get information about calendar
    String dateAndTime = calendar.getTime().toString();

    // tidy string to just return date and time (hours and minutes)
    String dateAndTimeTidied = dateAndTime.substring(0, 16);

    // set a label to display the tidied date and time
    time.setText(dateAndTimeTidied);
  }

  /**
   * Disposes application resources.
   */
  void terminate() {
    if (sceneView != null) {
      sceneView.dispose();
    }
  }
}
