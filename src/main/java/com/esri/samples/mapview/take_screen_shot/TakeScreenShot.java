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

package com.esri.samples.mapview.take_screen_shot;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Optional;

import javax.imageio.ImageIO;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.MapView;

public class TakeScreenShot extends Application {

  private MapView mapView;

  @Override
  public void start(Stage stage) throws Exception {

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

      // create button to take screen shot
      Button screenShotButton = new Button("Take Screen Shot");
      screenShotButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
      screenShotButton.setOnAction(e -> {

        // creating image from map view
        ListenableFuture<Image> mapImage = mapView.exportImageAsync();
        mapImage.addDoneListener(() -> {
          try {
            // display dialog with map view image
            Image image = mapImage.get();
            createAlertDialog(new ImageView(image));
          } catch (Exception ex) {
            ex.printStackTrace();
          }
        });
      });

      // add the map view to stack pane
      borderPane.setCenter(mapView);
      borderPane.setBottom(screenShotButton);
      BorderPane.setAlignment(screenShotButton, Pos.CENTER);
    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
    }
  }

  /**
   * Creates an alert dialog that set the ImageView that was passed as it's main content.
   * <p>
   * The save button, saves the Image in the ImageView to this folder location.
   * The cancel button, will exit out of the dialog.
   *  
   * @param imageView holds image to display within alert dialog
   * @throws Exception if file can't be saved
   */
  private void createAlertDialog(ImageView imageView) throws Exception {

    Alert alert = new Alert(AlertType.CONFIRMATION);
    alert.setTitle("Save Image Dialog");
    alert.setHeaderText("MapView Screen Shot Image");
    ButtonType buttonTypeOne = new ButtonType("Save", ButtonData.OK_DONE);
    ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
    alert.getDialogPane().setContent(imageView);
    alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeCancel);
    Optional<ButtonType> result = alert.showAndWait();

    if (result.get() == buttonTypeOne) {

      File output = new File(System.getProperty("user.dir") +
          "/src/main/java/com/esri/samples/mapview/take_screen_shot/MapViewScreenShot.png");
      BufferedImage buffedImage = SwingFXUtils.fromFXImage(imageView.getImage(), null);
      ImageIO.write(buffedImage, "png", output);
    }
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
