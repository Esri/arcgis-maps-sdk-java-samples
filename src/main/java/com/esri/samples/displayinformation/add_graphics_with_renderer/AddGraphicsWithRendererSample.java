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

package com.esri.samples.displayinformation.add_graphics_with_renderer;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PolygonBuilder;
import com.esri.arcgisruntime.geometry.PolylineBuilder;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class AddGraphicsWithRendererSample extends Application {

  private MapView mapView;

  @Override
  public void start(Stage stage) throws Exception {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);

      // set title, size, and add scene to stage
      stage.setTitle("Add Graphics with Renderer Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // create a map with topographic basemap
      ArcGISMap map = new ArcGISMap(Basemap.Type.TOPOGRAPHIC, 15.169193, 16.333479, 2);

      // set the map to the view
      mapView = new MapView();
      mapView.setMap(map);

      // create a graphics overlay for displaying point graphic
      GraphicsOverlay pointGraphicOverlay = new GraphicsOverlay();
      // create point geometry
      Point point = new Point(40e5, 40e5, SpatialReferences.getWebMercator());
      // create graphic for point
      Graphic pointGraphic = new Graphic(point);
      // red (0xFFFF0000) diamond point symbol
      SimpleMarkerSymbol pointSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.DIAMOND, 0xFFFF0000, 10);
      // create simple renderer
      SimpleRenderer pointRenderer = new SimpleRenderer(pointSymbol);
      // set renderer on graphics overlay
      pointGraphicOverlay.setRenderer(pointRenderer);
      // add graphic to overlay
      pointGraphicOverlay.getGraphics().add(pointGraphic);
      // add graphics overlay to the MapView
      mapView.getGraphicsOverlays().add(pointGraphicOverlay);

      // solid blue (0xFF0000FF) line graphic
      GraphicsOverlay lineGraphicOverlay = new GraphicsOverlay();
      PolylineBuilder lineGeometry = new PolylineBuilder(SpatialReferences.getWebMercator());
      lineGeometry.addPoint(-10e5, 40e5);
      lineGeometry.addPoint(20e5, 50e5);
      Graphic lineGraphic = new Graphic(lineGeometry.toGeometry());
      lineGraphicOverlay.getGraphics().add(lineGraphic);
      SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0xFF0000FF, 5);
      SimpleRenderer lineRenderer = new SimpleRenderer(lineSymbol);
      lineGraphicOverlay.setRenderer(lineRenderer);
      mapView.getGraphicsOverlays().add(lineGraphicOverlay);

      // solid yellow (0xFFFFFF00) polygon graphic
      GraphicsOverlay polygonGraphicOverlay = new GraphicsOverlay();
      PolygonBuilder polygonGeometry = new PolygonBuilder(SpatialReferences.getWebMercator());
      polygonGeometry.addPoint(-20e5, 20e5);
      polygonGeometry.addPoint(20e5, 20e5);
      polygonGeometry.addPoint(20e5, -20e5);
      polygonGeometry.addPoint(-20e5, -20e5);
      Graphic polygonGraphic = new Graphic(polygonGeometry.toGeometry());
      polygonGraphicOverlay.getGraphics().add(polygonGraphic);
      SimpleFillSymbol polygonSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, 0xFFFFFF00, null);
      SimpleRenderer polygonRenderer = new SimpleRenderer(polygonSymbol);
      polygonGraphicOverlay.setRenderer(polygonRenderer);
      mapView.getGraphicsOverlays().add(polygonGraphicOverlay);

      // add the map view to stack pane
      stackPane.getChildren().add(mapView);
    } catch (Exception e) {
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
