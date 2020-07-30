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
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.ArcGISMapImageLayer;
import com.esri.arcgisruntime.layers.ArcGISTiledLayer;
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

  private static final int MIN_SCALE = 40000000;

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

      // create a map with the topographic basemap
      ArcGISMap map = new ArcGISMap(Basemap.createTopographic());

      // create a map view and set the ArcGISMap to it
      mapView = new MapView();
      mapView.setMap(map);

      // set viewpoint
      mapView.setViewpoint(new Viewpoint(new Point(-11e6, 45e5, SpatialReferences.getWebMercator()), MIN_SCALE));

      // create a control panel
      VBox controlsVBox = new VBox(6);
      controlsVBox.setBackground(new Background(new BackgroundFill(Paint.valueOf("rgba(0,0,0,0.3)"), CornerRadii.EMPTY,
          Insets.EMPTY)));
      controlsVBox.setPadding(new Insets(10.0));
      controlsVBox.setMaxSize(220, 90);
      controlsVBox.getStyleClass().add("panel-region");

      // create labels to display the view status of each layer
      Label currentStatusLabel = new Label("Current view Status: ");

      currentStatusLabel.getStyleClass().add("panel-label");

      // add labels to the control panel
      controlsVBox.getChildren().add(currentStatusLabel);

      // creating a layer from a portal item
      final PortalItem portalItem = new PortalItem(new Portal("https://runtime.maps.arcgis.com/"),
        "b8f4033069f141729ffb298b7418b653");
      final FeatureLayer featureLayer = new FeatureLayer(portalItem, 0);

      featureLayer.setMinScale(400_000_000.0);
      featureLayer.setMaxScale(400_000_000.0 / 10);
      // add the layer on the map to load it
      map.getOperationalLayers().add(featureLayer);

//      // fires every time a layers' view status has changed
//      mapView.addLayerViewStateChangedListener(e -> {
//        // holds the label that needs to be changed
//        Layer layer = e.getLayer();
//
//        String viewStatus = e.getLayerViewStatus().iterator().next().toString();
//        final int layerIndex = map.getOperationalLayers().indexOf(layer);
//
//        // finding and updating label that needs to be changed
//        switch (layerIndex) {
//          case TILED_LAYER:
//            worldTimeZonesLabel.setText("World Time Zones: " + viewStatus);
//            break;
//          case IMAGE_LAYER:
//            censusLabel.setText("Census: " + viewStatus);
//            break;
//          case FEATURE_LAYER:
//            facilitiesLabel.setText("Facilities: " + viewStatus);
//        }
//      });

      // add the map view and control panel to stack pane
      stackPane.getChildren().addAll(mapView, controlsVBox);
      StackPane.setAlignment(controlsVBox, Pos.TOP_LEFT);
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
