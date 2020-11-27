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

package com.esri.samples.take_screenshot;

import java.io.File;

import javax.imageio.ImageIO;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.view.MapView;

public class TakeScreenshotSample extends Application {

  private MapView mapView;

  @Override
  public void start(Stage stage) {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);

      // set title, size, and add scene to stage
      stage.setTitle("Take Screenshot Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // authentication with an API key or named user is required to access basemaps and other location services
      String yourAPIKey = System.getProperty("apiKey");
      ArcGISRuntimeEnvironment.setApiKey(yourAPIKey);

      // create map with a basemap style
      ArcGISMap map = new ArcGISMap(BasemapStyle.ARCGIS_STREETS);

      // create a map view and set its map
      mapView = new MapView();
      mapView.setMap(map);

      // create a file chooser for saving image
      final FileChooser fileChooser = new FileChooser();
      fileChooser.setInitialFileName("map-screenshot");
      fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG file (*.png)", "*.png"));

      // create button to take screen shot
      Button screenShotButton = new Button("Take Screenshot");
      
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
      stackPane.getChildren().addAll(mapView, screenShotButton);
      StackPane.setAlignment(screenShotButton, Pos.BOTTOM_CENTER);
      StackPane.setMargin(screenShotButton, new Insets(0, 0, 100, 0));
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
