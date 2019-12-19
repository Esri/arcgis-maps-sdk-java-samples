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

package com.esri.samples.control_annotation_sublayer_visibility;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import com.esri.arcgisruntime.layers.AnnotationLayer;
import com.esri.arcgisruntime.layers.AnnotationSublayer;
import com.esri.arcgisruntime.layers.Layer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.MobileMapPackage;
import com.esri.arcgisruntime.mapping.view.MapView;

import java.io.File;

public class ControlAnnotationSublayerVisibilitySample extends Application {

  private MapView mapView;
  private MobileMapPackage mobileMapPackage;

  @Override
  public void start(Stage stage) {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);

      // set title, size, and add scene to stage
      stage.setTitle("Control Annotation Sublayer Visibility");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // create a map view
      mapView = new MapView();

      // create checkboxes for toggling the sublayer visibility manually
      CheckBox closedSublayerCheckbox = new CheckBox();
      closedSublayerCheckbox.setSelected(true);
      closedSublayerCheckbox.setTextFill(Color.WHITE);
      CheckBox openSublayerCheckbox = new CheckBox();
      openSublayerCheckbox.setSelected(true);
      openSublayerCheckbox.setTextFill(Color.DARKGRAY);

      // create a control panel and label for the checkboxes
      VBox controlsVBox = new VBox(6);
      controlsVBox.getStyleClass().add("panel-region");
      controlsVBox.setBackground(new Background(new BackgroundFill(Paint.valueOf("rgba(0,0,0,0.3)"), CornerRadii.EMPTY,
              Insets.EMPTY)));
      controlsVBox.setPadding(new Insets(10.0));
      controlsVBox.setMaxSize(180, 85);

      // show current map scale in a label within the control panel
      Label currentMapScaleLabel = new Label();
      currentMapScaleLabel.setTextFill(Color.WHITE);
      mapView.addMapScaleChangedListener(mapScaleChangedEvent ->
              currentMapScaleLabel.setText("Scale: 1:" + Math.round(mapView.getMapScale()))
      );

      // add checkboxes and label to the control panel
      controlsVBox.getChildren().addAll(closedSublayerCheckbox, openSublayerCheckbox, currentMapScaleLabel);

      // load the mobile map package
      File mmpkFile = new File(System.getProperty("data.dir"), "./samples-data/mmpk/GasDeviceAnno.mmpk");
      mobileMapPackage = new MobileMapPackage(mmpkFile.getAbsolutePath());
      mobileMapPackage.loadAsync();
      mobileMapPackage.addDoneLoadingListener(() -> {
        if (mobileMapPackage.getLoadStatus() == LoadStatus.LOADED && !mobileMapPackage.getMaps().isEmpty()) {
          // set the mobile map package's map to the map view
          mapView.setMap(mobileMapPackage.getMaps().get(0));
          // find the annotation layer within the map
          for (Layer layer : mapView.getMap().getOperationalLayers()) {
            if (layer instanceof AnnotationLayer) {
              // load the annotation layer
              layer.loadAsync();
              layer.addDoneLoadingListener(() -> {
                if (layer.getLoadStatus() == LoadStatus.LOADED) {
                  // get annotation sublayer name from sublayer content
                  AnnotationSublayer closedSublayer = (AnnotationSublayer) layer.getSubLayerContents().get(0);
                  AnnotationSublayer openSublayer = (AnnotationSublayer) layer.getSubLayerContents().get(1);

                  // set the layer name for the checkboxes
                  closedSublayerCheckbox.setText(closedSublayer.getName());
                  openSublayerCheckbox.setText(String.format("%s (1:%d - 1:%d)", openSublayer.getName(), Math.round(openSublayer.getMaxScale()), Math.round(openSublayer.getMinScale())));

                  // toggle annotation sublayer visibility on check
                  closedSublayerCheckbox.setOnAction(event -> closedSublayer.setVisible(closedSublayerCheckbox.isSelected()));
                  openSublayerCheckbox.setOnAction(event -> openSublayer.setVisible(openSublayerCheckbox.isSelected()));

                  // gray out the open sublayer when the layer is out of scale
                  mapView.addMapScaleChangedListener(mapScaleChangedEvent -> {
                    if (openSublayer.isVisibleAtScale(mapView.getMapScale())) {
                      openSublayerCheckbox.setTextFill(Color.WHITE);
                    } else {
                      openSublayerCheckbox.setTextFill(Color.DARKGRAY);
                    }
                  });

                } else {
                  new Alert(Alert.AlertType.ERROR, "Error loading Annotation Layer " + layer.getName()).show();
                }
              });
            }
          }

        } else {
          new Alert(Alert.AlertType.ERROR, "Mobile Map Package failed to load.").show();
        }
      });

      // add map view and label to stack pane
      stackPane.getChildren().addAll(mapView, controlsVBox);
      StackPane.setAlignment(controlsVBox, Pos.TOP_LEFT);
      StackPane.setMargin(controlsVBox, new Insets(20, 0, 0, 20));

    } catch (Exception e) {
      // on any error, print the stack trace
      e.printStackTrace();
    }
  }

  /**
   * Stops and releases all resources used in application.
   */
  @Override
  public void stop() {

    // release resources when the application closes
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
