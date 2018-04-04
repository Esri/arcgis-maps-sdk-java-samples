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

package com.esri.samples.tiledlayers.vector_tiled_layer_url;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.layers.ArcGISVectorTiledLayer;
import com.esri.arcgisruntime.layers.Layer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.MapView;

public class VectorTiledLayerURLSample extends Application {

  private MapView mapView;

  @Override
  public void start(Stage stage) throws Exception {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);

      // set title, size, and add scene to stage
      stage.setTitle("Vector Tiled Layer URL Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // create a map view and set a map to it without a basemap
      mapView = new MapView();
      ArcGISMap map = new ArcGISMap();
      mapView.setMap(map);

      // set up a list view of vector tiled layers
      ListView<ArcGISVectorTiledLayer> layerList = new ListView<>();
      layerList.setMaxSize(250, 150);

      // show the layer's name in the list after it's done loading
      layerList.setCellFactory(comboBox -> new ListCell<ArcGISVectorTiledLayer>() {

        @Override
        protected void updateItem(ArcGISVectorTiledLayer layer, boolean empty) {
          super.updateItem(layer, empty);
          if (!empty) {
            layer.addDoneLoadingListener(() -> setText(layer.getItem().getTitle()));
          }
        }
      });

      // add some vector tiled layers to the list view
      layerList.getItems().addAll(
          new ArcGISVectorTiledLayer("http://www.arcgis.com/home/item.html?id=7675d44bb1e4428aa2c30a9b68f97822"),
          new ArcGISVectorTiledLayer("http://www.arcgis.com/home/item.html?id=4cf7e1fb9f254dcda9c8fbadb15cf0f8"),
          new ArcGISVectorTiledLayer("http://www.arcgis.com/home/item.html?id=dfb04de5f3144a80bc3f9f336228d24a"),
          new ArcGISVectorTiledLayer("http://www.arcgis.com/home/item.html?id=75f4dfdff19e445395653121a95a85db"),
          new ArcGISVectorTiledLayer("http://www.arcgis.com/home/item.html?id=86f556a2d1fd468181855a35e344567f")
      );

      // load all of the layers so their names can be shown in the list and so they appear as soon as they are selected
      layerList.getItems().forEach(Layer::loadAsync);

      // set the layer as the map's basemap when selected
      layerList.getSelectionModel().selectedIndexProperty().addListener(e -> {
        ArcGISVectorTiledLayer layer = layerList.getSelectionModel().getSelectedItem();
        map.setBasemap(new Basemap(layer));
      });

      // select the first layer on start
      layerList.getSelectionModel().selectFirst();

      // add the map view and control panel to stack pane
      stackPane.getChildren().addAll(mapView, layerList);
      StackPane.setAlignment(layerList, Pos.TOP_LEFT);
      StackPane.setMargin(layerList, new Insets(10, 0, 0, 10));

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
