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

package com.esri.samples.display_layer_view_state;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.layers.Layer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.portal.Portal;
import com.esri.arcgisruntime.portal.PortalItem;

public class DisplayLayerViewStateSample extends Application {

  private MapView mapView;
  private FeatureLayer featureLayer;
  private Label currentStatusLabel;
  private Button loadButton;
  private Button hideButton;

  @Override
  public void start(Stage stage) {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);
      scene.getStylesheets().add(getClass().getResource("/display_layer_view_state/style.css").toExternalForm());

      // set title, size, and add scene to stage
      stage.setTitle("Display Layer View State Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // create an ArcGISMap with the topographic basemap
      ArcGISMap map = new ArcGISMap(Basemap.createTopographic());

      // create a map view and set the ArcGISMap to it
      mapView = new MapView();
      mapView.setMap(map);

      // set the initial viewpoint for the map view
      mapView.setViewpoint(new Viewpoint(new Point(-11e6, 45e5, SpatialReferences.getWebMercator()), 40000000));

      // create a control panel
      VBox controlsVBox = new VBox(6);
      controlsVBox.setBackground(new Background(new BackgroundFill(Paint.valueOf("rgba(0,0,0,0.3)"), CornerRadii.EMPTY,
          Insets.EMPTY)));
      controlsVBox.setPadding(new Insets(10.0));
      controlsVBox.setMaxSize(350, 40);
      controlsVBox.getStyleClass().add("panel-region");

      // create a label to display the view status of the layer
      currentStatusLabel = new Label("Current view Status: ");
      currentStatusLabel.getStyleClass().add("panel-label");

      // add the label to the control panel
      controlsVBox.getChildren().add(currentStatusLabel);

      // create a listener that fires every time a layers' view status has changed
      mapView.addLayerViewStateChangedListener(statusChangeEvent -> {

        // get the layer which state has changed
        Layer layer = statusChangeEvent.getLayer();
        // only update if the layer is the feature layer we're tracking
        if (layer != featureLayer) {
          return ;
        }

        // get the layer view status
        String layerViewStatus = statusChangeEvent.getLayerViewStatus().iterator().next().toString();

        //////// ********** print out layer view for testing
        System.out.println(layerViewStatus);

        // update the status label to display the layer view status
        currentStatusLabel.setText("Current view status: " + layerViewStatus);

      });

      VBox buttonsVBox = new VBox(6);
      buttonsVBox.setBackground(new Background(new BackgroundFill(Paint.valueOf("rgba(0,0,0,0.3)"), CornerRadii.EMPTY,
        Insets.EMPTY)));
      buttonsVBox.setPadding(new Insets(10.0));
      buttonsVBox.setMaxSize(150, 20);
      buttonsVBox.getStyleClass().add("panel-region");
      loadButton = new Button("Show Layer");
      loadButton.setMaxWidth(130);
      hideButton = new Button("Hide Layer");
      hideButton.setMaxWidth(130);
      buttonsVBox.getChildren().add(loadButton);

      loadButton.setOnAction(event -> {

        if (featureLayer != null && !featureLayer.isVisible()){
          featureLayer.setVisible(true);
        }
        else {
          // create a feature layer from a portal item
          final PortalItem portalItem = new PortalItem(new Portal("https://runtime.maps.arcgis.com/"),
            "b8f4033069f141729ffb298b7418b653");
          featureLayer = new FeatureLayer(portalItem, 0);

          //  set a minimum and maximum scale for the visibility of the feature layer
          featureLayer.setMinScale(40000000);
          featureLayer.setMaxScale(40000000 / 10);

          // add the feature layer to the map to load it
          map.getOperationalLayers().add(featureLayer);
        }

          // hide the load button
          buttonsVBox.getChildren().clear();
          buttonsVBox.getChildren().add(hideButton);
      });

      hideButton.setOnAction(event -> {
        if (featureLayer == null) return;

        if (featureLayer.isVisible()) {
          featureLayer.setVisible(false);
          buttonsVBox.getChildren().clear();
          buttonsVBox.getChildren().add(loadButton);
        }
        else {
          featureLayer.setVisible(true);
          buttonsVBox.getChildren().clear();
          buttonsVBox.getChildren().add(hideButton);
        }
      });

      // add the map view and control panel to the stack pane
      stackPane.getChildren().addAll(mapView, controlsVBox, buttonsVBox);
      StackPane.setAlignment(controlsVBox, Pos.TOP_CENTER);
      StackPane.setAlignment(buttonsVBox, Pos.TOP_LEFT);
      StackPane.setMargin(buttonsVBox, new Insets(10, 0, 0, 10));
      StackPane.setMargin(controlsVBox, new Insets(10, 0, 0, 10));
    } catch (Exception e) {
      // on any error, display the stack trace
      e.printStackTrace();
    }
  }

  /**
   * Stops and releases all resources used in application.
   */
  @Override
  public void stop() {

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
