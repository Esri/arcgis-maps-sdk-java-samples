/*
 * Copyright 2020 Esri
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.esri.samples.show_location_history;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;

public class ShowLocationHistorySample extends Application {

  private MapView mapView;
  private boolean isTrackingEnabled = false;

  @Override
  public void start(Stage stage) {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);

      // set title, size, and add scene to stage
      stage.setTitle("Show Location History Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // create a map with a dark gray canvas basemap
      ArcGISMap map = new ArcGISMap("https://www.arcgis.com/home/item.html?id=1970c1995b8f44749f4b9b6e81b5ba45");

      // create a map view and set its map
      mapView = new MapView();
      mapView.setMap(map);

      // set the map views's viewpoint centered on Los Angeles, California and scaled
      Point center = new Point(-13185535.98, 4037766.28, SpatialReference.create(3857));
      mapView.setViewpoint(new Viewpoint(center, 7000));

      // create a graphics overlay for the points and use a red circle for the symbols
      GraphicsOverlay locationHistoryOverlay = new GraphicsOverlay();
      SimpleMarkerSymbol locationSymbol = new SimpleMarkerSymbol(
        SimpleMarkerSymbol.Style.CIRCLE, 0xFFFF0000, 10f);
      SimpleRenderer locationHistoryRenderer = new SimpleRenderer(locationSymbol);
      locationHistoryOverlay.setRenderer(locationHistoryRenderer);

      // create a graphics overlay for the lines connecting the points and use a green line for the symbol
      GraphicsOverlay locationHistoryLineOverlay = new GraphicsOverlay();
      SimpleLineSymbol locationLineSymbol = new SimpleLineSymbol(
        SimpleLineSymbol.Style.SOLID, 0xFF00FF00, 2.0f);
      SimpleRenderer locationHistoryLineRenderer = new SimpleRenderer(locationLineSymbol);
      locationHistoryLineOverlay.setRenderer(locationHistoryLineRenderer);

      // add the graphics overlays to the map view
      mapView.getGraphicsOverlays().addAll(Arrays.asList(locationHistoryOverlay, locationHistoryLineOverlay));
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
