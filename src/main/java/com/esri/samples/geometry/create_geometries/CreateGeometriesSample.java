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

package com.esri.samples.geometry.create_geometries;

import java.util.Arrays;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Multipoint;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.Polygon;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
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

      // create a map with a basemap and add it to the map view
      ArcGISMap map = new ArcGISMap(Basemap.createTopographic());
      mapView = new MapView();
      mapView.setMap(map);

      // create a graphics overlay
      GraphicsOverlay graphicsOverlay = new GraphicsOverlay();
      mapView.getGraphicsOverlays().add(graphicsOverlay);

      // create envelope
      Envelope envelope = new Envelope(-123.0, 33.5, -101.0, 48.0, SpatialReferences.getWgs84());

      // create a point
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

      // create a polyline
      PointCollection borderCAtoNV = new PointCollection(SpatialReferences.getWgs84());
      borderCAtoNV.add(-119.992, 41.989);
      borderCAtoNV.add(-119.994, 38.994);
      borderCAtoNV.add(-114.620, 35.0);
      Polyline polyline = new Polyline(borderCAtoNV);
      Graphic polylineGraphic = new Graphic(polyline, new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0xFF00FFFF,
          3));

      // create a polygon
      PointCollection coloradoCorners = new PointCollection(SpatialReferences.getWgs84());
      coloradoCorners.add(-109.048, 40.998);
      coloradoCorners.add(-102.047, 40.998);
      coloradoCorners.add(-102.037, 36.989);
      coloradoCorners.add(-109.048, 36.998);
      Polygon polygon = new Polygon(coloradoCorners);
      Graphic polygonGraphic = new Graphic(polygon, new SimpleFillSymbol(SimpleFillSymbol.Style.CROSS, 0xFF00FF00,
          null));

      // add all of the graphics to the graphics overlay
      graphicsOverlay.getGraphics().addAll(Arrays.asList(multipointGraphic, polylineGraphic, polygonGraphic,
          pointGraphic));

      // use the envelope to set the viewpoint
      mapView.setViewpointGeometryAsync(envelope);

      // add the map view to the stack pane
      stackPane.getChildren().addAll(mapView);
    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
    }
  }

  /**
   * Creates a polyline along the US/Canada border over Lake Superior.
   *
   * @return poyline
   */
  private Polyline createBorder() {
    PointCollection points = new PointCollection(SpatialReferences.getWebMercator());
    points.add(new Point(-9981328.687124, 6111053.281447));
    points.add(new Point(-9946518.044066, 6102350.620682));
    points.add(new Point(-9872545.427566, 6152390.920079));
    points.add(new Point(-9838822.617103, 6157830.083057));
    points.add(new Point(-9446115.050097, 5927209.572793));
    points.add(new Point(-9430885.393759, 5876081.440801));
    points.add(new Point(-9415655.737420, 5860851.784463));
    return new Polyline(points);
  }

  /**
   * Creates a polygon of points around Lake Superior.
   *
   * @return polygon
   */
  private Polygon createLakeSuperiorPolygon() {
    PointCollection points = new PointCollection(SpatialReferences.getWebMercator());
    points.add(new Point(-10254374.668616, 5908345.076380));
    points.add(new Point(-10178382.525314, 5971402.386779));
    points.add(new Point(-10118558.923141, 6034459.697178));
    points.add(new Point(-9993252.729399, 6093474.872295));
    points.add(new Point(-9882498.222673, 6209888.368416));
    points.add(new Point(-9821057.766387, 6274562.532928));
    points.add(new Point(-9690092.583250, 6241417.023616));
    points.add(new Point(-9605207.742329, 6206654.660191));
    points.add(new Point(-9564786.389509, 6108834.986367));
    points.add(new Point(-9449989.747500, 6095091.726408));
    points.add(new Point(-9462116.153346, 6044160.821855));
    points.add(new Point(-9417652.665244, 5985145.646738));
    points.add(new Point(-9438671.768711, 5946341.148031));
    points.add(new Point(-9398250.415891, 5922088.336339));
    points.add(new Point(-9419269.519357, 5855797.317714));
    points.add(new Point(-9467775.142741, 5858222.598884));
    points.add(new Point(-9462924.580403, 5902686.086985));
    points.add(new Point(-9598740.325877, 5884092.264688));
    points.add(new Point(-9643203.813979, 5845287.765981));
    points.add(new Point(-9739406.633691, 5879241.702350));
    points.add(new Point(-9783061.694736, 5922896.763395));
    points.add(new Point(-9844502.151022, 5936640.023354));
    points.add(new Point(-9773360.570059, 6019099.583107));
    points.add(new Point(-9883306.649729, 5968977.105610));
    points.add(new Point(-9957681.938918, 5912387.211662));
    points.add(new Point(-10055501.612742, 5871965.858842));
    points.add(new Point(-10116942.069028, 5884092.264688));
    points.add(new Point(-10111283.079633, 5933406.315128));
    points.add(new Point(-10214761.742852, 5888134.399970));
    points.add(new Point(-10254374.668616, 5901877.659929));
    return new Polygon(points);
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
