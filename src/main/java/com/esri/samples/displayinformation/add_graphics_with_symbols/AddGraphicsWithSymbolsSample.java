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

package com.esri.samples.displayinformation.add_graphics_with_symbols;

import java.util.Arrays;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.Polygon;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.TextSymbol;
import com.esri.arcgisruntime.symbology.TextSymbol.HorizontalAlignment;
import com.esri.arcgisruntime.symbology.TextSymbol.VerticalAlignment;

public class AddGraphicsWithSymbolsSample extends Application {

  private MapView mapView;
  private GraphicsOverlay graphicsOverlay;

  private static final SpatialReference SPATIAL_REFERENCE = SpatialReferences.getWgs84();

  @Override
  public void start(Stage stage) throws Exception {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);

      // set title, size, and add scene to stage
      stage.setTitle("Add Graphics with Symbols Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // create the graphics overlay
      graphicsOverlay = new GraphicsOverlay();

      // create a ArcGISMap with a topographic basemap
      final ArcGISMap map = new ArcGISMap(Basemap.Type.OCEANS, 56.075844, -2.681572, 13);

      // create a view for the ArcGISMap and set the ArcGISMap to it
      mapView = new MapView();
      mapView.setMap(map);

      // add the graphic overlay to the map view
      mapView.getGraphicsOverlays().add(graphicsOverlay);

      // add some red circled points to the graphics overlay
      createPoints();

      // add the purple polyline to the graphics overlay
      createPolyline();

      // add the green polygon mesh to the graphics overlay
      createPolygon();

      // add the blue text symbols to the graphics overlay
      createText();

      // add the map view to stack pane
      stackPane.getChildren().add(mapView);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Creates four Points using a SimpleMarkerSymbol and adds them to a
   * GraphicsOverlay.
   */
  private void createPoints() {

    //[DocRef: Name=Display_Information-Graphics-Marker_Symbol-Java
    // create a red (0xFFFF0000) circle simple marker symbol
    SimpleMarkerSymbol redCircleSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, 0xFFFF0000, 10);
    //[DocRef: Name=Display_Information-Graphics-Marker_Symbol-Java

    //[DocRef: Name=Display_Information-Graphics-Point
    Point buoy1Loc = new Point(-2.72, 56.065, SPATIAL_REFERENCE);
    Point buoy2Loc = new Point(-2.69, 56.065, SPATIAL_REFERENCE);
    Point buoy3Loc = new Point(-2.66, 56.065, SPATIAL_REFERENCE);
    Point buoy4Loc = new Point(-2.63, 56.065, SPATIAL_REFERENCE);
    //[DocRef: Name=Display_Information-Graphics-Point

    //[DocRef: Name=Display_Information-Graphics-Graphics
    // create graphics and add to graphics overlay
    Graphic buoyGraphic1 = new Graphic(buoy1Loc, redCircleSymbol);
    Graphic buoyGraphic2 = new Graphic(buoy2Loc, redCircleSymbol);
    Graphic buoyGraphic3 = new Graphic(buoy3Loc, redCircleSymbol);
    Graphic buoyGraphic4 = new Graphic(buoy4Loc, redCircleSymbol);
    //[DocRef: Name=Display_Information-Graphics-Graphics

    //[DocRef: Name=Display_Information-Graphics-Add_Graphics
    graphicsOverlay.getGraphics().addAll(Arrays.asList(buoyGraphic1, buoyGraphic2, buoyGraphic3, buoyGraphic4));
    //[DocRef: Name=Display_Information-Graphics-Add_Graphics
  }

  /**
   * Creates a Polyline and adds it to a GraphicsOverlay.
   */
  private void createPolyline() {

    //[DocRef: Name=Display_Information-Graphics-Line_Symbol-Java
    // create a purple (0xFF800080) simple line symbol
    SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.DASH, 0xFF800080, 4);
    //[DocRef: Name=Display_Information-Graphics-Line_Symbol-Java

    //[DocRef: Name=Display_Information-Graphics-Polyline
    // create a new point collection for polyline
    PointCollection points = new PointCollection(SPATIAL_REFERENCE);

    // create and add points to the point collection
    points.add(new Point(-2.715, 56.061));
    points.add(new Point(-2.6438, 56.079));
    points.add(new Point(-2.638, 56.079));
    points.add(new Point(-2.636, 56.078));
    points.add(new Point(-2.636, 56.077));
    points.add(new Point(-2.637, 56.076));
    points.add(new Point(-2.715, 56.061));

    // create the polyline from the point collection
    Polyline polyline = new Polyline(points);
    //[DocRef: Name=Display_Information-Graphics-Polyline

    //[DocRef: Name=Display_Information-Graphics-Graphic_Polyline
    // create the graphic with polyline and symbol
    Graphic graphic = new Graphic(polyline, lineSymbol);
    //[DocRef: Name=Display_Information-Graphics-Graphic_Polyline

    //[DocRef: Name=Display_Information-Graphics-GraphicsOverlay_Polyline
    // add graphic to the graphics overlay
    graphicsOverlay.getGraphics().add(graphic);
    //[DocRef: Name=Display_Information-Graphics-GraphicsOverlay_Polyline
  }

  /**
   * Creates a Polygon and adds it to a GraphicsOverlay.
   */
  private void createPolygon() {

    //[DocRef: Name=Display_Information-Graphics-Fill_Symbol-Java
    // create a green (0xFF005000) simple line symbol
    SimpleLineSymbol outlineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.DASH, 0xFF005000, 1);
    // create a green (0xFF005000) mesh simple fill symbol
    SimpleFillSymbol fillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.DIAGONAL_CROSS, 0xFF005000,
        outlineSymbol);
    //[DocRef: Name=Display_Information-Graphics-Fill_Symbol-Java

    //[DocRef: Name=Display_Information-Graphics-Polygon
    // create a new point collection for polygon
    PointCollection points = new PointCollection(SPATIAL_REFERENCE);

    // create and add points to the point collection
    points.add(new Point(-2.6425, 56.0784));
    points.add(new Point(-2.6430, 56.0763));
    points.add(new Point(-2.6410, 56.0759));
    points.add(new Point(-2.6380, 56.0765));
    points.add(new Point(-2.6380, 56.0784));
    points.add(new Point(-2.6410, 56.0786));

    // create the polyline from the point collection
    Polygon polygon = new Polygon(points);
    //[DocRef: Name=Display_Information-Graphics-Polygon

    //[DocRef: Name=Display_Information-Graphics-Polygon
    // create the graphic with polyline and symbol
    Graphic graphic = new Graphic(polygon, fillSymbol);
    //[DocRef: Name=Display_Information-Graphics-Polygon

    //[DocRef: Name=Display_Information-Graphics-GraphicsOverlay_Polygon
    // add graphic to the graphics overlay
    graphicsOverlay.getGraphics().add(graphic);
    //[DocRef: Name=Display_Information-Graphics-GraphicsOverlay_Polygon
  }

  /**
   * Creates two TextSymbols and adds them to a GraphicsOverlay.
   */
  private void createText() {

    //[DocRef: Name=Display_Information-Graphics-Text_Symbol-Java
    final int BLUE = 0xFF0000E6;
    // create two text symbols
    TextSymbol bassRockTextSymbol = new TextSymbol(10, "Bass Rock", BLUE, HorizontalAlignment.LEFT,
        VerticalAlignment.BOTTOM);
    TextSymbol craigleithTextSymbol = new TextSymbol(10, "Craigleith", BLUE, HorizontalAlignment.RIGHT,
        VerticalAlignment.TOP);
    //[DocRef: Name=Display_Information-Graphics-Text_Symbol-Java

    //[DocRef: Name=Display_Information-Graphics-Text_Point
    // create two points
    Point bassPoint = new Point(-2.64, 56.079, SPATIAL_REFERENCE);
    Point craigleithPoint = new Point(-2.72, 56.076, SPATIAL_REFERENCE);
    //[DocRef: Name=Display_Information-Graphics-Text_Point

    //[DocRef: Name=Display_Information-Graphics-Text_Graphic
    // create two graphics from the points and symbols
    Graphic bassRockGraphic = new Graphic(bassPoint, bassRockTextSymbol);
    Graphic craigleithGraphic = new Graphic(craigleithPoint, craigleithTextSymbol);
    //[DocRef: Name=Display_Information-Graphics-Text_Graphic

    //[DocRef: Name=Display_Information-Graphics-Text_GraphicsOverlay
    // add graphics to the graphics overlay
    graphicsOverlay.getGraphics().add(bassRockGraphic);
    graphicsOverlay.getGraphics().add(craigleithGraphic);
    //[DocRef: Name=Display_Information-Graphics-Text_GraphicsOverlay
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
