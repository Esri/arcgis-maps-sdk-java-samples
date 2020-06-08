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

package com.esri.samples.convex_hull;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Multipoint;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;

public class ConvexHullSample extends Application {

  private MapView mapView;

  @Override
  public void start(Stage stage) {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);

      // set title, size, and add scene to stage
      stage.setTitle("Convex Hull Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // create a map with a basemap and add it to the map view
      ArcGISMap map = new ArcGISMap(Basemap.createTopographic());
      mapView = new MapView();
      mapView.setMap(map);

      // create a graphics overlay to show the input points and convex hull
      GraphicsOverlay graphicsOverlay = new GraphicsOverlay();
      mapView.getGraphicsOverlays().add(graphicsOverlay);

      // create a graphic to show the points
      SimpleMarkerSymbol markerSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, 0xFFFF0000, 10);
      Graphic inputsGraphic = new Graphic();
      inputsGraphic.setSymbol(markerSymbol);
      graphicsOverlay.getGraphics().add(inputsGraphic);

      // create a graphic to show the convex hull as a blue outline
      SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0xFF0000FF, 3);
      SimpleFillSymbol fillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.NULL, 0x00000000, lineSymbol);
      Graphic convexHullGraphic = new Graphic();
      graphicsOverlay.getGraphics().add(convexHullGraphic);

      // keep track of the points added by the user
      List<Point> inputs = new ArrayList<>();

      // create a button to create and show the convex hull
      Button convexHullButton = new Button("Create Convex Hull");
      convexHullButton.setMaxWidth(130);
      convexHullButton.setDisable(true);
      convexHullButton.setOnAction(e -> {
        convexHullButton.setDisable(true);
        // change the symbol depending on the returned geometry type
        Geometry convexHull = GeometryEngine.convexHull(inputsGraphic.getGeometry());
        switch (convexHull.getGeometryType()) {
          case POINT:
            convexHullGraphic.setSymbol(markerSymbol);
            break;
          case POLYLINE:
            convexHullGraphic.setSymbol(lineSymbol);
            break;
          case POLYGON:
            convexHullGraphic.setSymbol(fillSymbol);
            break;
        }
        convexHullGraphic.setGeometry(convexHull);
      });

      // create a button to clear all graphics
      Button clearButton = new Button("Clear");
      clearButton.setMaxWidth(130);
      clearButton.setDisable(true);
      clearButton.setOnAction(e -> {
        inputs.clear();
        inputsGraphic.setGeometry(null);
        convexHullGraphic.setGeometry(null);
        convexHullButton.setDisable(true);
        clearButton.setDisable(true);
      });

      VBox vBox = new VBox(6);
      vBox.setPickOnBounds(false);
      vBox.setBackground(new Background(new BackgroundFill(Paint.valueOf("rgba(0,0,0,0.3)"), CornerRadii.EMPTY, Insets.EMPTY)));
      vBox.setPadding(new Insets(10.0));
      vBox.setMaxSize(150, 50);
      vBox.getStyleClass().add("panel-region");
      vBox.getChildren().addAll(convexHullButton, clearButton);

      // add a point where the user clicks on the map
      mapView.setOnMouseClicked(e -> {
        if (e.isStillSincePress() && e.getButton() == MouseButton.PRIMARY) {
          Point2D point2D = new Point2D(e.getX(), e.getY());
          Point point = mapView.screenToLocation(point2D);
          inputs.add(point);
          // update the inputs graphic geometry
          Multipoint inputsGeometry = new Multipoint(new PointCollection(inputs));
          inputsGraphic.setGeometry(inputsGeometry);
          // if a new point is added, enable the convex hull and clear buttons
          if (inputs.size() > 0) {
            convexHullButton.setDisable(false);
            clearButton.setDisable(false);
          }
        }
      });

      // add the map view to the stack pane
      stackPane.getChildren().addAll(mapView, vBox);
      StackPane.setAlignment(vBox, Pos.TOP_LEFT);
      StackPane.setMargin(vBox, new Insets(10, 0, 0, 10));
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
