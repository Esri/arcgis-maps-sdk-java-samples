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

package com.esri.samples.realistic_lighting_and_shadows;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;

import com.esri.arcgisruntime.layers.ArcGISSceneLayer;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.ArcGISTiledElevationSource;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Surface;
import com.esri.arcgisruntime.mapping.view.AtmosphereEffect;
import com.esri.arcgisruntime.mapping.view.Camera;
import com.esri.arcgisruntime.mapping.view.LightingMode;
import com.esri.arcgisruntime.mapping.view.SceneView;

public class RealisticLightingAndShadowsController {

  @FXML private SceneView sceneView;
  @FXML private Label timeLabel;
  @FXML private Slider timeSlider;
  @FXML private ComboBox<LightingMode> comboBox;
  private Surface surface;
  private Calendar calendar;
  private SimpleDateFormat dateFormat;

  public void initialize() {
    try {

      // create a scene and add a basemap to it
      ArcGISScene scene = new ArcGISScene();
      scene.setBasemap(Basemap.createTopographic());

      // add the scene to the scene view
      sceneView.setArcGISScene(scene);

      // add a base surface for elevation data to the scene
      surface = new Surface();
      ArcGISTiledElevationSource elevationSource = new ArcGISTiledElevationSource("http://elevation3d.arcgis.com/arcgis/rest/services/WorldElevation3D/Terrain3D/ImageServer");
      surface.getElevationSources().add(elevationSource);
      scene.setBaseSurface(surface);

      // add a scene layer
      ArcGISSceneLayer sceneLayer = new ArcGISSceneLayer("http://tiles.arcgis.com/tiles/P3ePLMYs2RVChkJx/arcgis/rest/services/DevA_BuildingShells/SceneServer/layers/0");
      scene.getOperationalLayers().add(sceneLayer);

      // add a camera and initial camera position
      Camera camera = new Camera(45.54605153789073, -122.69033380511073, 941.0002111233771, 162.58544227544266, 60.0,0.0);
      sceneView.setViewpointCamera(camera);

      // set atmosphere effect to realistic
      sceneView.setAtmosphereEffect(AtmosphereEffect.REALISTIC);

      // set a calendar with a date and time
      calendar = new GregorianCalendar(2018, 7, 10, 12, 00, 0);
      calendar.setTimeZone(TimeZone.getTimeZone("PST"));

      // set the sun time to the calendar
      sceneView.setSunTime(calendar);

      // format the string to just return the date and time (hours and minutes)
      dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm");
      dateFormat.setTimeZone(TimeZone.getTimeZone("PST"));
      String formattedDateAndTime = dateFormat.format(calendar.getTime());

      // set a label to display the formatted date and time
      timeLabel.setText(formattedDateAndTime);

      // set the slider to display tick labels as time strings
      timeSlider.setLabelFormatter(new SliderStringConverter());

      // add the lighting modes to the combo box
      comboBox.getItems().add(LightingMode.NO_LIGHT);
      comboBox.getItems().add(LightingMode.LIGHT);
      comboBox.getItems().add(LightingMode.LIGHT_AND_SHADOWS);

      // show the name of the lighting modes in the combo box
      comboBox.setConverter(new ComboBoxStringConverter());
      comboBox.setCellFactory(comboBox -> new LightingModeListCell());

      // update the atmosphere effect based on the lighting mode chosen from the combo box
      comboBox.getSelectionModel().selectedItemProperty().addListener(e -> {
        sceneView.setSunLighting(comboBox.getSelectionModel().getSelectedItem());
      });

    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
    }
  }

  /**
   * Set the sun time based on the time from the slider.
   */
  @FXML
  public void updateTimeOfDay() {
    // when the slider changes, update the hour of the day based on the value of the slider
    timeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {

        // get the value from the slider
        int sliderValue = newValue.intValue();

        // get the hour from the slider
        int hours = sliderValue / 60;

        // get the minutes from the slider
        int minutes = sliderValue % 60;

        // set the calendar with the hour and minute values from the slider
        calendar.set(Calendar.MONTH, 7);
        calendar.set(Calendar.DAY_OF_MONTH, 10);
        calendar.set(Calendar.HOUR_OF_DAY, hours);
        calendar.set(Calendar.MINUTE, minutes);

        // format the string to just return the date and time (hours and minutes)
        String formattedDateAndTime = dateFormat.format(calendar.getTime());

        // update the label to reflect the current date and time
        timeLabel.setText(formattedDateAndTime);

        // set the sun time to the calendar
        sceneView.setSunTime(calendar);
      }
    );
  }

  /**
   * Disposes of application resources.
   */
  void terminate() {
    if (sceneView != null) {
      sceneView.dispose();
    }
  }
}
