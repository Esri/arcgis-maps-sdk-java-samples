/*
 * Copyright 2016 Esri. Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package com.esri.samples.na.offline_routing;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.TileCache;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.layers.ArcGISTiledLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.IdentifyGraphicsOverlayResult;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.LineSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.TextSymbol;
import com.esri.arcgisruntime.tasks.networkanalysis.Route;
import com.esri.arcgisruntime.tasks.networkanalysis.RouteParameters;
import com.esri.arcgisruntime.tasks.networkanalysis.RouteResult;
import com.esri.arcgisruntime.tasks.networkanalysis.RouteTask;
import com.esri.arcgisruntime.tasks.networkanalysis.Stop;
import com.esri.arcgisruntime.tasks.networkanalysis.TravelMode;

public class OfflineRoutingSample extends Application {

  private MapView mapView;
  private GraphicsOverlay stopsOverlay;
  private GraphicsOverlay routeOverlay;
  private RouteTask routeTask;
  private RouteParameters routeParameters;
  private LineSymbol lineSymbol;

  private EventHandler<MouseEvent> mouseMovedListener;

  @Override
  public void start(Stage stage) throws Exception {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);
      scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

      // set title, size, and add scene to stage
      stage.setTitle("Offline Routing Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // create the map's basemap from a local tile package
      TileCache tileCache = new TileCache("./samples-data/san_diego/streetmap_SD.tpk");
      ArcGISTiledLayer tiledLayer = new ArcGISTiledLayer(tileCache);
      Basemap basemap = new Basemap(tiledLayer);
      ArcGISMap map = new ArcGISMap(basemap);
      mapView = new MapView();
      mapView.setMap(map);

      // create graphics overlays for route and stops
      stopsOverlay = new GraphicsOverlay();
      routeOverlay = new GraphicsOverlay();
      mapView.getGraphicsOverlays().addAll(Arrays.asList(routeOverlay, stopsOverlay));

      //[DocRef: Name=Route_And_Directions-Find_Route-Geodatabase-Java
      // create an offline RouteTask
      routeTask = new RouteTask("./samples-data/san_diego/sandiego.geodatabase", "Streets_ND");
      routeTask.loadAsync();
      //[DocRef: Name=Route_And_Directions-Find_Route-Geodatabase-Java

      // create route parameters
      routeParameters = routeTask.createDefaultParametersAsync().get();

      // create symbol for route
      lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0xFF0000FF, 3);

      // create mouse moved event listener to update the route when moving stops
      mouseMovedListener = event -> {

        if (!stopsOverlay.getSelectedGraphics().isEmpty()) {

          Point2D hoverLocation = new Point2D(event.getX(), event.getY());
          Point hoverPoint = mapView.screenToLocation(hoverLocation);

          // update the moving stop and graphic
          Graphic stopGraphic = stopsOverlay.getSelectedGraphics().get(0);
          stopGraphic.setGeometry(hoverPoint);
          int stopIndex = (int) stopGraphic.getAttributes().get("stopIndex");
          Stop newStop = new Stop(hoverPoint);
          routeParameters.getStops().set(stopIndex, newStop);
          stopGraphic.getAttributes().put("stopIndex", stopIndex);

          // update route
          if (stopsOverlay.getGraphics().size() > 1) {
            updateRoute();
          }
        }

      };

      // use mouse clicks to add and move stops
      mapView.setOnMouseClicked(event -> {

        if (routeTask.getLoadStatus() == LoadStatus.LOADED && event.isStillSincePress()) {

          // get mouse click location
          Point2D clickLocation = new Point2D(event.getX(), event.getY());
          Point point = mapView.screenToLocation(clickLocation);

          // left click adds a stop when not already moving a stop
          if (event.getButton() == MouseButton.PRIMARY && stopsOverlay.getSelectedGraphics().isEmpty()) {

            // add stop to route parameters
            Stop stop = new Stop(point);
            routeParameters.getStops().add(stop);

            // create graphic for stop
            TextSymbol stopLabel = new TextSymbol(20, Integer.toString(stopsOverlay.getGraphics().size() + 1),
                0xFFFF0000, TextSymbol.HorizontalAlignment.RIGHT, TextSymbol.VerticalAlignment.TOP);

            // save the stop with the graphic
            HashMap<String, Object> attributes = new HashMap<>();
            attributes.put("stopIndex", routeParameters.getStops().size() - 1);

            // create and add the stop graphic to the graphics overlay
            Graphic stopGraphic = new Graphic(point, attributes, stopLabel);
            stopsOverlay.getGraphics().add(stopGraphic);

            // update the route
            updateRoute();
          }

          // right click to select/deselect a stop to move
          if (event.getButton() == MouseButton.SECONDARY) {

            // select a stop
            if (stopsOverlay.getSelectedGraphics().isEmpty()) {
              // identify the selected graphic
              ListenableFuture<IdentifyGraphicsOverlayResult> results = mapView.identifyGraphicsOverlayAsync(
                  stopsOverlay, clickLocation, 10, false);
              results.addDoneListener(() -> {
                try {
                  List<Graphic> graphics = results.get().getGraphics();
                  if (graphics.size() > 0) {
                    // set the graphic as selected
                    Graphic graphic = graphics.get(0);
                    graphic.setSelected(true);

                    // add the mouse move event listener to update the route
                    mapView.setOnMouseMoved(mouseMovedListener);
                  }

                } catch (InterruptedException | ExecutionException e) {
                  e.printStackTrace();
                }
              });
            } else {
              stopsOverlay.clearSelection();

              // remove the mouse moved event listener when not moving stops
              mapView.setOnMouseMoved(null);
            }
          }
        }
      });

      // create travel mode selector
      List<TravelMode> travelModeList = routeTask.getRouteTaskInfo().getTravelModes();
      ComboBox<TravelMode> travelModes = new ComboBox<>();
      travelModes.getItems().addAll(travelModeList);
      travelModes.getSelectionModel().selectedItemProperty().addListener(o -> {
        routeParameters.setTravelMode(travelModes.getSelectionModel().getSelectedItem());
        updateRoute();
      });
      travelModes.setConverter(new StringConverter<TravelMode>() {

        @Override
        public String toString(TravelMode travelMode) {

          return travelMode.getName();
        }

        @Override
        public TravelMode fromString(String fileName) {

          return null;
        }
      });
      travelModes.getSelectionModel().select(0);

      // add the map view and control panel to stack pane
      stackPane.getChildren().addAll(mapView, travelModes);
      StackPane.setAlignment(travelModes, Pos.TOP_LEFT);
      StackPane.setMargin(travelModes, new Insets(10, 0, 0, 10));

    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
    }
  }

  /**
   * Update the route based on the set or moving stops.
   */
  private void updateRoute() {

    if (routeParameters.getStops().size() > 1) {
      // remove listener until route task is solved
      if (!stopsOverlay.getSelectedGraphics().isEmpty()) {
        mapView.setOnMouseMoved(null);
      }

      // solve route
      ListenableFuture<RouteResult> results = routeTask.solveRouteAsync(routeParameters);
      results.addDoneListener(() -> {
        try {
          RouteResult result = results.get();
          Route route = result.getRoutes().get(0);

          // create graphic for route
          Graphic graphic = new Graphic(route.getRouteGeometry(), lineSymbol);

          // replace route graphic
          routeOverlay.getGraphics().clear();
          routeOverlay.getGraphics().add(graphic);

        } catch (InterruptedException | ExecutionException e) {
          // ignore, no route solution

        } finally {
          // add mouse moved listener back
          if (!stopsOverlay.getSelectedGraphics().isEmpty()) {
            mapView.setOnMouseMoved(mouseMovedListener);
          }
        }
      });
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
