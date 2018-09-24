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

package com.esri.samples.ogc.display_kml_network_links;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.KmlLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.SceneView;
import com.esri.arcgisruntime.ogc.kml.KmlDataset;

public class DisplayKMLNetworkLinksSample extends Application {

  private SceneView sceneView;

  @Override
  public void start(Stage stage) {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene fxScene = new Scene(stackPane);
      fxScene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

      // set title, size, and add scene to stage
      stage.setTitle("Dislay KML Network Links Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(fxScene);
      stage.show();

      VBox controlsVBox = new VBox(6);
      controlsVBox.setBackground(new Background(new BackgroundFill(Paint.valueOf("rgba(0,0,0,0.3)"), CornerRadii.EMPTY,
          Insets.EMPTY)));
      controlsVBox.setPadding(new Insets(10.0));
      controlsVBox.setMaxSize(220, 240);
      controlsVBox.getStyleClass().add("panel-region");

      Label networkMessagesLabel = new Label("Network Link Messages:");

      // create a list view to show KML network link messages
      ListView<String> networkMessagesListView = new ListView<>();
      networkMessagesListView.setMaxSize(300, 400);

      controlsVBox.getChildren().addAll(networkMessagesLabel, networkMessagesListView);

      // create a map and add it to the map view
      ArcGISScene scene = new ArcGISScene(Basemap.createImageryWithLabels());
      sceneView = new SceneView();
      sceneView.setArcGISScene(scene);

      // start centered over Germany
      sceneView.setViewpoint(new Viewpoint(new Point(8.150526, 50.472421, SpatialReferences.getWgs84()), 2000000));

      // create a KML dataset from KML hosted at a URL
      KmlDataset kmlDataset = new KmlDataset("https://www.arcgis.com/sharing/rest/content/items/600748d4464442288f6db8a4ba27dc95/data");

      // add network link messages to the list view when received
      kmlDataset.addKmlNetworkLinkMessageReceivedListener(kmlNetworkLinkMessageReceivedEvent ->
        networkMessagesListView.getItems().add(kmlNetworkLinkMessageReceivedEvent.getMessage())
      );

      // add the KML layer as an operational layer and zoom to it after it is loaded
      KmlLayer kmlLayer = new KmlLayer(kmlDataset);
      scene.getOperationalLayers().add(kmlLayer);
      kmlLayer.addDoneLoadingListener(() -> {
        if (kmlLayer.getLoadStatus() != LoadStatus.LOADED) {
          new Alert(Alert.AlertType.ERROR, "Error loading KML layer").show();
        }
      });

      // add the map view and list view to the stack pane
      stackPane.getChildren().addAll(sceneView, controlsVBox);
      StackPane.setAlignment(controlsVBox, Pos.TOP_LEFT);
      StackPane.setMargin(controlsVBox, new Insets(10));
    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
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
