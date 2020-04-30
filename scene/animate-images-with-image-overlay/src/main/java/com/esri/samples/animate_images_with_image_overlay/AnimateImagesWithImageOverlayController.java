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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.layers.ArcGISTiledLayer;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.ArcGISTiledElevationSource;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Surface;
import com.esri.arcgisruntime.mapping.view.Camera;
import com.esri.arcgisruntime.mapping.view.ImageFrame;
import com.esri.arcgisruntime.mapping.view.ImageOverlay;
import com.esri.arcgisruntime.mapping.view.SceneView;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;

public class AnimateImagesWithImageOverlayController {

  @FXML
  private SceneView sceneView;
  @FXML
  private Button controlAnimationButton;
  @FXML
  private Slider opacitySlider;
  @FXML
  private ComboBox<String> framesComboBox;
  
  private List<ImageFrame> imageFrames;

  public void initialize() {

    try {
      
      // create a new tiled layer from the World Dark Gray Base REST service and set it as the scene's basemap
      ArcGISScene scene = new ArcGISScene();
      sceneView.setArcGISScene(scene);

      Basemap basemap = new Basemap(new ArcGISTiledLayer("https://services.arcgisonline" +
        ".com/arcgis/rest/services/Canvas/World_Dark_Gray_Base/MapServer"));
      scene.setBasemap(basemap);

      // create a new elevation source from the Terrain3D REST service and set it as the scene's base surface
      Surface surface = new Surface();
      surface.getElevationSources().add(new ArcGISTiledElevationSource("https://elevation3d.arcgis" +
        ".com/arcgis/rest/services/WorldElevation3D/Terrain3D/ImageServer"));
      scene.setBaseSurface(surface);

      // create a camera, looking at the pacific southwest sector
      Point observationPoint = new Point(-116.621, 24.7773, 856977.0);
      Camera camera = new Camera(observationPoint, 353.994, 48.5495, 0.0);
      sceneView.setViewpointCamera(camera);
      
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
          ImageFrame imageFrame = new ImageFrame(file.getAbsolutePath(), new Envelope((observationPoint), 15.09589635986124, -14.3770441522488));
          imageFrames.add(imageFrame);
        }
      }

    } catch (Exception e) {
      // on any exception, print the stack trace
      e.printStackTrace();
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
