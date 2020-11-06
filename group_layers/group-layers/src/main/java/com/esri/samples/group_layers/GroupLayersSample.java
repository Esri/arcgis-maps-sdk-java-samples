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
import javafx.scene.control.Alert;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.layers.ArcGISSceneLayer;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.layers.GroupLayer;
import com.esri.arcgisruntime.layers.GroupVisibilityMode;
import com.esri.arcgisruntime.layers.Layer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.ArcGISTiledElevationSource;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Surface;
import com.esri.arcgisruntime.mapping.view.Camera;
import com.esri.arcgisruntime.mapping.view.SceneView;

public class GroupLayersSample extends Application {

  private GroupLayer buildingsGroupLayer; // keep loadable in scope to avoid garbage collection
  private GroupLayer projectAreaGroupLayer; // keep loadable in scope to avoid garbage collection

  private SceneView sceneView;
  static ToggleGroup buildingsToggleGroup;

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

      // create two group layers, set the visibility modes and add layers
      projectAreaGroupLayer = new GroupLayer();
      projectAreaGroupLayer.setName("Project area group");
      projectAreaGroupLayer.setVisibilityMode(GroupVisibilityMode.INDEPENDENT);
      projectAreaGroupLayer.getLayers().addAll(Arrays.asList(
        (new FeatureLayer(new ServiceFeatureTable("https://services.arcgis.com/P3ePLMYs2RVChkJx/arcgis/rest/services/DevelopmentProjectArea/FeatureServer/0"))),
        (new FeatureLayer(new ServiceFeatureTable("https://services.arcgis.com/P3ePLMYs2RVChkJx/arcgis/rest/services/DevA_Pathways/FeatureServer/1"))),
        (new ArcGISSceneLayer("https://tiles.arcgis.com/tiles/P3ePLMYs2RVChkJx/arcgis/rest/services/DevA_Trees/SceneServer"))
      ));

      buildingsGroupLayer = new GroupLayer();
      buildingsGroupLayer.setName("Buildings group");
      buildingsGroupLayer.setVisibilityMode(GroupVisibilityMode.EXCLUSIVE);
      buildingsGroupLayer.getLayers().addAll(Arrays.asList(
        (new ArcGISSceneLayer("https://tiles.arcgis.com/tiles/P3ePLMYs2RVChkJx/arcgis/rest/services/DevA_BuildingShells/SceneServer")),
        (new ArcGISSceneLayer("https://tiles.arcgis.com/tiles/P3ePLMYs2RVChkJx/arcgis/rest/services/DevB_BuildingShells/SceneServer"))
      ));

      // load the group layers
      projectAreaGroupLayer.loadAsync();
      buildingsGroupLayer.loadAsync();

      // Display an alert if the child layers in the group layers fail to load
      projectAreaGroupLayer.getLayers().forEach(layer ->
        layer.addDoneLoadingListener(() -> {
          if (layer.getLoadStatus() == LoadStatus.LOADED) {
            // zoom to the extent of the project area group layer when the child layers are loaded
            sceneView.setViewpointCamera(new Camera(projectAreaGroupLayer.getFullExtent().getCenter(), 700, 0, 60, 0));
          } else new Alert(Alert.AlertType.ERROR, "Layer failed to load:\n" + layer.getLoadError().getCause().getMessage()).show();
        })
      );

      buildingsGroupLayer.getLayers().forEach(layer ->
        layer.addDoneLoadingListener(() -> {
          if (layer.getLoadStatus() != LoadStatus.LOADED) {
            new Alert(Alert.AlertType.ERROR, "Layer failed to load:\n" + layer.getLoadError().getCause().getMessage()).show();
          }
        })
      );

      // add the group layers to the scene operational layers
      scene.getOperationalLayers().addAll(Arrays.asList(projectAreaGroupLayer, buildingsGroupLayer));

      // create a JavaFX tree view to show the layers in the scene
      TreeView<Layer> layerTreeView = new TreeView<>();
      layerTreeView.setMaxSize(250, 200);
      layerTreeView.setPadding(new Insets(10, 0, 0, 5));
      TreeItem<Layer> rootTreeItem = new TreeItem<>();
      layerTreeView.setRoot(rootTreeItem);
      layerTreeView.setShowRoot(false);
      StackPane.setAlignment(layerTreeView, Pos.TOP_RIGHT);
      StackPane.setMargin(layerTreeView, new Insets(10));
      stackPane.getChildren().add(layerTreeView);

      // create a toggle group for the tree view UI
      buildingsToggleGroup = new ToggleGroup();

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
      // check the layer has loaded before creating UI
      layer.addDoneLoadingListener(() -> {
        if (layer.getLoadStatus() == LoadStatus.LOADED) {
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
