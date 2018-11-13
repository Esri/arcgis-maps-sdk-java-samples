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

package com.esri.samples.geometry.convex_hull_list;

import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.Polygon;
import com.esri.arcgisruntime.geometry.PolygonBuilder;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;
import com.sun.xml.internal.bind.v2.TODO;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.scene.control.Button;

import java.util.ArrayList;
import java.util.List;


public class ConvexHullListSample extends Application {

  private MapView mapView;

  @Override
  public void start(Stage stage) {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);

      // set title, size, and add scene to stage
      stage.setTitle("Convex Hull List Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // create a map with a basemap and add it to the map view
      ArcGISMap map = new ArcGISMap(Basemap.createTopographic());
      mapView = new MapView();
      mapView.setMap(map);

      // graphics overlay
      GraphicsOverlay graphicsOverlay = new GraphicsOverlay();
      mapView.getGraphicsOverlays().add(graphicsOverlay);

      // create a simple line symbol for the outline of the two input polygon graphics
      SimpleLineSymbol polygonOutline = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0xFF0000FF, 3);
      // create a simple fill symbol for the two input polygon graphics
      SimpleFillSymbol polygonFill = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, 0x300000FF, polygonOutline);

      // TODO: ask about bneefits of Polygon Buildr ovr Polygon.

      PointCollection firstPolygonPointCollection = new PointCollection(SpatialReferences.getWebMercator());
      firstPolygonPointCollection.add(new Point(-4983189.15470412, 8679428.55774286));
      firstPolygonPointCollection.add(new Point(-5222621.66664186, 5147799.00666126));
      firstPolygonPointCollection.add(new Point(-13483043.3284937, 4728792.11077023));
      firstPolygonPointCollection.add(new Point(-13273539.8805482, 2244679.79941622));
      firstPolygonPointCollection.add(new Point(-5372266.98660294, 2035176.3514707));
      firstPolygonPointCollection.add(new Point(-5432125.11458738, -4100281.76693377));
      firstPolygonPointCollection.add(new Point(-2469147.7793579, -4160139.89491821));
      firstPolygonPointCollection.add(new Point(-1900495.56350578, 2035176.3514707));
      firstPolygonPointCollection.add(new Point(2768438.41928007, 1975318.22348627));
      firstPolygonPointCollection.add(new Point(2409289.65137346, 5477018.71057565));
      firstPolygonPointCollection.add(new Point(-2409289.65137346, 5387231.518599));
      firstPolygonPointCollection.add(new Point(-2469147.7793579, 8709357.62173508));
      Polygon firstPolygon = new Polygon(firstPolygonPointCollection);
      Graphic firstPolygonGraphic = new Graphic (firstPolygon, polygonFill);
      firstPolygonGraphic.setZIndex(1);
      graphicsOverlay.getGraphics().add(firstPolygonGraphic);

      PointCollection secondPolygonPointCollection = new PointCollection(SpatialReferences.getWebMercator());
      secondPolygonPointCollection.add(new Point(5993520.19456882, -1063938.49607736));
      secondPolygonPointCollection.add(new Point(3085421.63862418, -1383120.04490055));
      secondPolygonPointCollection.add(new Point(3794713.96934239, -2979027.78901651));
      secondPolygonPointCollection.add(new Point(6880135.60796657, -4078430.90162972));
      secondPolygonPointCollection.add(new Point(7092923.30718203, -2837169.32287287));
      secondPolygonPointCollection.add(new Point(8617901.81822617, -2092412.37561875));
      secondPolygonPointCollection.add(new Point(6986529.4575743, 354646.16535905));
      secondPolygonPointCollection.add(new Point(5319692.48038653, 1205796.96222089));
      Polygon secondPolygon = new Polygon(secondPolygonPointCollection);
      Graphic secondPolygonGraphic = new Graphic(secondPolygon, polygonFill);
      secondPolygonGraphic.setZIndex(1);
      graphicsOverlay.getGraphics().add(secondPolygonGraphic);

//      // create the first polygon graphic, and add to the map view
//      GraphicsOverlay firstPolygonGraphicOverlay = new GraphicsOverlay();
//      PolygonBuilder firstPolygonGeometry = new PolygonBuilder(SpatialReferences.getWebMercator());
//      firstPolygonGeometry.addPoint(-4983189.15470412, 8679428.55774286);
//      firstPolygonGeometry.addPoint(-5222621.66664186, 5147799.00666126);
//      firstPolygonGeometry.addPoint(-13483043.3284937, 4728792.11077023);
//      firstPolygonGeometry.addPoint(-13273539.8805482, 2244679.79941622);
//      firstPolygonGeometry.addPoint(-5372266.98660294, 2035176.3514707);
//      firstPolygonGeometry.addPoint(-5432125.11458738, -4100281.76693377);
//      firstPolygonGeometry.addPoint(-2469147.7793579, -4160139.89491821);
//      firstPolygonGeometry.addPoint(-1900495.56350578, 2035176.3514707);
//      firstPolygonGeometry.addPoint(2768438.41928007, 1975318.22348627);
//      firstPolygonGeometry.addPoint(2409289.65137346, 5477018.71057565);
//      firstPolygonGeometry.addPoint(-2409289.65137346, 5387231.518599);
//      firstPolygonGeometry.addPoint(-2469147.7793579, 8709357.62173508);
//      Graphic firstPolygonGraphic = new Graphic(firstPolygonGeometry.toGeometry());
//      // set the Z index for the first polygon graphic so that it appears above the convex hull graphic added later
//      firstPolygonGraphic.setZIndex(1);
//      firstPolygonGraphicOverlay.getGraphics().add(firstPolygonGraphic);


//      // render the first polygon
//      SimpleRenderer firstPolygonRenderer = new SimpleRenderer(polygonFill);
//      firstPolygonGraphicOverlay.setRenderer(firstPolygonRenderer);
//      // add first polygon to the map view
//      mapView.getGraphicsOverlays().add(firstPolygonGraphicOverlay);

      // create the second polygon graphic, and add to the map
//      GraphicsOverlay secondPolygonGraphicOverlay = new GraphicsOverlay();
//      PolygonBuilder secondPolygonGeometry = new PolygonBuilder(SpatialReferences.getWebMercator());
//      secondPolygonGeometry.addPoint(5993520.19456882, -1063938.49607736);
//      secondPolygonGeometry.addPoint(3085421.63862418, -1383120.04490055);
//      secondPolygonGeometry.addPoint(3794713.96934239, -2979027.78901651);
//      secondPolygonGeometry.addPoint(6880135.60796657, -4078430.90162972);
//      secondPolygonGeometry.addPoint(7092923.30718203, -2837169.32287287);
//      secondPolygonGeometry.addPoint(8617901.81822617, -2092412.37561875);
//      secondPolygonGeometry.addPoint(6986529.4575743, 354646.16535905);
//      secondPolygonGeometry.addPoint(5319692.48038653, 1205796.96222089);
//      Graphic secondPolygonGraphic = new Graphic(secondPolygonGeometry.toGeometry());
//      // set the Z index for the second polygon graphic so that it appears above the convex hull graphic added later
//      secondPolygonGraphic.setZIndex(1);
//      secondPolygonGraphicOverlay.getGraphics().add(secondPolygonGraphic);
//
//      // render the second polygon
//      SimpleRenderer secondPolygonRenderer = new SimpleRenderer(polygonFill);
//      secondPolygonGraphicOverlay.setRenderer(secondPolygonRenderer);
//      mapView.getGraphicsOverlays().add(secondPolygonGraphicOverlay);

      // create a graphics overlay for displaying convex hull polygon
      GraphicsOverlay convexHullGraphicsOverlay = new GraphicsOverlay();
      mapView.getGraphicsOverlays().add(convexHullGraphicsOverlay);

      // create a graphic to show the convex hull as a red outline
//      Graphic convexHullGraphic = new Graphic();
//      convexHullGraphic.setSymbol(convexHullFill);
//      graphicsOverlay.getGraphics().add(convexHullGraphic);
      
      // create a button to create and show the convex hull
      Button convexHullButton = new Button("Create Convex Hull");

      // create a button to clear the convex hull
      Button clearButton = new Button("Clear");

      // create a check box for unioning the result TODO: change grammar
      CheckBox checkBox = new CheckBox("Union");


      convexHullButton.setOnAction(e -> {

        // TODO: isn't this a little hacky? Only way I can think of to not have bug where the original hulls remain in place.
        // TODO: add another graphics overlay (clear results graphic)
        graphicsOverlay.getGraphics().clear();
        graphicsOverlay.getGraphics().add(firstPolygonGraphic);
        graphicsOverlay.getGraphics().add(secondPolygonGraphic);

        Boolean unionBool = checkBox.isSelected();

        // add graphics to a geometry list TODO: should this be a new array list?
        List<Geometry> allPolygonGeometries = new ArrayList<>();
        allPolygonGeometries.add(secondPolygonGraphic.getGeometry());
        allPolygonGeometries.add(firstPolygonGraphic.getGeometry());

        List<Geometry> convexHullGeometries = GeometryEngine.convexHull(allPolygonGeometries, unionBool);

        for (Geometry geometry: convexHullGeometries) {

          SimpleLineSymbol convexHullLine = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0xFFFF0000, 5);
          SimpleFillSymbol convexHullFill = new SimpleFillSymbol(SimpleFillSymbol.Style.NULL, 0x00000000, convexHullLine);

          Graphic convexHullGraphicNew = new Graphic(geometry, convexHullFill);
          convexHullGraphicNew.setZIndex(0);

          graphicsOverlay.getGraphics().add(convexHullGraphicNew);

        }

        clearButton.setDisable(false);
        checkBox.setSelected(false);
      });


      clearButton.setDisable(true);
      clearButton.setOnAction(e -> {
        graphicsOverlay.getGraphics().clear();
        graphicsOverlay.getGraphics().add(firstPolygonGraphic);
        graphicsOverlay.getGraphics().add(secondPolygonGraphic);
        clearButton.setDisable(true);
        convexHullButton.setDisable(false);
        checkBox.setSelected(false);
      });


      // create label
      // TODO: make all this text visible
      Label informationLabel = new Label("Click the 'ConvexHull' button to create convex hull(s) from the polygon\n" +
              "                           graphics. If the 'Union' checkbox is checked, the resulting output will\n" +
              "                           be one polygon being the convex hull for the two input polygons. If the\n" +
              "                           'Union' checkbox is un-checked, the resulting output will have two convex\n" +
              "                           hull polygons - one for each of the two input polygons.");
      informationLabel.setWrapText(true); // this isn't wrapping the text.
      informationLabel.setTextAlignment(TextAlignment.JUSTIFY);
      informationLabel.setPadding(new Insets(10));
      informationLabel.getStyleClass().add("panel-label");


      // create a control panel
      VBox controlsVBox = new VBox(6);
      controlsVBox.setBackground(new Background(new BackgroundFill(Paint.valueOf("rgba(0, 0, 0, 0.3)"),
              CornerRadii.EMPTY, Insets.EMPTY)));
      controlsVBox.setPadding(new Insets(10));
      controlsVBox.setMaxSize(260, 110);
      controlsVBox.getStyleClass().add("panel-region");
      controlsVBox.getChildren().addAll(informationLabel, convexHullButton, clearButton, checkBox);

      // add the map view to the stack pane
      stackPane.getChildren().addAll(mapView, controlsVBox);
      stackPane.setAlignment(controlsVBox, Pos.TOP_RIGHT);
      stackPane.setMargin(controlsVBox, new Insets(10, 10, 0, 10));


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
