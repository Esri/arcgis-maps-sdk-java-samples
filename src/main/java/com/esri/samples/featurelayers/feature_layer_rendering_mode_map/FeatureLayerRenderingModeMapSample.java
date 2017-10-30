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

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
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

      // create border pane and JavaFX app scene
      BorderPane borderPane = new BorderPane();
      Scene fxScene = new Scene(borderPane);

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
      mapViewTop.setMinHeight(stage.getHeight() / 2);
      mapViewTop.setMap(mapTop);
      borderPane.setTop(mapViewTop);
      // creating bottom map view
      mapViewBottom = new MapView();
      mapViewTop.setMinHeight(stage.getHeight() / 2);
      mapViewBottom.setMap(mapBottom);
      borderPane.setCenter(mapViewBottom);

      // create service feature table using a point, polyline, and polygon service
      ServiceFeatureTable poinServiceFeatureTable = new ServiceFeatureTable("http://sampleserver6.arcgisonline.com/arcgis/rest/services/Energy/Geology/FeatureServer/0");
      ServiceFeatureTable polylineServiceFeatureTable = new ServiceFeatureTable("http://sampleserver6.arcgisonline.com/arcgis/rest/services/Energy/Geology/FeatureServer/8");
      ServiceFeatureTable polygonServiceFeatureTable = new ServiceFeatureTable("http://sampleserver6.arcgisonline.com/arcgis/rest/services/Energy/Geology/FeatureServer/9");

      // create feature layer from service feature tables
      FeatureLayer pointFeatureLayer = new FeatureLayer(poinServiceFeatureTable);
      FeatureLayer polylineFeatureLayer = new FeatureLayer(polylineServiceFeatureTable);
      FeatureLayer polygonFeatureLayer = new FeatureLayer(polygonServiceFeatureTable);

      // add each layer to top and bottom scene
      mapTop.getOperationalLayers().add(pointFeatureLayer);
      mapTop.getOperationalLayers().add(polylineFeatureLayer);
      mapTop.getOperationalLayers().add(polygonFeatureLayer);
      mapBottom.getOperationalLayers().add(pointFeatureLayer.copy());
      mapBottom.getOperationalLayers().add(polylineFeatureLayer.copy());
      mapBottom.getOperationalLayers().add(polygonFeatureLayer.copy());

      // viewpoint locations for map view to zoom in and out to
      Viewpoint zoomOutPoint = new Viewpoint(new Point(-118.37, 34.46, SpatialReferences.getWgs84()), 650000, 0);
      Viewpoint zoomInPoint = new Viewpoint(new Point(-118.45, 34.395, SpatialReferences.getWgs84()), 50000, 90);
      mapViewTop.setViewpoint(zoomOutPoint);
      mapViewBottom.setViewpoint(zoomOutPoint);

      Button zoomButton = new Button("Start Zoom");
      zoomButton.setOnAction(e -> {
        zoomButton.setDisable(true);
        // zoom in for five seconds
        zoomTo(zoomInPoint, 5).addDoneListener(() -> {
          // wait for three seconds before returning
          zoomTo(zoomInPoint, 3).addDoneListener(() -> {
            // zoom out for five seconds
            zoomTo(zoomOutPoint, 5).addDoneListener(() -> {
              zoomButton.setDisable(false);
            });
          });
        });
      });
      zoomButton.setMaxWidth(Double.MAX_VALUE);
      borderPane.setBottom(zoomButton);

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
   *
   * @return a ListenableFuture representing the result of the Viewpoint change.
   */
  private ListenableFuture<Boolean> zoomTo(Viewpoint viewpoint, int seconds) {
    ListenableFuture<Boolean> setViewpointFuture = mapViewTop.setViewpointAsync(viewpoint, seconds);
    mapViewBottom.setViewpointAsync(viewpoint, seconds);
    return setViewpointFuture;
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
