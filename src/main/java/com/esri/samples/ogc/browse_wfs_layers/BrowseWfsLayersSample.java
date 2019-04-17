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

package com.esri.samples.ogc.browse_wfs_layers;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.GeometryType;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.ogc.wfs.OgcAxisOrder;
import com.esri.arcgisruntime.ogc.wfs.WfsFeatureTable;
import com.esri.arcgisruntime.ogc.wfs.WfsLayerInfo;
import com.esri.arcgisruntime.ogc.wfs.WfsService;
import com.esri.arcgisruntime.symbology.Renderer;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import java.awt.*;
import java.util.List;
import java.util.Random;

public class BrowseWfsLayersSample extends Application {

  private MapView mapView;
  private CheckBox checkBox;
  private ArcGISMap map;
  private ProgressIndicator progressIndicator;

  @Override
  public void start(Stage stage) throws Exception {

    // create stack pane and JavaFX app scene
    StackPane stackPane = new StackPane();
    Scene scene = new Scene(stackPane);
    scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

    // set title, size, and add JavaFX scene to stage
    stage.setTitle("Browse WFS Layers");
    stage.setWidth(800);
    stage.setHeight(700);
    stage.setScene(scene);
    stage.show();

    // create a control panel
    VBox controlsVBox = new VBox(6);
    controlsVBox.setBackground(new Background(new BackgroundFill(Paint.valueOf("rgba(0,0,0,0.3)"), CornerRadii.EMPTY, Insets.EMPTY)));
    controlsVBox.setPadding(new Insets(10.0));
    controlsVBox.setMaxSize(170, 220);
    controlsVBox.getStyleClass().add("panel-region");

    // create a check box to swap coordinate order
    checkBox = new CheckBox("Swap coordinate order");
    checkBox.setMaxWidth(Double.MAX_VALUE);

    // create a list to hold the names of the bookmarks
    ListView<WfsLayerInfo> wfsLayerNamesListView = new ListView<>();
    wfsLayerNamesListView.setMaxHeight(160);

    // create a progress indicator
    progressIndicator = new ProgressIndicator();
    progressIndicator.setVisible(false);

    // create an ArcGISMap with topographic basemap and set it to the map view
    map = new ArcGISMap(Basemap.createImagery());
    mapView = new MapView();
    mapView.setMap(map);

    // URL to the WFS service
    String serviceUrl = "https://dservices2.arcgis.com/ZQgQTuoyBrtmoGdP/arcgis/services/Seattle_Downtown_Features/WFSServer?service=wfs&request=getcapabilities";

    // create a WFS service and load it
    WfsService wfsService = new WfsService(serviceUrl);
    wfsService.loadAsync();

    // when the WFS service has loaded, get its layer information, and add them to the list view for browsingI
    wfsService.addDoneLoadingListener(() -> {
      if (wfsService.getLoadStatus() == LoadStatus.LOADED) {
        // add the list of WFS layers to the list view
        List<WfsLayerInfo> wfsLayerInfos = wfsService.getServiceInfo().getLayerInfos();
        wfsLayerNamesListView.getItems().addAll(wfsLayerInfos);
        for (WfsLayerInfo info: wfsLayerInfos){

          System.out.println(info.getExtent());
        }
      } else {
        Alert alert = new Alert(Alert.AlertType.ERROR, "WFS Service Failed to Load!");
        alert.show();
      }
    });

    // populate the list view with layer names
    wfsLayerNamesListView.setCellFactory(list -> new ListCell<>() {

      @Override
      protected void updateItem(WfsLayerInfo wfsLayerInfo, boolean bln) {
        super.updateItem(wfsLayerInfo, bln);
        if (wfsLayerInfo != null) {
          String fullNameOfWfsLayer = wfsLayerInfo.getName();
          String[] split = fullNameOfWfsLayer.split(":");
          String smallName = split[1];
          setText(smallName);
        }
      }
    });

    // load the selected layer from the list view when the layer is selected
    wfsLayerNamesListView.getSelectionModel().selectedItemProperty().addListener(observable -> {
      updateMap(wfsLayerNamesListView.getSelectionModel().getSelectedItem());
    });



    // add the list view, button and check box to the control panel
    controlsVBox.getChildren().addAll(wfsLayerNamesListView, checkBox);
    // add the mapview to the stackpane
    stackPane.getChildren().addAll(mapView, controlsVBox, progressIndicator);
    StackPane.setAlignment(controlsVBox, Pos.TOP_LEFT);
    StackPane.setMargin(controlsVBox, new Insets(10, 0, 0, 10));
  }

  /**
   * Adds a WfsLayerInfo to the map's operational layers, with a random color renderer.
   * @param wfsLayerInfo the WfsLayerInfo that the map will display
   */
  private void updateMap(WfsLayerInfo wfsLayerInfo){

    progressIndicator.setVisible(true);

    // clear the map's operational layers
    map.getOperationalLayers().clear();

    // create a WFSFeatureTable from the WFSLayerInfo
    WfsFeatureTable wfsFeatureTable = new WfsFeatureTable(wfsLayerInfo);

    // set the feature request mode to manual. The table must be manually populated as panning and zooming won't request features automatically.
    wfsFeatureTable.setFeatureRequestMode(ServiceFeatureTable.FeatureRequestMode.MANUAL_CACHE);
    // define the coordinate order for the WFS service.
    // no swap will keep the co-ordinates in the order they are retrieved from the WFS service; swap will reverse the order.
    wfsFeatureTable.setAxisOrder(checkBox.isSelected() ? OgcAxisOrder.SWAP : OgcAxisOrder.NO_SWAP);
    System.out.println("wfs layer info Extent " + wfsLayerInfo.getExtent());
    System.out.println("wfs layer info Description " + wfsLayerInfo.getDescription());

    // create a feature layer to visualize the WFS features
    FeatureLayer wfsFeatureLayer = new FeatureLayer(wfsFeatureTable);
    System.out.println("feature table get layer info " + wfsFeatureTable.getLayerInfo());
    System.out.println("feature table get extent " + wfsFeatureTable.getExtent());
    System.out.println("feature table get internal " + wfsFeatureTable.getInternal());

    // populate the table and then remove progress indicator and set the viewpoint to that of the layer's full extent when done.
    wfsFeatureTable.populateFromServiceAsync(new QueryParameters(), false, null ).addDoneListener(()->{
      progressIndicator.setVisible(false);
      mapView.setViewpointGeometryAsync(wfsFeatureLayer.getFullExtent(), 50);
    });

    // apply a renderer to the feature layer once the table is loaded (the renderer is based on the table's geometry type)
    wfsFeatureTable.addDoneLoadingListener(()->{
      wfsFeatureLayer.setRenderer(getRandomRendererForTable(wfsFeatureTable));
    });

    System.out.println(wfsFeatureLayer.isVisible());

    // add the layer to the map's operational layers
    map.getOperationalLayers().add(wfsFeatureLayer);

  }

  /**
   * Returns a simple renderer for a WfsFeatureTable based on its geometry type. A random color is assigned to the renderer.
   * @param table the WfsFeatureTable the renderer is to be set on
   * @return renderer
   */
  private Renderer getRandomRendererForTable (WfsFeatureTable table) {

    Random random = new Random();
    float r = random.nextFloat();
    float g = random.nextFloat();
    float b = random.nextFloat();

    Color randomColor = new Color(r, g, b);

    if (table.getGeometryType() == GeometryType.POINT || table.getGeometryType() == GeometryType.MULTIPOINT){
      return new SimpleRenderer(
              new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, randomColor.getRGB(), 3));
    } else if (table.getGeometryType() == GeometryType.POLYGON || table.getGeometryType() == GeometryType.ENVELOPE){
      return new SimpleRenderer(
              new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, randomColor.getRGB(), null));
    } else {
      return new SimpleRenderer(new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, randomColor.getRGB(), 2));
    }
  }

  /**
   * Stops and releases all resources used in application.
   */
  @Override
  public void stop() {
    // release resources when the application closes
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