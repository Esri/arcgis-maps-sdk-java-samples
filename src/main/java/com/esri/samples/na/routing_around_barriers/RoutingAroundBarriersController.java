package com.esri.samples.na.routing_around_barriers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Alert;
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
  @FXML private CheckBox findBestSequenceCheckBox;
  @FXML private CheckBox preserveFirstStopCheckBox;
  @FXML private CheckBox preserveLastStopCheckBox;
  @FXML private ListView<String> directionsList;
  @FXML private TitledPane routeInformationTitledPane;

  // for displaying stops to the mapview
  private GraphicsOverlay stopsGraphicsOverlay;
  // for displaying routes to the mapview
  private GraphicsOverlay routeGraphicsOverlay;
  // for displaying barriers to the mapview
  private GraphicsOverlay barriersGraphicsOverlay;
  // task to find route between stops
  private RouteTask routeTask;
  // used for solving task above
  private RouteParameters routeParameters;
  // for grouping stops to add to the routing task
  private ArrayList<Stop> stopsList;
  // for grouping barriers to add to the routing task
  private ArrayList<PolygonBarrier> barriersList;

  @FXML
  public void initialize() {
    // create an ArcGISMap with a streets basemap
    ArcGISMap map = new ArcGISMap(Basemap.createStreets());
    // set the ArcGISMap to be displayed in this view
    mapView.setMap(map);
    // zoom to viewpoint
    mapView.setViewpoint(new Viewpoint(32.727, -117.1750, 40000));

    // create graphics overlays for stops, barriers and routes
    stopsGraphicsOverlay = new GraphicsOverlay();
    barriersGraphicsOverlay = new GraphicsOverlay();
    routeGraphicsOverlay = new GraphicsOverlay();

    // add the graphics overlays to the map view
    mapView.getGraphicsOverlays().addAll(Arrays.asList(stopsGraphicsOverlay, barriersGraphicsOverlay, routeGraphicsOverlay));

    // create a list of stops and barriers
    stopsList = new ArrayList<>();
    barriersList = new ArrayList<>();

    // Initialize the TitlePane of the Accordion box
    routeInformationTitledPane.setText("No route to display");

    // listen to mouse clicks to add/remove stops or barriers
    mapView.setOnMouseClicked(e -> {
      // convert clicked point to a map point
      Point mapPoint = mapView.screenToLocation(new Point2D(e.getX(), e.getY()));

      // if the primary mouse button was clicked, add a stop or barrier, respectively
      if (e.getButton() == MouseButton.PRIMARY && e.isStillSincePress()) {
        if (btnAddStop.isSelected()) {
          addStop(mapPoint);
        } else if (btnAddBarrier.isSelected()) { // TODO: check against this is unnecessary since it's either or
          addBarrier(mapPoint);
        }

        // if the secondary mouse button was clicked, delete the last stop or barrier, respectively
      } else if (e.getButton() == MouseButton.SECONDARY && e.isStillSincePress()) {
        // clear the displayed route, if it exists, since it might not be up to date any more
        routeGraphicsOverlay.getGraphics().clear();
        if (btnAddStop.isSelected()) {
          removeLastStop();
        } else if (btnAddBarrier.isSelected()) { // TODO: check against this is unnecessary since it's either or
          removeLastBarrier();
        }
      }
    });
  }

  /**
   * Creates a stop at the clicked point and adds it to the list of stops
   *
   * @param mapPoint The point on the map at which to create a stop
   */
  private void addStop(Point mapPoint) {
    // use the clicked map point to construct a stop
    Stop stopPoint = new Stop(new Point(mapPoint.getX(), mapPoint.getY(), mapPoint.getSpatialReference()));

    // add the new stop to the list of stops
    stopsList.add(stopPoint);

    // create a marker symbol and graphics, and add the graphics to the graphics overlay
    CompositeSymbol newStopSymbol = createCompositeStopSymbol(stopsList.size());
    Graphic stopGraphic = new Graphic(mapPoint, newStopSymbol);
    stopsGraphicsOverlay.getGraphics().add(stopGraphic);
  }

  /**
   * Build a composite symbol out of a pin symbol and a text symbol marking the stop number
   *
   * @param stopNumber The number of the current stop being added
   * @return A composite symbol to mark the current stop
   */
  private CompositeSymbol createCompositeStopSymbol(Integer stopNumber) {
    // create a marker with a pin image and position it
    Image pinImage = new Image(getClass().getResourceAsStream("/symbols/orange_symbol.png"), 0, 40, true, true);
    PictureMarkerSymbol pinSymbol = new PictureMarkerSymbol(pinImage);
    pinSymbol.setOffsetY(20);
    pinSymbol.loadAsync();

    // determine the stop number and create a new label
    String stopNumberText = ((stopNumber).toString());
    TextSymbol stopTextSymbol = new TextSymbol(16, stopNumberText, 0xFFFFFFFF, TextSymbol.HorizontalAlignment.CENTER, TextSymbol.VerticalAlignment.BOTTOM);
    stopTextSymbol.setOffsetY((float) pinImage.getHeight() / 2);

    // construct a composite symbol out of the pin and text symbols, and return it
    return new CompositeSymbol(Arrays.asList(pinSymbol, stopTextSymbol));
  }

  /**
   * Creates a barrier at the clicked point and adds it to the list of barriers
   *
   * @param mapPoint The point on the map at which to create a barrier
   */
  private void addBarrier(Point mapPoint) {
    // clear the displayed route, if it exists, since it might not be up to date any more
    routeGraphicsOverlay.getGraphics().clear();

    // create a buffered polygon around the mapPoint
    Polygon bufferedBarrierPolygon = GeometryEngine.buffer(mapPoint, 500);

    // create a polygon barrier for the routing task
    PolygonBarrier barrier = new PolygonBarrier(bufferedBarrierPolygon);
    barriersList.add(barrier);

    // create a symbol for the barrier
    SimpleFillSymbol barrierSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.DIAGONAL_CROSS, 0xFFFF0000, null);

    // build graphics for the barrier and add it to the graphics overlay
    Graphic barrierGraphic = new Graphic(bufferedBarrierPolygon, barrierSymbol);
    barriersGraphicsOverlay.getGraphics().add(barrierGraphic);
  }

  /**
   * Remove the last stop from the view and from the list of stops used in the routing task
   */
  private void removeLastStop() {
    // check if there are stops to remove
    if (!stopsList.isEmpty()) {

      // remove the last stop from the stop list and the graphics overlay
      stopsList.remove(stopsList.size() - 1);
      stopsGraphicsOverlay.getGraphics().remove(stopsGraphicsOverlay.getGraphics().size() - 1);
    }
  }

  /**
   * Remove the last barrier from the view and from the list
   */
  private void removeLastBarrier() {
    // chek if there are barriers to remove
    if (!barriersList.isEmpty()) {

      // remove the last barrier from the barrier list and the graphics overlay
      barriersList.remove(barriersList.size() - 1);
      barriersGraphicsOverlay.getGraphics().remove(barriersGraphicsOverlay.getGraphics().size() - 1);
    }
  }

  /**
   * Create a route task with the existing list of stops
   */
  @FXML
  private void setupRouteTask() {
    // create route task from San Diego service
    routeTask = new RouteTask("http://sampleserver6.arcgisonline.com/arcgis/rest/services/NetworkAnalysis/SanDiego/NAServer/Route");
    routeTask.loadAsync();
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

          } catch (InterruptedException | ExecutionException e) {
            new Alert(Alert.AlertType.ERROR, "Cannot create RouteTask parameters " + e.getMessage()).show();
          }
        });
        // solve the route task and display it
        createRouteAndDisplay();

      } else {
        new Alert(Alert.AlertType.ERROR, "Unable to load RouteTask " + routeTask.getLoadStatus().toString()).show();
      }
    });
  }

  /**
   * Use the list of stops and the route task to determine the route and display it
   */
  private void createRouteAndDisplay() {
    if (stopsList.size() >= 2) {
      // clear the previous route from the graphics overlay, if it exists
      routeGraphicsOverlay.getGraphics().clear();

      // add the existing stops and barriers to the route parameters
      routeParameters.setStops(stopsList);
      routeParameters.setPolygonBarriers(barriersList);

      // apply the requested route finding parameters
      checkAndApplyRouteOptions();

      // solve the route task
      final ListenableFuture<RouteResult> routeResultFuture = routeTask.solveRouteAsync(routeParameters);
      routeResultFuture.addDoneListener(() -> {
        try {
          RouteResult routeResult = routeResultFuture.get();
          if (routeResult.getRoutes().size() > 0) {
            // get the first route result
            Route firstRoute = routeResult.getRoutes().get(0);

            // add a graphics for this route to display it
            Geometry routeShape = firstRoute.getRouteGeometry();

            // create symbol used to display the route
            SimpleLineSymbol routeLineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0x800000FF, 5.0f);

            // create a graphic for the route and display it
            Graphic routeGraphic = new Graphic(routeShape, routeLineSymbol);
            routeGraphicsOverlay.getGraphics().add(routeGraphic);

            // update the route information on the TitlePanel of the Accordion
            retrieveAndDisplayRouteInformation(firstRoute);

            // get the direction text for each maneuver and display
            for (DirectionManeuver step : firstRoute.getDirectionManeuvers()) {
              directionsList.getItems().add(step.getDirectionText());
            }

          } else {
            new Alert(Alert.AlertType.ERROR, "No possible routes found").show();
          }

        } catch (InterruptedException | ExecutionException e) {
          new Alert(Alert.AlertType.ERROR, "Solve RouteTask failed " + e.getMessage() + e.getMessage()).show();
        }
      });
    } else {
      new Alert(Alert.AlertType.ERROR, "Cannot run the routing task, a minimum of two stops is required").show();
    }
  }

  /**
   * Check the status of the route options checkboxes and apply the parameters accordingly
   */
  private void checkAndApplyRouteOptions() {
    // update the route parameters according to the checkboxes ticked
    routeParameters.setFindBestSequence(findBestSequenceCheckBox.isSelected());
    routeParameters.setPreserveFirstStop(preserveFirstStopCheckBox.isSelected());
    routeParameters.setPreserveLastStop(preserveLastStopCheckBox.isSelected());
  }

  /**
   * Toggle and reset preserve stops checkboxes
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
   * Retrieves the route information and updates the TitlePanel of the Accordion box
   *
   * @param route  the route of which to retrieve the information
   */
  private void retrieveAndDisplayRouteInformation(Route route) {

    // retrieve route travel time, rounded to the nearest minute
    String routeTravelTime = ((Long) Math.round(route.getTravelTime())).toString();

    // retrieve route length (m), convert to miles, and round to two decimal points
    double routeLengthKm = route.getTotalLength() / 1000;
    double routeLengthImperial = routeLengthKm / 1.609 ;
    double routeLengthRounded = Math.round(routeLengthImperial * 100d) / 100d;

    // set the title of the Information panel
    String output = String.format("Route directions: %s min (%s mi)", routeTravelTime, routeLengthRounded);
    routeInformationTitledPane.setText(output);
  }

  /**
   * Clear stops and barriers from the route parameters, clears direction list and graphics overlays
   */
  @FXML
  private void clearRouteAndGraphics() {
    // clear stops from route parameters and stops list
    routeParameters.clearStops();
    stopsList.clear();

    // clear barriers from route parameters and barriers list
    routeParameters.clearPointBarriers();
    barriersList.clear();

    // clear the directions list
    directionsList.getItems().clear();

    // clear route information from the TitlePane of the Accordion box
    routeInformationTitledPane.setText("No route to display");

    // clear all graphics overlays
    for (GraphicsOverlay graphicsOverlay : mapView.getGraphicsOverlays()) {
      graphicsOverlay.getGraphics().clear();
    }
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