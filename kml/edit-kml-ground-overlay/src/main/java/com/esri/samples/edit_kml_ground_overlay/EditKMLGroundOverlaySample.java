/*
 * Copyright 2018 Esri.
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

package com.esri.samples.edit_kml_ground_overlay;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.KmlLayer;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Camera;
import com.esri.arcgisruntime.mapping.view.SceneView;
import com.esri.arcgisruntime.ogc.kml.KmlDataset;
import com.esri.arcgisruntime.ogc.kml.KmlGroundOverlay;
import com.esri.arcgisruntime.ogc.kml.KmlIcon;
import com.esri.arcgisruntime.symbology.ColorUtil;

public class EditKMLGroundOverlaySample extends Application {

  private SceneView sceneView;

  @Override
  public void start(Stage stage) {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene fxScene = new Scene(stackPane);
      fxScene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

      // set title, size, and add scene to stage
      stage.setTitle("Edit KML Ground Overlay Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(fxScene);
      stage.show();

      // create a map and add it to the map view
      ArcGISScene scene = new ArcGISScene(Basemap.createImagery());
      sceneView = new SceneView();
      sceneView.setArcGISScene(scene);

      // create a slider for adjusting the rotation
      Slider slider = new Slider(0, 1, 1);
      slider.setMaxWidth(300);
      slider.setShowTickLabels(true);
      slider.setShowTickMarks(true);
      // create a label for the slider
      Label label = new Label("Overlay Opacity: ");

      // create a controls box and add the slider and label
      HBox controlsHBox = new HBox();
      controlsHBox.setBackground(new Background(new BackgroundFill(Paint.valueOf("rgba(0,0,0,0.3)"), CornerRadii.EMPTY,
              Insets.EMPTY)));
      controlsHBox.setPadding(new Insets(10.0));
      controlsHBox.setMaxSize(250, 20);
      controlsHBox.getStyleClass().add("panel-region");
      controlsHBox.getChildren().addAll(label, slider);

      // create a geometry for the ground overlay
      Envelope overlayGeometry = new Envelope(-123.066227926904, 44.04736963555683, -123.0796942287304, 44.03878298600624, SpatialReferences.getWgs84());

      // create a KML icon for the overlay image
      KmlIcon overlayImage = new KmlIcon("./samples-data/images/1944.jpg");
//      KmlIcon overlayImage = new KmlIcon("https://ago-item-storage.s3.us-east-1.amazonaws.com/1f3677c24b2c446e96eaf1099292e83e/1944.jpg?X-Amz-Security-Token=AgoJb3JpZ2luX2VjEEoaCXVzLWVhc3QtMSJGMEQCICshnzTrzOv6b6NpolNhh3I%2Bjuv3XBn%2Fvd8uLsej%2BTxeAiBhTb2KvXMp30WZ6UsAQq31CunuScGVwEm3CxQ7vr6MdiraAwgTEAAaDDYwNDc1ODEwMjY2NSIMrJEL24VO52tvrhZ6KrcDvBkF1QNVJhjfyvfo2uotgHPHMLZCOUkM%2FN%2FFDV4V9%2FH85TM5RhIv16hWA7BuW7zueejiWZkJG0JeowEG4RmXK8aqA0ng2wC0kidx0TSL%2Bksb%2Fvozk%2BnO0Y%2B%2FzB8jl7HbDITAgS07Kjf1i%2BzuqlVD07QXQu1V7t8gnihOfeo9mTkBqsIuCIfIfVFlYtUr2HxWNAxeubDlKiqHlAlM3oq8HrwU3ww%2FX0grXgS1oWBCNoUFmCozilRg4w%2Fm0TkeJquzGUz%2F3xu1u2Q%2FXuY%2F%2FnCuPBo5ZEotbSPMCbwD0w%2FnBjv0zTng4OotwInpDghEKy4MjaynCAxdWb8gi9le%2B0xlstjn07KQgIBU6bg5uLRwH%2BglFkv7nnC91Nqg2eejfTCmiHV7JiyK3BE39QPEz3wFAbG7vLS5cTxS12aYJHJ9dM9jk6LKwXAiFk6geR6wuBlaqnocZavdsHye0DVE1L0NzBI9%2B%2BOaJiJdIgWIxFjbTYLsb%2BgqQYq3LaUwFnNh%2BNJkttuthJCNFpXgOiz6zdcga7CF7fYZlIvCn%2B%2F%2BUxEhCqnpjoZ%2B4VKpcVBkwVu%2BvADIfwxW%2F%2FWgrTC60u3rBTq1AVe8J6pv2bKqxlS7z48befFyNapR4C5qaz7vlZTjg5dOeISEl4snAMMXDVsb2yicciQEp9XQnZ3jHRcjwUxgdhfkQQMyNPrmJjI62DhBjaNMDgIJlR%2FemjPu8rBAO%2BzqOqytnh0WrlGlb5nIsK5U4Z8KVKly9f9L8jvQwLaxJiE9z4M8NLlFsazuiDkxdSdkJ2xR2VU8Rv1srLtJhBy74ER2FxJ30m6Bn4PU4qFz1atyMFyBynA%3D&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20190913T104306Z&X-Amz-SignedHeaders=host&X-Amz-Expires=300&X-Amz-Credential=ASIAYZTTEKKEQCTZVDQY%2F20190913%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Signature=b6f887990a6bd3a9990dddae12246eddf159862f2e2822330977c7e2761cf415");

      // create the KML ground overlay
      KmlGroundOverlay kmlGroundOverlay = new KmlGroundOverlay(overlayGeometry, overlayImage);

      // set the rotation of the ground overlay
      kmlGroundOverlay.setRotation(-3.046024799346924);

      // create a KML dataset with the ground overlay as the root node
      KmlDataset kmlDataset = new KmlDataset(kmlGroundOverlay);

      // create a KML layer for the scene view
      KmlLayer kmlLayer = new KmlLayer(kmlDataset);

      // add the KML layer to the scene
      sceneView.getArcGISScene().getOperationalLayers().add(kmlLayer);

      // move the viewpoint to the ground overlay
      sceneView.setViewpoint(new Viewpoint(kmlGroundOverlay.getExtent(), new Camera(kmlGroundOverlay.getGeometry().getExtent().getCenter(), 1250, 45, 60, 0)));

      slider.setOnMouseReleased((event) -> {
        kmlGroundOverlay.setColor(ColorUtil.colorToArgb(new Color(0, 0, 0, slider.getValue())));
      });
      slider.setOnMouseDragged((event) -> {
        kmlGroundOverlay.setColor(ColorUtil.colorToArgb(new Color(0, 0, 0, slider.getValue())));
      });

      // add the map view and list view to the stack pane
      stackPane.getChildren().addAll(sceneView, controlsHBox);
      StackPane.setAlignment(controlsHBox, Pos.TOP_LEFT);
      StackPane.setMargin(controlsHBox, new Insets(10, 0, 0, 10));

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