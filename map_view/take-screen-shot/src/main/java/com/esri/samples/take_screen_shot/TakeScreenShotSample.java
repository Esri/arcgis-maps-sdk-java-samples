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

package com.esri.samples.mapview.take_screen_shot;

import java.io.File;

import javax.imageio.ImageIO;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.MapView;

public class TakeScreenShotSample extends Application {

  private MapView mapView;

  @Override
  public void start(Stage stage) {

    try {
      // create stack pane and application scene
      BorderPane borderPane = new BorderPane();
      Scene scene = new Scene(borderPane);

      // set title, size, and add scene to stage
      stage.setTitle("Take Screen Shot Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // create map and set to map view
      ArcGISMap map = new ArcGISMap(Basemap.createStreets());
      mapView = new MapView();
      mapView.setMap(map);

      // create a file chooser for saving image
      final FileChooser fileChooser = new FileChooser();
      fileChooser.setInitialFileName("map-screenshot");
      fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG file (*.png)", "*.png"));

      // create button to take screen shot
      Button screenShotButton = new Button("Take Screenshot");
      screenShotButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
      screenShotButton.setOnAction(e -> {
        // export image from map view
        ListenableFuture<Image> mapImage = mapView.exportImageAsync();
        mapImage.addDoneListener(() -> {
          try {
            // get image
            Image image = mapImage.get();
            // choose a location to save the file
            File file = fileChooser.showSaveDialog(stage);
            if (file != null) {
              // write the image to the save location
              ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
            }
          } catch (Exception ex) {
            ex.printStackTrace();
          }
        });
      });

      // add the map view to stack pane
      borderPane.setCenter(mapView);
      borderPane.setBottom(screenShotButton);
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
