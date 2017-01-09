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

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReference;
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
import com.esri.arcgisruntime.tasks.networkanalysis.ClosestFacilityTask;
import com.esri.arcgisruntime.tasks.networkanalysis.Facility;

public class ClosestFacilitySample extends Application {

  private MapView mapView;

  @Override
  public void start(Stage stage) throws Exception {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);

      // set title, size, and add scene to stage
      stage.setTitle("Closest Facility Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // create a map with streets basemap
      ArcGISMap map = new ArcGISMap(Basemap.createStreets());

      // set the map to be displayed in this view
      mapView = new MapView();
      mapView.setMap(map);

      //
      SimpleMarkerSymbol crossSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CROSS, 0xFF000000, 20);
      SimpleLineSymbol routeSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0xFF0000FF, 2.0f);
      //
      String facilityUrl = "http://static.arcgis.com/images/Symbols/SafetyHealth/Hospital.png";
      PictureMarkerSymbol facilitySymbol = new PictureMarkerSymbol(facilityUrl);
      facilitySymbol.setHeight(30);
      facilitySymbol.setWidth(30);

      //
      GraphicsOverlay graphicsOverlay = new GraphicsOverlay();
      mapView.getGraphicsOverlays().add(graphicsOverlay);

      // set viewpoint to San Diego
      mapView.setViewpoint(new Viewpoint(32.727, -117.1750, 40000));

      // create some facilities 
      SpatialReference spatialReference = mapView.getSpatialReference();
      List<Point> facilityPoints = new ArrayList<>();
      facilityPoints.add(new Point(-1.3042129900625112E7, 3860127.9479775648, spatialReference));
      facilityPoints.add(new Point(-1.3042193400557665E7, 3862448.873041752, spatialReference));
      facilityPoints.add(new Point(-1.3046882875518233E7, 3862704.9896770366, spatialReference));
      facilityPoints.add(new Point(-1.3040539754780494E7, 3862924.5938606677, spatialReference));
      facilityPoints.add(new Point(-1.3042571225655518E7, 3858981.773018156, spatialReference));
      facilityPoints.add(new Point(-1.3039784633928463E7, 3856692.5980474586, spatialReference));
      facilityPoints.add(new Point(-1.3049023883956768E7, 3861993.789732541, spatialReference));

      List<Facility> facilities = new ArrayList<>();
      facilityPoints.forEach(facilityPoint -> {
        Graphic graphic = new Graphic(facilityPoint, facilitySymbol);
        graphicsOverlay.getGraphics().add(graphic);
        facilities.add(new Facility(facilityPoint));
      });

      ClosestFacilityTask task = new ClosestFacilityTask(
          "http://ragss12512:6080/arcgis/rest/services/NA/SanDiegoTM/NAServer/Closest%20Facility");
      task.loadAsync();

      mapView.setOnMouseClicked(e -> {
        // check that the primary mouse button was clicked
        if (e.getButton() == MouseButton.PRIMARY && e.isStillSincePress()) {
          //
          Graphic graphic = new Graphic(new Point(e.getX(), e.getY(), spatialReference), crossSymbol);
          graphicsOverlay.getGraphics().add(graphic);

          // call task to find nearest facility
          final ListenableFuture<ClosestFacilityParameters> parameters = task.createDefaultParametersAsync();
          parameters.addDoneListener(() -> {
            try {
              ClosestFacilityParameters facilityParameters = parameters.get();
              //add facilities
              // waiting on methods for facilityParameters to be completed
              //            facilityParameters.

              //add incident (cross)

              // catch all exceptions 
            } catch (Exception ex) {
              ex.printStackTrace();
            }
          });
        }
      });

      // add the map view to stack pane
      stackPane.getChildren().addAll(mapView);
    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
    }
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
