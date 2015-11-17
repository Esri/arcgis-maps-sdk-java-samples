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

package com.esri.sampleviewer.samples.mapview;

import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Map;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.Viewpoint;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * This sample demonstrates two different ways to rotate the view of the Map.
 * <h4>How it Works</h4>
 * <li>To display the {@link MapView} with a starting rotation pass a
 * {@link Viewpoint} that is initialized with an angle in degrees and a
 * {@link Envelope} to the {@link MapView#setViewpointAsync} method.</li>
 * <li>The {@link MapView#setViewpointRotationAsync} method takes an angle in
 * degrees and rotates the MapView to that angle.</li>
 */
public class MapRotation extends Application {

  private MapView mapView;

  @Override
  public void start(Stage stage) throws Exception {

    // create stack pane and application scene
    StackPane stackPane = new StackPane();
    Scene scene = new Scene(stackPane);
    scene.getStylesheets().add(getClass()
        .getResource("../resources/SamplesTheme.css").toExternalForm());

    // set title, size, and add scene to stage
    stage.setTitle("Map Rotation Sample");
    stage.setWidth(800);
    stage.setHeight(700);
    stage.setScene(scene);
    stage.show();

    // create a control panel
    VBox vBoxControl = new VBox(6);
    vBoxControl.setMaxSize(250, 190);
    vBoxControl.getStyleClass().add("panel-region");

    // create sample description
    Label descriptionLabel = new Label("Sample Description:");
    descriptionLabel.getStyleClass().add("panel-label");
    TextArea description =
        new TextArea("This sample shows how to rotate a Map. This Map starts"
            + " with a five degreee rotation. The slider can also be used to "
            + "rotate the Map.");
    description.setWrapText(true);
    description.autosize();
    description.setEditable(false);

    // create a slider with a range of 360 units and start it half way
    Slider slider = new Slider(-180.0, 180.0, 0.0);
    slider.setDisable(true);

    // listen for the value in the slider to change
    slider.valueProperty().addListener(e -> {
      // rotate map view based on new value in slider
      mapView.setViewpointRotationAsync(slider.getValue());
    });

    // add controls to the user interface panel
    vBoxControl.getChildren().addAll(descriptionLabel, description, slider);
    try {

      // create a map with topographic basemap
      Map map = new Map(Basemap.createTopographic());

      // enable slider when map view is done loading
      map.addDoneLoadingListener(() -> {
        slider.setDisable(false);
      });

      // create a view and add a map to it
      mapView = new MapView();
      mapView.setMap(map);

      // create a starting viewpoint for the map view
      SpatialReference spatialReference = SpatialReferences.getWebMercator();
      Point pointBottomLeft =
          new Point(-13639984.0, 4537387.0, spatialReference);
      Point pointTopRight = new Point(-13606734.0, 4558866, spatialReference);
      Envelope envelope = new Envelope(pointBottomLeft, pointTopRight);
      Viewpoint viewpoint = new Viewpoint(envelope, 5.0f);

      // set viewpoint to the map view
      mapView.setViewpointAsync(viewpoint);

      // add map view and control panel to stack pane
      stackPane.getChildren().addAll(mapView, vBoxControl);
      StackPane.setAlignment(vBoxControl, Pos.TOP_LEFT);
      StackPane.setMargin(vBoxControl, new Insets(10, 0, 0, 10));
    } catch (Exception e) {
      // on any error, display the stack trace
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
