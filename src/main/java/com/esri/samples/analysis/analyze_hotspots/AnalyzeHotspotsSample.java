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

package com.esri.samples.analysis.analyze_hotspots;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutionException;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import com.esri.arcgisruntime.concurrent.Job;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.layers.ArcGISMapImageLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.tasks.geoprocessing.GeoprocessingJob;
import com.esri.arcgisruntime.tasks.geoprocessing.GeoprocessingParameters;
import com.esri.arcgisruntime.tasks.geoprocessing.GeoprocessingResult;
import com.esri.arcgisruntime.tasks.geoprocessing.GeoprocessingString;
import com.esri.arcgisruntime.tasks.geoprocessing.GeoprocessingTask;

public class AnalyzeHotspotsSample extends Application {

  private MapView mapView;
  private GeoprocessingJob geoprocessingJob;

  @Override
  public void start(Stage stage) {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);

      // set title, size, and add scene to stage
      stage.setTitle("Analyze Hotspots Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // create a view with a map and topographic basemap
      ArcGISMap map = new ArcGISMap(Basemap.createTopographic());
      map.setInitialViewpoint(new Viewpoint(new Point(-13671170, 5693633, SpatialReference.create(3857)), 57779));
      mapView = new MapView();
      mapView.setMap(map);

      // show progress indicator when geoprocessing task is loading or geoprocessing job is running
      ProgressIndicator progress = new ProgressIndicator(ProgressIndicator.INDETERMINATE_PROGRESS);
      progress.setMaxWidth(30);

      // create date pickers to choose the date range, initialize with the min & max dates
      SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
      Timestamp minDate = Timestamp.valueOf("1998-01-01 00:00:00");
      Timestamp maxDate = Timestamp.valueOf("1998-05-31 00:00:00");
      DatePicker begDatePicker = new DatePicker(minDate.toLocalDateTime().toLocalDate());
      begDatePicker.setPromptText("Beg");
      DatePicker endDatePicker = new DatePicker(maxDate.toLocalDateTime().toLocalDate());
      endDatePicker.setPromptText("End");

      // create a button to create and start the geoprocessing job, disable until geoprocessing task is loaded
      Button analyzeButton = new Button("Analyze Hotspots");
      analyzeButton.setDisable(true);

      VBox controlsVBox = new VBox(6);
      controlsVBox.setMaxSize(180, 250);
      controlsVBox.getChildren().addAll(begDatePicker, endDatePicker, analyzeButton);

      // and the mapView, controls, and progress indicator to the stack pane
      stackPane.getChildren().addAll(mapView, controlsVBox, progress);
      StackPane.setAlignment(progress, Pos.CENTER);
      StackPane.setAlignment(controlsVBox, Pos.TOP_LEFT);
      StackPane.setMargin(controlsVBox, new Insets(10, 0, 0, 10));

      // create the geoprocessing task with the service URL and load it
      GeoprocessingTask geoprocessingTask = new GeoprocessingTask("http://sampleserver6.arcgisonline.com/arcgis/rest/services/911CallsHotspot/GPServer/911%20Calls%20Hotspot");
      geoprocessingTask.loadAsync();

      geoprocessingTask.addDoneLoadingListener(() -> {
        if (geoprocessingTask.getLoadStatus() == LoadStatus.LOADED) {
          // hide the progress when the geoprocessing task is done loading and enable the button
          progress.setVisible(false);
          analyzeButton.setDisable(false);

          analyzeButton.setOnAction(e -> {
            // clear any previous results, and show the progress indicator until the job is complete
            map.getOperationalLayers().clear();
            progress.setVisible(true);

            // get the task's default geoprocessing parameters
            ListenableFuture<GeoprocessingParameters> defaultParameters = geoprocessingTask.createDefaultParametersAsync();
            defaultParameters.addDoneListener(() -> {
              try {
                GeoprocessingParameters parameters = defaultParameters.get();

                // get timestamps from the datepickers
                Timestamp beg = Timestamp.valueOf(begDatePicker.getValue().atStartOfDay());
                Timestamp end = Timestamp.valueOf(endDatePicker.getValue().atStartOfDay());

                // check that the timestamps are in the valid range for the data
                if (beg.getTime() >= minDate.getTime() && beg.getTime() <= maxDate.getTime() && end.getTime() >=
                    minDate.getTime() && end.getTime() <= maxDate.getTime()) {

                  // add a date range query to the geoprocessing parameters
                  String begTimestamp = dateFormat.format(beg);
                  String endTimestamp = dateFormat.format(end);
                  String query = "(\"DATE\" > date '" + begTimestamp + "' AND \"Date\" < date '" + endTimestamp + "')";
                  parameters.getInputs().put("Query", new GeoprocessingString(query));

                  // create a geoprocessing job from the task with the parameters
                  geoprocessingJob = geoprocessingTask.createJob(parameters);

                  // start the job and wait for the result
                  geoprocessingJob.start();
                  geoprocessingJob.addJobDoneListener(() -> {
                    if (geoprocessingJob.getStatus() == Job.Status.SUCCEEDED) {
                      // get the map image layer from the job's result
                      GeoprocessingResult geoprocessingResult = geoprocessingJob.getResult();
                      ArcGISMapImageLayer hotSpotLayer = geoprocessingResult.getMapImageLayer();
                      // make the layer semi-transparent to reference the basemap streets underneath
                      hotSpotLayer.setOpacity(0.7f);
                      // add the layer to the map and zoom to it when loaded
                      map.getOperationalLayers().add(hotSpotLayer);
                      hotSpotLayer.loadAsync();
                      hotSpotLayer.addDoneLoadingListener(() -> {
                        if (hotSpotLayer.getLoadStatus() == LoadStatus.LOADED) {
                          mapView.setViewpointGeometryAsync(hotSpotLayer.getFullExtent());
                        } else {
                          new Alert(AlertType.ERROR, "Result layer failed to load: " + hotSpotLayer.getLoadError()
                              .getMessage()).show();
                        }
                      });
                    } else {
                      new Alert(AlertType.ERROR, "Geoprocessing job failed: " + geoprocessingJob.getError().getMessage()).show();
                    }
                    // hide the progress when the job is complete
                    progress.setVisible(false);
                    // cancel the job if it's still going and set it to null to re-enable the mouse click listener
                    if (geoprocessingJob != null) {
                      geoprocessingJob.cancel();
                      geoprocessingJob = null;
                    }
                  });
                } else {
                  new Alert(AlertType.ERROR, "Time range must be within " + minDate.toString() + " and " + maxDate
                      .toString()).show();
                  progress.setVisible(false);
                }
              } catch (InterruptedException | ExecutionException ex) {
                new Alert(AlertType.ERROR, "Error creating default geoprocessing parameters").show();
                progress.setVisible(false);
              }
            });
          });
        } else {
          new Alert(AlertType.ERROR, "Failed to load geoprocessing task").show();
          progress.setVisible(false);
        }
      });
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

    if (mapView != null) {
      mapView.dispose();
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
