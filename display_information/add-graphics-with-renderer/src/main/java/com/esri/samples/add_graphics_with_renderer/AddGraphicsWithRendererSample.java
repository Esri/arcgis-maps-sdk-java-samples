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

package com.esri.samples.add_graphics_with_renderer;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.geometry.AngularUnit;
import com.esri.arcgisruntime.geometry.AngularUnitId;
import com.esri.arcgisruntime.geometry.CubicBezierSegment;
import com.esri.arcgisruntime.geometry.EllipticArcSegment;
import com.esri.arcgisruntime.geometry.GeodesicEllipseParameters;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.GeometryType;
import com.esri.arcgisruntime.geometry.LinearUnit;
import com.esri.arcgisruntime.geometry.LinearUnitId;
import com.esri.arcgisruntime.geometry.Part;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.Polygon;
import com.esri.arcgisruntime.geometry.PolygonBuilder;
import com.esri.arcgisruntime.geometry.PolylineBuilder;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.ColorUtil;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Arrays;

public class AddGraphicsWithRendererSample extends Application {

  private MapView mapView;

  @Override
  public void start(Stage stage) {

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

      // authentication with an API key or named user is required to access basemaps and other location services
      String yourAPIKey = System.getProperty("apiKey");
      ArcGISRuntimeEnvironment.setApiKey(yourAPIKey);

      // create a map with the topographic basemap style
      ArcGISMap map = new ArcGISMap(BasemapStyle.ARCGIS_TOPOGRAPHIC);

      // create a map view and set the map to it
      mapView = new MapView();
      mapView.setMap(map);

      // set a viewpoint on the map view
      mapView.setViewpoint(new Viewpoint(15.169193, 16.333479, 1479143818));

      // create a graphics overlay for displaying point graphic
      GraphicsOverlay pointGraphicOverlay = new GraphicsOverlay();
      // create point geometry
      Point point = new Point(40e5, 40e5, SpatialReferences.getWebMercator());
      // create graphic for point
      Graphic pointGraphic = new Graphic(point);
      // green diamond point symbol
      SimpleMarkerSymbol pointSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.DIAMOND, ColorUtil.colorToArgb(Color.GREEN), 10);
      // create simple renderer
      SimpleRenderer pointRenderer = new SimpleRenderer(pointSymbol);
      // set renderer on graphics overlay
      pointGraphicOverlay.setRenderer(pointRenderer);
      // add graphic to overlay
      pointGraphicOverlay.getGraphics().add(pointGraphic);
      // add graphics overlay to the MapView
      mapView.getGraphicsOverlays().add(pointGraphicOverlay);

      // solid blue line graphic
      GraphicsOverlay lineGraphicOverlay = new GraphicsOverlay();
      PolylineBuilder lineGeometry = new PolylineBuilder(SpatialReferences.getWebMercator());
      lineGeometry.addPoint(-10e5, 40e5);
      lineGeometry.addPoint(20e5, 50e5);
      Graphic lineGraphic = new Graphic(lineGeometry.toGeometry());
      lineGraphicOverlay.getGraphics().add(lineGraphic);
      SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, ColorUtil.colorToArgb(Color.BLUE), 5);
      SimpleRenderer lineRenderer = new SimpleRenderer(lineSymbol);
      lineGraphicOverlay.setRenderer(lineRenderer);
      mapView.getGraphicsOverlays().add(lineGraphicOverlay);

      // solid yellow polygon graphic
      GraphicsOverlay polygonGraphicOverlay = new GraphicsOverlay();
      PolygonBuilder polygonGeometry = new PolygonBuilder(SpatialReferences.getWebMercator());
      polygonGeometry.addPoint(-20e5, 20e5);
      polygonGeometry.addPoint(20e5, 20e5);
      polygonGeometry.addPoint(20e5, -20e5);
      polygonGeometry.addPoint(-20e5, -20e5);
      Graphic polygonGraphic = new Graphic(polygonGeometry.toGeometry());
      polygonGraphicOverlay.getGraphics().add(polygonGraphic);
      SimpleFillSymbol polygonSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, ColorUtil.colorToArgb(Color.YELLOW), null);
      SimpleRenderer polygonRenderer = new SimpleRenderer(polygonSymbol);
      polygonGraphicOverlay.setRenderer(polygonRenderer);
      mapView.getGraphicsOverlays().add(polygonGraphicOverlay);

      // polygon with curve segments (red heart) graphic
      GraphicsOverlay curvedGraphicOverlay = new GraphicsOverlay();
      // create a simple fill symbol with outline
      SimpleLineSymbol curvedLineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, ColorUtil.colorToArgb(Color.BLACK), 1);
      SimpleFillSymbol curvedFillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, ColorUtil.colorToArgb(Color.RED), curvedLineSymbol);
      SimpleRenderer curvedRenderer = new SimpleRenderer(curvedFillSymbol);
      curvedGraphicOverlay.setRenderer(curvedRenderer);
      // create a heart-shaped graphic and add it to the map view
      Point originPointForHeart = new Point(40e5, 5e5, SpatialReferences.getWebMercator());
      Geometry heartGeometry = makeHeartGeometry(originPointForHeart);
      Graphic heartGraphic = new Graphic(heartGeometry);
      curvedGraphicOverlay.getGraphics().add(heartGraphic);
      mapView.getGraphicsOverlays().add(curvedGraphicOverlay);

      // purple ellipse polygon graphic
      GraphicsOverlay ellipseGraphicOverlay = new GraphicsOverlay();
      SimpleFillSymbol ellipseSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, ColorUtil.colorToArgb(Color.PURPLE), null);
      SimpleRenderer ellipseRenderer = new SimpleRenderer(ellipseSymbol);
      ellipseGraphicOverlay.setRenderer(ellipseRenderer);
      Graphic ellipseGraphic = new Graphic(makeEllipse());
      ellipseGraphicOverlay.getGraphics().add(ellipseGraphic);
      mapView.getGraphicsOverlays().add(ellipseGraphicOverlay);

      // add the map view to stack pane
      stackPane.getChildren().add(mapView);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Create a heart-shaped geometry with bezier and elliptic arc segments.
   *
   * @param centerOfHeart the center of the square that contains the heart shape
   * @return a heart-shaped geometry
   */
  private Geometry makeHeartGeometry(Point centerOfHeart) {

    // define the side length of the square that contains the heart shape
    double sideLength = 10e5;
    // define a spatial reference to create segments with
    SpatialReference spatialReference = centerOfHeart.getSpatialReference();
    // define the x and y coordinates to simplify the calculation
    double minX = centerOfHeart.getX() - 0.5 * sideLength;
    double minY = centerOfHeart.getY() - 0.5 * sideLength;
    // define the radius of the arcs
    double arcRadius = sideLength * 0.25;

    // construct the bottom left curve
    Point leftCurveStart = new Point(centerOfHeart.getX(), minY);
    Point leftCurveEnd = new Point(minX, minY + 0.75 * sideLength);
    Point leftControlPoint1 = new Point(centerOfHeart.getX(), minY + 0.25 * sideLength);
    Point leftControlPoint2 = new Point(minX, centerOfHeart.getY());
    CubicBezierSegment leftCurve = new CubicBezierSegment(leftCurveStart, leftControlPoint1, leftControlPoint2, leftCurveEnd, spatialReference);

    // construct the top left arc
    Point leftArcCenter = new Point(minX + 0.25 * sideLength, minY + 0.75 * sideLength);
    EllipticArcSegment leftArc = EllipticArcSegment.createCircularEllipticArc(leftArcCenter, arcRadius, Math.PI, -Math.PI, spatialReference);

    // construct the top right arc
    Point rightArcCenter = new Point(minX + 0.75 * sideLength, minY + 0.75 * sideLength);
    EllipticArcSegment rightArc = EllipticArcSegment.createCircularEllipticArc(rightArcCenter, arcRadius, Math.PI, -Math.PI, spatialReference);

    // construct the bottom right curve
    Point rightCurveStart = new Point(minX + sideLength, minY + 0.75 * sideLength);
    Point rightControlPoint1 = new Point(minX + sideLength, centerOfHeart.getY());
    CubicBezierSegment rightCurve = new CubicBezierSegment(rightCurveStart, rightControlPoint1, leftControlPoint1, leftCurveStart, spatialReference);

    // create a part and add a collection of segments to it to define the heart shape
    Part part = new Part(spatialReference);
    part.addAll(Arrays.asList(leftCurve, leftArc, rightArc, rightCurve));

    // use a polygon builder to construct a heart shape from the parts above
    PolygonBuilder heartShape = new PolygonBuilder(spatialReference);
    heartShape.getParts().add(part);

    // return the geometry of the heart-shaped polygon
    return heartShape.toGeometry();
  }

  /**
   * Create an ellipse shaped polygon.
   *
   * @return ellipse shaped polygon
   */
  private Polygon makeEllipse() {
    // create and set all the parameters so that the ellipse has a major axis of 400 kilometres,
    // a minor axis of 200 kilometres and is rotated at an angle of -45 degrees.
    GeodesicEllipseParameters parameters = new GeodesicEllipseParameters();

    parameters.setCenter(new Point(40e5,25e5, SpatialReferences.getWebMercator()));
    parameters.setGeometryType(GeometryType.POLYGON);
    parameters.setSemiAxis1Length(200);
    parameters.setSemiAxis2Length(400);
    parameters.setAxisDirection(-45);
    parameters.setMaxPointCount(100);
    parameters.setAngularUnit(new AngularUnit(AngularUnitId.DEGREES));
    parameters.setLinearUnit(new LinearUnit(LinearUnitId.KILOMETERS));
    parameters.setMaxSegmentLength(20);

    return (Polygon) GeometryEngine.ellipseGeodesic(parameters);
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
