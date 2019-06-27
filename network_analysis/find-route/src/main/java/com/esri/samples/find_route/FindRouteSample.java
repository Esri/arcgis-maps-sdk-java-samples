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

package com.esri.samples.find_route;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.DrawStatus;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol.Style;
import com.esri.arcgisruntime.symbology.TextSymbol;
import com.esri.arcgisruntime.symbology.TextSymbol.HorizontalAlignment;
import com.esri.arcgisruntime.symbology.TextSymbol.VerticalAlignment;
import com.esri.arcgisruntime.tasks.networkanalysis.DirectionManeuver;
import com.esri.arcgisruntime.tasks.networkanalysis.Route;
import com.esri.arcgisruntime.tasks.networkanalysis.RouteParameters;
import com.esri.arcgisruntime.tasks.networkanalysis.RouteResult;
import com.esri.arcgisruntime.tasks.networkanalysis.RouteTask;
import com.esri.arcgisruntime.tasks.networkanalysis.Stop;

public class FindRouteSample extends Application {

  private MapView mapView;
  private RouteTask routeTask;
  private RouteParameters routeParameters;
  private final ListView<String> directionsList = new ListView<>();

  private Graphic routeGraphic;
  private final GraphicsOverlay routeGraphicsOverlay = new GraphicsOverlay();

  private final SpatialReference ESPG_3857 = SpatialReference.create(102100);
  private static final int WHITE_COLOR = 0xffffffff;
  private static final int BLUE_COLOR = 0xff0000ff;

  private static final String ROUTE_TASK_SANDIEGO =
      "http://sampleserver6.arcgisonline.com/arcgis/rest/services/NetworkAnalysis/SanDiego/NAServer/Route";

  @Override
  public void start(Stage stage) {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);
      scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

      // set title, size, and add scene to stage
      stage.setTitle("Find Route Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // create a control panel
      VBox controlsVBox = new VBox(6);
      controlsVBox.setBackground(new Background(new BackgroundFill(Paint.valueOf("rgba(0,0,0,0.3)"), CornerRadii.EMPTY,
          Insets.EMPTY)));
      controlsVBox.setPadding(new Insets(10.0));
      controlsVBox.setMaxSize(400, 300);
      controlsVBox.getStyleClass().add("panel-region");

      Label directionsLabel = new Label("Route directions:");
      directionsLabel.getStyleClass().add("panel-label");

      // create buttons for user interaction
      Button findButton = new Button("Find route");
      findButton.setMaxWidth(Double.MAX_VALUE);
      findButton.setDisable(true);
      Button resetButton = new Button("Reset");
      resetButton.setMaxWidth(Double.MAX_VALUE);
      resetButton.setDisable(true);

      // find route
      findButton.setOnAction(e -> {
        try {
          RouteResult result = routeTask.solveRouteAsync(routeParameters).get();
          List<Route> routes = result.getRoutes();
          if (routes.size() < 1) {
            directionsList.getItems().add("No Routes");
          }
          Route route = routes.get(0);
          Geometry shape = route.getRouteGeometry();
          routeGraphic = new Graphic(shape, new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, BLUE_COLOR, 2));
          routeGraphicsOverlay.getGraphics().add(routeGraphic);

          // get the direction text for each maneuver
          for (DirectionManeuver step : route.getDirectionManeuvers()) {
            directionsList.getItems().add(step.getDirectionText());
          }

          resetButton.setDisable(false);
          findButton.setDisable(true);

        } catch (Exception ex) {
          ex.printStackTrace();
        }
      });

      // clear the route and the directions maneuver found
      resetButton.setOnAction(e -> {
        routeGraphicsOverlay.getGraphics().remove(routeGraphic);
        directionsList.getItems().clear();
        resetButton.setDisable(true);
        findButton.setDisable(false);
      });

      // add buttons and direction list and label to the control panel
      controlsVBox.getChildren().addAll(directionsLabel, directionsList, findButton, resetButton);

      // create a ArcGISMap with a streets basemap
      ArcGISMap map = new ArcGISMap(Basemap.createStreets());

      // set the ArcGISMap to be displayed in this view
      mapView = new MapView();
      mapView.setMap(map);

      // enable find a route button when mapview is done loading
      mapView.addDrawStatusChangedListener(e -> {
        if (e.getDrawStatus() == DrawStatus.COMPLETED) {
          findButton.setDisable(false);
        }
      });

      // set the viewpoint to San Diego (U.S.)
      mapView.setViewpointGeometryAsync(new Envelope(-13067866, 3843014, -13004499, 3871296, ESPG_3857));

      // add the graphic overlay to the map view
      mapView.getGraphicsOverlays().add(routeGraphicsOverlay);

      try {
        // create route task from San Diego service
        routeTask = new RouteTask(ROUTE_TASK_SANDIEGO);

        // load route task
        routeTask.loadAsync();
        routeTask.addDoneLoadingListener(() -> {
          if (routeTask.getLoadStatus() == LoadStatus.LOADED) {
            try {
              // get default route parameters
              routeParameters = routeTask.createDefaultParametersAsync().get();
              routeParameters.setOutputSpatialReference(ESPG_3857);

              // set flags to return stops and directions
              routeParameters.setReturnStops(true);
              routeParameters.setReturnDirections(true);

              // set stop locations
              Point stop1Loc = new Point(-1.3018598562659847E7, 3863191.8817135547, ESPG_3857);
              Point stop2Loc = new Point(-1.3036911787723785E7, 3839935.706521739, ESPG_3857);

              // add route stops
              List<Stop> routeStops = new ArrayList<>();
              routeStops.add(new Stop(stop1Loc));
              routeStops.add(new Stop(stop2Loc));
              routeParameters.setStops(routeStops);

              // add route stops to the stops overlay
              SimpleMarkerSymbol stopMarker = new SimpleMarkerSymbol(Style.CIRCLE, BLUE_COLOR, 14);
              routeGraphicsOverlay.getGraphics().add(new Graphic(stop1Loc, stopMarker));
              routeGraphicsOverlay.getGraphics().add(new Graphic(stop2Loc, stopMarker));

              // add order text symbols to the stops
              TextSymbol stop1Text = new TextSymbol(10, "1", WHITE_COLOR, HorizontalAlignment.CENTER,
                  VerticalAlignment.MIDDLE);
              TextSymbol stop2Text = new TextSymbol(10, "2", WHITE_COLOR, HorizontalAlignment.CENTER,
                  VerticalAlignment.MIDDLE);
              routeGraphicsOverlay.getGraphics().add(new Graphic(stop1Loc, stop1Text));
              routeGraphicsOverlay.getGraphics().add(new Graphic(stop2Loc, stop2Text));

            } catch (Exception ex) {
              ex.printStackTrace();
            }
          } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Route Task Failed to Load!");
            alert.show();
          }
        });

      } catch (Exception e) {
        e.printStackTrace();
      }

      // add the map view and control panel to stack pane
      stackPane.getChildren().addAll(mapView, controlsVBox);
      StackPane.setAlignment(controlsVBox, Pos.TOP_LEFT);
      StackPane.setMargin(controlsVBox, new Insets(10, 0, 0, 10));

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
