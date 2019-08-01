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
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import com.esri.arcgisruntime.geometry.Point;
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

      // set title, size, and add scene to stage
      stage.setTitle("Control Annotation Sublayer Visibility");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // create ArcGISMap with imagery basemap
      ArcGISMap map = new ArcGISMap(Basemap.createStreets());

      // create a map view
      mapView = new MapView();

      // show current map scale in a label at the bottom of the screen
      Label currentMapScaleLabel = new Label();
      mapView.addMapScaleChangedListener(mapScaleChangedEvent -> currentMapScaleLabel.setText("Scale: 1:"+Math.round(mapView.getMapScale())));

      // load the mobile map package
      MobileMapPackage mobileMapPackage = new MobileMapPackage("./samples-data/mmpk/GasDeviceAnno.mmpk");
      mobileMapPackage.loadAsync();
      mobileMapPackage.addDoneLoadingListener(()->{
        if (mobileMapPackage.getLoadStatus() == LoadStatus.LOADED){
          // set the mobile map package's map to the map view
          mapView.setMap(mobileMapPackage.getMaps().get(0));

        } else {
          new Alert(Alert.AlertType.ERROR, "Mobile Map Package failed to load.").show();
        }
      });

      // add map view and label to stack pane
      stackPane.getChildren().addAll(mapView, currentMapScaleLabel);
      StackPane.setAlignment(currentMapScaleLabel, Pos.BOTTOM_CENTER);
      StackPane.setMargin(currentMapScaleLabel, new Insets(0,0,20,0));

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
