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

package com.esri.samples.local_server_dynamic_workspace_shapefile;

import java.io.File;
import java.util.Collections;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

import com.esri.arcgisruntime.layers.ArcGISMapImageLayer;
import com.esri.arcgisruntime.layers.ArcGISMapImageSublayer;
import com.esri.arcgisruntime.layers.TableSublayerSource;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.localserver.DynamicWorkspace;
import com.esri.arcgisruntime.localserver.LocalMapService;
import com.esri.arcgisruntime.localserver.LocalServer;
import com.esri.arcgisruntime.localserver.LocalServerStatus;
import com.esri.arcgisruntime.localserver.ShapefileWorkspace;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol.Style;
import com.esri.arcgisruntime.symbology.SimpleRenderer;

public class LocalServerDynamicWorkspaceShapefileSample extends Application {

  private MapView mapView;
  private static LocalServer server;

  @Override
  public void start(Stage stage) {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);
      scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

      // set title, size, and add scene to stage
      stage.setTitle("Local Server Dynamic Workspace Shapefile Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // create Choose Shapefile button
      Button addButton = new Button("Choose Shapefile");
      addButton.setMaxSize(150, 25);
      addButton.setDisable(true);

      // check local server is installed
      if (LocalServer.INSTANCE.checkInstallValid()) {
        server = LocalServer.INSTANCE;
        server.addStatusChangedListener(status -> {
          if (server.getStatus() == LocalServerStatus.STARTED) {
            addButton.setDisable(false);
          }
        });
        // start local server
        server.startAsync();
      } else {
        Platform.runLater(() -> {
          Alert dialog = new Alert(AlertType.INFORMATION);
          dialog.initOwner(mapView.getScene().getWindow());
          dialog.setHeaderText("Local Server Load Error");
          dialog.setContentText("Local Server install path couldn't be located.");
          dialog.showAndWait();
          Platform.exit();
        });
      }

      FileChooser fileChooser = new FileChooser();
      fileChooser.setTitle("Open Resource File");
      fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Shapefiles", "*.shp"));
      fileChooser.setInitialDirectory(new File(System.getProperty("data.dir"), "./samples-data/shapefiles/"));
      // choose the file, then start the local map service
      addButton.setOnAction(e -> {
        // browse to the shapefile file
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
          String fileName = selectedFile.getName();
          String path = selectedFile.getParent();
          startLocalMapService(fileName, path);
        }
      });

      // create a map and add it to a map view
      ArcGISMap map = new ArcGISMap(Basemap.createTopographic());
      mapView = new MapView();
      mapView.setMap(map);

      // add the map view and control panel to stack pane
      stackPane.getChildren().addAll(mapView, addButton);
      StackPane.setAlignment(addButton, Pos.TOP_LEFT);
      StackPane.setMargin(addButton, new Insets(10, 0, 0, 10));
    } catch (Exception e) {
      // on any error, display the stack trace
      e.printStackTrace();
    }
  }

  /**
   * Start a LocalMapService and attaches a dynamic workspace shapefile.
   * 
   * @param fileName mpk file name
   * @param path path to mpk file
   */
  private void startLocalMapService(String fileName, String path) {

    // start a service from the blank MPK
    String mapServiceURL = "./samples-data/local_server/mpk_blank.mpk";
    LocalMapService localMapService = new LocalMapService(mapServiceURL);

    //create a shapefile workspace
    ShapefileWorkspace shapefileWorkspace = new ShapefileWorkspace("shp_wkspc", path);
    // create a layersource that represents the actual shapefile on disk
    TableSublayerSource source = new TableSublayerSource(shapefileWorkspace.getId(), fileName);
    // create a sublayer instance from the source
    ArcGISMapImageSublayer shapefileSublayer = new ArcGISMapImageSublayer(0, source);
    // add the dynamic workspace to the localMapService
    Iterable<DynamicWorkspace> dynamicWorkspaces = Collections.singletonList(shapefileWorkspace);
    localMapService.setDynamicWorkspaces(dynamicWorkspaces);
    localMapService.addStatusChangedListener(event -> {
      if (event.getNewStatus() == LocalServerStatus.STARTED) {
        // ready to add the shapefile layer to the map. Create a map image layer using url
        ArcGISMapImageLayer imageLayer = new ArcGISMapImageLayer(localMapService.getUrl());

        // add the sub layer to the image layer
        imageLayer.addDoneLoadingListener(() -> {
          if (imageLayer.getLoadStatus() == LoadStatus.LOADED) {
            // default symbol and renderer need to be created and applied
            SimpleLineSymbol lineSymbol = new SimpleLineSymbol(Style.SOLID, 0xFFFF0000, 3);
            shapefileSublayer.setRenderer(new SimpleRenderer(lineSymbol));
            imageLayer.getSublayers().add(shapefileSublayer);

            shapefileSublayer.addDoneLoadingListener(() -> {
              if (shapefileSublayer.getLoadStatus() == LoadStatus.LOADED) {
                // zoom the map to the extent of the added shapefile layer
                mapView.setViewpoint(new Viewpoint(shapefileSublayer.getMapServiceSublayerInfo().getExtent()));
              } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Map Image Sublayer Failed to Load!");
                alert.show();
              }
            });
            shapefileSublayer.loadAsync();
          } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Map Image Layer Failed to Load!");
            alert.show();
          }
        });
        imageLayer.loadAsync();

        // add the image layer to map. Clear any previous layers
        mapView.getMap().getOperationalLayers().clear();
        mapView.getMap().getOperationalLayers().add(imageLayer);
      }
    });
    localMapService.startAsync();
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
