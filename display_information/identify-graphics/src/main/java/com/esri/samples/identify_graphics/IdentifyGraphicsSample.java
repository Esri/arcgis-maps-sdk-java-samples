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

package com.esri.samples.identify_graphics;

import java.util.Arrays;
import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.Polygon;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.IdentifyGraphicsOverlayResult;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;

public class IdentifyGraphicsSample extends Application {

  private MapView mapView;
  private GraphicsOverlay graphicsOverlay;
  private ListenableFuture<IdentifyGraphicsOverlayResult> identifyGraphics;

  @Override
  public void start(Stage stage) {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);

      // set title, size, and add scene to stage
      stage.setTitle("Identify Graphics Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // authentication with an API key or named user is required to access basemaps and other location services
      String yourAPIKey = System.getProperty("apiKey");
      ArcGISRuntimeEnvironment.setApiKey(yourAPIKey);

      // create a map with the topographic basemap style
      final ArcGISMap map = new ArcGISMap(BasemapStyle.ARCGIS_TOPOGRAPHIC);

      // create a map view and set the map to it
      mapView = new MapView();
      mapView.setMap(map);

      // create a graphics overlay
      graphicsOverlay = new GraphicsOverlay();

      // create polygon
      var pointsPoly = new PointCollection(mapView.getSpatialReference());
      pointsPoly.addAll(Arrays.asList(
        new Point(-20E5, 20E5), new Point(20E5, 20E5), new Point(20E5, -20E5), new Point(-20E5, -20E5)));
      var fillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, Color.YELLOW, null);
      var polygon = new Polygon(pointsPoly);
      // create graphic from polygon and symbol
      var polygonGraphic = new Graphic(polygon, fillSymbol);
      // add the polygon graphic to the graphics overlay
      graphicsOverlay.getGraphics().add(polygonGraphic);

      // add graphics overlay to the map view
      mapView.getGraphicsOverlays().add(graphicsOverlay);

      mapView.setOnMouseClicked(e -> {
        if (e.getButton() == MouseButton.PRIMARY && e.isStillSincePress()) {
          // create a point from location clicked
          Point2D mapViewPoint = new Point2D(e.getX(), e.getY());

          // identify graphics on the graphics overlay
          identifyGraphics = mapView.identifyGraphicsOverlayAsync(graphicsOverlay, mapViewPoint, 10, false);

          identifyGraphics.addDoneListener(() -> Platform.runLater(this::createGraphicDialog));
        }
      });

      // add the map view to stack pane
      stackPane.getChildren().add(mapView);

    } catch (Exception e) {
      // on any error, print stack trace
      e.printStackTrace();
    }
  }

  /**
   * Indicates when a graphic is clicked by showing an Alert.
   */
  private void createGraphicDialog() {

    try {
      // get the list of graphics returned by identify
      IdentifyGraphicsOverlayResult result = identifyGraphics.get();
      List<Graphic> graphics = result.getGraphics();

      if (!graphics.isEmpty()) {
        // show an alert dialog box if a graphic was returned
        var dialog = new Alert(AlertType.INFORMATION);
        dialog.initOwner(mapView.getScene().getWindow());
        dialog.setHeaderText(null);
        dialog.setTitle("Information Dialog Sample");
        dialog.setContentText("Clicked on " + graphics.size() + " graphic");
        dialog.showAndWait();
      }
    } catch (Exception e) {
      // on any error, display the stack trace
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
