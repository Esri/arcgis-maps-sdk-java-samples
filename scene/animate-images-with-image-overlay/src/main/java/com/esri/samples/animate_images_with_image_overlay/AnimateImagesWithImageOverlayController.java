/*
 * Copyright 2020 Esri.
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

package com.esri.samples.animate_images_with_image_overlay;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.util.Duration;

import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.ArcGISTiledLayer;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.ArcGISTiledElevationSource;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Surface;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Camera;
import com.esri.arcgisruntime.mapping.view.ImageFrame;
import com.esri.arcgisruntime.mapping.view.ImageOverlay;
import com.esri.arcgisruntime.mapping.view.SceneView;

public class AnimateImagesWithImageOverlayController {

  @FXML private SceneView sceneView;
  @FXML private Button controlAnimationButton;
  @FXML private Slider opacitySlider;
  @FXML private ComboBox<String> framesComboBox;

  private List<ImageFrame> imageFrames;
  private ImageOverlay imageOverlay;

  private Integer frameIndex = 0;
  private Integer period = 67;
  private Timeline animation;

  public void initialize() {

    try {
      
      // populate the frames combo box with speed descriptions
      framesComboBox.getItems().addAll("Fast", "Medium", "Slow");
      // open the sample at slow speed
      framesComboBox.getSelectionModel().select(2);

      // create a new ArcGISScene and set it to the scene view
      ArcGISScene scene = new ArcGISScene();
      sceneView.setArcGISScene(scene);

      // create a camera, looking at the pacific southwest sector
      Point observationPoint = new Point(-116.621, 24.7773, 856977.0);
      Camera camera = new Camera(observationPoint, 353.994, 48.5495, 0.0);

      // create an envelope of the pacific southwest sector for displaying the image frame
      Point pointForImageFrame = new Point(-120.0724273439448, 35.131016955536694, SpatialReferences.getWgs84());
      Envelope imageFrameEnvelope = new Envelope(pointForImageFrame, 15.09589635986124, -14.3770441522488);
      scene.setInitialViewpoint(new Viewpoint(imageFrameEnvelope, camera));

      // create a new tiled layer from the World Dark Gray Base REST service and set it as the scene's basemap
      Basemap basemap = new Basemap(new ArcGISTiledLayer("https://services.arcgisonline" +
        ".com/arcgis/rest/services/Canvas/World_Dark_Gray_Base/MapServer"));
      scene.setBasemap(basemap);

      // create a new elevation source from the Terrain3D REST service and set it as the scene's base surface
      Surface surface = new Surface();
      surface.getElevationSources().add(new ArcGISTiledElevationSource("https://elevation3d.arcgis" +
        ".com/arcgis/rest/services/WorldElevation3D/Terrain3D/ImageServer"));
      scene.setBaseSurface(surface);

      // create and append an image overlay to the scene view
      sceneView.getImageOverlays().add(new ImageOverlay());
      // store the newly created image overlay
      imageOverlay = sceneView.getImageOverlays().get(0);

      // get the image files from local storage as an unordered list
      File[] imageFiles = new File(System.getProperty("data.dir"), "./samples-data/psw/PacificSouthWest").listFiles();
      // sort the list of image files by file name in ascending order
      if (imageFiles != null) {
        imageFrames = Arrays.stream(imageFiles)
          .sorted()
          .map(f -> new ImageFrame(f.getAbsolutePath(), imageFrameEnvelope))
          .collect(Collectors.toList());
      }

      startNewAnimationTimeline();

    } catch (Exception e) {
      // on any exception, print the stack trace
      e.printStackTrace();
    }
  }

  /**
   * Set up a timeline to display the images at the specified speed from the combobox.
   */
  private void startNewAnimationTimeline() {

    animation = new Timeline();
    animation.setCycleCount(-1); // loop animation
    animation.getKeyFrames().add(new KeyFrame(Duration.millis(period), e -> {
      // set image frame to image overlay
      imageOverlay.setImageFrame(imageFrames.get(frameIndex));
      // update to the next frame
      frameIndex = (frameIndex + 1) % imageFrames.size();
    }));
    animation.play();
  }
  
  /**
   * Controls the opacity of the image overlay using the slider.
   */
  @FXML
  private void changeImageOverlayOpacity() {
    imageOverlay.setOpacity((float) opacitySlider.getValue());
  }

  /**
   * Handles the rate at which the image frames are displayed using the combo box.
   */
  @FXML
  private void handleFramesComboBoxInteraction() {
    // set the period for the chosen frame display speed 
    switch (framesComboBox.getSelectionModel().getSelectedItem()) {
      case "Fast":
        period = 17;
        break;
      case "Medium":
        period = 33;
        break;
      case "Slow":
        period = 67;
        break;
    }

    if (animation.getStatus() == Animation.Status.RUNNING) {
      animation.pause();
      startNewAnimationTimeline();
    }
  }

  /**
   * Stops/starts the animation of the image frames on the image overlay.
   */
  @FXML
  private void handleControlAnimationButtonClicked() {
    if (animation.getStatus() == Animation.Status.RUNNING) {
      animation.pause();
      controlAnimationButton.setText("Start");
    } else {
      startNewAnimationTimeline();
      controlAnimationButton.setText("Stop");
    }
  }
  
  /**
   * Disposes of application resources.
   */
  void terminate() {

    // release resources when the application closes
    if (sceneView != null) {
      sceneView.dispose();
    }
  }
}
