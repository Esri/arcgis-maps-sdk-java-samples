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

package com.esri.samples.web_tiled_layer;

import java.util.Arrays;
import java.util.List;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.layers.WebTiledLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.MapView;

public class WebTiledLayerSample extends Application {

  private MapView mapView;
  private WebTiledLayer webTiledLayer;

  @Override
  public void start(Stage stage) {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);

      // set title, size, and add scene to stage
      stage.setTitle("Web Tiled Layer Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // create a map view and set a map to it without a basemap
      mapView = new MapView();
      final ArcGISMap map = new ArcGISMap();
      mapView.setMap(map);

      // create a list of subdomains and template URI
      List<String> subDomains = Arrays.asList("a", "b", "c", "d");
      String templateURI = "https://server.arcgisonline.com/arcgis/rest/services/Ocean/World_Ocean_Base/MapServer/tile/{level}/{row}/{col}.jpg";

      // create a web tiled layer
      webTiledLayer = new WebTiledLayer(templateURI, subDomains);
      webTiledLayer.loadAsync();
      webTiledLayer.addDoneLoadingListener(() -> {
        if(webTiledLayer.getLoadStatus() == LoadStatus.LOADED){
          // set the web tiled layer as the map's basemap
          map.setBasemap(new Basemap(webTiledLayer));
          // set custom attribution on the layer
          webTiledLayer.setAttribution("Map tiles by ArcGIS Living Atlas of the World" +
              ", under the Esri Master License Agreement." +
              "Data by Esri, Garmin, GEBCO, NOAA NGDC, and other contributors.");
        } else {
          new Alert(Alert.AlertType.ERROR, webTiledLayer.getLoadError().getMessage()).show();
        }
      });

      // add the map view and control panel to stack pane
      stackPane.getChildren().addAll(mapView);
    } catch (Exception e) {
      // on any error, display stack trace
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
