/*
 COPYRIGHT 1995-2020 ESRI

 TRADE SECRETS: ESRI PROPRIETARY AND CONFIDENTIAL
 Unpublished material - all rights reserved under the
 Copyright Laws of the United States.

 For additional information, contact:
 Environmental Systems Research Institute, Inc.
 Attn: Contracts Dept
 380 New York Street
 Redlands, California, USA 92373

 email: contracts@esri.com
 */

package com.esri.samples.animate_images_with_image_overlay;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;

public class AnimateImagesWithImageOverlayController {

  @FXML
  private SceneView sceneView;
  @FXML
  private Button controlAnimationButton;
  @FXML
  private Slider opacitySlider;
  @FXML
  private ComboBox<Integer> framesComboBox;
  
  private List<ImageFrame> imageFrames;

  private Integer imageIndex = 0;
  
  private Timer timer = null;
  private boolean isTimerRunning = true;


  public void initialize() {

    try {
      
      // create a new tiled layer from the World Dark Gray Base REST service and set it as the scene's basemap
      ArcGISScene scene = new ArcGISScene();
      sceneView.setArcGISScene(scene);
      
      // create a camera, looking at the pacific southwest sector
      Point observationPoint = new Point(-116.621, 24.7773, 856977.0);
      Camera camera = new Camera(observationPoint, 353.994, 48.5495, 0.0);

      // // create an envelope of the pacific southwest sector for displaying the image frame
      Point pointForImageFrame = new Point(-120.0724273439448, 35.131016955536694, SpatialReferences.getWgs84());
      Envelope imageFrameEnvelope = new Envelope(pointForImageFrame, 15.09589635986124, -14.3770441522488);
      scene.setInitialViewpoint(new Viewpoint(imageFrameEnvelope, camera));

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
      if (imageFiles != null){
        Arrays.sort(imageFiles);
        // create an image with the given path and use it to create an image frame
        for (File file: imageFiles) {
          ImageFrame imageFrame = new ImageFrame(file.getAbsolutePath(), imageFrameEnvelope);
          imageFrames.add(imageFrame);
        }
      }
      
      // populate the frames combo box with values
      framesComboBox.getItems().addAll(17, 33, 67);
      // open the sample at 15fps
      framesComboBox.getSelectionModel().select(2);
//      
//      opacitySlider.valueProperty().addListener(o -> sceneView.getImageOverlays().get(0).setOpacity((float)opacitySlider.getValue()));
      
      startAnimation();
      
    } catch (Exception e) {
      // on any exception, print the stack trace
      e.printStackTrace();
    }
  }

  /**
   * Set up a timer to display the images at the specified frame rate from the combobox.
   *
   */
  private void startAnimation(){
    
    timer = new Timer(true);
    TimerTask timerTask = new TimerTask() {
      @Override
      public void run() {
        addNextImageFrameToImageOverlay();
      }
    };

    timer.scheduleAtFixedRate(timerTask, 1L, framesComboBox.getSelectionModel().getSelectedItem());
  }
  
  /**
   * Create a new image frame from the image at the current index and add it to the image overlay.
   */
  @FXML
  private void addNextImageFrameToImageOverlay() {
    
    // set image frame to image overlay
    sceneView.getImageOverlays().get(0).setImageFrame(imageFrames.get(imageIndex));
    // increment the index to keep track of which image to load next
    imageIndex++;
    // reset index once all files have been loaded
    if (imageIndex == imageFrames.size())
      imageIndex = 0;
  }

  /**
   * Stops/starts the animation of the image frames on the image overlay.
   */
  @FXML
  private void handleButtonClicked() {
    if (isTimerRunning) {
      timer.cancel();
      isTimerRunning = false;
      controlAnimationButton.setText("Start");
    } else {
      startAnimation();
      isTimerRunning = true;
      controlAnimationButton.setText("Stop");
    }
  }
  
  /**
   * Controls the opacity of the image overlay using the slider.
   */
  @FXML
  private void changeImageOverlayOpacity() {
    sceneView.getImageOverlays().get(0).setOpacity((float) opacitySlider.getValue());
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
