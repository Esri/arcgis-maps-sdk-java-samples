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

package com.esri.samples.symbology.simple_renderer;

import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class SimpleRendererSample extends Application {

  private MapView mapView;

  @Override
  public void start(Stage stage) throws Exception {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);

      // size the stage, add a title, and set scene to stage
      stage.setTitle("Simple Renderer Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // create a ArcGISMap with the imagery basemap
      final ArcGISMap map = new ArcGISMap(Basemap.createImageryWithLabels());

      // create a view and set ArcGISMap to it
      mapView = new MapView();
      mapView.setMap(map);

      // create SpatialReference for points
      final SpatialReference spatialReference = SpatialReferences.getWgs84();

      // create points for displaying graphics
      Point oldFaithfullPoint = new Point(-110.828140, 44.460458, spatialReference);
      Point cascadeGeyserPoint = new Point(-110.829004, 44.462438, spatialReference);
      Point plumeGeyserPoint = new Point(-110.829381, 44.462735, spatialReference);

      // create initial viewpoint using an envelope
      Envelope envelope = new Envelope(oldFaithfullPoint, plumeGeyserPoint);

      // set viewpoint on mapview with padding
      mapView.setViewpointGeometryAsync(envelope, 100.0);

      // create a graphics overlay and add it to the mapview
      GraphicsOverlay graphicsOverlay = new GraphicsOverlay();
      mapView.getGraphicsOverlays().add(graphicsOverlay);

      // create a red (0xFFFF0000) simple symbol for use in a simple renderer
      SimpleMarkerSymbol symbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CROSS, 0xFFFF0000, 12);
      SimpleRenderer renderer = new SimpleRenderer(symbol);

      // apply the renderer to the graphics overlay
      graphicsOverlay.setRenderer(renderer);

      // create graphics from the location points.
      Graphic oldFaithfullGraphic = new Graphic(oldFaithfullPoint);

      Graphic cascadeGeyserGraphic = new Graphic(cascadeGeyserPoint);
      Graphic plumeGeyserGraphic = new Graphic(plumeGeyserPoint);
      graphicsOverlay.getGraphics().add(oldFaithfullGraphic);
      graphicsOverlay.getGraphics().add(cascadeGeyserGraphic);
      graphicsOverlay.getGraphics().add(plumeGeyserGraphic);

      // add the map view and control box to stack pane
      stackPane.getChildren().add(mapView);
    } catch (Exception e) {
      // on any error, display stack trace
      e.printStackTrace();
    }
  }

  /**
   * Stops and releases all resources used in application.
   */
  @Override
  public void stop() throws Exception {

    // release resources when the application closes
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
