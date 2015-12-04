/* Copyright 2015 Esri.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
limitations under the License.  */

package com.esri.sampleviewer.samples.imagelayers;

import org.controlsfx.control.CheckComboBox;

import com.esri.arcgisruntime.layers.ArcGISMapImageLayer;
import com.esri.arcgisruntime.layers.SubLayerList;
import com.esri.arcgisruntime.mapping.BasemapType;
import com.esri.arcgisruntime.mapping.Map;
import com.esri.arcgisruntime.mapping.view.MapView;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * The <b>Change Sub Layer Visibility</b> sample demonstrates how to add
 * multiple layers to your <@Map> using a <@BasemapType> and an
 * <@ArcGISMapImageLayer> which as multiple sub-layers. The application allows
 * you to turn on or off the sub-layers from the <@ArcGISMapImageLayer>.
 * <p>
 * You gain access to the sub-layers from the
 * <@ArcGISMapImageLayer.getSubLayers()> method which returns a <@SubLayerList>.
 * The <@SubLayerList> is a modifiable list of <@ArcGISSubLayers> which gives
 * you access to determine if the layer is visible or not and to turn on or off
 * the layers visibility.
 * </p>
 */
public class MapImageLayerSublayerVisibility extends Application {

  private MapView mapView;
  private Map map;
  private SubLayerList layers;

  private CheckComboBox<String> checkComboBox;

  // World Topo Map Service URL
  private static final String WORLD_CITIES_SERVICE =
      "http://sampleserver6.arcgisonline.com/arcgis/rest/services/SampleWorldCities/MapServer";

  @Override
  public void start(Stage stage) throws Exception {

    try {
      // create a border pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);

      // size the stage and add a title
      stage.setTitle("Change Sub-Layer Visibility");
      stage.setWidth(700);
      stage.setHeight(800);
      stage.setScene(scene);
      stage.show();

      // create the layers data to show in the CheckComboBox
      ObservableList<String> layersList = FXCollections.observableArrayList();
      layersList.add("Cities");
      layersList.add("Continents");
      layersList.add("World");

      // create the CheckComboBox with the data
      checkComboBox = new CheckComboBox<>(layersList);

      // set all sub layers on by default
      checkComboBox.getCheckModel().checkAll();
      checkComboBox.setPrefSize(200, 20);
      checkComboBox.setPadding(new Insets(20, 0, 0, 10));

      // handle sub layer selection
      checkComboBox.getCheckModel().getCheckedItems()
          .addListener((ListChangeListener<String>) c -> {
            layersList.forEach(this::toggleVisibility);
          });

      // create a map with the a BasemapTyppe Topographic
      map = new Map(BasemapType.TOPOGRAPHIC, 48.354406, -99.998267, 2);

      // create a Image Layer with dynamically generated map images
      ArcGISMapImageLayer imageLayer = new ArcGISMapImageLayer(
          WORLD_CITIES_SERVICE);
      imageLayer.setOpacity(0.7f);

      // add world cities layers as map operational layer
      map.getOperationalLayers().add(imageLayer);

      // set the map to be displayed in this view
      mapView = new MapView();
      mapView.setMap(map);

      // get the layers from the map image layer
      layers = imageLayer.getSubLayers();

      // add the MapView and sublayers menu
      stackPane.getChildren().addAll(mapView, checkComboBox);
      StackPane.setAlignment(checkComboBox, Pos.TOP_LEFT);

    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
    }
  }

  @Override
  public void stop() throws Exception {

    // releases resources when the application closes
    if (mapView != null) {
      mapView.dispose();
    }
    if (map != null) {
      map.dispose();
    }
    Platform.exit();
    System.exit(0);
  }

  /**
   * Starting point of this application.
   * 
   * @args arguments to this application.
   */
  public static void main(String[] args) {

    Application.launch(args);
  }

  /**
   * Toggle sub layer visibility.
   * 
   * @sublayer sub layer to be toggled
   */
  private void toggleVisibility(String sublayer) {

    int indexSublayer = checkComboBox.getCheckModel().getItemIndex(sublayer);
    // check if the sub layer is selected in the checkComboBox
    if (checkComboBox.getCheckModel().getCheckedItems().contains(sublayer)) {
      // sub layer is off, turn it on
      if (!layers.get(indexSublayer).isVisible()) {
        layers.get(indexSublayer).setVisible(true);
      }
    } else {
      // sub layer is on, turn it off
      if (layers.get(indexSublayer).isVisible()) {
        layers.get(indexSublayer).setVisible(false);
      }
    }
  }
}
