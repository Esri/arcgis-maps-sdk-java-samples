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

package com.esri.samples.analysis.distance_measurement_analysis;

import java.text.DecimalFormat;
import java.util.concurrent.ExecutionException;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import com.esri.arcgisruntime.UnitSystem;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.geoanalysis.LocationDistanceMeasurement;
import com.esri.arcgisruntime.geometry.Distance;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.ArcGISSceneLayer;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.ArcGISTiledElevationSource;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Surface;
import com.esri.arcgisruntime.mapping.view.AnalysisOverlay;
import com.esri.arcgisruntime.mapping.view.Camera;
import com.esri.arcgisruntime.mapping.view.SceneView;

public class DistanceMeasurementAnalysisController {

  @FXML private SceneView sceneView;
  @FXML private Label directDistanceLabel;
  @FXML private Label verticalDistanceLabel;
  @FXML private Label horizontalDistanceLabel;
  @FXML private ComboBox<UnitSystem> unitSystemComboBox;

  private LocationDistanceMeasurement distanceMeasurement;

  public void initialize() {

    // create a scene and set it to the scene view
    ArcGISScene scene = new ArcGISScene();
    scene.setBasemap(Basemap.createImagery());
    sceneView.setArcGISScene(scene);

    // add base surface for elevation data
    Surface surface = new Surface();
    surface.getElevationSources().add(new ArcGISTiledElevationSource("http://elevation3d.arcgis" +
        ".com/arcgis/rest/services/WorldElevation3D/Terrain3D/ImageServer"));
    surface.getElevationSources().add(new ArcGISTiledElevationSource("https://tiles.arcgis.com/tiles/d3voDfTFbHOCRwVR/arcgis/rest/services/MNT_IDF/ImageServer"));
    scene.setBaseSurface(surface);

    final String buildings = "http://tiles.arcgis.com/tiles/P3ePLMYs2RVChkJx/arcgis/rest/services/Buildings_Brest/SceneServer/layers/0";
    ArcGISSceneLayer sceneLayer = new ArcGISSceneLayer(buildings);
    scene.getOperationalLayers().add(sceneLayer);

    // create an analysis overlay and add it to the scene view
    AnalysisOverlay analysisOverlay = new AnalysisOverlay();
    sceneView.getAnalysisOverlays().add(analysisOverlay);

    // initialize a distance measurement and add it to the analysis overlay
    Point start = new Point(-4.494677, 48.384472, 24.772694, SpatialReferences.getWgs84());
    Point end = new Point(-4.495646, 48.384377, 58.501115, SpatialReferences.getWgs84());
    distanceMeasurement = new LocationDistanceMeasurement(start, end);
    analysisOverlay.getAnalyses().add(distanceMeasurement);

    // zoom to the initial measurement
    sceneView.setViewpointCamera(new Camera(start, 200.0, 0.0, 45.0, 0.0));

    // show the distances in the UI when the measurement changes
    DecimalFormat decimalFormat = new DecimalFormat("#.##");
    distanceMeasurement.addMeasurementChangedListener(e -> {
      Distance directDistance = e.getDirectDistance();
      Distance verticalDistance = e.getVerticalDistance();
      Distance horizontalDistance = e.getHorizontalDistance();
      directDistanceLabel.setText(decimalFormat.format(directDistance.getValue()) + " " + directDistance.getUnit()
          .getAbbreviation());
      verticalDistanceLabel.setText(decimalFormat.format(verticalDistance.getValue()) + " " + verticalDistance
          .getUnit().getAbbreviation());
      horizontalDistanceLabel.setText(decimalFormat.format(horizontalDistance.getValue()) + " " + horizontalDistance
          .getUnit().getAbbreviation());
    });

    // add the unit system options to the UI initialized with the measurement's default value (METRIC)
    unitSystemComboBox.getItems().addAll(UnitSystem.values());
    unitSystemComboBox.setValue(distanceMeasurement.getUnitSystem());

    // remove the default mouse move handler
    sceneView.setOnMouseMoved(null);

    // create a handler to update the measurement's end location when the mouse moves
    EventHandler<MouseEvent> mouseMoveEventHandler = event -> {
      Point2D point2D = new Point2D(event.getX(), event.getY());
      // get the scene location from the screen position
      ListenableFuture<Point> pointFuture = sceneView.screenToLocationAsync(point2D);
      pointFuture.addDoneListener(() -> {
        try {
          // update the end location
          Point point = pointFuture.get();
          distanceMeasurement.setEndLocation(point);
        } catch (InterruptedException | ExecutionException e) {
          e.printStackTrace();
        }
      });
    };

    // mouse click to start/stop moving a measurement
    sceneView.setOnMouseClicked(event -> {
      if (event.isStillSincePress() && event.getButton() == MouseButton.PRIMARY) {
        // get the clicked location
        Point2D point2D = new Point2D(event.getX(), event.getY());
        ListenableFuture<Point> pointFuture = sceneView.screenToLocationAsync(point2D);
        pointFuture.addDoneListener(() -> {
          try {
            Point point = pointFuture.get();
            if (sceneView.getOnMouseMoved() == null) {
              // reset the measurement at the clicked location and start listening for mouse movement
              sceneView.setOnMouseMoved(mouseMoveEventHandler);
              distanceMeasurement.setStartLocation(point);
              distanceMeasurement.setEndLocation(point);
            } else {
              // set the measurement end location and stop listening for mouse movement
              sceneView.setOnMouseMoved(null);
              distanceMeasurement.setEndLocation(point);
            }
          } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
          }
        });
      }
    });
  }

  /**
   * Update the measurement's unit system when the combo box option is changed.
   */
  @FXML
  private void changeUnitSystem() {

    distanceMeasurement.setUnitSystem(unitSystemComboBox.getValue());
  }

  /**
   * Stops and releases all resources used in application.
   */
  void terminate() {

    if (sceneView != null) {
      sceneView.dispose();
    }
  }

}
