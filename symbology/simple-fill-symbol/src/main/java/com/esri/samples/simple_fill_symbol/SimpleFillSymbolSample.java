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

package com.esri.samples.simple_fill_symbol;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.Polygon;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol.Style;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;

public class SimpleFillSymbolSample extends Application {

  private VBox controlsVBox;
  private MapView mapView;
  private SimpleFillSymbol fillSymbol;
  private List<SimpleLineSymbol> lineSymbols;

  @Override
  public void start(Stage stage) {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);
      scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

      // set title, size, and add scene to stage
      stage.setTitle("Simple Fill Symbol Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // create a control panel
      controlsVBox = new VBox(6);
      controlsVBox.setBackground(new Background(new BackgroundFill(Paint.valueOf("rgba(0,0,0,0.3)"), CornerRadii.EMPTY,
          Insets.EMPTY)));
      controlsVBox.setPadding(new Insets(10.0));
      controlsVBox.setMaxSize(180, 200);
      controlsVBox.getStyleClass().add("panel-region");

      createSymbolFunctionality();

      final ArcGISMap map = new ArcGISMap(Basemap.createTopographic());

      // set initial map view point
      Point initialPoint = new Point(-12000000, 5400000, SpatialReferences.getWebMercator());
      Viewpoint viewpoint = new Viewpoint(initialPoint, 10000000); // point, scale
      map.setInitialViewpoint(viewpoint);

      // create a view for this ArcGISMap and set ArcGISMap to it
      mapView = new MapView();
      mapView.setMap(map);

      // creates a square from four points
      PointCollection points = new PointCollection(SpatialReferences.getWebMercator());
      points.add(-1.1579397849033352E7, 5618494.623878779);
      points.add(-1.158486021463032E7, 5020365.591010623);
      points.add(-1.236324731219847E7, 5009440.859816683);
      points.add(-1.2360516129399985E7, 5621225.806677263);
      Polygon square = new Polygon(points);

      // transparent red (0x88FF0000) color symbol
      fillSymbol = new SimpleFillSymbol(Style.SOLID, 0x88FF0000, null);

      // renders graphics to the GeoView
      GraphicsOverlay graphicsOverlay = new GraphicsOverlay();
      mapView.getGraphicsOverlays().add(graphicsOverlay);
      graphicsOverlay.getGraphics().add(new Graphic(square, fillSymbol));

      createLineSymbols();

      // add the map view and control panel to stack pane
      stackPane.getChildren().addAll(mapView, controlsVBox);
      StackPane.setAlignment(controlsVBox, Pos.TOP_LEFT);
      StackPane.setMargin(controlsVBox, new Insets(10, 0, 0, 10));

    } catch (Exception e) {
      // on any error, display the stack trace
      e.printStackTrace();
    }
  }

  /**
   * Creates a list of three SimpleLineSymbols, (blue, red, green).
   */
  private void createLineSymbols() {

    lineSymbols = new ArrayList<>();

    // solid blue (0xFF0000FF) simple line symbol
    SimpleLineSymbol blueLineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0xFF0000FF, 3);
    lineSymbols.add(blueLineSymbol);

    // solid green (0xFF00FF00) simple line symbol
    SimpleLineSymbol greenLineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0xFF00FF00, 3);
    lineSymbols.add(greenLineSymbol);

    // solid red (0xFFFF0000) simple line symbol
    SimpleLineSymbol redLineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0xFFFF0000, 3);
    lineSymbols.add(redLineSymbol);
  }

  /**
   * Creates the UI control pane for setting the SimpleFillSymbol properties:
   * color, outline and style.
   */
  private void createSymbolFunctionality() {

    // select a color for the fill symbol
    Label colorLabel = new Label("Change Fill Color");
    colorLabel.getStyleClass().add("panel-label");
    ComboBox<String> colorBox = new ComboBox<>();
    colorBox.getItems().addAll("Blue", "Green", "Red");
    colorBox.setMaxWidth(Double.MAX_VALUE);

    colorBox.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
      final int color;

      switch (newValue) {
        case "Blue":
          color = 0x880000FF;
          break;
        case "Green":
          color = 0x8800FF00;
          break;
        default:
          color = 0xFFFF0000; // red
          break;
      }
      fillSymbol.setColor(color);
    });

    // select the outline color
    Label lineLabel = new Label("Change Outline Color");
    lineLabel.getStyleClass().add("panel-label");
    ComboBox<String> lineBox = new ComboBox<>();
    lineBox.getItems().addAll("Blue", "Green", "Red");
    lineBox.setMaxWidth(Double.MAX_VALUE);

    lineBox.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> fillSymbol.setOutline(lineSymbols.get(lineBox.getSelectionModel().getSelectedIndex())));

    // select a style for the fill symbol
    Label stlyeLabel = new Label("Change Fill Style");
    stlyeLabel.getStyleClass().add("panel-label");
    ComboBox<Style> styleBox = new ComboBox<>();
    styleBox.getItems().addAll(Style.BACKWARD_DIAGONAL, Style.FORWARD_DIAGONAL, Style.DIAGONAL_CROSS, Style.HORIZONTAL,
        Style.VERTICAL, Style.CROSS, Style.SOLID, Style.NULL);
    styleBox.setMaxWidth(Double.MAX_VALUE);

    styleBox.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> fillSymbol.setStyle(newValue));

    // add functionality to the control pane
    controlsVBox.getChildren().addAll(colorLabel, colorBox, lineLabel, lineBox, stlyeLabel, styleBox);
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
