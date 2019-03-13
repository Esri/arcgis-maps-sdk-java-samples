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

package com.esri.samples.kml.play_kml_tours;

import java.io.File;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

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

public class PlayKMLToursController {

  @FXML
  private SceneView sceneView;
  @FXML
  private Button playButton;
  /*@FXML
  private Button audioButton;*/

  private final ImageView playIcon = new ImageView(new Image(PlayKMLToursController.class.getResourceAsStream(
      "/icons/play.png")));
  private final ImageView pauseIcon = new ImageView(new Image(PlayKMLToursController.class.getResourceAsStream(
      "/icons/pause.png")));
  private final ImageView audioIcon = new ImageView(new Image(PlayKMLToursController.class.getResourceAsStream(
      "/icons/volume-high.png")));
  private final ImageView audioOffIcon = new ImageView(new Image(PlayKMLToursController.class.getResourceAsStream(
      "/icons/volume-off.png")));

  private boolean playing = false;
  private boolean audioOn = true;
  //private KmlTourController kmlTourController;
  private KmlTour kmlTour;

  /**
   * Called after FXML loads. Sets up scene and map and configures property bindings.
   */
  public void initialize() {

    try {

      playButton.setGraphic(playIcon);
      //audioButton.setGraphic(audioIcon);

      // create a scene
      ArcGISScene scene = new ArcGISScene(Basemap.createImagery());
      sceneView.setArcGISScene(scene);

      // add elevation data
      Surface surface = new Surface();
      surface.getElevationSources().add(new ArcGISTiledElevationSource("http://elevation3d.arcgis.com/arcgis/rest/services/WorldElevation3D/Terrain3D/ImageServer"));
      scene.setBaseSurface(surface);

      // add a KML layer from a KML dataset with a KML tour
      KmlDataset kmlDataset = new KmlDataset(new File("./samples-data/kml/Esri_tour.kmz").getAbsolutePath());
      KmlLayer kmlLayer = new KmlLayer(kmlDataset);
      scene.getOperationalLayers().add(kmlLayer);

      kmlLayer.addDoneLoadingListener(() -> {
        if (kmlLayer.getLoadStatus() == LoadStatus.LOADED) {
          // find the first KML tour in the dataset when loaded
          kmlTour = findFirstKMLTour(kmlDataset.getRootNodes());
          if (kmlTour != null) {
            // set the tour to the tour controller and enable UI controls
            //kmlTourController.setTour(kmlTour);
            playButton.setDisable(false);
          } else {
            new Alert(Alert.AlertType.WARNING, "No KML tour found in dataset").show();
          }
        } else {
          new Alert(Alert.AlertType.ERROR, "KML Layer failed to load").show();
        }
      });

    } catch (Exception e) {
      // on any exception, print the stack trace
      e.printStackTrace();
    }
  }

  /**
   * Recursively searches for the first KML tour in a list of KML nodes.
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

  @FXML
  private void togglePlay() {
    playing = !playing;
    playButton.setGraphic(playing ? pauseIcon : playIcon);
    if (playing) {
      //kmlTourController.play();
    } else {
      //kmlTourController.pause();
    }
  }

  /**
   * Disposes of application resources.
   */
  void terminate() {

    if (sceneView != null) {
      sceneView.dispose();
    }
  }
}
