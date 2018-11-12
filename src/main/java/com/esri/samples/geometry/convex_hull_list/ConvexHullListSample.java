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

      // create a simple line symbol for the outline of the two input polygon graphics
      SimpleLineSymbol polygonOutline = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0xFF0000FF, 3);
      // create a simple fill symbol for the two input polygon graphics
      SimpleFillSymbol polygonFill = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, 0x300000FF, polygonOutline);

      // create the first polygon graphic, and add to the map view
      GraphicsOverlay firstPolygonGraphicOverlay = new GraphicsOverlay();
      PolygonBuilder firstPolygonGeometry = new PolygonBuilder(SpatialReferences.getWebMercator());
      firstPolygonGeometry.addPoint(-4983189.15470412, 8679428.55774286);
      firstPolygonGeometry.addPoint(-5222621.66664186, 5147799.00666126);
      firstPolygonGeometry.addPoint(-13483043.3284937, 4728792.11077023);
      firstPolygonGeometry.addPoint(-13273539.8805482, 2244679.79941622);
      firstPolygonGeometry.addPoint(-5372266.98660294, 2035176.3514707);
      firstPolygonGeometry.addPoint(-5432125.11458738, -4100281.76693377);
      firstPolygonGeometry.addPoint(-2469147.7793579, -4160139.89491821);
      firstPolygonGeometry.addPoint(-1900495.56350578, 2035176.3514707);
      firstPolygonGeometry.addPoint(2768438.41928007, 1975318.22348627);
      firstPolygonGeometry.addPoint(2409289.65137346, 5477018.71057565);
      firstPolygonGeometry.addPoint(-2409289.65137346, 5387231.518599);
      firstPolygonGeometry.addPoint(-2469147.7793579, 8709357.62173508);
      Graphic firstPolygonGraphic = new Graphic(firstPolygonGeometry.toGeometry());
      // set the Z index for the first polygon graphic so that it appears above the convex hull graphic added later
      firstPolygonGraphic.setZIndex(1);
      firstPolygonGraphicOverlay.getGraphics().add(firstPolygonGraphic);

      System.out.println(firstPolygonGraphicOverlay.getGraphics().size());

      // render the first polygon
      SimpleRenderer firstPolygonRenderer = new SimpleRenderer(polygonFill);
      firstPolygonGraphicOverlay.setRenderer(firstPolygonRenderer);
      // add first polygon to the map view
      mapView.getGraphicsOverlays().add(firstPolygonGraphicOverlay);

      // create the second polygon graphic, and add to the map
      GraphicsOverlay secondPolygonGraphicOverlay = new GraphicsOverlay();
      PolygonBuilder secondPolygonGeometry = new PolygonBuilder(SpatialReferences.getWebMercator());
      secondPolygonGeometry.addPoint(5993520.19456882, -1063938.49607736);
      secondPolygonGeometry.addPoint(3085421.63862418, -1383120.04490055);
      secondPolygonGeometry.addPoint(3794713.96934239, -2979027.78901651);
      secondPolygonGeometry.addPoint(6880135.60796657, -4078430.90162972);
      secondPolygonGeometry.addPoint(7092923.30718203, -2837169.32287287);
      secondPolygonGeometry.addPoint(8617901.81822617, -2092412.37561875);
      secondPolygonGeometry.addPoint(6986529.4575743, 354646.16535905);
      secondPolygonGeometry.addPoint(5319692.48038653, 1205796.96222089);
      Graphic secondPolygonGraphic = new Graphic(secondPolygonGeometry.toGeometry());
      // set the Z index for the second polygon graphic so that it appears above the convex hull graphic added later
      secondPolygonGraphic.setZIndex(1);
      secondPolygonGraphicOverlay.getGraphics().add(secondPolygonGraphic);

      // render the second polygon
      SimpleRenderer secondPolygonRenderer = new SimpleRenderer(polygonFill);
      secondPolygonGraphicOverlay.setRenderer(secondPolygonRenderer);
      mapView.getGraphicsOverlays().add(secondPolygonGraphicOverlay);

      // create a graphics overlay for displaying convex hull polygon
      GraphicsOverlay convexHullGraphicsOverlay = new GraphicsOverlay();
      mapView.getGraphicsOverlays().add(convexHullGraphicsOverlay);

      // create a graphic to show the convex hull as a red outline
      SimpleLineSymbol convexHullLine = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0xFFFF0000, 5 );
      SimpleFillSymbol convexHullFill = new SimpleFillSymbol(SimpleFillSymbol.Style.NULL, 0x00000000, convexHullLine);
      Graphic convexHullGraphic = new Graphic();
      convexHullGraphic.setSymbol(convexHullFill);
      convexHullGraphicsOverlay.getGraphics().add(convexHullGraphic);

      // testing adding in nw graphics to see if it rendrs ok.
      GraphicsOverlay testGraphicsOverlay = new GraphicsOverlay();
      mapView.getGraphicsOverlays().add(testGraphicsOverlay);

      Graphic testGraphic = new Graphic();
      testGraphic.setSymbol(convexHullFill);
      testGraphicsOverlay.getGraphics().add(testGraphic);

      // add graphics to a geometry list
      List<Geometry> allPolygonGeometries = new ArrayList<>();
      allPolygonGeometries.add(secondPolygonGraphic.getGeometry());
      allPolygonGeometries.add(firstPolygonGraphic.getGeometry());


      // create a button to create and show the convex hull
      Button convexHullButton = new Button("Create Convex Hull");

      // create a button to clear the convex hull
      Button clearButton = new Button("Clear");

      // create a check box for unioning the result TODO: change grammar
      CheckBox checkBox = new CheckBox("Union");

      clearButton.setDisable(true);
      clearButton.setOnAction(e -> {
        convexHullGraphic.setGeometry(null);
        testGraphic.setGeometry(null);
        clearButton.setDisable(true);
        convexHullButton.setDisable(false);
        checkBox.setSelected(false);
      });

      convexHullButton.setOnAction(e -> {

        Geometry firstGeometry = GeometryEngine.convexHull(firstPolygonGraphic.getGeometry());
        convexHullGraphic.setGeometry(firstGeometry);
        Geometry secondGeometry = GeometryEngine.convexHull(secondPolygonGeometry.toGeometry());
        testGraphic.setGeometry(secondGeometry);

        List<Geometry> convexHullGeometries = GeometryEngine.convexHull(allPolygonGeometries, checkBox.isSelected());
        convexHullGeometries

        for (Geometry geometry: convexHullGeometries) {
          convexHullGraphic.setGeometry(geometry);
        }
        clearButton.setDisable(false);
      });


      // create label
      Label informationLabel = new Label("Click the button to do the thing");
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
