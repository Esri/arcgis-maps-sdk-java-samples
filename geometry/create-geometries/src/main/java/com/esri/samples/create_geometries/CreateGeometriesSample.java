/*
 * Copyright 2018 Esri.
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

package com.esri.samples.create_geometries;

import java.util.Arrays;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Multipoint;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.Polygon;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;

public class CreateGeometriesSample extends Application {

  private MapView mapView;

  @Override
  public void start(Stage stage) {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);

      // set title, size, and add scene to stage
      stage.setTitle("Create Geometries Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // authentication with an API key or named user is required to access basemaps and other location services
      String yourAPIKey = System.getProperty("apiKey");
      ArcGISRuntimeEnvironment.setApiKey(yourAPIKey);

      // create a map with a basemap style
      ArcGISMap map = new ArcGISMap(BasemapStyle.ARCGIS_TOPOGRAPHIC);

      // create a map view and set its map
      mapView = new MapView();
      mapView.setMap(map);

      // create a graphics overlay for displaying different geometries as graphics
      GraphicsOverlay graphicsOverlay = new GraphicsOverlay();
      mapView.getGraphicsOverlays().add(graphicsOverlay);

      // create a point geometry
      Point point = new Point(-117.195800, 34.056295, SpatialReferences.getWgs84());
      Graphic pointGraphic = new Graphic(point, new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.TRIANGLE, 0xFF0000FF, 14));

      // create a multipoint geometry
      PointCollection stateCapitalsPST = new PointCollection(SpatialReferences.getWgs84());
      stateCapitalsPST.add(-121.491014, 38.579065); // Sacramento, CA
      stateCapitalsPST.add(-122.891366, 47.039231); // Olympia, WA
      stateCapitalsPST.add(-123.043814, 44.93326); // Salem, OR
      stateCapitalsPST.add(-119.766999, 39.164885); // Carson City, NV
      Multipoint multipoint = new Multipoint(stateCapitalsPST);
      Graphic multipointGraphic = new Graphic(multipoint, new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE,
          0xFFFF0000, 12));

      // create a polyline geometry
      PointCollection borderCAtoNV = new PointCollection(SpatialReferences.getWgs84());
      borderCAtoNV.add(-119.992, 41.989);
      borderCAtoNV.add(-119.994, 38.994);
      borderCAtoNV.add(-114.620, 35.0);
      Polyline polyline = new Polyline(borderCAtoNV);
      Graphic polylineGraphic = new Graphic(polyline, new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0xFF00FFFF,
          3));

      // create a polygon geometry
      PointCollection coloradoCorners = new PointCollection(SpatialReferences.getWgs84());
      coloradoCorners.add(-109.048, 40.998);
      coloradoCorners.add(-102.047, 40.998);
      coloradoCorners.add(-102.037, 36.989);
      coloradoCorners.add(-109.048, 36.998);
      Polygon polygon = new Polygon(coloradoCorners);
      Graphic polygonGraphic = new Graphic(polygon, new SimpleFillSymbol(SimpleFillSymbol.Style.CROSS, 0xFF00FF00,
          null));

      // create an envelope
      Envelope envelope = new Envelope(-123.0, 33.5, -101.0, 48.0, SpatialReferences.getWgs84());

      // use the envelope to set the viewpoint
      mapView.setViewpointGeometryAsync(envelope);

      // add all of the graphics to the graphics overlay
      graphicsOverlay.getGraphics().addAll(Arrays.asList(multipointGraphic, polylineGraphic, polygonGraphic,
          pointGraphic));

      // add the map view to the stack pane
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
