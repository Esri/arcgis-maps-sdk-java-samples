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
import javafx.scene.control.ListCell;
import javafx.scene.control.Slider;
import javafx.util.StringConverter;

import com.esri.arcgisruntime.layers.ArcGISSceneLayer;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.ArcGISTiledElevationSource;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Surface;
import com.esri.arcgisruntime.mapping.view.AtmosphereEffect;
import com.esri.arcgisruntime.mapping.view.Camera;
import com.esri.arcgisruntime.mapping.view.LightingMode;
import com.esri.arcgisruntime.mapping.view.SceneView;

import static java.lang.StrictMath.floor;

public class RealisticLightingAndShadowsController {

  @FXML private SceneView sceneView;
  @FXML private Label time;
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
      final String buildings = "http://tiles.arcgis.com/tiles/P3ePLMYs2RVChkJx/arcgis/rest/services/DevA_BuildingShells/SceneServer/layers/0";
      ArcGISSceneLayer sceneLayer = new ArcGISSceneLayer(buildings);
      scene.getOperationalLayers().add(sceneLayer);

      // add a camera and initial camera position
      Camera camera = new Camera(45.54605153789073, -122.69033380511073, 941.0002111233771, 162.58544227544266, 60.0,0.0);
      sceneView.setViewpointCamera(camera);

      // set atmosphere effect to realistic
      sceneView.setAtmosphereEffect(AtmosphereEffect.REALISTIC);

      // set a new calendar and add a date and time
      calendar = new GregorianCalendar(2018, 7, 10, 12, 00, 0);
      calendar.setTimeZone(TimeZone.getTimeZone("PST"));

      // set the time label on the control panel
      sceneView.setSunTime(calendar);

      // tidy string to just return date and time (hours and minutes)
      dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm");
      dateFormat.setTimeZone(TimeZone.getTimeZone("PST"));
      String dateAndTimeTidied = dateFormat.format(calendar.getTime());

      // set a label to display the tidied date and time
      time.setText(dateAndTimeTidied);

      // set the slider to display tick labels as time strings
      setSliderLabels();

      // set the combo box to display the lighting modes as strings
      setComboBoxLabels();

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
  public void changeTimeOfDay() {
    // when the slider changes, update the hour of the day based on the value of the slider
    timeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {

        // get the hour value from the slider
        int hours = newValue.intValue();

        // get the minutes from the slider
        double afterDecimal = newValue.doubleValue() - floor(newValue.doubleValue());
        int minutes = (int) (afterDecimal * 60);

        // set the calendar for given hour and minute from slider value
        calendar.set(Calendar.HOUR_OF_DAY, hours);
        calendar.set(Calendar.MINUTE, minutes);

        // tidy string to just return date and time (hours and minutes)
        String dateAndTimeTidied = dateFormat.format(calendar.getTime());

        // update label to reflect current date and time
        time.setText(dateAndTimeTidied);

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
      public String toString(Double hour) {

        if (hour == 4) return "4am";
        if (hour == 8) return "8am";
        if (hour == 12) return "Midday";
        if (hour == 16) return "4pm";
        if (hour == 20) return "8pm";

        return "Midnight";
      }

      @Override
      public Double fromString(String string) {
        return null;
      }
    });
  }

  /**
   * Set labels to display in the combo box.
   */
  private void setComboBoxLabels() {

    comboBox.setConverter(new StringConverter<LightingMode>() {

      @Override
      public String toString(LightingMode mode) {

        if (mode == LightingMode.LIGHT) return "Sun light only";
        if (mode == LightingMode.LIGHT_AND_SHADOWS) return "Sun light with shadows";
        if (mode == LightingMode.NO_LIGHT) return "No sun light effect";

        else return "Sun light only";
      }

      @Override
      public LightingMode fromString(String string) {
        return null;
      }
    });

    comboBox.setCellFactory(comboBox -> new ListCell<LightingMode>() {

      @Override
      protected void updateItem(LightingMode mode, boolean empty) {

        super.updateItem(mode, empty);

        if (mode == LightingMode.LIGHT) setText("Sun light only");
        else if (mode == LightingMode.LIGHT_AND_SHADOWS) setText("Sun light with shadows");
        else if (mode == LightingMode.NO_LIGHT) setText("No sun light effect");

        else setText("Sun light only");
      }
    });
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
