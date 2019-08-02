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

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.Expiration;
import com.esri.arcgisruntime.mapping.MobileMapPackage;
import com.esri.arcgisruntime.mapping.view.MapView;

public class HonorMobileMapPackageExpirationDateSample extends Application {

  private MapView mapView;
  private MobileMapPackage mobileMapPackage;

  @Override
  public void start(Stage stage) {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);
      scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

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
      expirationOverlayVbox.setMaxSize(800, 150);
      expirationOverlayVbox.setAlignment(Pos.CENTER);
      expirationOverlayVbox.getStyleClass().add("panel-region");

      // create a map view
      mapView = new MapView();

      // load the mobile map package
      mobileMapPackage = new MobileMapPackage("./samples-data/mmpk/LothianRiversAnno.mmpk");
      mobileMapPackage.loadAsync();
      mobileMapPackage.addDoneLoadingListener(() -> {
        // handle map package expiration, if expired
        if (mobileMapPackage.getExpiration() != null && mobileMapPackage.getExpiration().isExpired()) {

          // get the Expiration of the mobile map package
          Expiration expiration = mobileMapPackage.getExpiration();

          // create label for the expiration message
          Label expirationMessageLabel = new Label(expiration.getMessage());
          expirationMessageLabel.setFont(new Font(16));

          // create a label to display the time since expiration
          Label timeSinceExpirationLabel = new Label();
          timeSinceExpirationLabel.setFont(new Font(16));

          // determine the time in milliseconds when the mobile map package expired
          long expirationTime = expiration.getDateTime().getTimeInMillis();

          // create a timeline to count the time since expiration
          Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), actionEvent -> {
                // get a calendar for the current time
                Calendar currentTimeCalendar = Calendar.getInstance();

                // determine the time since expiration
                long millisecondsSinceExpiration = currentTimeCalendar.getTimeInMillis() - expirationTime;

                // format the label
                String formattedTimeSinceExpiration = String.format("Expired %d days and %02d:%02d:%02d hours ago.",
                        TimeUnit.MILLISECONDS.toDays(millisecondsSinceExpiration),
                        TimeUnit.MILLISECONDS.toHours(millisecondsSinceExpiration)
                                - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(millisecondsSinceExpiration)),
                        TimeUnit.MILLISECONDS.toMinutes(millisecondsSinceExpiration)
                                - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisecondsSinceExpiration)),
                        TimeUnit.MILLISECONDS.toSeconds(millisecondsSinceExpiration)
                                - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisecondsSinceExpiration))
                );

                // set the label to display the time since expiration
                timeSinceExpirationLabel.setText(formattedTimeSinceExpiration);
              }
            ),
            new KeyFrame(Duration.seconds(1))
          );
          timeline.setCycleCount(Animation.INDEFINITE);
          timeline.play();

          // add the labels to the overlay
          expirationOverlayVbox.getChildren().addAll(expirationMessageLabel, timeSinceExpirationLabel);
        }
        if (mobileMapPackage.getLoadStatus() == LoadStatus.LOADED) {

          // add the map from the mobile map package to the map view
          mapView.setMap(mobileMapPackage.getMaps().get(0));

        } else {
          new Alert(Alert.AlertType.ERROR, "Failed to load the mobile map package: " + mobileMapPackage.getLoadError().getMessage()).show();
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
