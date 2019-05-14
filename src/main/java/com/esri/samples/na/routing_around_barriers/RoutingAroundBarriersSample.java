package com.esri.samples.na.routing_around_barriers;

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
import com.esri.arcgisruntime.symbology.*;
import com.esri.arcgisruntime.tasks.networkanalysis.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class RoutingAroundBarriersSample extends Application {

  private MapView mapView;
  private CheckBox findBestSequenceCheckBox;
  private CheckBox preserveFirstStopCheckBox;
  private CheckBox preserveLastStopCheckBox;
  private ArrayList<Stop> stopsList;
  private ArrayList<PolygonBarrier> barriersList;
  private GraphicsOverlay stopsGraphicsOverlay;
  private GraphicsOverlay routeGraphicsOverlay;
  private GraphicsOverlay barriersGraphicsOverlay;
  private ArcGISMap map;
  private RouteTask solveRouteTask;
  private RouteParameters routeParameters;
  private SimpleLineSymbol routeLineSymbol;
  private ListView<String> directionsList;
  private RoutingAroundBarriersSample.InsertMode currentInsertMode;
  private SimpleFillSymbol barrierSymbol;

  @Override
  public void start(Stage stage) {

    try{
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);
      scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

      // set title, size and add scene to stage
      stage.setTitle("Routing Around Barriers Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // create a control panel
      VBox controlsVBox = new VBox(6);
      controlsVBox.setBackground(new Background(new BackgroundFill(Paint.valueOf("rgba(0,0,0,0.3)"), CornerRadii.EMPTY, Insets.EMPTY)));
      controlsVBox.setPadding(new Insets(10.0));
      controlsVBox.setMaxSize(150,400);
      controlsVBox.getStyleClass().add("panel-region");

      // create a list view and label for route directions
      Label directionsLabel = new Label("Route directions:");
      directionsLabel.getStyleClass().add("panel-label");
      directionsList = new ListView<>();

      // create buttons for user interaction
      Button addStopsButton = new Button("Add Stops");
      addStopsButton.setMaxWidth(Double.MAX_VALUE);
      Button addBarriersButton = new Button("Add Barriers");
      addBarriersButton.setMaxWidth(Double.MAX_VALUE);
      Button calculateRouteButton = new Button("Calculate Route");
      calculateRouteButton.setMaxWidth(Double.MAX_VALUE);
      Button resetButton = new Button("Reset");
      resetButton.setMaxWidth(Double.MAX_VALUE);
      findBestSequenceCheckBox = new CheckBox("Find best sequence");
      preserveFirstStopCheckBox = new CheckBox("Preserve first stop");
      preserveFirstStopCheckBox.setDisable(true);
      preserveLastStopCheckBox = new CheckBox("Preserve last stop");
      preserveLastStopCheckBox.setDisable(true);

      // add buttons, checkboxes and directions list to the control panel
      controlsVBox.getChildren().addAll(addStopsButton, addBarriersButton, calculateRouteButton, resetButton, findBestSequenceCheckBox, preserveFirstStopCheckBox, preserveLastStopCheckBox, directionsLabel, directionsList);

      // create symbols for stops and route
      routeLineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0x800000FF, 5.0f);
      barrierSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.DIAGONAL_CROSS, 0xFFFF0000, null);

      // create graphics overlays for stops and routes
      stopsGraphicsOverlay = new GraphicsOverlay();
      barriersGraphicsOverlay = new GraphicsOverlay();
      routeGraphicsOverlay = new GraphicsOverlay();

      // create a list of stops and barriers
      stopsList = new ArrayList<>();
      barriersList = new ArrayList<>();

      // create an ArcGISMap with a streets basemap
      map = new ArcGISMap(Basemap.createStreets());

      // set the ArcGISMap to be displayed in this view
      mapView = new MapView();
      mapView.setMap(map);

      // toggle preserve checkboxes on findBestSequenceCheckBox tick
      findBestSequenceCheckBox.setOnAction(event -> {
        togglePreserveStopsCheckBoxes();
      });

      // zoom to viewpoint
      mapView.setViewpoint(new Viewpoint(32.727, -117.1750, 40000));

      // add the graphics overlays to the map view
      mapView.getGraphicsOverlays().addAll(Arrays.asList(stopsGraphicsOverlay, barriersGraphicsOverlay, routeGraphicsOverlay));

      // TODO: add drawStatusListener to mapView and only then enable UI?

      // set the mode to adding stops
      currentInsertMode = InsertMode.STOPS;

      // change to adding stops when button is pressed
      addStopsButton.setOnAction(e -> currentInsertMode = InsertMode.STOPS);

      // change to adding barriers when button is pressed
      addBarriersButton.setOnAction(e -> currentInsertMode = InsertMode.BARRIERS);

      // listen to mouse clicks to add stops or barriers
      mapView.setOnMouseClicked(e ->{
        // convert clicked point to a map point
        Point mapPoint = mapView.screenToLocation(new Point2D(e.getX(), e.getY()));

        // check that the primary mouse button was clicked
        if (e.getButton() == MouseButton.PRIMARY && e.isStillSincePress()) {

          // check which insertion mode is active
          switch (currentInsertMode) {
            case STOPS:
              // add the map point as a stop
              addStop(mapPoint);
              break;
            case BARRIERS:
              // add the map point as a barrier
              addBarrier(mapPoint);
              break;
          }
        } else if (e.getButton() == MouseButton.SECONDARY && e.isStillSincePress()) {

          // check which insertion mode is active
          switch (currentInsertMode) {
            case STOPS:
              // add the map point as a stop
              removeLastStop();
              break;
            case BARRIERS:
              // add the map point as a barrier
              removeLastBarrier();
              break;
          }
        }
      });

      // solve the route task when button is pressed
      calculateRouteButton.setOnAction(e-> {
        System.out.println("button press");
        setupRouteTask();
      });

      // clear view and route parameters when button is pressed
      resetButton.setOnAction(e -> {
        // clear stops and barriers
        routeParameters.clearStops();
        stopsList.clear();
        routeParameters.clearPointBarriers();
        barriersList.clear();

        // clear the directions list
        directionsList.getItems().clear();

        // clear all graphics overlays
        for (GraphicsOverlay graphicsOverlay : mapView.getGraphicsOverlays()){
          graphicsOverlay.getGraphics().clear();
        }
      });

      // add the map view and control panel to the stack pane
      stackPane.getChildren().addAll(mapView, controlsVBox);
      StackPane.setAlignment(controlsVBox, Pos.TOP_LEFT);
      StackPane.setMargin(controlsVBox, new Insets(10,0,0,10));

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Creates a stop at the clicked point and adds it to the list of stops
   * @param mapPoint    The point on the map at which to create a stop
   */
  private void addStop(Point mapPoint){
    // use the clicked map point to construct a stop
    Stop stopPoint = new Stop(new Point(mapPoint.getX(), mapPoint.getY(), map.getSpatialReference()));

    // add the new stop to the list of stops
    stopsList.add(stopPoint);

    // create a marker graphics and add it to the graphics overlay
    Graphic stopGraphic = new Graphic(mapPoint, createSymbolForStopGraphic(stopsList.size()));
    stopsGraphicsOverlay.getGraphics().add(stopGraphic);
  }

  /**
   * Build a composite symbol out of a pin symbol and a text symbol marking the stop number
   * @param stopNumber   The number of the current stop being added
   * @return            A composite symbol to mark the current stop
   */
  private CompositeSymbol createSymbolForStopGraphic(Integer stopNumber){
    // create a marker with a pin image and position it
    Image pinImage = new Image(getClass().getResourceAsStream("/symbols/pin.png"), 0, 80, true, true);
    PictureMarkerSymbol pinSymbol = new PictureMarkerSymbol(pinImage);
    pinSymbol.loadAsync();

    // determine the stop number and create a new label
    String stopNumberText = ((stopNumber).toString());
    TextSymbol stopTextSymbol = new TextSymbol(20, stopNumberText, 0xFF0000FF, TextSymbol.HorizontalAlignment.CENTER, TextSymbol.VerticalAlignment.MIDDLE);
    stopTextSymbol.setOffsetY((float) pinImage.getHeight()/2);

    // construct a composite symbol out of the pin and text symbols, and return it
    CompositeSymbol compositeSymbol = new CompositeSymbol(Arrays.asList(pinSymbol, stopTextSymbol));
    return compositeSymbol;
  }

  /**
   * Creates a barrier at the clicked point and adds it to the list of barriers
   * @param mapPoint    The point on the map at which to create a barrier
   */
  private void addBarrier(Point mapPoint){
    // create a buffered polygon around the mapPoint
    Polygon bufferedBarrierPolygon = GeometryEngine.buffer(mapPoint, 500);

    // create a polygon barrier for the routing task
    PolygonBarrier barrier = new PolygonBarrier(bufferedBarrierPolygon);
    barriersList.add(barrier);

    // build graphics, and display it
    Graphic barrierGraphic = new Graphic(bufferedBarrierPolygon, barrierSymbol);
    barriersGraphicsOverlay.getGraphics().add(barrierGraphic);
  }

  /**
   * Remove the last stop from the view and from the list of stops used in the routing task
   */
  private void removeLastStop(){
    if (!stopsList.isEmpty()) {
      stopsList.remove(stopsList.size() - 1);
      stopsGraphicsOverlay.getGraphics().remove(stopsGraphicsOverlay.getGraphics().size() - 1);
    }
  }

  /**
   * Remove the last barrier from the view and from the list
   */
  private void removeLastBarrier(){
    if (!barriersList.isEmpty()) {
      barriersList.remove(barriersList.size() - 1);
      barriersGraphicsOverlay.getGraphics().remove(barriersGraphicsOverlay.getGraphics().size() - 1);
    }
  }

  /**
   * create a route task with the existing list of stops
   */
  private void setupRouteTask(){
    // create route task from San Diego service
    solveRouteTask  = new RouteTask("http://sampleserver6.arcgisonline.com/arcgis/rest/services/NetworkAnalysis/SanDiego/NAServer/Route");
    solveRouteTask.loadAsync();
    solveRouteTask.addDoneLoadingListener(() -> {
      if (solveRouteTask.getLoadStatus() == LoadStatus.LOADED){
        // get default route parameters
        final ListenableFuture<RouteParameters> routeParametersFuture = solveRouteTask.createDefaultParametersAsync();
        routeParametersFuture.addDoneListener(()->{

          try{
            routeParameters = routeParametersFuture.get();
            // set flags to return stops and directions
            routeParameters.setReturnStops(true);
            routeParameters.setReturnDirections(true);

          } catch (InterruptedException | ExecutionException e){
            new Alert(Alert.AlertType.ERROR, "Cannot create RouteTask parameters " + e.getMessage()).show();
          }
        });
        // solve the route and display it
        createRouteAndDisplay();

      } else {
        new Alert(Alert.AlertType.ERROR, "Unable to load RouteTask " + solveRouteTask.getLoadStatus().toString()).show();
      }
    });
  }

  /**
   * Check the status of the route options checkboxes and apply the parameters accordingly
   */
  private void checkAndApplyRouteOptions(){
      routeParameters.setFindBestSequence(findBestSequenceCheckBox.isSelected());
      routeParameters.setPreserveFirstStop(preserveFirstStopCheckBox.isSelected());
      routeParameters.setPreserveLastStop(preserveLastStopCheckBox.isSelected());
  }

  /**
   * Use the list of stops and the route task to determine the route and display it
   */
  private void createRouteAndDisplay(){
    if (stopsList.size() >= 2){
      // clear the previous route, if it exists
      routeGraphicsOverlay.getGraphics().clear();

      // add the existing stops and barriers to the route parameters
      routeParameters.setStops(stopsList);
      routeParameters.setPolygonBarriers(barriersList);

      // apply the requested route finding parameters
      checkAndApplyRouteOptions();

      // solve the route task
      final ListenableFuture<RouteResult> routeResultFuture = solveRouteTask.solveRouteAsync(routeParameters);
      routeResultFuture.addDoneListener(() -> {
        try {
          RouteResult routeResult = routeResultFuture.get();
          if (routeResult.getRoutes().size() > 0){
            // get the first route result
            Route firstRoute = routeResult.getRoutes().get(0);

            // add a graphics for this route to display it
            Geometry routeShape = firstRoute.getRouteGeometry();
            Graphic routeGraphic = new Graphic(routeShape, routeLineSymbol);
            routeGraphicsOverlay.getGraphics().add(routeGraphic);

            // get the direction text for each maneuver and display
            for (DirectionManeuver step : firstRoute.getDirectionManeuvers()){
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
      //TODO: alert for not enough stops
    }
  }

  /**
   * Toggles and resets preserve stops checkboxes
   */
  private void togglePreserveStopsCheckBoxes(){
    preserveFirstStopCheckBox.setSelected(false);
    preserveFirstStopCheckBox.setDisable(!preserveFirstStopCheckBox.isDisabled());
    preserveLastStopCheckBox.setSelected(false);
    preserveLastStopCheckBox.setDisable(!preserveLastStopCheckBox.isDisabled());
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

  private enum InsertMode { // enumeration to track which elements are added when clicks are registered
    STOPS,                  // stops will be added
    BARRIERS                // barriers will be added
  }
}


