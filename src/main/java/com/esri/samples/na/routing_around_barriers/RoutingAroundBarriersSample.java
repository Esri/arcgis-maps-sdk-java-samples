package com.esri.samples.na.routing_around_barriers;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.TextSymbol;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import java.util.ArrayList;

public class RoutingAroundBarriersSample extends Application {

  MapView mapView;

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
      controlsVBox.setMaxSize(150,150);
      controlsVBox.getStyleClass().add("panel-region");

      // create a list view and label for route directions
      Label directionsLabel = new Label("Route directions:");
      directionsLabel.getStyleClass().add("panel-label");
      ListView<String> directionsList = new ListView<>();

      // create buttons for user interaction
      Button addStopsButton = new Button("Add Stops");
      addStopsButton.setMaxWidth(Double.MAX_VALUE);
      Button addBarriersButton = new Button("Add Barriers");
      addBarriersButton.setMaxWidth(Double.MAX_VALUE);
      Button calculateRouteButton = new Button("Calculate Route");
      calculateRouteButton.setMaxWidth(Double.MAX_VALUE);
      Button resetButton = new Button("Reset");
      resetButton.setMaxWidth(Double.MAX_VALUE);
      CheckBox findBestSequenceCheckBox = new CheckBox("Find best sequence");
      CheckBox preserveFirstStopCheckBox = new CheckBox("Preserve first stop");
      CheckBox preserveLastStopCheckBox = new CheckBox("Preserve last stop");

      // add buttons, checkboxes and directions list to the control panel
      controlsVBox.getChildren().addAll(addStopsButton, addBarriersButton, calculateRouteButton, resetButton, findBestSequenceCheckBox, preserveFirstStopCheckBox, preserveLastStopCheckBox, directionsLabel, directionsList);

      // create symbols for stops and route
      SimpleLineSymbol routeLineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0xFF0000FF, 2.0f);

      // create a graphics overlay for stops
      GraphicsOverlay stopsGraphicsOverlay = new GraphicsOverlay();

      // create a list of stops
      ArrayList<Point> stopsList = new ArrayList<>();

      // create an ArcGISMap with a streets basemap
      ArcGISMap map = new ArcGISMap(Basemap.createStreets());

      // set the ArcGISMap to be displayed in this view
      mapView = new MapView();
      mapView.setMap(map);

      // zoom to viewpoint
      mapView.setViewpoint(new Viewpoint(32.727, -117.1750, 40000));

      mapView.getGraphicsOverlays().add(stopsGraphicsOverlay);

      // listen to mouse clicks to add stops
      mapView.setOnMouseClicked(e ->{
        // check that the primary mouse button was clicked
        if (e.getButton() == MouseButton.PRIMARY && e.isStillSincePress()) {

          Point mapPoint = mapView.screenToLocation(new Point2D(e.getX(), e.getY()));
          Point stopPoint = new Point(mapPoint.getX(), mapPoint.getY(), map.getSpatialReference());


          // add the new stop to the list of stops
          stopsList.add(stopPoint);

          // determine the stop number and create a new label
          String stopNumberText = ((Integer) stopsList.size()).toString();
          TextSymbol stopTextSymbol = new TextSymbol(12, stopNumberText, 0xFF0000FF, TextSymbol.HorizontalAlignment.CENTER, TextSymbol.VerticalAlignment.BOTTOM);

          // create a marker graphics and add it to the graphics overlay
          Graphic stopGraphic = new Graphic(stopPoint, stopTextSymbol);
          stopsGraphicsOverlay.getGraphics().add(stopGraphic);
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
   * Stops and releases all resources used in application.
   */
  @Override
  public void stop() {

//    if (mapView != null) {
//      mapView.dispose();
//    }
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


