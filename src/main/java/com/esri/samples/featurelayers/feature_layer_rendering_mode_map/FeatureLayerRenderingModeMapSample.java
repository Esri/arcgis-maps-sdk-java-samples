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

package com.esri.samples.featurelayers.feature_layer_rendering_mode_map;

import java.util.Arrays;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.MapView;

public class FeatureLayerRenderingModeMapSample extends Application {

  private MapView mapViewTop;
  private MapView mapViewBottom;

  @Override
  public void start(Stage stage) throws Exception {

    try {

      // create splitPane pane and JavaFX app scene
      SplitPane splitPane = new SplitPane();
      splitPane.setOrientation(Orientation.VERTICAL);
      Scene fxScene = new Scene(splitPane);

      // set title, size, and add JavaFX scene to stage
      stage.setTitle("Feature Layer Rendering Mode Map Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(fxScene);
      stage.show();

      // create a map (top) and set it to render all features in static rendering mode
      ArcGISMap mapTop = new ArcGISMap();
      mapTop.getLoadSettings().setPreferredPointFeatureRenderingMode(FeatureLayer.RenderingMode.STATIC);
      mapTop.getLoadSettings().setPreferredPolylineFeatureRenderingMode(FeatureLayer.RenderingMode.STATIC);
      mapTop.getLoadSettings().setPreferredPolygonFeatureRenderingMode(FeatureLayer.RenderingMode.STATIC);

      // create a map (bottom) and set it to render all features in dynamic rendering mode
      ArcGISMap mapBottom = new ArcGISMap();
      mapBottom.getLoadSettings().setPreferredPointFeatureRenderingMode(FeatureLayer.RenderingMode.DYNAMIC);
      mapBottom.getLoadSettings().setPreferredPolylineFeatureRenderingMode(FeatureLayer.RenderingMode.DYNAMIC);
      mapBottom.getLoadSettings().setPreferredPolygonFeatureRenderingMode(FeatureLayer.RenderingMode.DYNAMIC);

      // creating top map view
      mapViewTop = new MapView();
      mapViewTop.setMap(mapTop);
      splitPane.getItems().add(mapViewTop);
      // creating bottom map view
      mapViewBottom = new MapView();
      mapViewBottom.setMap(mapBottom);
      splitPane.getItems().add(mapViewBottom);

      // create service feature table using a point, polyline, and polygon service
      ServiceFeatureTable poinServiceFeatureTable = new ServiceFeatureTable("http://sampleserver6.arcgisonline.com/arcgis/rest/services/Energy/Geology/FeatureServer/0");
      ServiceFeatureTable polylineServiceFeatureTable = new ServiceFeatureTable("http://sampleserver6.arcgisonline.com/arcgis/rest/services/Energy/Geology/FeatureServer/8");
      ServiceFeatureTable polygonServiceFeatureTable = new ServiceFeatureTable("http://sampleserver6.arcgisonline.com/arcgis/rest/services/Energy/Geology/FeatureServer/9");

      // create feature layer from service feature tables
      FeatureLayer pointFeatureLayer = new FeatureLayer(poinServiceFeatureTable);
      FeatureLayer polylineFeatureLayer = new FeatureLayer(polylineServiceFeatureTable);
      FeatureLayer polygonFeatureLayer = new FeatureLayer(polygonServiceFeatureTable);

      // add each layer to top and bottom map
      mapTop.getOperationalLayers().addAll(Arrays.asList(pointFeatureLayer, polylineFeatureLayer, polygonFeatureLayer));
      mapBottom.getOperationalLayers().addAll(Arrays.asList(pointFeatureLayer.copy(), polylineFeatureLayer.copy(), polygonFeatureLayer.copy()));

      // viewpoint locations for map view to zoom in and out to
      Viewpoint zoomOutPoint = new Viewpoint(new Point(-118.37, 34.46, SpatialReferences.getWgs84()), 650000, 0);
      Viewpoint zoomInPoint = new Viewpoint(new Point(-118.45, 34.395, SpatialReferences.getWgs84()), 50000, 90);
      mapViewTop.setViewpoint(zoomOutPoint);
      mapViewBottom.setViewpoint(zoomOutPoint);

      //loop an animation into and out from the zoom in point (5 seconds each) with a 2 second gap between zooming
      Timeline timeline = new Timeline();
      timeline.setCycleCount(Animation.INDEFINITE);
      timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(7), event -> zoomTo(zoomInPoint, 5)));
      timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(14), event -> zoomTo(zoomOutPoint, 5)));
      timeline.play();

    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
    }
  }

  /**
   * Sets both MapViews to a Viewpoint over a number of seconds.
   *
   * @param viewpoint to which both MapViews should be set.
   * @param seconds over which the viewpoint is asynchronously set.
   */
  private void zoomTo(Viewpoint viewpoint, int seconds) {
    mapViewTop.setViewpointAsync(viewpoint, seconds);
    mapViewBottom.setViewpointAsync(viewpoint, seconds);
  }

  /**
   * Stops and releases all resources used in application.
   */
  @Override
  public void stop() {

    if (mapViewTop != null) {
      mapViewTop.dispose();
    }
    if (mapViewBottom != null) {
      mapViewBottom.dispose();
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
