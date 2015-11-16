/*
 * Copyright 2015 Esri.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.esri.sampleviewer.samples.graphicsoverlay;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.Polygon;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.BasemapType;
import com.esri.arcgisruntime.mapping.Map;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.RgbColor;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.TextSymbol;
import com.esri.arcgisruntime.symbology.TextSymbol.HorizontalAlignment;
import com.esri.arcgisruntime.symbology.TextSymbol.VerticalAlignment;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * This sample demonstrates how to add Points, Polylines, Polygons, and text as
 * Graphics to a GraphicsOverlay using symbols.
 * <h4>How it Works</h4>
 * 
 * Points are created by adding each of them to a {@link Graphic} along with a
 * {@link SimpleMarkerSymbol}. This Graphic is then added to the
 * {@link GraphicsOverlay}. Polylines, Polygons, and text are created in the
 * same fashion by using a {@link SimpleLineSymbol}, {@link SimpleFillSymbol},
 * {@link TextSymbol} respectively.
 */
public class AddGraphicsWithSymbols extends Application {

  private MapView mapView;

  private static final SpatialReference SPATIAL_REFERENECE =
      SpatialReferences.getWgs84();

  @Override
  public void start(Stage stage) throws Exception {

    // create stack pane and application scene
    StackPane stackPane = new StackPane();
    Scene scene = new Scene(stackPane);
    scene.getStylesheets().add(getClass()
        .getResource("../resources/SamplesTheme.css").toExternalForm());

    // set title, size, and add scene to stage
    stage.setTitle("Add Graphics with Symbols Sample");
    stage.setWidth(700);
    stage.setHeight(800);
    stage.setScene(scene);
    stage.show();

    // create a control panel
    VBox vBoxControl = new VBox(6);
    vBoxControl.setMaxSize(240, 170);
    vBoxControl.getStyleClass().add("panel-region");

    // create sample description
    Label descriptionLabel = new Label("Sample Description:");
    descriptionLabel.getStyleClass().add("panel-label");
    TextArea description = new TextArea(
        "This sample shows how add points, polylines, polygons, and text as "
            + "Graphics to a Graphics Overlay using symbols.");
    description.setWrapText(true);
    description.autosize();
    description.setEditable(false);

    // add labels, sample description and lists to the control panel
    vBoxControl.getChildren().addAll(descriptionLabel, description);
    try {

      // create the graphics overlay
      final GraphicsOverlay graphicsOverlay = new GraphicsOverlay();

      // create a map with a topographic basemap
      final Map map = new Map(BasemapType.OCEANS, 56.075844, -2.681572, 13);

      // create a view for the map and set the map to it
      mapView = new MapView();
      mapView.setMap(map);

      // add the graphic overlay to the map view
      mapView.getGraphicsOverlays().add(graphicsOverlay);

      // add some red circled points to the graphics overlay
      createPoints(graphicsOverlay);

      // add the purple polyline to the graphics overlay
      createPolyline(graphicsOverlay);

      // add the green polygon mesh to the graphics overlay
      createPolygon(graphicsOverlay);

      // add the blue text symbols to the graphics overlay
      createText(graphicsOverlay);

      // add the map view and control box to stack pane
      stackPane.getChildren().addAll(mapView, vBoxControl);
      StackPane.setAlignment(vBoxControl, Pos.TOP_LEFT);
      StackPane.setMargin(vBoxControl, new Insets(10, 0, 0, 10));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Creates four Points using a SimpleMarkerSymbol and adds them to a
   * GraphicsOverlay.
   * 
   * @param GraphicsOverlay holds Graphics information on Map
   */
  private void createPoints(GraphicsOverlay graphicsOverlay) {

    // create a red circle simple marker symbol
    SimpleMarkerSymbol redCircleSymbol = new SimpleMarkerSymbol(
        new RgbColor(255, 0, 0, 255), 10, SimpleMarkerSymbol.Style.CIRCLE);

    // create graphics and add to graphics overlay
    Graphic graphic;
    graphic = new Graphic(new Point(-2.72, 56.065, SPATIAL_REFERENECE),
        redCircleSymbol);
    graphicsOverlay.getGraphics().add(graphic);

    graphic = new Graphic(new Point(-2.69, 56.065, SPATIAL_REFERENECE),
        redCircleSymbol);
    graphicsOverlay.getGraphics().add(graphic);

    graphic = new Graphic(new Point(-2.66, 56.065, SPATIAL_REFERENECE),
        redCircleSymbol);
    graphicsOverlay.getGraphics().add(graphic);

    graphic = new Graphic(new Point(-2.63, 56.065, SPATIAL_REFERENECE),
        redCircleSymbol);
    graphicsOverlay.getGraphics().add(graphic);
  }

  /**
   * Creates a Polyline and adds it to a GraphicsOverlay.
   * 
   * @param GraphicsOverlay holds Graphics information on Map
   */
  private void createPolyline(GraphicsOverlay graphicsOverlay) {

    // create a purple simple line symbol
    SimpleLineSymbol lineSymbol = new SimpleLineSymbol(
        SimpleLineSymbol.Style.DASH, new RgbColor(128, 0, 128, 255), 4, 1.0f);

    // create a new point collection for polyline
    PointCollection points = new PointCollection(SPATIAL_REFERENECE);

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

    // create the graphic with polyline and symbol
    Graphic graphic = new Graphic(polyline, lineSymbol);

    // add graphic to the graphics overlay
    graphicsOverlay.getGraphics().add(graphic);
  }

  /**
   * Creates a Polygon and adds it to a GraphicsOverlay.
   * 
   * @param GraphicsOverlay holds Graphics information on Map
   */
  private void createPolygon(GraphicsOverlay graphicsOverlay) {

    // create a green color
    RgbColor green = new RgbColor(0, 80, 0, 255);

    // create a green simple line symbol
    SimpleLineSymbol outlineSymbol =
        new SimpleLineSymbol(SimpleLineSymbol.Style.DASH, green, 1, 1.0f);
    // create a green mesh simple fill symbol
    SimpleFillSymbol fillSymbol = new SimpleFillSymbol(green,
        SimpleFillSymbol.Style.DIAGONAL_CROSS, outlineSymbol, 1.0f);

    // create a new point collection for polygon
    PointCollection points = new PointCollection(SPATIAL_REFERENECE);

    // create and add points to the point collection
    points.add(new Point(-2.6425, 56.0784));
    points.add(new Point(-2.6430, 56.0763));
    points.add(new Point(-2.6410, 56.0759));
    points.add(new Point(-2.6380, 56.0765));
    points.add(new Point(-2.6380, 56.0784));
    points.add(new Point(-2.6410, 56.0786));

    // create the polyline from the point collection
    Polygon polygon = new Polygon(points);

    // create the graphic with polyline and symbol
    Graphic graphic = new Graphic(polygon, fillSymbol);

    // add graphic to the graphics overlay
    graphicsOverlay.getGraphics().add(graphic);
  }

  /**
   * Creates two TextSymbols and adds them to a GraphicsOverlay.
   * 
   * @param GraphicsOverlay holds Graphics information on Map
   */
  private void createText(GraphicsOverlay graphicsOverlay) {

    // create blue color
    RgbColor blue = new RgbColor(0, 0, 230, 255);

    // create two text symbols
    TextSymbol bassRockTextSymbol = new TextSymbol(10, "Bass Rock", blue,
        HorizontalAlignment.LEFT, VerticalAlignment.BOTTOM);

    TextSymbol craigleithTextSymbol = new TextSymbol(10, "Craigleith", blue,
        HorizontalAlignment.RIGHT, VerticalAlignment.TOP);

    // create two points
    Point bassPoint = new Point(-2.64, 56.079, SPATIAL_REFERENECE);
    Point craigleithPoint = new Point(-2.72, 56.076, SPATIAL_REFERENECE);

    // create two graphics from the points and symbols
    Graphic bassRockGraphic = new Graphic(bassPoint, bassRockTextSymbol);
    Graphic craigleithGraphic =
        new Graphic(craigleithPoint, craigleithTextSymbol);

    // add graphics to the graphics overlay
    graphicsOverlay.getGraphics().add(bassRockGraphic);
    graphicsOverlay.getGraphics().add(craigleithGraphic);
  }

  /**
   * Stops and releases all resources used in application.
   *
   * @throws Exception if security manager doesn't allow JVM to exit with
   *           current status
   */
  @Override
  public void stop() throws Exception {

    if (mapView != null) {
      mapView.dispose();
    }
    Platform.exit();
    System.exit(0);
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
