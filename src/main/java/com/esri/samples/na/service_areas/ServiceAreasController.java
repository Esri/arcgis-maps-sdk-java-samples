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
package com.esri.samples.na.service_areas;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseButton;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geometry.PolylineBuilder;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.tasks.networkanalysis.PolylineBarrier;
import com.esri.arcgisruntime.tasks.networkanalysis.ServiceAreaFacility;
import com.esri.arcgisruntime.tasks.networkanalysis.ServiceAreaParameters;
import com.esri.arcgisruntime.tasks.networkanalysis.ServiceAreaPolygon;
import com.esri.arcgisruntime.tasks.networkanalysis.ServiceAreaResult;
import com.esri.arcgisruntime.tasks.networkanalysis.ServiceAreaTask;

public class ServiceAreasController {

  @FXML private MapView mapView;
  @FXML private ToggleButton btnAddFacility;
  @FXML private ToggleButton btnAddBarrier;
  @FXML private Button btnShowServiceArea;
  @FXML private Button btnReset;

  // holds all location to find service areas around
  private List<ServiceAreaFacility> serviceAreaFacilities;
  // different colors to display multiple service areas
  private List<SimpleFillSymbol> fillSymbols;

  // task to find service area around a location
  private ServiceAreaTask task;
  // used to solve task above
  private ServiceAreaParameters serviceAreaParameters;
  // holds all facilities that are displayed to the mapview
  private GraphicsOverlay facilityOverlay;
  // holds all polygon service areas that are displayed to the mapview
  private GraphicsOverlay polygonOverlay;
  // holds all barriers that are displayed to mapview
  private GraphicsOverlay barrierOverlay;
  // used to make barriers
  private PolylineBuilder barrierBuilder;
  // used for placing geometry on mapview
  private SpatialReference spatialReference = SpatialReferences.getWebMercator();

  public void initialize() {

    // create map of creates and add to mapview
    ArcGISMap map = new ArcGISMap(Basemap.createStreets());
    mapView.setMap(map);
    // set mapview to San Francisco
    mapView.setViewpoint(new Viewpoint(37.77, -122.41, 40000));

    createServiceAreaTask();
    setDisplayValues();

    // icon used to display facilities to mapview
    String facilityUrl = "http://static.arcgis.com/images/Symbols/SafetyHealth/Hospital.png";
    PictureMarkerSymbol facilitySymbol = new PictureMarkerSymbol(facilityUrl);
    facilitySymbol.setHeight(30);
    facilitySymbol.setWidth(30);

    // creates facilities and barriers at user's clicked location
    mapView.setOnMouseClicked(e -> {
      // check that the primary mouse button was clicked
      if (e.getButton() == MouseButton.PRIMARY && e.isStillSincePress()) {
        // create a point from where the user clicked
        Point2D point = new Point2D(e.getX(), e.getY());
        // create a map point from user point
        Point mapPoint = mapView.screenToLocation(point);
        if (btnAddFacility.isSelected()) {
          // create facility and display to mapview
          Point servicePoint = new Point(mapPoint.getX(), mapPoint.getY(), spatialReference);
          serviceAreaFacilities.add(new ServiceAreaFacility(servicePoint));
          Graphic graphic = new Graphic(servicePoint, facilitySymbol);
          facilityOverlay.getGraphics().add(graphic);
        } else if (btnAddBarrier.isSelected()) {
          // create barrier and display to mapview
          barrierBuilder.addPoint(new Point(mapPoint.getX(), mapPoint.getY(), spatialReference));
          barrierOverlay.getGraphics().add(barrierOverlay.getGraphics().size(),
              new Graphic(barrierBuilder.toGeometry(), fillSymbols.get(0).getOutline()));
        }
      }
    });
  }

  /**
   * creates task to compute services areas with given region from url and get default parameters from task.
   */
  private void createServiceAreaTask() {
    final String sanFranRegion =
        "http://ragss12512:6080/arcgis/rest/services/NA/SanFrancisco_GPNAS/NAServer/Service%20Area";
    task = new ServiceAreaTask(sanFranRegion);
    task.loadAsync();

    // get default parameters from service area task
    ListenableFuture<ServiceAreaParameters> parameters = task.createDefaultParametersAsync();
    parameters.addDoneListener(() -> {
      try {
        serviceAreaParameters = parameters.get();
        serviceAreaParameters.setOutputSpatialReference(spatialReference);
        // allows use to display service areas to mapview
        serviceAreaParameters.setReturnPolygons(true);
      } catch (ExecutionException | InterruptedException e) {
        e.printStackTrace();
      }
    });
  }

  /**
   * Starts creating a new barrier is barrier button is selected.
   */
  @FXML
  private void createBarrier() {
    if (btnAddBarrier.isSelected()) {
      barrierBuilder = new PolylineBuilder(spatialReference);
    }
  }

  /**
   * Clears all graphics from mapview and clears all faclilities and barriers from service area parameters.
   */
  @FXML
  private void clearRouteAndGraphics() {
    serviceAreaParameters.clearFacilities();
    serviceAreaParameters.clearPolylineBarriers();
    serviceAreaFacilities.clear();
    facilityOverlay.getGraphics().clear();
    polygonOverlay.getGraphics().clear();
    barrierOverlay.getGraphics().clear();
  }

  /**
   * Solves Service Areas Task using the facilities and barriers that were added to the mapview.
   * <p>
   * All service areas that are return will be displayed to the mapview.
   */
  @FXML
  private void showServiceAreas() {

    //turn barrier button off and add any barriers to service area parameters
    btnAddBarrier.setSelected(false);
    List<PolylineBarrier> polylineBarriers = new ArrayList<>();
    barrierOverlay.getGraphics().forEach(graphic -> {
      polylineBarriers.add(new PolylineBarrier((Polyline) graphic.getGeometry()));
    });
    serviceAreaParameters.setPolylineBarriers(polylineBarriers);

    // need at least one facility for the task to work
    if (serviceAreaFacilities.size() > 0) {
      // get rid of any previous service area graphics
      polygonOverlay.getGraphics().clear();
      serviceAreaParameters.setFacilities(serviceAreaFacilities);
      // find service areas around facility using parameters that were set
      ListenableFuture<ServiceAreaResult> result = task.solveServiceAreaAsync(serviceAreaParameters);
      result.addDoneListener(() -> {
        try {
          // display all service areas for all service are facilities and display to mapview
          List<Graphic> graphics = polygonOverlay.getGraphics();
          ServiceAreaResult serviceAreaResult = result.get();
          for (int i = 0; i < serviceAreaFacilities.size(); i++) {
            List<ServiceAreaPolygon> polygons = serviceAreaResult.getResultPolygons(i);
            for (int j = 0; j < polygons.size(); j++) {
              graphics.add(new Graphic(polygons.get(j).getGeometry(), fillSymbols.get(j % 3)));
            }
          }
        } catch (ExecutionException | InterruptedException e) {
          if (e.getMessage().contains("Unable to complete operation")) {
            showErrorMessage("Facility not within SanFrancisco area!");
          } else {
            e.printStackTrace();
          }
        }
      });
    } else {
      showErrorMessage("Must have atleast 1 Facility!");
    }
  }

  /** 
   * Shows error message to user if something went wrong with solving task.
   * 
   * @param message error message to show.
   */
  private void showErrorMessage(String message) {
    Alert dialog = new Alert(AlertType.WARNING);
    dialog.setHeaderText(null);
    dialog.setTitle("Error");
    dialog.setContentText(message);
    dialog.showAndWait();
  }

  /**
   * Creates objects for displaying graphics to mapview.
   */
  private void setDisplayValues() {
    // setup colors for multiple service areas
    SimpleLineSymbol outline = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0xFF000000, 3.0f);
    fillSymbols = new ArrayList<>();
    fillSymbols.add(new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, 0x6600FF00, outline));
    fillSymbols.add(new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, 0x66FFFF00, outline));
    fillSymbols.add(new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, 0x66FF0000, outline));

    // storing graphics to display to mapview
    facilityOverlay = new GraphicsOverlay();
    polygonOverlay = new GraphicsOverlay();
    barrierOverlay = new GraphicsOverlay();
    mapView.getGraphicsOverlays().add(facilityOverlay);
    mapView.getGraphicsOverlays().add(polygonOverlay);
    mapView.getGraphicsOverlays().add(barrierOverlay);

    barrierBuilder = new PolylineBuilder(spatialReference);
    serviceAreaFacilities = new ArrayList<>();
  }

  /**
   * Stops and releases all resources used in application.
   */
  void terminate() {

    if (mapView != null) {
      mapView.dispose();
    }
  }
}
