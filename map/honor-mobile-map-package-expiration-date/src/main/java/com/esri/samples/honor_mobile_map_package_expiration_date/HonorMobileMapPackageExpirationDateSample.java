/*
 * Copyright 2019 Esri.
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

package com.esri.samples.honor_mobile_map_package_expiration_date;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.Expiration;
import com.esri.arcgisruntime.mapping.MobileMapPackage;
import com.esri.arcgisruntime.mapping.view.MapView;

public class HonorMobileMapPackageExpirationDateSample extends Application {

  private MapView mapView;

  @Override
  public void start(Stage stage) {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);

      // set title, size, and add scene to stage
      stage.setTitle("Honor Mobile Map Package Expiration Date Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // create an overlay to display the expiration information
      VBox expirationOverlayVbox = new VBox(6);
      expirationOverlayVbox.setBackground(new Background(new BackgroundFill(Paint.valueOf("rgba(0,0,0,0.3)"), CornerRadii.EMPTY,
              Insets.EMPTY)));
      expirationOverlayVbox.setPadding(new Insets(10.0));
      expirationOverlayVbox.setMaxSize(210, 150);
      expirationOverlayVbox.getStyleClass().add("panel-region");

      // create a map view
      mapView = new MapView();

      // load a mobile map package
      MobileMapPackage mobileMapPackage = new MobileMapPackage("./samples-data/mmpk/LothianRiversAnno.mmpk");
      mobileMapPackage.loadAsync();
      System.out.printf("loading");
      mobileMapPackage.addDoneLoadingListener(() -> {
        if (mobileMapPackage.getLoadStatus() == LoadStatus.LOADED && !mobileMapPackage.getMaps().isEmpty()) {
          // add the map from the mobile map package to the map view
          mapView.setMap(mobileMapPackage.getMaps().get(0));

          System.out.println("loaded");

          // get the Expiration of the mobile map package
          Expiration expiration = mobileMapPackage.getExpiration();

          // create labels for the expiration message and date
          Label expirationMessageLabel = new Label(expiration.getMessage());
          // @TODO: format time @ date appropriately
          Label expirationDateLabel = new Label(expiration.getDateTime().toString());

          // add the labels to the overlay
          expirationOverlayVbox.getChildren().addAll(expirationMessageLabel, expirationDateLabel);

        } else {
          new Alert(Alert.AlertType.ERROR, "Failed to load the mobile map package").show();
        }
      });

      // add the map view and overlay to the stack pane
      stackPane.getChildren().addAll(mapView, expirationOverlayVbox);
      StackPane.setAlignment(expirationOverlayVbox, Pos.CENTER);

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
