/*
 * Copyright 2021 Esri.
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

package com.esri.samples.browse_ogc_api_feature_service;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.OgcFeatureCollectionTable;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.layers.OgcFeatureCollectionInfo;
import com.esri.arcgisruntime.layers.OgcFeatureService;
import com.esri.arcgisruntime.layers.OgcFeatureServiceInfo;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.ColorUtil;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import java.util.List;

public class BrowseOgcApiFeatureServiceSample extends Application {

  private ArcGISMap map;
  private MapView mapView;
  private ListView<OgcFeatureCollectionInfo> ogcLayerNamesListView;
  private OgcFeatureService ogcFeatureService; // keep loadable in scope to avoid garbage collection

  // UI class variables
  private ProgressIndicator progressIndicator;
  private TextField textField;
  private Button loadButton;
  private Button loadSelectedLayerButton;

  @Override
  public void start(Stage stage) {

    // create stack pane and JavaFX app scene
    StackPane stackPane = new StackPane();
    Scene scene = new Scene(stackPane);
    scene.getStylesheets().add(getClass().getResource("/browse_ogc_api_feature_service/style.css").toExternalForm());

    // set title, size, and add JavaFX scene to stage
    stage.setTitle("Browse OGC API Feature Service");
    stage.setWidth(800);
    stage.setHeight(700);
    stage.setScene(scene);
    stage.show();

    // authentication with an API key or named user is required to access basemaps and other location services
    String yourAPIKey = System.getProperty("apiKey");
    ArcGISRuntimeEnvironment.setApiKey(yourAPIKey);

    // create a progress indicator
    progressIndicator = new ProgressIndicator();
    progressIndicator.setVisible(false);

    // create a map with the topographic basemap style
    map = new ArcGISMap(BasemapStyle.ARCGIS_TOPOGRAPHIC);

    // create a map view and set the map to it
    mapView = new MapView();
    mapView.setMap(map);

    // add the map view, UI controls, and the progress indicator to the stack pane
    stackPane.getChildren().addAll(mapView, uiControlsVBox(), progressIndicator);

    // when the load button is clicked, load the provided OGC API service url
    loadButton.setOnAction(e -> loadOgcApiService(textField.getText()));

    // when the load selected layer button is clicked, load the selected layer from the list view
    loadSelectedLayerButton.setOnAction(e -> updateMap(ogcLayerNamesListView.getSelectionModel().getSelectedItem()));

  }

  /**
   * Loads an OGC feature service from the provided service URL string.
   *
   * @param serviceUrl the service url of the OGC feature service
   */
  private void loadOgcApiService(String serviceUrl) {

    // clear any previously loaded OGC layers from the list view, and show the progress indicator
    ogcLayerNamesListView.getItems().clear();
    progressIndicator.setVisible(true);

    // check if the text box is populated before instantiating and loading a new OGC feature service from the provided URL
    if (!textField.getText().isEmpty()) {

      ogcFeatureService = new OgcFeatureService(serviceUrl);
      ogcFeatureService.loadAsync();

      // when the feature service has loaded, get a list of its layers and add them to the list view
      ogcFeatureService.addDoneLoadingListener(() -> {
        if (ogcFeatureService.getLoadStatus() == LoadStatus.LOADED) {

          // hide progress indicator
          progressIndicator.setVisible(false);

          // get the service metadata and then a list of available collections
          OgcFeatureServiceInfo serviceInfo = ogcFeatureService.getServiceInfo();
          List<OgcFeatureCollectionInfo> layerList = serviceInfo.getFeatureCollectionInfos();
          ogcLayerNamesListView.getItems().addAll(layerList);

          // populate the list view with layer names
          ogcLayerNamesListView.setCellFactory(list -> new ListCell<>() {

            @Override
            protected void updateItem(OgcFeatureCollectionInfo ogcFeatureCollectionInfo, boolean bln) {
              super.updateItem(ogcFeatureCollectionInfo, bln);
              if (ogcFeatureCollectionInfo != null) {
                String titleOfOgcFeatureServiceLayer = ogcFeatureCollectionInfo.getTitle();
                setText(titleOfOgcFeatureServiceLayer);
              }
            }
          });
          // when a layer is selected, enable the load selected layer button
          ogcLayerNamesListView.setOnMouseClicked(e -> loadSelectedLayerButton.setDisable(false));

          // if the feature service fails to load, display an error
        } else {
          new Alert(Alert.AlertType.ERROR, "Failed to load OGC feature service: " +
            ogcFeatureService.getLoadError().getCause().getLocalizedMessage()).show();
        }
      });

      // if the text field is blank and the load button is clicked, display an error
    } else {
      new Alert(Alert.AlertType.ERROR, "Enter a service url to continue").show();
    }
  }

  /**
   * Adds an OGCFeatureCollectionInfo to the map's operational layers.
   *
   * @param ogcFeatureCollectionInfo the ogcFeatureCollectionInfo that the map will display
   */
  private void updateMap(OgcFeatureCollectionInfo ogcFeatureCollectionInfo) {

    progressIndicator.setVisible(true);

    // clear the map's operational layers
    map.getOperationalLayers().clear();

    // create an OGC feature collection table from the feature collection info
    var ogcFeatureCollectionTable = new OgcFeatureCollectionTable(ogcFeatureCollectionInfo);

    // set the feature request mode to manual
    // in this mode, the table must be manually populated as panning and zooming won't request features automatically.
    ogcFeatureCollectionTable.setFeatureRequestMode(ServiceFeatureTable.FeatureRequestMode.MANUAL_CACHE);

    // create new query parameters
    var queryParameters = new QueryParameters();
    // set a limit of 1000 on the number of returned features per request, the default on some services could be as low as 10
    queryParameters.setMaxFeatures(1000);

    try {
      // populate and load the table with the query parameters
      // set the clearCache parameter to false to include existing table entries, and set the outfields parameter to null to request all fields
      ListenableFuture<FeatureQueryResult> result = ogcFeatureCollectionTable.populateFromServiceAsync(queryParameters, false, null);
      result.addDoneListener(() -> progressIndicator.setVisible(false));

    } catch (Exception exception) {
      exception.printStackTrace();
      new Alert(Alert.AlertType.ERROR, exception.getMessage()).show();
    }

    // apply a renderer to the feature layer once the table is loaded (the renderer is based on the table's geometry type)
    ogcFeatureCollectionTable.addDoneLoadingListener(() -> {

      // create a feature layer to visualize the OGC features
      FeatureLayer ogcFeatureLayer = new FeatureLayer(ogcFeatureCollectionTable);

      switch (ogcFeatureCollectionTable.getGeometryType()) {
        case POINT:
        case MULTIPOINT:
          ogcFeatureLayer.setRenderer(new SimpleRenderer(
            new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, ColorUtil.colorToArgb(Color.BLUE), 5)));
          break;
        case POLYGON:
        case ENVELOPE:
          ogcFeatureLayer.setRenderer(new SimpleRenderer(
            new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, ColorUtil.colorToArgb(Color.BLUE), null)));
          break;
        case POLYLINE:
          ogcFeatureLayer.setRenderer(new SimpleRenderer(
            new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, ColorUtil.colorToArgb(Color.BLUE), 1)));
          break;
      }

      // add the layer to the map's operational layers
      map.getOperationalLayers().add(ogcFeatureLayer);

      // set the map view's viewpoint to the extent of the layer
      Envelope collectionExtent = ogcFeatureLayer.getFullExtent();
      if (!collectionExtent.isEmpty()) {
        mapView.setViewpointGeometryAsync(collectionExtent, 100);
      }
    });

  }

  /**
   * Sets up the user interaction controls by adding a text field, load button, list view and load selected layer button to a VBox.
   *
   * @return a VBox containing user interaction controls
   */
  private VBox uiControlsVBox() {

    // create a control panel
    VBox controlsVBox = new VBox(6);
    controlsVBox.setBackground(new Background(new BackgroundFill(Paint.valueOf("rgba(0,0,0,0.3)"), CornerRadii.EMPTY,
      Insets.EMPTY)));
    controlsVBox.setPadding(new Insets(10.0));
    controlsVBox.setMaxSize(300, 300);

    // create a text field for entering a service URL, and provide an example service url to start with
    textField = new TextField();
    textField.setMaxWidth(300);
    textField.setText("https://demo.ldproxy.net/daraa");

    // create a button that allows the provided service to be loaded
    loadButton = new Button("Load");

    // create a list view to show all of the layers in an OGC API feature service
    ogcLayerNamesListView = new ListView<>();
    ogcLayerNamesListView.setMaxSize(300, 250);

    // create a button that loads the layer selected in the list
    loadSelectedLayerButton = new Button("Load selected layer");
    loadSelectedLayerButton.setDisable(true);

    HBox hbox = new HBox(6);
    hbox.getChildren().addAll(textField, loadButton);
    HBox.setHgrow(textField, Priority.ALWAYS);
    controlsVBox.getChildren().addAll(hbox, ogcLayerNamesListView, loadSelectedLayerButton);
    StackPane.setAlignment(controlsVBox, Pos.TOP_LEFT);
    StackPane.setMargin(controlsVBox, new Insets(10, 0, 0, 10));

    return controlsVBox;

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
