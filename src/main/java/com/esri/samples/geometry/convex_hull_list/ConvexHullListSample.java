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

import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.scene.control.Button;


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

      // create a graphics overlay for displaying point graphic and convex hull
      GraphicsOverlay pointGraphicOverlay = new GraphicsOverlay();
      mapView.getGraphicsOverlays().add(pointGraphicOverlay);

      // create a graphic to show the polygons with blue outlines and blue transparent fill

      // create a graphic to show the convex hull as a red outline

      // create a button to create and show the convex hull
      Button convexHullButton = new Button("Create Convex Hull");

      // create a button to clear the convex hull
      Button clearButton = new Button("Clear");

      // create a check box for unioning the result TODO: change grammar
      CheckBox checkBox = new CheckBox("Union");


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
