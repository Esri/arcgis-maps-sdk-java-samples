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

package com.esri.samples.map.min_max_scale;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.MapView;

public class MinMaxScaleSample extends Application {

  private MapView mapView;

  @Override
  public void start(Stage stage) throws Exception {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);

      // set title, size, and add scene to stage
      stage.setTitle("Min Max Scale Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // create a ArcGISMap with basemap streets
      ArcGISMap map = new ArcGISMap(Basemap.createStreets());

      // set the scale at which this layer can be viewed
      map.setMinScale(8000);
      map.setMaxScale(2000);

      // create a view for this ArcGISMap and set ArcGISMap to it
      mapView = new MapView();
      mapView.setMap(map);

      // a point where the map view will focus and zoom to
      mapView.setViewpoint(new Viewpoint(new Point(-355453, 7548720, SpatialReferences.getWebMercator()), 3000));

      // add the map view to stack pane
      stackPane.getChildren().addAll(mapView);

    } catch (Exception e) {
      // on any error, display the stack trace
      e.printStackTrace();
    }
  }

  /**
   * Stops and releases all resources used in application.
   */
  @Override
  public void stop() throws Exception {

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
