/*
 * Copyright 2016 Esri.
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
package com.esri.samples.na.closest_facility;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.tasks.networkanalysis.ClosestFacilityParameters;
import com.esri.arcgisruntime.tasks.networkanalysis.ClosestFacilityResult;
import com.esri.arcgisruntime.tasks.networkanalysis.ClosestFacilityRoute;
import com.esri.arcgisruntime.tasks.networkanalysis.ClosestFacilityTask;
import com.esri.arcgisruntime.tasks.networkanalysis.Facility;
import com.esri.arcgisruntime.tasks.networkanalysis.Incident;

public class ClosestFacilitySample extends Application {

  // black cross were user clicked
  private Point incidentPoint;
  private GraphicsOverlay graphicsOverlay;
  // holds locations of hospitals around San Diego
  private List<Facility> facilities;
  private MapView mapView;
  // solves task to find closest route between a incident and a facility
  private ClosestFacilityTask task;

  // used to display route between incident and facility to mapview
  private SimpleLineSymbol routeSymbol;
  // same spatial reference of the map
  private SpatialReference spatialReference = SpatialReferences.getWebMercator();

  @Override
  public void start(Stage stage) throws Exception {

    // pane will hold mapview to be displayed on application 
    StackPane stackPane = new StackPane();
    Scene scene = new Scene(stackPane);

    // set title, size, and add scene to stage
    stage.setTitle("Closest Facility Sample");
    stage.setWidth(800);
    stage.setHeight(700);
    stage.setScene(scene);
    stage.show();

    try {

      // create a map with streets basemap and add to view
      ArcGISMap map = new ArcGISMap(Basemap.createStreets());
      mapView = new MapView();
      mapView.setMap(map);
      // add the mapview to stack pane
      stackPane.getChildren().addAll(mapView);

      // set view to be over San Diego
      mapView.setViewpoint(new Viewpoint(32.727, -117.1750, 40000));

      graphicsOverlay = new GraphicsOverlay();
      createFacilitiesAndGraphics();
      // to load graphics faster, add graphics overlay to view once all graphics are in graphics overlay
      mapView.getGraphicsOverlays().add(graphicsOverlay);

      // task to find the closest route between an incident and a facility 
      final String sanDiegoRegion =
          "http://sampleserver6.arcgisonline.com/arcgis/rest/services/NetworkAnalysis/SanDiego/NAServer/ClosestFacility";
      task = new ClosestFacilityTask(sanDiegoRegion);
      task.loadAsync();

      // symbols that display incident(black cross) and route(blue line) to view
      SimpleMarkerSymbol incidentSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CROSS, 0xFF000000, 20);
      routeSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0xFF0000FF, 2.0f);

      // place incident were user clicks and display route to closest facility
      mapView.setOnMouseClicked(e -> {
        // check that the primary mouse button was clicked
        if (e.getButton() == MouseButton.PRIMARY && e.isStillSincePress()) {
          // show incident to the mapview
          Point mapPoint = mapView.screenToLocation(new Point2D(e.getX(), e.getY()));
          incidentPoint = new Point(mapPoint.getX(), mapPoint.getY(), spatialReference);
          Graphic graphic = new Graphic(incidentPoint, incidentSymbol);
          graphicsOverlay.getGraphics().add(graphic);

          createParametersAndSolveRoute();
        }
      });
    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
    }
  }

  /**
   * Creates points around the San Diego region that will be used to create facilities and Graphics of those facilities.
   */
  private void createFacilitiesAndGraphics() {
    // List of facilities to be placed around San Diego area
    List<Point> facilityPoints = new ArrayList<>();
    facilityPoints.add(new Point(-1.3042129900625112E7, 3860127.9479775648, spatialReference));
    facilityPoints.add(new Point(-1.3042193400557665E7, 3862448.873041752, spatialReference));
    facilityPoints.add(new Point(-1.3046882875518233E7, 3862704.9896770366, spatialReference));
    facilityPoints.add(new Point(-1.3040539754780494E7, 3862924.5938606677, spatialReference));
    facilityPoints.add(new Point(-1.3042571225655518E7, 3858981.773018156, spatialReference));
    facilityPoints.add(new Point(-1.3039784633928463E7, 3856692.5980474586, spatialReference));
    facilityPoints.add(new Point(-1.3049023883956768E7, 3861993.789732541, spatialReference));

    // image for displaying facility
    String facilityUrl = "http://static.arcgis.com/images/Symbols/SafetyHealth/Hospital.png";
    PictureMarkerSymbol facilitySymbol = new PictureMarkerSymbol(facilityUrl);
    facilitySymbol.setHeight(30);
    facilitySymbol.setWidth(30);

    List<Graphic> graphics = graphicsOverlay.getGraphics();
    // list of facilities that will be needed to solve our closest facility route task
    facilities = new ArrayList<>();
    facilityPoints.forEach(facilityPoint -> {
      facilities.add(new Facility(facilityPoint));
      graphics.add(new Graphic(facilityPoint, facilitySymbol));
    });
  }

  /**
   * Adds facilities(hospitals) and user's incident(black cross) to closest facility parameters which will be used to 
   * display the closest route from the user's incident to its' nearest facility.
   */
  private void createParametersAndSolveRoute() {
    // parameters used to find closest facility to incident
    final ListenableFuture<ClosestFacilityParameters> parameters = task.createDefaultParametersAsync();
    parameters.addDoneListener(() -> {
      try {
        // add facilities from map and incident from user to parameters
        ClosestFacilityParameters facilityParameters = parameters.get();
        facilityParameters.getFacilities().addAll(facilities);
        facilityParameters.getIncidents().add(new Incident(incidentPoint));

        // find closest route using parameters from above
        ListenableFuture<ClosestFacilityResult> result = task.solveClosestFacilityAsync(facilityParameters);
        result.addDoneListener(() -> {
          try {
            ClosestFacilityResult facilityResult = result.get();
            // a list of closest facilities based on users incident
            List<Integer> rankedList = facilityResult.getRankedFacilities(0);
            // get the index of the closest facility to incident
            int closestFacility = rankedList.get(0);
            // get route from incident to closest facility and display to mapview
            ClosestFacilityRoute route = facilityResult.getRoute(closestFacility, 0);
            graphicsOverlay.getGraphics().add(new Graphic(route.getRouteGeometry(), routeSymbol));
          } catch (ExecutionException | InterruptedException ex) {
            ex.printStackTrace();
          }
        });
      } catch (ExecutionException | InterruptedException ex) {
        ex.printStackTrace();
      }
    });
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
