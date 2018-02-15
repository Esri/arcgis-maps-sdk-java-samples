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

package com.esri.samples.ogc.wms_layer_url;

import java.util.Collections;
import java.util.List;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.layers.WmsLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.MapView;

public class WmsLayerUrlSample extends Application {

  private MapView mapView;

  @Override
  public void start(Stage stage) throws Exception {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);

      // set title, size, and add scene to stage
      stage.setTitle("WMS Layer URL Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // create a map and add it to the map view
      ArcGISMap map = new ArcGISMap(Basemap.createImagery());
      mapView = new MapView();
      mapView.setMap(map);

      // create a WMS layer
      List<String> wmsLayerNames = Collections.singletonList("0");
      String url = "https://certmapper.cr.usgs.gov/arcgis/services/geology/africa/MapServer/WMSServer?request=GetCapabilities&service=WMS";
      WmsLayer wmsLayer = new WmsLayer(url, wmsLayerNames);
      // load the layer and add it as an operational layer
      wmsLayer.addDoneLoadingListener(() -> {
        if (wmsLayer.getLoadStatus() == LoadStatus.LOADED) {
          map.getOperationalLayers().add(wmsLayer);
          mapView.setViewpoint(new Viewpoint(wmsLayer.getFullExtent()));
        } else {
          Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to load WMS layer");
          alert.show();
        }
      });
      wmsLayer.loadAsync();

      // add the map view to stack pane
      stackPane.getChildren().addAll(mapView);
    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
    }
  }

  /**
   * Stops and releases all resources used in application.
   */
  @Override
  public void stop() throws Exception {

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
