/*
 * Copyright 2022 Esri.
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

package com.esri.samples.apply_unique_values_with_alternate_symbols;

import java.util.List;

import com.esri.arcgisruntime.loadable.LoadStatus;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.mapping.view.AnimationCurve;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.symbology.MultilayerPointSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.SymbolReferenceProperties;
import com.esri.arcgisruntime.symbology.UniqueValueRenderer;

public class ApplyUniqueValuesWithAlternateSymbolsSample extends Application{

  private MapView mapView;

  private FeatureLayer featureLayer;

  private final static String FEATURE_SERVICE_URL = "https://sampleserver6.arcgisonline.com/arcgis/rest/services/SF311/FeatureServer/0";

  private Button btnReset;

  private Label lblScale;

  @Override
  public void start(Stage stage) {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);

      // set title, size, and add scene to stage
      stage.setTitle("Apply unique values with alternate symbols");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // authentication with an API key or named user is required to access base maps and other location services
      String yourAPIKey = System.getProperty("apiKey");
      ArcGISRuntimeEnvironment.setApiKey(yourAPIKey);

      // create a point located at San Francisco, CA to be used as the viewpoint for the map
      Point vPoint = new Point(-13631205.660131, 4546829.846004, SpatialReferences.getWebMercator());

      // create a map with the standard imagery basemap style
      ArcGISMap map = new ArcGISMap(BasemapStyle.ARCGIS_TOPOGRAPHIC);

      // create a map view and set the map to it
      mapView = new MapView();
      mapView.setMap(map);

      // set a viewpoint to the map view centered on a point
      mapView.setViewpointCenterAsync(vPoint, 25000.0);

      // creating box, button, and label UI components
      VBox vBox = controlsVBox();

      // resetting map viewpoint on button press
      btnReset.setOnAction(event -> {
        // reset the map viewpoint
        mapView.setViewpointAsync(new Viewpoint(vPoint, 25000.0), 1.5f, AnimationCurve.EASE_IN_OUT_SINE);
      });

      // updating label scale values
      mapView.addViewpointChangedListener(event->
        // update label text
        lblScale.setText("Current scale: 1:" + String.format("%.2f", mapView.getMapScale())));

      // creating simple markers where the blue square and yellow diamond are alternate symbols
      // the red triangle is used to create a unique symbol and the purple diamond is the default symbol for the unique value renderer
      SimpleMarkerSymbol sBlueSquareSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.SQUARE, 0xFF0000FF, 15);
      SimpleMarkerSymbol sYellowDiamondSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.DIAMOND, 0xFFFFFF00, 15);
      SimpleMarkerSymbol sRedTriangleSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.TRIANGLE, 0xFFFF0000, 15);
      SimpleMarkerSymbol sPurpleDiamondSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.DIAMOND, 0xFF800080, 12);

      // converting simpler marker symbols to multilayer point symbols
      MultilayerPointSymbol mBlueSquareSymbol = sBlueSquareSymbol.toMultilayerSymbol();
      MultilayerPointSymbol mYellowDiamondSymbol = sYellowDiamondSymbol.toMultilayerSymbol();
      MultilayerPointSymbol mRedTriangleSymbol = sRedTriangleSymbol.toMultilayerSymbol();
      MultilayerPointSymbol mPurpleDiamondSymbol = sPurpleDiamondSymbol.toMultilayerSymbol();

      // setting scale range values through reference properties for the multilayer symbols
      SymbolReferenceProperties blueSquareRef = new SymbolReferenceProperties(10000.0, 5000.0);
      SymbolReferenceProperties yellowDiamondRef = new SymbolReferenceProperties(20000.0, 10000.0);
      SymbolReferenceProperties redTriangleRef = new SymbolReferenceProperties(5000.0, 0.0);

      mBlueSquareSymbol.setReferenceProperties(blueSquareRef);
      mYellowDiamondSymbol.setReferenceProperties(yellowDiamondRef);
      mRedTriangleSymbol.setReferenceProperties(redTriangleRef);

      // Creating unique value for the red triangle and list of alternate symbols
      UniqueValueRenderer.UniqueValue uniqueValue =
        new UniqueValueRenderer.UniqueValue("unique values based on request type", "unique value",
          mRedTriangleSymbol, List.of("Damaged Property"), List.of(mBlueSquareSymbol, mYellowDiamondSymbol));

      // Creating unique value renderer
      UniqueValueRenderer uniqueValRenderer = new UniqueValueRenderer();
      uniqueValRenderer.getUniqueValues().add(uniqueValue);
      uniqueValRenderer.getFieldNames().add(("req_type"));

      // Applying default symbol for the unique value renderer.
      uniqueValRenderer.setDefaultSymbol(mPurpleDiamondSymbol);

      // creating a service feature table using the url
      final ServiceFeatureTable featureTable = new ServiceFeatureTable(FEATURE_SERVICE_URL);

      // creating a feature layer from the service feature table
      featureLayer = new FeatureLayer(featureTable);

      // setting the unique value renderer on the feature layer
      featureLayer.setRenderer(uniqueValRenderer);

      // waiting for the feature layer to load
      featureLayer.loadAsync();

      featureLayer.addDoneLoadingListener(()->{
        if (featureLayer.getLoadStatus() == LoadStatus.LOADED) {
          // adding feature layer to ArcGISMap
          map.getOperationalLayers().add(featureLayer);
        } else {
          new Alert(Alert.AlertType.ERROR, "Error loading Feature Table from service" + featureLayer.getLoadError().getMessage()).show();
        }
      });

      // adding the map view and UI elements to the stack pane
      stackPane.getChildren().addAll(mapView, vBox);
      StackPane.setAlignment(vBox, Pos.TOP_LEFT);
      StackPane.setMargin(vBox, new Insets(10, 0, 0, 10));

    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
    }
  }

  /**
   * Creates a UI with three buttons and a label.
   * @return a vBox populated with buttons and labels
   */
  private VBox controlsVBox() {

    // creating label to show scale values and button to reset viewpoint
    lblScale = new Label("Current scale: 1:25000.00");
    lblScale.setTextFill(Color.WHITE);
    btnReset = new Button("Reset Viewpoint");

    // creating and setting a box to hold the scale label and reset button
    VBox vBox = new VBox(5);
    vBox.setMinWidth(150);
    vBox.getChildren().addAll(btnReset);
    btnReset.prefWidthProperty().bind(vBox.widthProperty());

    VBox controlsVBox = new VBox(10);
    controlsVBox.setBackground(new Background(new BackgroundFill(Paint.valueOf("rgba(0,0,0,0.7)"),
      CornerRadii.EMPTY, Insets.EMPTY)));
    controlsVBox.setPadding(new Insets(10.0));
    controlsVBox.setMaxSize(150, 75);
    controlsVBox.getStyleClass().add("panel-region");

    HBox hBox = new HBox();
    hBox.getChildren().addAll(lblScale);
    controlsVBox.getChildren().addAll(hBox, vBox);

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