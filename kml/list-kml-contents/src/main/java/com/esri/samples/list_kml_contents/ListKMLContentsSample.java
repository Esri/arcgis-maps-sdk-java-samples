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

package com.esri.samples.list_kml_contents;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.layers.KmlLayer;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.SceneView;
import com.esri.arcgisruntime.ogc.kml.KmlContainer;
import com.esri.arcgisruntime.ogc.kml.KmlDataset;
import com.esri.arcgisruntime.ogc.kml.KmlDocument;
import com.esri.arcgisruntime.ogc.kml.KmlFolder;
import com.esri.arcgisruntime.ogc.kml.KmlGroundOverlay;
import com.esri.arcgisruntime.ogc.kml.KmlNetworkLink;
import com.esri.arcgisruntime.ogc.kml.KmlNode;
import com.esri.arcgisruntime.ogc.kml.KmlPlacemark;
import com.esri.arcgisruntime.ogc.kml.KmlScreenOverlay;

public class ListKMLContentsSample extends Application {

  private SceneView sceneView;

  @Override
  public void start(Stage stage) {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene fxScene = new Scene(stackPane);

      // set title, size, and add scene to stage
      stage.setTitle("List KML Contents Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(fxScene);
      stage.show();

      // create a map and add it to the map view
      ArcGISScene scene = new ArcGISScene(Basemap.createImageryWithLabels());
      sceneView = new SceneView();
      sceneView.setArcGISScene(scene);

      // load a KML dataset from a local KMZ file and show it as an operational layer
      File kmzFile = new File("./samples-data/kml/esri_test_data.kmz");
      KmlDataset kmlDataset = new KmlDataset(kmzFile.getAbsolutePath());
      KmlLayer kmlLayer = new KmlLayer(kmlDataset);
      scene.getOperationalLayers().add(kmlLayer);

      // create a tree view to list the contents of the KML dataset
      TreeView<KmlNode> kmlTree = new TreeView<>();
      kmlTree.setMaxSize(300, 400);
      TreeItem<KmlNode> root = new TreeItem<>(null);
      kmlTree.setRoot(root);
      kmlTree.setShowRoot(false);

      // when the dataset is loaded, recursively build the tree view with KML nodes starting with the root node(s)
      kmlDataset.addDoneLoadingListener(() -> kmlDataset.getRootNodes().forEach(kmlNode -> {
        TreeItem<KmlNode> kmlNodeTreeItem = buildTree(new TreeItem<>(kmlNode));
        root.getChildren().add(kmlNodeTreeItem);
      }));

      // show the KML node in the tree view with its name and type
      kmlTree.setCellFactory(param -> new TextFieldTreeCell<>(new StringConverter<KmlNode>() {

        @Override
        public String toString(KmlNode node) {
          String type = null;
          if (node instanceof KmlDocument) {
            type = "KmlDocument";
          } else if (node instanceof KmlFolder) {
            type = "KmlFolder";
          } else if (node instanceof KmlGroundOverlay) {
            type = "KmlGroundOverlay";
          } else if (node instanceof KmlScreenOverlay) {
            type = "KmlScreenOverlay";
          } else if (node instanceof KmlPlacemark) {
              type = "KmlPlacemark";
          }
          return node.getName() + " - " + type;
        }

        @Override
        public KmlNode fromString(String string) {
          return null; //not needed
        }
      }));

      // when a tree item is selected, zoom to its node's extent (if it has one)
      kmlTree.getSelectionModel().selectedItemProperty().addListener(o -> {
        TreeItem<KmlNode> selectedTreeItem = kmlTree.getSelectionModel().getSelectedItem();
        KmlNode selectedNode = selectedTreeItem.getValue();
        Envelope nodeExtent = selectedNode.getExtent();
        if (nodeExtent != null && !nodeExtent.isEmpty()) {
          sceneView.setViewpointAsync(new Viewpoint(nodeExtent));
        }
      });

      // add the map view to stack pane
      stackPane.getChildren().addAll(sceneView, kmlTree);
      StackPane.setAlignment(kmlTree, Pos.TOP_LEFT);
      StackPane.setMargin(kmlTree, new Insets(10));
    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
    }
  }

  /**
   * Recursively adds child tree items to the given parent tree item based on its node's child nodes.
   *
   * @param parent a parent KML node tree item
   * @return the original parent, now with the child tree attached to it
   */
  private TreeItem<KmlNode> buildTree(TreeItem<KmlNode> parent) {
    KmlNode node = parent.getValue();
    node.setVisible(true);
    List<KmlNode> children = new ArrayList<>();
    if (parent.getValue() instanceof KmlContainer) {
      children.addAll(((KmlContainer) node).getChildNodes());
    } else if (parent.getValue() instanceof KmlNetworkLink) {
      children.addAll(((KmlNetworkLink) node).getChildNodes());
    }
    children.forEach(childNode -> {
      TreeItem<KmlNode> childTreeItem = buildTree(new TreeItem<>(childNode));
      parent.getChildren().add(childTreeItem);
    });
    parent.setExpanded(true); // expand all nodes in the tree view
    return parent;
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
