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

package com.esri.samples.map.manage_operational_layers;

import java.util.ArrayList;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import com.esri.arcgisruntime.layers.ArcGISMapImageLayer;
import com.esri.arcgisruntime.layers.Layer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.LayerList;
import com.esri.arcgisruntime.mapping.view.MapView;

public class ManageOperationalLayersSample extends Application {

  private MapView mapView;
  private LayerList mapAddedLayers;

  private static final String ELEVATION_LAYER =
      "http://sampleserver5.arcgisonline.com/arcgis/rest/services/Elevation/WorldElevations/MapServer";
  private static final String CENSUS_LAYER =
      "http://sampleserver5.arcgisonline.com/arcgis/rest/services/Census/MapServer";
  private static final String DAMAGE_LAYER =
      "http://sampleserver5.arcgisonline.com/arcgis/rest/services/DamageAssessment/MapServer";

  @Override
  public void start(Stage stage) {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);
      scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

      // set title, size, and add scene to stage
      stage.setTitle("Manage Operational Layers Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // create a control panel
      VBox controlsVBox = new VBox(6);
      controlsVBox.setBackground(new Background(new BackgroundFill(Paint.valueOf("rgba(0,0,0,0.3)"), CornerRadii.EMPTY,
          Insets.EMPTY)));
      controlsVBox.setPadding(new Insets(10.0));
      controlsVBox.setMaxSize(200, 260);
      controlsVBox.getStyleClass().add("panel-region");

      // create labels for add/delete layers
      Label addLayersLabel = new Label("Layers on the ArcGISMap");
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
        int selectedIndex = addedLayerNames.getSelectionModel().getSelectedIndex();
        if (e.getButton() == MouseButton.PRIMARY) {

          // store selected layer
          ArcGISMapImageLayer temp = (ArcGISMapImageLayer) mapAddedLayers.get(selectedIndex);
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
          deletedLayerNames.getItems().add(addedLayerNames.getSelectionModel().getSelectedItem());
          addedLayerNames.getItems().remove(selectedIndex);
        }
      });

      deletedLayerNames.setOnMouseClicked(e -> {
        // index of selected item
        int selectedIndex = deletedLayerNames.getSelectionModel().getSelectedIndex();
        if (e.getButton() == MouseButton.SECONDARY) {

          // append deleted layer to added list
          mapAddedLayers.add(deletedLayers.get(selectedIndex));
          // remove delete layer from deleted list
          deletedLayers.remove(selectedIndex);

          // make names match corresponding list
          addedLayerNames.getItems().add(deletedLayerNames.getSelectionModel().getSelectedItem());
          deletedLayerNames.getItems().remove(selectedIndex);
        }
      });

      // add labels and lists to the control panel
      controlsVBox.getChildren().addAll(addLayersLabel, addedLayerNames, deleteLayersLabel, deletedLayerNames);

      // create a ArcGISMap with the basemap Topographic
      final ArcGISMap map = new ArcGISMap(Basemap.Type.TOPOGRAPHIC, 34.056295, -117.195800, 14);

      // get the operational layers list from the ArcGISMap
      mapAddedLayers = map.getOperationalLayers();

      // create a view and added ArcGISMap to it
      mapView = new MapView();
      mapView.setMap(map);

      // create the elevation, census, and damage default layers
      ArcGISMapImageLayer imageLayerElevation = new ArcGISMapImageLayer(ELEVATION_LAYER);
      ArcGISMapImageLayer imageLayerCensus = new ArcGISMapImageLayer(CENSUS_LAYER);
      ArcGISMapImageLayer imageLayerDamage = new ArcGISMapImageLayer(DAMAGE_LAYER);

      // add default ArcGISMap image layers to the ArcGISMap
      mapAddedLayers.add(imageLayerElevation);
      mapAddedLayers.add(imageLayerCensus);
      mapAddedLayers.add(imageLayerDamage);

      // add the default names to added list
      addedLayerNames.getItems().add(mapAddedLayers.get(0).getName());
      addedLayerNames.getItems().add(mapAddedLayers.get(1).getName());
      addedLayerNames.getItems().add(mapAddedLayers.get(2).getName());

      // add the map view and control box to stack pane
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
