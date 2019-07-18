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

package com.esri.samples.play_a_kml_tour;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import com.esri.arcgisruntime.layers.KmlLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.ArcGISTiledElevationSource;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Surface;
import com.esri.arcgisruntime.mapping.view.SceneView;
import com.esri.arcgisruntime.ogc.kml.KmlContainer;
import com.esri.arcgisruntime.ogc.kml.KmlDataset;
import com.esri.arcgisruntime.ogc.kml.KmlNode;
import com.esri.arcgisruntime.ogc.kml.KmlTour;
import com.esri.arcgisruntime.ogc.kml.KmlTourController;
import com.esri.arcgisruntime.ogc.kml.KmlTourStatus;

public class PlayAKMLTourSample extends Application {

  private SceneView sceneView;
  private KmlTourController kmlTourController;

  @Override
  public void start(Stage stage) throws IOException {
    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene fxScene = new Scene(stackPane);

      // set up the stage
      stage.setTitle("Play a KML Tour Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(fxScene);
      stage.show();

      // create a scene and show it in a scene view
      sceneView = new SceneView();
      ArcGISScene scene = new ArcGISScene(Basemap.createImagery());
      sceneView.setArcGISScene(scene);

      // add elevation data
      Surface surface = new Surface();
      surface.getElevationSources().add(new ArcGISTiledElevationSource("http://elevation3d.arcgis.com/arcgis/rest/services/WorldElevation3D/Terrain3D/ImageServer"));
      scene.setBaseSurface(surface);

      // create play/pause button
      final ImageView playIcon = new ImageView(new Image(PlayAKMLTourSample.class.getResourceAsStream(
          "/play.png")));
      final ImageView pauseIcon = new ImageView(new Image(PlayAKMLTourSample.class.getResourceAsStream(
          "/pause.png")));

      Button playPauseButton = new Button();
      playPauseButton.setGraphic(playIcon);
      playPauseButton.setDisable(true);

      playPauseButton.setOnAction(e -> {
        if (kmlTourController.getTour().getTourStatus() == KmlTourStatus.PLAYING) {
          kmlTourController.pause();
        } else {
          kmlTourController.play();
        }
      });

      // create replay button
      final ImageView replayIcon = new ImageView(new Image(PlayAKMLTourSample.class.getResourceAsStream(
          "/replay.png")));

      Button replayButton = new Button();
      replayButton.setGraphic(replayIcon);
      replayButton.setDisable(true);

      replayButton.setOnAction(e -> {
        kmlTourController.reset();
        playPauseButton.setGraphic(playIcon);
        playPauseButton.setDisable(false);
      });

      VBox controlsVBox = new VBox(6);
      controlsVBox.setMaxSize(50, 100);
      controlsVBox.getChildren().addAll(playPauseButton, replayButton);

      // add a KML layer from a KML dataset with a KML tour
      KmlDataset kmlDataset = new KmlDataset(new File("./samples-data/kml/Esri_tour.kmz").getAbsolutePath());
      KmlLayer kmlLayer = new KmlLayer(kmlDataset);
      scene.getOperationalLayers().add(kmlLayer);

      kmlLayer.addDoneLoadingListener(() -> {
        if (kmlLayer.getLoadStatus() == LoadStatus.LOADED) {
          // find the first KML tour in the dataset when loaded
          KmlTour kmlTour = findFirstKMLTour(kmlDataset.getRootNodes());
          if (kmlTour != null) {
            // enable/disable buttons based on the KML tour status
            kmlTour.addStatusChangedListener(kmlTourStatusChangedEvent -> {
              switch (kmlTourStatusChangedEvent.getStatus()) {
                case NOT_INITIALIZED:
                  // before kml tour is added to controller
                case INITIALIZING:
                  // when kml tour is added to controller
                  playPauseButton.setDisable(true);
                  replayButton.setDisable(true);
                  break;
                case INITIALIZED:
                  // when kml tour is ready to be played
                  playPauseButton.setDisable(false);
                  replayButton.setDisable(false);
                  break;
                case PAUSED:
                  playPauseButton.setGraphic(playIcon);
                  break;
                case PLAYING:
                  playPauseButton.setGraphic(pauseIcon);
                  break;
                case COMPLETED:
                  playPauseButton.setDisable(true);
                  break;
              }
            });

            // set the tour to the tour controller
            kmlTourController = new KmlTourController();
            kmlTourController.setTour(kmlTour);
          } else {
            new Alert(Alert.AlertType.WARNING, "No KML tour found in dataset").show();
          }
        } else {
          new Alert(Alert.AlertType.ERROR, "KML Layer failed to load").show();
        }
      });

      stackPane.getChildren().addAll(sceneView, controlsVBox);
      StackPane.setMargin(controlsVBox, new Insets(10));
      StackPane.setAlignment(controlsVBox, Pos.TOP_LEFT);
    } catch (Exception ex) {
      // on any exception, print the stack trace
      ex.printStackTrace();
    }
  }

  /**
   * Recursively searches for the first KML tour in a list of KML nodes.
   *
   * @return the first KML tour or null if there are no tours.
   */
  private KmlTour findFirstKMLTour(List<KmlNode> kmlNodes) {
    for (KmlNode node : kmlNodes) {
      if (node instanceof KmlTour) {
        return (KmlTour) node;
      } else if (node instanceof KmlContainer) {
        return findFirstKMLTour(((KmlContainer) node).getChildNodes());
      }
    }
    return null;
  }

  /**
   * Stops and releases all resources used in application.
   */
  @Override
  public void stop() {

    if (sceneView != null) {
      sceneView.dispose();
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
