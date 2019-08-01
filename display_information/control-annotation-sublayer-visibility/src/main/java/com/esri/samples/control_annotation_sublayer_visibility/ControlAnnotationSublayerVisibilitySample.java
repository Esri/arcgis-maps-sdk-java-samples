/*
 * Copyright 2017 Esri.
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
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.util.Duration;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.layers.AnnotationLayer;
import com.esri.arcgisruntime.layers.AnnotationSublayer;
import com.esri.arcgisruntime.layers.Layer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.MobileMapPackage;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.MapView;

public class ControlAnnotationSublayerVisibilitySample extends Application {

  private MapView mapView;

  @Override
  public void start(Stage stage) {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);
      scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

      // set title, size, and add scene to stage
      stage.setTitle("Control Annotation Sublayer Visibility");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // create checkboxes for toggling the sublayer visibility manually
      CheckBox closedSublayerCheckbox = new CheckBox();
      closedSublayerCheckbox.setSelected(true);
      CheckBox openSublayerCheckbox = new CheckBox();
      openSublayerCheckbox.setSelected(true);

      // create a control panel and add the checkboxes
      VBox controlsVBox = new VBox(6);
      controlsVBox.setBackground(new Background(new BackgroundFill(Paint.valueOf("rgba(0,0,0,0.3)"), CornerRadii.EMPTY,
              Insets.EMPTY)));
      controlsVBox.setPadding(new Insets(10.0));
      controlsVBox.setMaxSize(180, 50);
      controlsVBox.getStyleClass().add("panel-region");
      // add checkboxes to control panel
      controlsVBox.getChildren().addAll(closedSublayerCheckbox,openSublayerCheckbox);

      // create a map view
      mapView = new MapView();

      // show current map scale in a label at the bottom of the screen
      Label currentMapScaleLabel = new Label();
      currentMapScaleLabel.setTextFill(Color.WHITE);
      mapView.addMapScaleChangedListener(mapScaleChangedEvent -> currentMapScaleLabel.setText("Scale: 1:"+Math.round(mapView.getMapScale())));

      // load the mobile map package
      MobileMapPackage mobileMapPackage = new MobileMapPackage("./samples-data/mmpk/GasDeviceAnno.mmpk");
      mobileMapPackage.loadAsync();
      mobileMapPackage.addDoneLoadingListener(()->{
        if (mobileMapPackage.getLoadStatus() == LoadStatus.LOADED){
          // set the mobile map package's map to the map view
          mapView.setMap(mobileMapPackage.getMaps().get(0));
          // find the annotation layer withing the map
          for (Layer layer : mapView.getMap().getOperationalLayers()) {
            if (layer instanceof AnnotationLayer){
              // load the annotation layer
              layer.loadAsync();
              layer.addDoneLoadingListener(()->{
                if (layer.getLoadStatus() == LoadStatus.LOADED) {
                  // get annotation sublayer name from sublayer content
                  AnnotationSublayer closedSublayer = (AnnotationSublayer) layer.getSubLayerContents().get(0);
                  AnnotationSublayer openSublayer = (AnnotationSublayer) layer.getSubLayerContents().get(1);

                  // set the layer name for the checkboxes
                  closedSublayerCheckbox.setText(buildLayerName(closedSublayer));
                  openSublayerCheckbox.setText(buildLayerName(openSublayer));

                  // toggle annotation sublayer visibility on check
                  closedSublayerCheckbox.setOnAction(event -> closedSublayer.setVisible(closedSublayerCheckbox.isSelected()));
                  openSublayerCheckbox.setOnAction(event -> openSublayer.setVisible(openSublayerCheckbox.isSelected()));

                  mapView.addMapScaleChangedListener(mapScaleChangedEvent->{
                    // if the "open" sublayer is visible, set the text color to white, otherwise set it to gray
                    if (openSublayer.isVisibleAtScale(mapView.getMapScale())){
                      openSublayerCheckbox.setTextFill(Color.WHITE);
                    } else {
                      openSublayerCheckbox.setTextFill(Color.DARKGRAY);
                    }
                  });

                } else {
                  new Alert(Alert.AlertType.ERROR, "Error loading Annotation Layer "+layer.getName()).show();
                }
              });
            }
          }

        } else {
          new Alert(Alert.AlertType.ERROR, "Mobile Map Package failed to load.").show();
        }
      });

      // add map view and label to stack pane
      stackPane.getChildren().addAll(mapView, controlsVBox, currentMapScaleLabel);
      StackPane.setAlignment(currentMapScaleLabel, Pos.BOTTOM_CENTER);
      StackPane.setMargin(currentMapScaleLabel, new Insets(0,0,20,0));
      StackPane.setAlignment(controlsVBox, Pos.TOP_LEFT);
      StackPane.setMargin(controlsVBox, new Insets(20,0,0,20));

    } catch (Exception e) {
      // on any error, print the stack trace
      e.printStackTrace();
    }
  }

  /**
   * Get name, and where relevant, append min and max scales of each annotation sublayer.
   *
   * @param annotationSublayer
   * @return the layer name with min max scales, where relevant
   */
  private String buildLayerName(AnnotationSublayer annotationSublayer){
    StringBuilder layerNameBuilder = new StringBuilder(annotationSublayer.getName());
    if (!Double.isNaN(annotationSublayer.getMaxScale()) && !Double.isNaN(annotationSublayer.getMinScale())){
      layerNameBuilder.append(" (1:").append((int) annotationSublayer.getMaxScale()).append(" -1:").append((int) annotationSublayer.getMinScale()).append(")");
    }
    return layerNameBuilder.toString();
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
