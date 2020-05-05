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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;

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

  private Integer frameIndex = 0;
  private Integer period = 67;

  private Timer timer;
  private boolean isTimerRunning;

  public void initialize() {

    try {

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

      // instantiate a new empty list to hold image frames
      imageFrames = new ArrayList<>();
      // get the image files from local storage as an unordered list
      File[] imageFiles = new File(System.getProperty("data.dir"), "./samples-data/PacificSouthWest").listFiles();
      // sort the list of image files
      if (imageFiles != null) {
        Arrays.sort(imageFiles);
        // create an image with the given path and use it to create an image frame
        for (File file : imageFiles) {
          ImageFrame imageFrame = new ImageFrame(file.getAbsolutePath(), imageFrameEnvelope);
          imageFrames.add(imageFrame);
        }
      }

      // populate the frames combo box with values
      framesComboBox.getItems().addAll("60 frames per second", "30 frames per second", "15 frames per second");
      // open the sample at 15fps
      framesComboBox.getSelectionModel().select(2);
      // set timer running tracker to true when sample loads
      isTimerRunning = true;
      startNewAnimationTimer();

    } catch (Exception e) {
      // on any exception, print the stack trace
      e.printStackTrace();
    }
  }

  /**
   * Create a new image frame from the image at the current index and add it to the image overlay.
   */
  private void addNextImageFrameToImageOverlay() {

    // set image frame to image overlay
    sceneView.getImageOverlays().get(0).setImageFrame(imageFrames.get(frameIndex));
    // increment the index to keep track of which image to load next
    frameIndex++;
    // reset index once all files have been loaded
    if (frameIndex == imageFrames.size())
      frameIndex = 0;
  }

  /**
   * Controls the opacity of the image overlay using the slider.
   */
  @FXML
  private void changeImageOverlayOpacity() {
    sceneView.getImageOverlays().get(0).setOpacity((float) opacitySlider.getValue());
  }

  /**
   * Set up a timer to display the images at the specified frame rate from the combobox.
   */
  private void startNewAnimationTimer() {

    timer = new Timer(true);
    TimerTask timerTask = new TimerTask() {
      @Override
      public void run() {
        addNextImageFrameToImageOverlay();
      }
    };
    timer.scheduleAtFixedRate(timerTask, 1L, period);
  }

  /**
   * Handles the rate at which the image frames are displayed using the combo box.
   */
  @FXML
  private void handleframesComboBoxInteraction() {
    // set the period for the chosen fps 
    switch (framesComboBox.getSelectionModel().getSelectedItem()) {
      case "60 frames per second":
        period = 17; // 1000ms/17 = 60 fps
        break;
      case "30 frames per second":
        period = 33; // 1000ms/33 = 30 fps
        break;
      case "15 frames per second":
        period = 67; // 1000ms/67 = 15 fps
        break;
    }

    if (isTimerRunning) {
      timer.cancel();
      startNewAnimationTimer();
    }
  }

  /**
   * Stops/starts the animation of the image frames on the image overlay.
   */
  @FXML
  private void handlecontrolAnimationButtonClicked() {
    if (isTimerRunning) {
      timer.cancel();
      isTimerRunning = false;
      controlAnimationButton.setText("Start");
    } else {
      startNewAnimationTimer();
      isTimerRunning = true;
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
