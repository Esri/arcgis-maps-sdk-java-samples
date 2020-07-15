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

package com.esri.samples.change_feature_layer_renderer;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;

public class ChangeFeatureLayerRendererSample extends Application {

  private MapView mapView;
  private FeatureLayer featureLayer;

  private final static String FEATURE_SERVICE_URL =
      "https://services.arcgis.com/V6ZHFr6zdgNZuVG0/arcgis/rest/services/Landscape_Trees/FeatureServer/0";

  @Override
  public void start(Stage stage) {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);
      scene.getStylesheets().add(getClass().getResource("/change_feature_layer_renderer/style.css").toExternalForm());

      // set title, size, and add scene to stage
      stage.setTitle("Change Feature Layer Renderer Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // create a marker symbol renderer with a blue circle
      SimpleMarkerSymbol markerSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, 0xFF0000FF, 5 );
      SimpleRenderer blueRenderer = new SimpleRenderer(markerSymbol);

      // create renderer toggle switch
      ToggleButton rendererSwitch = new ToggleButton();
      rendererSwitch.setText("Blue Renderer");

      // set the render if the switch is selected
      rendererSwitch.selectedProperty().addListener((observable, oldValue, newValue) -> {
        if (rendererSwitch.isSelected()) {
          featureLayer.setRenderer(blueRenderer);
          rendererSwitch.setText("Show Original Renderer");
        } else {
          // reset the renderer if not selected
          featureLayer.resetRenderer();
          rendererSwitch.setText("Blue Renderer");
        }
      });

      // create a ArcGISMap with basemap topographic
      final ArcGISMap map = new ArcGISMap(Basemap.createTopographic());

      // create starting envelope for the ArcGISMap
      Point topLeftPoint = new Point(-9177811, 4247000);
      Point bottomRightPoint = new Point(-9176791, 4247784);
      Envelope envelope = new Envelope(topLeftPoint, bottomRightPoint);

      // set starting envelope for the ArcGISMap
      map.setInitialViewpoint(new Viewpoint(envelope));

      // create a service feature table using the url
      final ServiceFeatureTable featureTable = new ServiceFeatureTable(FEATURE_SERVICE_URL);

      // create a feature layer from the service feature table
      featureLayer = new FeatureLayer(featureTable);

      // wait for the feature layer to load
      featureLayer.loadAsync();
      featureLayer.addDoneLoadingListener(()->{
        if (featureLayer.getLoadStatus() == LoadStatus.LOADED) {
          // add feature layer to ArcGISMap
          map.getOperationalLayers().add(featureLayer);
        } else {
          new Alert(Alert.AlertType.ERROR, "Error loading Feature Table from service").show();
        }
      });

      // create a view for the ArcGISMap and set ArcGISMap to it
      mapView = new MapView();
      mapView.setMap(map);

      // add the map view and control panel to stack pane
      stackPane.getChildren().addAll(mapView, rendererSwitch);
      StackPane.setAlignment(rendererSwitch, Pos.TOP_LEFT);
      StackPane.setMargin(rendererSwitch, new Insets(10, 0, 0, 10));

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
