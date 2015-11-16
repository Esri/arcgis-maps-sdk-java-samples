/*
 * Copyright 2015 Esri.
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

package com.esri.sampleviewer.samples.map;

import java.util.ArrayList;

import com.esri.arcgisruntime.layers.ArcGISMapImageLayer;
import com.esri.arcgisruntime.layers.Layer;
import com.esri.arcgisruntime.mapping.BasemapType;
import com.esri.arcgisruntime.mapping.LayerList;
import com.esri.arcgisruntime.mapping.Map;
import com.esri.arcgisruntime.mapping.view.MapView;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * This sample demonstrates how to add, remove, and re-order operational layers
 * of a Map.
 * <h4>How it Works</h4>
 * 
 * A {@link LayerList} is returned from the {@link Map} using the
 * {@link Map#getOperationalLayers} method, which manages all layers that are
 * assigned to the Map. Next some {@link ArcGISMapImageLayer}s are added to that
 * LayerList. Layers can be add/remove from the Map by add/removing them from
 * the LayerList. The last Layer added to the list will be the Layer that is on
 * top.
 */
public class ManageOperationalLayers extends Application {

  private MapView mapView;
  private LayerList mapAddedLayers;

  private static final String ELEVATION_LAYER =
      "http://sampleserver5.arcgisonline.com/arcgis/rest/services/Elevation/WorldElevations/MapServer";
  private static final String CENSUS_LAYER =
      "http://sampleserver5.arcgisonline.com/arcgis/rest/services/Census/MapServer";
  private static final String DAMAGE_LAYER =
      "http://sampleserver5.arcgisonline.com/arcgis/rest/services/DamageAssessment/MapServer";

  @Override
  public void start(Stage stage) throws Exception {

    // create stack pane and application scene
    StackPane stackPane = new StackPane();
    Scene scene = new Scene(stackPane);
    scene.getStylesheets().add(getClass()
        .getResource("../resources/SamplesTheme.css").toExternalForm());

    // set title, size, and add scene to stage
    stage.setTitle("Manage Operation Layer Sample");
    stage.setWidth(700);
    stage.setHeight(800);
    stage.setScene(scene);
    stage.show();

    // create a control panel
    VBox vBoxControl = new VBox(6);
    vBoxControl.setMaxSize(250, 520);
    vBoxControl.getStyleClass().add("panel-region");

    // create sample description
    Label descriptionLabel = new Label("Sample Description:");
    descriptionLabel.getStyleClass().add("panel-label");
    TextArea description = new TextArea(
        "This sample shows how to add, remove, and re-order operational layers. "
            + "Click on a layer from the 'Layers on the map' list to place that "
            + "layer on top. Right click the layer to remove it from the Map. "
            + "Right clicking a layer in the 'Deleted Layers' list will re-add "
            + "the layer to the Map.");
    description.setMinHeight(220);
    description.setWrapText(true);
    description.autosize();
    description.setEditable(false);

    // create labels for add/delete layers
    Label addLayersLabel = new Label("Layers on the map");
    Label deleteLayersLabel = new Label("Deleted Layers");
    addLayersLabel.getStyleClass().add("panel-label");
    deleteLayersLabel.getStyleClass().add("panel-label");

    // create list for deleted layers
    ArrayList<Layer> deletedLayers = new ArrayList<>();

    // create a list view for names of layers added/deleted
    ListView<String> addedLayerNames = new ListView<>();
    ListView<String> deletedLayerNames = new ListView<>();

    addedLayerNames.setOnMouseClicked(e -> {
      // index of selected item
      int selectedIndex =
          addedLayerNames.getSelectionModel().getSelectedIndex();
      if (e.getButton() == MouseButton.PRIMARY) {

        // store selected layer
        ArcGISMapImageLayer temp =
            (ArcGISMapImageLayer) mapAddedLayers.get(selectedIndex);
        // remove selected layer
        mapAddedLayers.remove(selectedIndex);
        // add selected layer to front
        mapAddedLayers.add(temp);

        // make names match added layers list
        String temp2 = addedLayerNames.getItems().get(selectedIndex);
        addedLayerNames.getItems().remove(selectedIndex);
        addedLayerNames.getItems().add(temp2);
      } else if (e.getButton() == MouseButton.SECONDARY) {

        // append added layer to deleted list
        deletedLayers.add(mapAddedLayers.get(selectedIndex));
        // remove layer from added list
        mapAddedLayers.remove(selectedIndex);

        // make names match corresponding list
        deletedLayerNames.getItems()
            .add(addedLayerNames.getSelectionModel().getSelectedItem());
        addedLayerNames.getItems().remove(selectedIndex);
      }
    });

    deletedLayerNames.setOnMouseClicked(e -> {
      // index of selected item
      int selectedIndex =
          deletedLayerNames.getSelectionModel().getSelectedIndex();
      if (e.getButton() == MouseButton.SECONDARY) {

        // append deleted layer to added list
        mapAddedLayers.add(deletedLayers.get(selectedIndex));
        // remove delete layer from deleted list
        deletedLayers.remove(selectedIndex);

        // make names match corresponding list
        addedLayerNames.getItems()
            .add(deletedLayerNames.getSelectionModel().getSelectedItem());
        deletedLayerNames.getItems().remove(selectedIndex);
      }
    });

    // add labels, sample description and lists to the control panel
    vBoxControl.getChildren().addAll(descriptionLabel, description,
        addLayersLabel, addedLayerNames, deleteLayersLabel, deletedLayerNames);
    try {

      // create a map with the basemap Topographic
      final Map map =
          new Map(BasemapType.TOPOGRAPHIC, 34.056295, -117.195800, 14);

      // get the operational layers list from the map
      mapAddedLayers = map.getOperationalLayers();

      // create a view and added map to it
      mapView = new MapView();
      mapView.setMap(map);

      // create the elevation, census, and damage default layers
      ArcGISMapImageLayer imageLayerElevation =
          new ArcGISMapImageLayer(ELEVATION_LAYER);
      ArcGISMapImageLayer imageLayerCensus =
          new ArcGISMapImageLayer(CENSUS_LAYER);
      ArcGISMapImageLayer imageLayerDamage =
          new ArcGISMapImageLayer(DAMAGE_LAYER);

      // add default map image layers to the map
      mapAddedLayers.add(imageLayerElevation);
      mapAddedLayers.add(imageLayerCensus);
      mapAddedLayers.add(imageLayerDamage);

      // add the default names to added list
      addedLayerNames.getItems().add(mapAddedLayers.get(0).getName());
      addedLayerNames.getItems().add(mapAddedLayers.get(1).getName());
      addedLayerNames.getItems().add(mapAddedLayers.get(2).getName());

      // add the map view and control box to stack pane
      stackPane.getChildren().addAll(mapView, vBoxControl);
      StackPane.setAlignment(vBoxControl, Pos.TOP_LEFT);
      StackPane.setMargin(vBoxControl, new Insets(10, 0, 0, 10));
    } catch (Exception e) {
      // on any error, display the stack trace
      e.printStackTrace();
    }
  }

  /**
   * Stops and releases all resources used in application.
   *
   * @throws Exception if security manager doesn't allow JVM to exit with
   *           current status
   */
  @Override
  public void stop() throws Exception {

    if (mapView != null) {
      mapView.dispose();
    }
    Platform.exit();
    System.exit(0);
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
