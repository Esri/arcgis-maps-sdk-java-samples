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

package com.esri.samples.na.find_service_areas_for_multiple_facilities;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.ArcGISFeatureTable;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.DrawStatus;
import com.esri.arcgisruntime.mapping.view.DrawStatusChangedEvent;
import com.esri.arcgisruntime.mapping.view.DrawStatusChangedListener;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;
import com.esri.arcgisruntime.tasks.networkanalysis.ServiceAreaParameters;
import com.esri.arcgisruntime.tasks.networkanalysis.ServiceAreaPolygon;
import com.esri.arcgisruntime.tasks.networkanalysis.ServiceAreaPolygonDetail;
import com.esri.arcgisruntime.tasks.networkanalysis.ServiceAreaResult;
import com.esri.arcgisruntime.tasks.networkanalysis.ServiceAreaTask;

public class FindServiceAreasForMultipleFacilitiesSample extends Application {

  private MapView mapView;

  @Override
  public void start(Stage stage) {

    // create stack pane and application scene
    StackPane stackPane = new StackPane();
    Scene scene = new Scene(stackPane);
    scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

    // set title, size, and add scene to stage
    stage.setTitle("Find Service Area for Facilities Sample");
    stage.setWidth(800);
    stage.setHeight(700);
    stage.setScene(scene);
    stage.show();

    // create button
    Button findServiceAreasButton = new Button("Find Service Areas");
    findServiceAreasButton.setMaxWidth(150);
    findServiceAreasButton.setDisable(true);

    // create a progress indicator
    ProgressIndicator progressIndicator = new ProgressIndicator();
    progressIndicator.setVisible(false);

    // create an ArcGISMap with a streets basemap
    ArcGISMap map = new ArcGISMap(Basemap.createLightGrayCanvas());

    // set the ArcGISMap to be displayed in the map view
    mapView = new MapView();
    mapView.setMap(map);

    // create a graphics overlay for displaying service areas
    GraphicsOverlay serviceAreasGraphicsOverlay = new GraphicsOverlay();

    // add the graphics overlay to the map view
    mapView.getGraphicsOverlays().add(serviceAreasGraphicsOverlay);

    // create fill symbols for rendering the result
    ArrayList<SimpleFillSymbol> fillSymbols = new ArrayList<>();
    fillSymbols.add(new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, 0x66FFA500, null));
    fillSymbols.add(new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, 0x66FF0000, null));

    // create a feature table of facilities using a FeatureServer
    ArcGISFeatureTable facilitiesTable = new ServiceFeatureTable("https://services2.arcgis.com/ZQgQTuoyBrtmoGdP/ArcGIS/rest/services/San_Diego_Facilities/FeatureServer/0");
    // create a feature layer from the table
    FeatureLayer facilitiesFeatureLayer = new FeatureLayer(facilitiesTable);

    // create a symbol used to display the facilities
    PictureMarkerSymbol facilitySymbol = new PictureMarkerSymbol("http://static.arcgis.com/images/Symbols/SafetyHealth/Hospital.png");
    facilitySymbol.setHeight(50);
    facilitySymbol.setWidth(50);
    // set the renderer of the facilities feature layer to use the facilities symbol
    facilitiesFeatureLayer.setRenderer(new SimpleRenderer(facilitySymbol));

    // add the facilities feature layer to the map
    map.getOperationalLayers().add(facilitiesFeatureLayer);

    // wait for the facilities feature layer to load
    facilitiesFeatureLayer.addDoneLoadingListener(() -> {
      if (facilitiesFeatureLayer.getLoadStatus() == LoadStatus.LOADED) {

        // zoom to the extent of the feature layer
        mapView.setViewpointGeometryAsync(facilitiesFeatureLayer.getFullExtent(), 130);

        // enable the find service areas button when the draw status is completed for the first time
        mapView.addDrawStatusChangedListener(new DrawStatusChangedListener() {
          @Override
          public void drawStatusChanged(DrawStatusChangedEvent drawStatusChangedEvent) {
            if (drawStatusChangedEvent.getDrawStatus() == DrawStatus.COMPLETED) {
              // enable the 'find service areas' button
              findServiceAreasButton.setDisable(false);
              mapView.removeDrawStatusChangedListener(this);
            }
          }
        });

        // determine the service areas and display them when the button is clicked
        findServiceAreasButton.setOnAction(event -> {

          // disable the button
          findServiceAreasButton.setDisable(true);

          // show the progress indicator
          progressIndicator.setVisible(true);

          // create a service area task from URL
          ServiceAreaTask serviceAreaTask = new ServiceAreaTask("https://sampleserver6.arcgisonline.com/arcgis/rest/services/NetworkAnalysis/SanDiego/NAServer/ServiceArea");
          serviceAreaTask.loadAsync();

          // create default service area task parameters
          ListenableFuture<ServiceAreaParameters> serviceAreaTaskParametersFuture = serviceAreaTask.createDefaultParametersAsync();
          serviceAreaTaskParametersFuture.addDoneListener(() -> {
            try {
              ServiceAreaParameters serviceAreaParameters = serviceAreaTaskParametersFuture.get();
              // set the task parameters to have the task return polygons
              serviceAreaParameters.setPolygonDetail(ServiceAreaPolygonDetail.HIGH);
              serviceAreaParameters.setReturnPolygons(true);
              // add a service area of 3 minutes travel time by car in addition to the default of 5 minutes travel time
              serviceAreaParameters.getDefaultImpedanceCutoffs().add(3.0);

              // create query parameters used to select all facilities from the feature table
              QueryParameters queryParameters = new QueryParameters();
              queryParameters.setWhereClause("1=1");

              // add all facilities to the service area parameters
              serviceAreaParameters.setFacilities(facilitiesTable, queryParameters);

              // find the service areas around the facilities using the parameters
              ListenableFuture<ServiceAreaResult> serviceAreaResultFuture = serviceAreaTask.solveServiceAreaAsync(serviceAreaParameters);
              serviceAreaResultFuture.addDoneListener(() -> {
                try {
                  // get the task results
                  ServiceAreaResult serviceAreaResult = serviceAreaResultFuture.get();

                  // display all the service areas that were found to the map view
                  List<Graphic> serviceAreaGraphics = serviceAreasGraphicsOverlay.getGraphics();

                  // iterate through all the facilities to get the service area polygons
                  for (int i = 0; i < serviceAreaResult.getFacilities().size(); i++) {
                    List<ServiceAreaPolygon> serviceAreaPolygonList = serviceAreaResult.getResultPolygons(i);
                    // create a graphic for each available polygon, as there may be more than one for each service area
                    for (int j = 0; j < serviceAreaPolygonList.size(); j++) {
                      // create and show a graphics for the service area
                      serviceAreaGraphics.add(new Graphic(serviceAreaPolygonList.get(j).getGeometry(), fillSymbols.get(j % fillSymbols.size())));
                    }
                  }

                } catch (ExecutionException | InterruptedException e) {
                  new Alert(Alert.AlertType.ERROR, "Error solving the service area task").show();
                }
                // hide the progress indicator after the task is complete
                progressIndicator.setVisible(false);
              });

            } catch (ExecutionException | InterruptedException e) {
              e.printStackTrace();
            }
          });
        });
      }
    });

    // add the map view, find service area button, and progress indicator to stack pane
    stackPane.getChildren().addAll(mapView, findServiceAreasButton, progressIndicator);
    StackPane.setAlignment(findServiceAreasButton, Pos.TOP_LEFT);
    StackPane.setMargin(findServiceAreasButton, new Insets(10, 0, 0, 10));
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
