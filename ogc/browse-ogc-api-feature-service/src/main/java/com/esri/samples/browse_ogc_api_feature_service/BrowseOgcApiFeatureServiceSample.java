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
import com.esri.arcgisruntime.ogc.wfs.OgcAxisOrder;
import com.esri.arcgisruntime.symbology.*;

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

  private MapView mapView;
  private ArcGISMap map;

  // UI class variables
  private ProgressIndicator progressIndicator;
  private TextField textField;
  private Button loadButton;
  private Button loadSelectedLayerButton;
  private Label label;

  private ListView<OgcFeatureCollectionInfo> ogcLayerNamesListView;

  private OgcFeatureCollectionTable ogcFeatureCollectionTable;

  // keep loadables in scope to avoid garbage collection


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

    stackPane.getChildren().addAll(mapView, uiControlsVBox(), progressIndicator);

    // when the load button is clicked, load the provided OGC API service url
    loadButton.setOnAction(e -> loadOgcApiService(textField.getText()));

    // when the load selected layer button is clicked, load the selected layer from the list view when the layer is selected
    loadSelectedLayerButton.setOnAction(e -> {

        updateMap(ogcLayerNamesListView.getSelectionModel().getSelectedItem());

    });



  }

  private void loadOgcApiService(String serviceUrl) {

    ogcLayerNamesListView.getItems().clear();
    progressIndicator.setVisible(true);

    System.out.println("Button clicked");
    progressIndicator.setVisible(true);

    if (!textField.getText().isEmpty()) {
      System.out.println("text field isn't empty");

      var ogcFeatureService = new OgcFeatureService(serviceUrl);
      ogcFeatureService.loadAsync();
      ogcFeatureService.addDoneLoadingListener(() -> {
        if (ogcFeatureService.getLoadStatus() == LoadStatus.LOADED) {

          loadSelectedLayerButton.setDisable(false);
          progressIndicator.setVisible(false);
          label.setText("OGC feature service loaded. Select another layer.");

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
                String fullNameOfOgcFeatureServiceLayer = ogcFeatureCollectionInfo.getTitle();
                setText(fullNameOfOgcFeatureServiceLayer);
              }
            }
          });

        } else {
          System.out.println(ogcFeatureService.getLoadStatus());
          new Alert(Alert.AlertType.ERROR, "Failed to load OGC feature service: " +
            ogcFeatureService.getLoadError().getCause().getLocalizedMessage()).show();
        }
      });
    } else {
      new Alert(Alert.AlertType.ERROR, "Enter a service url to continue").show();
    }

  }

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

    // create a list view to show all of the layers in a OGC API feature service
    ogcLayerNamesListView = new ListView<>();
    ogcLayerNamesListView.setMaxSize(300, 250);

    // create a button that loads the layer selected in the list
    loadSelectedLayerButton = new Button("Load selected layer");
    loadSelectedLayerButton.setDisable(true);

    // create a label with user prompts
    label = new Label("Enter a valid service URL and click Load");

    HBox hbox = new HBox(6);
    hbox.getChildren().addAll(textField, loadButton);
    hbox.setMaxSize(300, 250);
//    hbox.setMaxWidth(300);
    controlsVBox.getChildren().addAll(hbox, ogcLayerNamesListView, loadSelectedLayerButton, label);
    StackPane.setAlignment(controlsVBox, Pos.TOP_LEFT);
    StackPane.setMargin(controlsVBox, new Insets(10, 0, 0, 10));

    return controlsVBox;

  }

  /**
   * Adds a OGCFeatureCollectionInfo to the map's operational layers.
   * @param ogcFeatureCollectionInfo the ogcFeatureCollectionInfo that the map will display
   */
  private void updateMap(OgcFeatureCollectionInfo ogcFeatureCollectionInfo){

    progressIndicator.setVisible(true);

    // clear the map's operational layers
    map.getOperationalLayers().clear();

    // create an OGC feature collection table from the feature collection info
    ogcFeatureCollectionTable = new OgcFeatureCollectionTable(ogcFeatureCollectionInfo);

    // set the feature request mode to manual (only manual is currently supported)
    // in this mode, the table must be manually populated as panning and zooming won't request features automatically.
    ogcFeatureCollectionTable.setFeatureRequestMode(ServiceFeatureTable.FeatureRequestMode.MANUAL_CACHE);

    // populate the table and then remove progress indicator and set the viewpoint to that of the layer's full extent when done.
    var queryParameters = new QueryParameters();
    queryParameters.setMaxFeatures(1000);
    ogcFeatureCollectionTable.populateFromServiceAsync(queryParameters, false, null).addDoneListener(() -> {
      progressIndicator.setVisible(false);
    });

    // create a feature layer to visualize the OGC features
    FeatureLayer ogcFeatureLayer = new FeatureLayer(ogcFeatureCollectionTable);

    ogcFeatureCollectionTable.loadAsync();
    // apply a renderer to the feature layer once the table is loaded (the renderer is based on the table's geometry type)
    ogcFeatureCollectionTable.addDoneLoadingListener(() -> {
      switch (ogcFeatureCollectionTable.getGeometryType()) {
        case POINT:
        case MULTIPOINT:
          ogcFeatureLayer.setRenderer(new SimpleRenderer(new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, ColorUtil.colorToArgb(Color.BLUE), 5)));
          break;
        case POLYGON:
        case ENVELOPE:
          ogcFeatureLayer.setRenderer(new SimpleRenderer(new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, ColorUtil.colorToArgb(Color.BLUE), null)));
          break;
        case POLYLINE:
          ogcFeatureLayer.setRenderer(new SimpleRenderer(new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, ColorUtil.colorToArgb(Color.BLUE), 1)));
          break;
      }

      Envelope collectionExtent = ogcFeatureCollectionInfo.getExtent();
      if (!collectionExtent.isEmpty()) {
        mapView.setViewpointGeometryAsync(collectionExtent, 100);
      }

    });

    // add the layer to the map's operational layers
    map.getOperationalLayers().add(ogcFeatureLayer);
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
