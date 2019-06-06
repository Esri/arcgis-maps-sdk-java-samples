/*
 * Copyright 2019 Esri.
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

package com.esri.samples.na.closest_facility_static;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.FeatureTable;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;
import com.esri.arcgisruntime.tasks.networkanalysis.ClosestFacilityParameters;
import com.esri.arcgisruntime.tasks.networkanalysis.ClosestFacilityResult;
import com.esri.arcgisruntime.tasks.networkanalysis.ClosestFacilityRoute;
import com.esri.arcgisruntime.tasks.networkanalysis.ClosestFacilityTask;
import com.esri.arcgisruntime.tasks.networkanalysis.Facility;
import com.esri.arcgisruntime.tasks.networkanalysis.Incident;

public class ClosestFacilityStaticSample extends Application {

  private MapView mapView;

  @Override
  public void start(Stage stage) throws Exception {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);

      // set title, size, and add scene to stage
      stage.setTitle("Closest Facility (Static) Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // create buttons
      Button solveRoutesButton = new Button("Solve Routes");
      solveRoutesButton.setMaxWidth(150);
      solveRoutesButton.setDisable(true);

      // create a progress indicator
      ProgressIndicator progressIndicator = new ProgressIndicator();
      progressIndicator.setVisible(true);

      // create a ArcGISMap with a Basemap instance with an Imagery base layer
      ArcGISMap map = new ArcGISMap(Basemap.createStreetsWithReliefVector());

      // set the map to be displayed in this view
      mapView = new MapView();
      mapView.setMap(map);

      // create a graphics overlay and add it to the map
      GraphicsOverlay graphicsOverlay = new GraphicsOverlay();
      mapView.getGraphicsOverlays().add(graphicsOverlay);

      // create Symbols for displaying facilities
      PictureMarkerSymbol facilitySymbol = new PictureMarkerSymbol(new Image("https://static.arcgis.com/images/Symbols/SafetyHealth/FireStation.png", 30, 30, true, false));
      PictureMarkerSymbol incidentSymbol = new PictureMarkerSymbol(new Image("https://static.arcgis.com/images/Symbols/SafetyHealth/esriCrimeMarker_56_Gradient.png", 30, 30, true, false));

      // create a line symbol to mark the route
      SimpleLineSymbol simpleLineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0x4D0000FF, 5.0f);

      // create a closest facility task from a network analysis service
      ClosestFacilityTask closestFacilityTask = new ClosestFacilityTask("https://sampleserver6.arcgisonline.com/arcgis/rest/services/NetworkAnalysis/SanDiego/NAServer/ClosestFacility");

      // create a table for facilities using the feature service
      FeatureTable facilitiesFeatureTable = new ServiceFeatureTable("https://services2.arcgis.com/ZQgQTuoyBrtmoGdP/ArcGIS/rest/services/San_Diego_Facilities/FeatureServer/0");
      // create a feature layer from the table, apply facilities icon
      FeatureLayer facilitiesFeatureLayer = new FeatureLayer(facilitiesFeatureTable);
      facilitiesFeatureLayer.setRenderer(new SimpleRenderer(facilitySymbol));

      // create a table for incidents using the feature service
      FeatureTable incidentsFeatureTable = new ServiceFeatureTable("https://services2.arcgis.com/ZQgQTuoyBrtmoGdP/ArcGIS/rest/services/San_Diego_Incidents/FeatureServer/0");
      // create a feature layer from the table, apply incident icon
      FeatureLayer incidentsFeatureLayer = new FeatureLayer(incidentsFeatureTable);
      incidentsFeatureLayer.setRenderer(new SimpleRenderer(incidentSymbol));

      // add the layers to the map
      map.getOperationalLayers().addAll(Arrays.asList(facilitiesFeatureLayer, incidentsFeatureLayer));

      // create the list to store the facilities
      ArrayList<Facility> facilities = new ArrayList<>();

      // create the list to store the incidents
      ArrayList<Incident> incidents = new ArrayList<>();

      // wait for the feature layers to load to retrieve the facilities and incidents
      facilitiesFeatureLayer.addDoneLoadingListener(() -> incidentsFeatureLayer.addDoneLoadingListener(() -> {
        if (facilitiesFeatureLayer.getLoadStatus() == LoadStatus.LOADED && incidentsFeatureLayer.getLoadStatus() == LoadStatus.LOADED) {

          // hide the progress indicator
          progressIndicator.setVisible(false);

          // zoom to the extent of the combined feature layers
          Envelope fullFeatureLayerExtent = GeometryEngine.combineExtents(facilitiesFeatureLayer.getFullExtent(), incidentsFeatureLayer.getFullExtent());
          mapView.setViewpointGeometryAsync(fullFeatureLayerExtent, 90);

          // create query parameters to select all features
          QueryParameters queryParameters = new QueryParameters();
          queryParameters.setWhereClause("1=1");

          // retrieve a list of all facilities
          ListenableFuture<FeatureQueryResult> result = facilitiesFeatureTable.queryFeaturesAsync(queryParameters);
          result.addDoneListener(() -> {
            try {
              FeatureQueryResult facilitiesResult = result.get();

              // add the found facilities to the list
              for (Feature facilityFeature : facilitiesResult) {
                // since we know our feature layer only contains point features, we can cast them as Point in order to create a Facility
                facilities.add(new Facility((Point) facilityFeature.getGeometry()));
              }

            } catch (InterruptedException | ExecutionException e) {
              new Alert(Alert.AlertType.ERROR, "Error retrieving list of facilities.").show();
            }
          });

          // retrieve a list of all incidents
          ListenableFuture<FeatureQueryResult> incidentsQueryResult = incidentsFeatureTable.queryFeaturesAsync(queryParameters);
          incidentsQueryResult.addDoneListener(() -> {
            try {
              FeatureQueryResult incidentsResult = incidentsQueryResult.get();

              // add the found incidents to the list
              for (Feature incidentFeature : incidentsResult) {
                // since we know our feature layer only contains point features, we can cast them as Point in order to create an Incident
                incidents.add(new Incident((Point) incidentFeature.getGeometry()));
              }

            } catch (InterruptedException | ExecutionException e) {
              new Alert(Alert.AlertType.ERROR, "Error retrieving list of incidents.").show();
            }
          });

          // enable the 'solve routes' button
          solveRoutesButton.setDisable(false);

          // resolve button press
          solveRoutesButton.setOnAction(e -> {

            // disable the 'solve routes' button and show the progress indicator
            solveRoutesButton.setDisable(true);
            progressIndicator.setVisible(true);

            // start the routing task
            closestFacilityTask.loadAsync();
            closestFacilityTask.addDoneLoadingListener(() -> {
              if (closestFacilityTask.getLoadStatus() == LoadStatus.LOADED) {
                try {
                  // create default parameters for the task and add facilities and incidents to parameters
                  ClosestFacilityParameters closestFacilityParameters = closestFacilityTask.createDefaultParametersAsync().get();
                  closestFacilityParameters.setFacilities(facilities);
                  closestFacilityParameters.setIncidents(incidents);

                  // solve closest facilities
                  try {
                    // use the task to solve for the closest facility
                    ListenableFuture<ClosestFacilityResult> closestFacilityTaskResult = closestFacilityTask.solveClosestFacilityAsync(closestFacilityParameters);
                    closestFacilityTaskResult.addDoneListener(() -> {
                      try {
                        ClosestFacilityResult closestFacilityResult = closestFacilityTaskResult.get();

                        // find the closest facility for each incident
                        for (int incidentIndex = 0; incidentIndex < incidents.size(); incidentIndex++) {

                          // get the index of the closest facility to incident
                          Integer closestFacilityIndex = closestFacilityResult.getRankedFacilityIndexes(incidentIndex).get(0);

                          // get the route to the closest facility
                          ClosestFacilityRoute closestFacilityRoute = closestFacilityResult.getRoute(closestFacilityIndex, incidentIndex);

                          // display the route on the graphics overlay
                          graphicsOverlay.getGraphics().add(new Graphic(closestFacilityRoute.getRouteGeometry(), simpleLineSymbol));

                          // hide the progress indicator and enable the reset button
                          progressIndicator.setVisible(false);
                        }

                      } catch (ExecutionException | InterruptedException ex) {
                        new Alert(Alert.AlertType.ERROR, "Error getting the closest facility task result.").show();
                      }
                    });

                  } catch (Exception ex) {
                    new Alert(Alert.AlertType.ERROR, "Error solving the closest facility task.").show();
                  }

                } catch (InterruptedException | ExecutionException ex) {
                  new Alert(Alert.AlertType.ERROR, "Error getting default route parameters.").show();
                }

              } else {
                new Alert(Alert.AlertType.ERROR, "Error loading route task.").show();
              }
            });
          });
        }
      })
      );

      // add the map view, control panel and progress indicator to the stack pane
      stackPane.getChildren().addAll(mapView, solveRoutesButton, progressIndicator);
      StackPane.setAlignment(solveRoutesButton, Pos.TOP_LEFT);
      StackPane.setMargin(solveRoutesButton, new Insets(10, 0, 0, 10));

    } catch (Exception e) {
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
