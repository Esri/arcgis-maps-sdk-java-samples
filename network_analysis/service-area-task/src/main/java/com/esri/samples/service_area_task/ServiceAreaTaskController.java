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

package com.esri.samples.na.service_area_task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ProgressIndicator;
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
import com.esri.arcgisruntime.tasks.networkanalysis.ServiceAreaPolygonDetail;
import com.esri.arcgisruntime.tasks.networkanalysis.ServiceAreaResult;
import com.esri.arcgisruntime.tasks.networkanalysis.ServiceAreaTask;

public class ServiceAreaTaskController {

  @FXML private MapView mapView;
  @FXML private ToggleButton btnAddFacility;
  @FXML private ToggleButton btnAddBarrier;
  @FXML private ProgressIndicator progressIndicator;

  // all location were service areas will be found
  private List<ServiceAreaFacility> serviceAreaFacilities;

  // task to find service area around a facility
  private ServiceAreaTask serviceAreaTask;
  // used for solving task above
  private ServiceAreaParameters serviceAreaParameters;
  // for displaying service area facilities to the mapview
  private GraphicsOverlay facilityOverlay;
  // for displaying service areas to the mapview
  private GraphicsOverlay serviceAreasOverlay;
  // for displaying barriers to mapview
  private GraphicsOverlay barrierOverlay;
  // used to make barriers
  private PolylineBuilder barrierBuilder;
  // fills service areas with a color when displayed to mapview
  private List<SimpleFillSymbol> fillSymbols;
  // used for placing geometry on mapview
  private final SpatialReference spatialReference = SpatialReferences.getWebMercator();

  @FXML
  public void initialize() {

    ArcGISMap map = new ArcGISMap(Basemap.createStreets());
    mapView.setMap(map);
    // set mapview to San Diego
    mapView.setViewpoint(new Viewpoint(32.73, -117.14, 60000));

    // create service area task from url
    final String SanDiegoRegion = "https://sampleserver6.arcgisonline.com/arcgis/rest/services/NetworkAnalysis/SanDiego/NAServer/ServiceArea";
    serviceAreaTask = new ServiceAreaTask(SanDiegoRegion);
    serviceAreaTask.loadAsync();
    // create default parameters from task
    ListenableFuture<ServiceAreaParameters> parameters = serviceAreaTask.createDefaultParametersAsync();
    parameters.addDoneListener(() -> {
      try {
        serviceAreaParameters = parameters.get();
        serviceAreaParameters.setPolygonDetail(ServiceAreaPolygonDetail.HIGH);
        serviceAreaParameters.setReturnPolygons(true);
        // adding another service area of 2 minutes
        // default parameters have a default service area of 5 minutes
        serviceAreaParameters.getDefaultImpedanceCutoffs().addAll(Collections.singletonList(2.0));
      } catch (ExecutionException | InterruptedException e) {
        e.printStackTrace();
      }
    });

    // for displaying graphics to mapview
    serviceAreasOverlay = new GraphicsOverlay();
    facilityOverlay = new GraphicsOverlay();
    barrierOverlay = new GraphicsOverlay();
    mapView.getGraphicsOverlays().addAll(Arrays.asList(serviceAreasOverlay, barrierOverlay, facilityOverlay));

    barrierBuilder = new PolylineBuilder(spatialReference);
    serviceAreaFacilities = new ArrayList<>();

    SimpleLineSymbol outline = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0xFF000000, 3.0f);
    fillSymbols = new ArrayList<>();
    fillSymbols.add(new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, 0x66FF0000, outline));
    fillSymbols.add(new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, 0x66FFA500, outline));

    // icon used to display facilities to mapview
    String facilityUrl = "http://static.arcgis.com/images/Symbols/SafetyHealth/Hospital.png";
    PictureMarkerSymbol facilitySymbol = new PictureMarkerSymbol(facilityUrl);
    facilitySymbol.setHeight(30);
    facilitySymbol.setWidth(30);

    // creates facilities and barriers at user's clicked location
    mapView.setOnMouseClicked(e -> {
      if (e.getButton() == MouseButton.PRIMARY && e.isStillSincePress()) {
        // create a point from where the user clicked
        Point2D point = new Point2D(e.getX(), e.getY());
        Point mapPoint = mapView.screenToLocation(point);
        if (btnAddFacility.isSelected()) {
          // create facility from point and display to mapview
          Point servicePoint = new Point(mapPoint.getX(), mapPoint.getY(), spatialReference);
          serviceAreaFacilities.add(new ServiceAreaFacility(servicePoint));
          facilityOverlay.getGraphics().add(new Graphic(servicePoint, facilitySymbol));
        } else if (btnAddBarrier.isSelected()) {
          // create barrier and display to mapview
          barrierBuilder.addPoint(new Point(mapPoint.getX(), mapPoint.getY(), spatialReference));
          barrierOverlay.getGraphics().add(barrierOverlay.getGraphics().size(), new Graphic(barrierBuilder.toGeometry(), outline));
        }
      }
    });
  }

  /**
   * Starts creating a new barrier if barrier button is selected.
   */
  @FXML
  private void createBarrier() {
    if (btnAddBarrier.isSelected()) {
      barrierBuilder = new PolylineBuilder(spatialReference);
    }
  }

  /**
   * Clears all graphics from mapview and clears all facilities and barriers from service area parameters.
   */
  @FXML
  private void clearRouteAndGraphics() {
    serviceAreaParameters.clearFacilities();
    serviceAreaParameters.clearPolylineBarriers();
    serviceAreaFacilities.clear();
    facilityOverlay.getGraphics().clear();
    serviceAreasOverlay.getGraphics().clear();
    barrierOverlay.getGraphics().clear();
  }

  /**
   * Solves Service Areas Task using the facilities and barriers that were added to the mapview.
   * <p>
   * All service areas that are return will be displayed to the mapview.
   */
  @FXML
  private void showServiceAreas() {

    // need at least one facility for the task to work
    if (serviceAreaFacilities.size() > 0) {
      progressIndicator.setVisible(true);
      //turn barrier button off and add any barriers to service area parameters
      btnAddBarrier.setSelected(false);
      List<PolylineBarrier> polylineBarriers = new ArrayList<>();
      barrierOverlay.getGraphics()
          .forEach(barrier -> polylineBarriers.add(new PolylineBarrier((Polyline) barrier.getGeometry())));
      serviceAreaParameters.setPolylineBarriers(polylineBarriers);

      serviceAreasOverlay.getGraphics().clear();
      serviceAreaParameters.setFacilities(serviceAreaFacilities);
      // find service areas around facility using parameters that were set
      ListenableFuture<ServiceAreaResult> result = serviceAreaTask.solveServiceAreaAsync(serviceAreaParameters);
      result.addDoneListener(() -> {
        try {
          // display all service areas that were found to mapview
          List<Graphic> graphics = serviceAreasOverlay.getGraphics();
          ServiceAreaResult serviceAreaResult = result.get();
          for (int i = 0; i < serviceAreaFacilities.size(); i++) {
            List<ServiceAreaPolygon> polygons = serviceAreaResult.getResultPolygons(i);
            // could be more than one service area
            for (int j = 0; j < polygons.size(); j++) {
              graphics.add(new Graphic(polygons.get(j).getGeometry(), fillSymbols.get(j % 2)));
            }
          }
        } catch (ExecutionException | InterruptedException e) {
          if (e.getMessage().contains("Unable to complete operation")) {
            showErrorMessage("Facility not within San Diego area!");
          } else {
            e.printStackTrace();
          }
        }
        progressIndicator.setVisible(false);
      });
    } else {
      showErrorMessage("Must have at least 1 Facility!");
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
   * Stops and releases all resources used in application.
   */
  void terminate() {

    if (mapView != null) {
      mapView.dispose();
    }
  }
}
