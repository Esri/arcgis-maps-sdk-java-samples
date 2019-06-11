/*
 * Copyright 2019 Esri.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.esri.samples.na.routing_around_barriers;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;

import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.Polygon;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.CompositeSymbol;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.TextSymbol;
import com.esri.arcgisruntime.tasks.networkanalysis.DirectionManeuver;
import com.esri.arcgisruntime.tasks.networkanalysis.PolygonBarrier;
import com.esri.arcgisruntime.tasks.networkanalysis.Route;
import com.esri.arcgisruntime.tasks.networkanalysis.RouteParameters;
import com.esri.arcgisruntime.tasks.networkanalysis.RouteResult;
import com.esri.arcgisruntime.tasks.networkanalysis.RouteTask;
import com.esri.arcgisruntime.tasks.networkanalysis.Stop;

public class RoutingAroundBarriersController {

  @FXML private MapView mapView;
  @FXML private ToggleButton btnAddStop;
  @FXML private ToggleButton btnAddBarrier;
  @FXML private Button btnDetermineRoute;
  @FXML private Button btnReset;
  @FXML private CheckBox findBestSequenceCheckBox;
  @FXML private CheckBox preserveFirstStopCheckBox;
  @FXML private CheckBox preserveLastStopCheckBox;
  @FXML private ListView<String> directionsList;
  @FXML private TitledPane routeInformationTitledPane;

  private GraphicsOverlay routeGraphicsOverlay;
  private SimpleLineSymbol routeLineSymbol;
  private RouteTask routeTask;
  private RouteParameters routeParameters;
  private LinkedList<Stop> stopsList;
  private LinkedList<PolygonBarrier> barriersList;
  private Image pinImage;
  private PictureMarkerSymbol pinSymbol;

  @FXML
  public void initialize() {
    // create an ArcGISMap with a streets basemap
    ArcGISMap map = new ArcGISMap(Basemap.createStreets());
    // set the ArcGISMap to be displayed in this view
    mapView.setMap(map);
    // zoom to viewpoint
    mapView.setViewpoint(new Viewpoint(32.727, -117.1750, 40000));

    // create graphics overlays for stops, barriers and route
    GraphicsOverlay stopsGraphicsOverlay = new GraphicsOverlay();
    GraphicsOverlay barriersGraphicsOverlay = new GraphicsOverlay();
    routeGraphicsOverlay = new GraphicsOverlay();

    // add the graphics overlays to the map view
    mapView.getGraphicsOverlays().addAll(Arrays.asList(stopsGraphicsOverlay, barriersGraphicsOverlay, routeGraphicsOverlay));

    // create a list of stops and a list of barriers
    stopsList = new LinkedList<>();
    barriersList = new LinkedList<>();

    // initialize the TitlePane of the Accordion box
    routeInformationTitledPane.setText("No route to display");

    // create symbols for displaying the barriers and the route line
    SimpleFillSymbol barrierSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.DIAGONAL_CROSS, 0xFFFF0000, null);
    routeLineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0x800000FF, 5.0f);

    // create a marker with a pin image and position it
    pinImage = new Image(getClass().getResourceAsStream("/symbols/orange_symbol.png"), 0, 40, true, true);
    pinSymbol = new PictureMarkerSymbol(pinImage);
    pinSymbol.setOffsetY(20);
    pinSymbol.loadAsync();

    // create route task from San Diego service
    routeTask = new RouteTask("http://sampleserver6.arcgisonline.com/arcgis/rest/services/NetworkAnalysis/SanDiego/NAServer/Route");
    routeTask.loadAsync();

    // wait for the route task to load
    routeTask.addDoneLoadingListener(() -> {
      if (routeTask.getLoadStatus() == LoadStatus.LOADED) {
        // get default route parameters
        final ListenableFuture<RouteParameters> routeParametersFuture = routeTask.createDefaultParametersAsync();
        routeParametersFuture.addDoneListener(() -> {
          try {
            routeParameters = routeParametersFuture.get();

            // set flags to return stops and directions
            routeParameters.setReturnStops(true);
            routeParameters.setReturnDirections(true);

            // enable the UI
            btnDetermineRoute.setDisable(false);

          } catch (InterruptedException | ExecutionException e) {
            new Alert(Alert.AlertType.ERROR, "Cannot create RouteTask parameters " + e.getMessage()).show();
          }
        });

      } else {
        new Alert(Alert.AlertType.ERROR, "Unable to load RouteTask " + routeTask.getLoadStatus().toString()).show();
      }
    });

    // listen to mouse clicks to add/remove stops or barriers
    mapView.setOnMouseClicked(e -> {
      // convert clicked point to a map point
      Point mapPoint = mapView.screenToLocation(new Point2D(e.getX(), e.getY()));

      // Normalize geometry - important for geometries that will be sent to a server for processing.
      mapPoint = (Point) GeometryEngine.normalizeCentralMeridian(mapPoint);

      // if the primary mouse button was clicked, add a stop or barrier  to the clicked map point
      if (e.getButton() == MouseButton.PRIMARY && e.isStillSincePress()) {
        if (btnAddStop.isSelected()) {
          // use the clicked map point to construct a stop
          Stop stopPoint = new Stop(new Point(mapPoint.getX(), mapPoint.getY(), mapPoint.getSpatialReference()));

          // add the new stop to the list of stops
          stopsList.add(stopPoint);

          // create a marker symbol and graphics, and add the graphics to the graphics overlay
          CompositeSymbol newStopSymbol = createCompositeStopSymbol(stopsList.size());
          Graphic stopGraphic = new Graphic(mapPoint, newStopSymbol);
          stopsGraphicsOverlay.getGraphics().add(stopGraphic);

        } else if (btnAddBarrier.isSelected()) {
          // clear the displayed route, if it exists, since it might not be up to date any more
          routeGraphicsOverlay.getGraphics().clear();

          // create a buffered polygon around the mapPoint
          Polygon bufferedBarrierPolygon = GeometryEngine.buffer(mapPoint, 500);

          // create a polygon barrier for the routing task
          PolygonBarrier barrier = new PolygonBarrier(bufferedBarrierPolygon);
          barriersList.add(barrier);

          // build graphics for the barrier and add it to the graphics overlay
          Graphic barrierGraphic = new Graphic(bufferedBarrierPolygon, barrierSymbol);
          barriersGraphicsOverlay.getGraphics().add(barrierGraphic);
        }

        // if the secondary mouse button was clicked, delete the last stop or barrier, respectively
      } else if (e.getButton() == MouseButton.SECONDARY && e.isStillSincePress()) {
        // clear the displayed route, if it exists, since it might not be up to date any more
        routeGraphicsOverlay.getGraphics().clear();

        if (btnAddStop.isSelected()) {
          // check if there are stops to remove
          if (!stopsList.isEmpty()) {

            // remove the last stop from the stop list and the graphics overlay
            stopsList.removeLast();
            stopsGraphicsOverlay.getGraphics().remove(stopsGraphicsOverlay.getGraphics().size() - 1);
          }
        } else if (btnAddBarrier.isSelected()) {
          // check if there are barriers to remove
          if (!barriersList.isEmpty()) {

            // remove the last barrier from the barrier list and the graphics overlay
            barriersList.removeLast();
            barriersGraphicsOverlay.getGraphics().remove(barriersGraphicsOverlay.getGraphics().size() - 1);
          }
        }
      }
    });
  }

  /**
   * Uses the route task with the lists of stops and barriers to determine the route and display it.
   */
  @FXML
  private void createRouteAndDisplay() {
    if (stopsList.size() >= 2) {
      // clear the previous route from the graphics overlay, if it exists
      routeGraphicsOverlay.getGraphics().clear();

      // add the existing stops and barriers to the route parameters
      routeParameters.setStops(stopsList);
      routeParameters.setPolygonBarriers(barriersList);

      // apply the requested route finding parameters
      routeParameters.setFindBestSequence(findBestSequenceCheckBox.isSelected());
      routeParameters.setPreserveFirstStop(preserveFirstStopCheckBox.isSelected());
      routeParameters.setPreserveLastStop(preserveLastStopCheckBox.isSelected());

      // solve the route task
      final ListenableFuture<RouteResult> routeResultFuture = routeTask.solveRouteAsync(routeParameters);
      routeResultFuture.addDoneListener(() -> {
        try {
          RouteResult routeResult = routeResultFuture.get();
          if (!routeResult.getRoutes().isEmpty()) {
            // get the first route result
            Route firstRoute = routeResult.getRoutes().get(0);

            // create a geometry for this route
            Geometry routeGeometry = firstRoute.getRouteGeometry();

            // create a graphic for the route and add it to the graphics overlay
            Graphic routeGraphic = new Graphic(routeGeometry, routeLineSymbol);
            routeGraphicsOverlay.getGraphics().add(routeGraphic);

            // set the title of the TitledPane to display the information
            String output = String.format("Route directions: %d min (%.2f km)", Math.round(firstRoute.getTravelTime()), firstRoute.getTotalLength() / 1000);
            routeInformationTitledPane.setText(output);

            // get the direction text for each maneuver and add them to the list to display
            for (DirectionManeuver maneuver : firstRoute.getDirectionManeuvers()) {
              directionsList.getItems().add(maneuver.getDirectionText());
            }

          } else {
            new Alert(Alert.AlertType.ERROR, "No possible routes found").show();
          }

        } catch (InterruptedException | ExecutionException e) {
          new Alert(Alert.AlertType.ERROR, "Solve RouteTask failed " + e.getMessage() + e.getMessage()).show();
        }

        // enable the reset button
        btnReset.setDisable(false);

      });
    } else {
      new Alert(Alert.AlertType.ERROR, "Cannot run the routing task, a minimum of two stops is required").show();
    }
  }

  /**
   * Clears stops and barriers from the route parameters, clears direction list and graphics overlays.
   */
  @FXML
  private void clearRouteAndGraphics() {
    // clear stops from route parameters and stops list
    routeParameters.clearStops();
    stopsList.clear();

    // clear barriers from route parameters and barriers list
    routeParameters.clearPointBarriers();
    barriersList.clear();

    // reset the route information title pane
    routeInformationTitledPane.setText("No route to display");

    // clear the directions list
    directionsList.getItems().clear();

    // clear all graphics overlays
    mapView.getGraphicsOverlays().forEach(graphicsOverlay -> graphicsOverlay.getGraphics().clear());

    // enable/disable buttons
    btnDetermineRoute.setDisable(false);
    btnReset.setDisable(true);
  }

  /**
   * Builds a composite symbol out of a pin symbol and a text symbol marking the stop number.
   *
   * @param stopNumber The number of the current stop being added
   * @return A composite symbol to mark the current stop
   */
  private CompositeSymbol createCompositeStopSymbol(Integer stopNumber) {
    // determine the stop number and create a new label
    TextSymbol stopTextSymbol = new TextSymbol(16, (stopNumber).toString(), 0xFFFFFFFF, TextSymbol.HorizontalAlignment.CENTER, TextSymbol.VerticalAlignment.BOTTOM);
    stopTextSymbol.setOffsetY((float) pinImage.getHeight() / 2);

    // construct a composite symbol out of the pin and text symbols, and return it
    return new CompositeSymbol(Arrays.asList(pinSymbol, stopTextSymbol));
  }

  /**
   * Toggles and resets the preserve stops checkboxes.
   */
  @FXML
  private void togglePreserveStopsCheckBoxes() {
    // un-tick and disable the second-tier checkboxes
    preserveFirstStopCheckBox.setSelected(false);
    preserveFirstStopCheckBox.setDisable(!preserveFirstStopCheckBox.isDisabled());
    preserveLastStopCheckBox.setSelected(false);
    preserveLastStopCheckBox.setDisable(!preserveLastStopCheckBox.isDisabled());
  }

  /**
   * Stops and releases all resources used in application.
   */
  public void terminate() {

    if (mapView != null) {
      mapView.dispose();
    }
  }
}