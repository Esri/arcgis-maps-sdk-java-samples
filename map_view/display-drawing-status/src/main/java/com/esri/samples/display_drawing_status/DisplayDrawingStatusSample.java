/*
 * Copyright 2017 Esri.
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

package com.esri.samples.iew.display_drawing_status;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.DrawStatus;
import com.esri.arcgisruntime.mapping.view.MapView;

public class DisplayDrawingStatusSample extends Application {

  private MapView mapView;

  @Override
  public void start(Stage stage) {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);

      // set title, size, and add scene to stage
      stage.setTitle("Display Drawing Status Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // create progress bar
      ProgressBar progressBar = new ProgressBar();
      progressBar.setMaxWidth(240.0);

      // create a ArcGISMap with topographic basemap
      final ArcGISMap map = new ArcGISMap(Basemap.createTopographic());

      // create a starting viewpoint for the ArcGISMap
      SpatialReference spatialReference = SpatialReferences.getWebMercator();
      Point bottomLeftPoint = new Point(-1.5054808160556655E7, 2718975.702666207, spatialReference);
      Point topRightPoint = new Point(-6810317.90634398, 6850505.377826911, spatialReference);
      Envelope envelope = new Envelope(bottomLeftPoint, topRightPoint);
      Viewpoint viewpoint = new Viewpoint(envelope);

      // set the initial viewpoint of ArcGISMap to viewpoint
      map.setInitialViewpoint(viewpoint);

      // create a feature table from a service URL
      final ServiceFeatureTable featureTable = new ServiceFeatureTable(
          "http://sampleserver6.arcgisonline.com/arcgis/rest/services/DamageAssessment/FeatureServer/0");

      // create a feature layer from service table
      final FeatureLayer featureLayer = new FeatureLayer(featureTable);

      // add feature layer to ArcGISMap
      map.getOperationalLayers().add(featureLayer);

      // create a view for this ArcGISMap
      mapView = new MapView();

      // set map to be displayed in map view
      mapView.setMap(map);

      mapView.addDrawStatusChangedListener(e -> {
        // check to see if draw status is in progress
        if (e.getDrawStatus() == DrawStatus.IN_PROGRESS) {
          // reset progress bar as in progress
          progressBar.setProgress(-100.0);

          // check to see if draw status is complete
        } else if (e.getDrawStatus() == DrawStatus.COMPLETED) {
          // set progress bar as complete
          progressBar.setProgress(100.0);
        }
      });

      // add map view and progressBar to stack pane
      stackPane.getChildren().addAll(mapView, progressBar);
      StackPane.setAlignment(progressBar, Pos.TOP_LEFT);
      StackPane.setMargin(progressBar, new Insets(10, 0, 0, 10));
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
