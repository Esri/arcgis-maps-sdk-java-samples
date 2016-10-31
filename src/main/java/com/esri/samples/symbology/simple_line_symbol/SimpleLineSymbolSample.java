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

package com.esri.samples.symbology.simple_line_symbol;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol.Style;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SimpleLineSymbolSample extends Application {

  private MapView mapView;
  private SimpleLineSymbol lineSymbol;

  @Override
  public void start(Stage stage) throws Exception {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);
      scene.getStylesheets().add(getClass().getResource("/SamplesTheme.css").toExternalForm());

      // set title, size, and add scene to stage
      stage.setTitle("Simple Line Symbol Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // create a control panel
      VBox vBoxControl = new VBox(6);
      vBoxControl.setMaxSize(180, 200);
      vBoxControl.getStyleClass().add("panel-region");

      createSymbolFuntionality(vBoxControl);

      final ArcGISMap map = new ArcGISMap(Basemap.createImagery());

      // set initial map view point
      Point point = new Point(-226773, 6550477, SpatialReferences.getWebMercator());
      Viewpoint viewpoint = new Viewpoint(point, 7200); // point, scale
      map.setInitialViewpoint(viewpoint);

      // create a view for this ArcGISMap and set ArcGISMap to it
      mapView = new MapView();
      mapView.setMap(map);

      // creates a line from two points
      PointCollection points = new PointCollection(SpatialReferences.getWebMercator());
      points.add(-226913, 6550477);
      points.add(-226643, 6550477);
      Polyline line = new Polyline(points);

      // creates a solid red (0xFFFF0000) simple line symbol
      lineSymbol = new SimpleLineSymbol(Style.SOLID, 0xFFFF0000, 3);

      // add line with symbol to graphics overlay and add overlay to map view
      GraphicsOverlay graphicsOverlay = new GraphicsOverlay();
      mapView.getGraphicsOverlays().add(graphicsOverlay);
      graphicsOverlay.getGraphics().add(new Graphic(line, lineSymbol));

      // add the map view and control panel to stack pane
      stackPane.getChildren().addAll(mapView, vBoxControl);
      StackPane.setAlignment(vBoxControl, Pos.TOP_LEFT);
      StackPane.setMargin(vBoxControl, new Insets(10, 0, 0, 10));
    } catch (Exception e) {
      // on any error, display the stack trace
      e.printStackTrace();
    }
  }

  /**
   * Creates a label and a combo box for setting the color, width, and style of
   * a SimpleLineSymbol. These labels and combo boxes are then added to the
   * control panel.
   * 
   * @param vBoxControl control pane for user interaction
   */
  private void createSymbolFuntionality(VBox vBoxControl) {

    // create functionality for selecting a color for the line symbol
    Label colorLabel = new Label("Change Line Color");
    colorLabel.getStyleClass().add("panel-label");
    ComboBox<String> colorBox = new ComboBox<>();
    colorBox.getItems().addAll("Blue", "Green", "Red");
    colorBox.setMaxWidth(Double.MAX_VALUE);

    colorBox.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
      final int color;

      switch (newValue) {
        case "Blue":
          color = 0xFF0000FF;
          break;
        case "Green":
          color = 0xFF00FF00;
          break;
        default:
          color = 0xFFFF0000; // red
          break;
      }
      lineSymbol.setColor(color);
    });

    // create functionality for selecting width of the line symbol
    Label widthLabel = new Label("Change Line Width");
    widthLabel.getStyleClass().add("panel-label");
    ComboBox<Float> widthBox = new ComboBox<>();
    widthBox.getItems().addAll(1f, 3f, 6f);
    widthBox.setMaxWidth(Double.MAX_VALUE);

    widthBox.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
      lineSymbol.setWidth(newValue);
    });

    // create functionality for selecting a style for the line symbol
    Label styleLabel = new Label("Change Line Style");
    styleLabel.getStyleClass().add("panel-label");
    ComboBox<Style> styleBox = new ComboBox<>();
    styleBox.getItems().addAll(Style.DASH, Style.DASH_DOT, Style.DASH_DOT_DOT, Style.DOT, Style.SOLID, Style.NULL);
    styleBox.setMaxWidth(Double.MAX_VALUE);

    styleBox.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
      lineSymbol.setStyle(newValue);
    });

    // add functionality to the control pane
    vBoxControl.getChildren().addAll(colorLabel, colorBox, widthLabel, widthBox, styleLabel, styleBox);
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
