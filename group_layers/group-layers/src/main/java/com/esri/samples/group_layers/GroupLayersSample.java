/*
 * Copyright 2019 Esri.
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

package com.esri.samples.group_layers;

import java.util.Arrays;
import java.util.List;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.layers.ArcGISSceneLayer;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.layers.GroupLayer;
import com.esri.arcgisruntime.layers.Layer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.ArcGISTiledElevationSource;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Surface;
import com.esri.arcgisruntime.mapping.view.Camera;
import com.esri.arcgisruntime.mapping.view.SceneView;

public class GroupLayersSample extends Application {

  private SceneView sceneView;

  @Override
  public void start(Stage stage) {

    try {

      // set the title and size of the stage and show it
      StackPane stackPane = new StackPane();
      Scene fxScene = new Scene(stackPane);
      stage.setTitle("Group Layers Sample");
      stage.setWidth(800);
      stage.setHeight(700);

      // create a JavaFX scene with a stackpane and set it to the stage
      stage.setScene(fxScene);
      stage.show();

      // create a scene view and add it to the stack pane
      sceneView = new SceneView();
      stackPane.getChildren().add(sceneView);

      // create a scene with a basemap and add it to the scene view
      ArcGISScene scene = new ArcGISScene();
      scene.setBasemap(Basemap.createImagery());
      sceneView.setArcGISScene(scene);

      // set the base surface with world elevation
      Surface surface = new Surface();
      surface.getElevationSources().add(new ArcGISTiledElevationSource("https://elevation3d.arcgis.com/arcgis/rest/services/WorldElevation3D/Terrain3D/ImageServer"));
      scene.setBaseSurface(surface);

      // create different types of layers
      ArcGISSceneLayer devABuildings = new ArcGISSceneLayer("https://tiles.arcgis.com/tiles/P3ePLMYs2RVChkJx/arcgis/rest/services/DevA_BuildingShells/SceneServer");
      ArcGISSceneLayer devBBuildings = new ArcGISSceneLayer("https://tiles.arcgis.com/tiles/P3ePLMYs2RVChkJx/arcgis/rest/services/DevB_BuildingShells/SceneServer");
      ArcGISSceneLayer devATrees = new ArcGISSceneLayer("https://tiles.arcgis.com/tiles/P3ePLMYs2RVChkJx/arcgis/rest/services/DevA_Trees/SceneServer");
      FeatureLayer devAPathways = new FeatureLayer(new ServiceFeatureTable(" https://services.arcgis.com/P3ePLMYs2RVChkJx/arcgis/rest/services/DevA_Pathways/FeatureServer/1"));
      FeatureLayer devProjectArea = new FeatureLayer(new ServiceFeatureTable("https://services.arcgis.com/P3ePLMYs2RVChkJx/arcgis/rest/services/DevelopmentProjectArea/FeatureServer/0"));

      // create a group layer from scratch by adding the layers as children
      GroupLayer groupLayer = new GroupLayer();
      groupLayer.setName("Group: Dev A");
      groupLayer.getLayers().addAll(Arrays.asList(devATrees, devAPathways, devABuildings));

      // add the group layer and other layers to the scene as operational layers
      scene.getOperationalLayers().addAll(Arrays.asList(groupLayer, devBBuildings, devProjectArea));

      // zoom to the extent of the group layer when the child layers are loaded
      groupLayer.getLayers().forEach(childLayer ->
        childLayer.addDoneLoadingListener(() -> {
          if (childLayer.getLoadStatus() == LoadStatus.LOADED) {
            sceneView.setViewpointCamera(new Camera(groupLayer.getFullExtent().getCenter(), 700, 0, 60, 0));
          }
        })
      );

      // create a JavaFX tree view to show the layers in the scene
      TreeView<Layer> layerTreeView = new TreeView<>();
      layerTreeView.setMaxSize(250, 200);
      TreeItem<Layer> rootTreeItem = new TreeItem<>();
      layerTreeView.setRoot(rootTreeItem);
      layerTreeView.setShowRoot(false);
      StackPane.setAlignment(layerTreeView, Pos.TOP_RIGHT);
      StackPane.setMargin(layerTreeView, new Insets(10));
      stackPane.getChildren().add(layerTreeView);

      // display each layer with a custom tree cell
      layerTreeView.setCellFactory(p -> new LayerTreeCell());

      // recursively build the table of contents from the scene's operational layers
      buildLayersView(rootTreeItem, scene.getOperationalLayers());

    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
    }
  }

  /**
   * Recursively builds a tree from a parent tree item using a list of operational layers (including group layers).
   *
   * @param parentItem tree item to build tree from
   * @param operationalLayers a list of operational layers
   */
  private void buildLayersView(TreeItem<Layer> parentItem, List<Layer> operationalLayers) {
    for (Layer layer : operationalLayers) {
      // load each layer before adding to ensure all metadata is ready for display
      layer.loadAsync();
      layer.addDoneLoadingListener(() -> {
        // add a tree item for the layer to the parent tree item
        if (layer.canShowInLegend()) {
          TreeItem<Layer> layerItem = new TreeItem<>(layer);
          layerItem.setExpanded(true);
          parentItem.getChildren().add(layerItem);
          // if the layer is a group layer, continue building with its children
          if (layer instanceof GroupLayer && ((GroupLayer) layer).isShowChildrenInLegend()) {
            buildLayersView(layerItem, ((GroupLayer) layer).getLayers());
          }
        }
      });
    }
  }

  /**
   * Stops and releases all resources used in application.
   */
  @Override
  public void stop() {

    if (sceneView != null) {
      sceneView.dispose();
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
