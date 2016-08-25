/*
 * Copyright 2016 Esri.
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

package com.esri.samples.featurelayers;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import org.controlsfx.control.ToggleSwitch;

import com.esri.arcgisruntime.datasource.arcgis.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;

public class ChangeFeatureLayerRenderer extends Application {

  private MapView mapView;
  private FeatureLayer featureLayer;

  private final static String FEATURE_SERVICE_URL =
      "http://sampleserver6.arcgisonline.com/arcgis/rest/services/PoolPermits/FeatureServer/0";

  @Override
  public void start(Stage stage) throws Exception {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);
      scene.getStylesheets().add(getClass().getResource("/SamplesTheme.css").toExternalForm());

      // set title, size, and add scene to stage
      stage.setTitle("Change Feature Layer Renderer Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // create a control panel
      VBox vBoxControl = new VBox(6);
      vBoxControl.setMaxSize(150, 40);
      vBoxControl.getStyleClass().add("panel-region");

      // create a blue (0xFF0000FF) line symbol renderer
      SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0xFF0000FF, 2);
      SimpleRenderer blueRenderer = new SimpleRenderer(lineSymbol);

      // create renderer toggle switch
      ToggleSwitch rendererSwitch = new ToggleSwitch();
      rendererSwitch.setText("blue renderer");

      // set the render if the switch is selected
      rendererSwitch.selectedProperty().addListener((observable, oldValue, newValue) -> {
        if (rendererSwitch.isSelected()) {
          featureLayer.setRenderer(blueRenderer);
        } else {
          // reset the renderer if not selected
          featureLayer.resetRenderer();
        }
      });

      // add buttons to the control panel
      vBoxControl.getChildren().addAll(rendererSwitch);

      // create starting envelope for the ArcGISMap
      SpatialReference spatialReference = SpatialReferences.getWebMercator();
      Point topLeftPoint = new Point(-1.30758164047166E7, 4014771.46954516, spatialReference);
      Point bottomRightPoint = new Point(-1.30730056797177E7, 4016869.78617381, spatialReference);
      Envelope envelope = new Envelope(topLeftPoint, bottomRightPoint);

      // create a service feature table using the url
      final ServiceFeatureTable featureTable = new ServiceFeatureTable(FEATURE_SERVICE_URL);

      // create a feature layer from the service feature table
      featureLayer = new FeatureLayer(featureTable);

      // create a ArcGISMap with basemap topographic
      final ArcGISMap map = new ArcGISMap(Basemap.createTopographic());

      // set starting envelope for the ArcGISMap
      map.setInitialViewpoint(new Viewpoint(envelope));

      // add feature layer to ArcGISMap
      map.getOperationalLayers().add(featureLayer);

      // create a view for this ArcGISMap and set ArcGISMap to it
      mapView = new MapView();
      mapView.setMap(map);

      // add the map view and control panel to stack pane
      stackPane.getChildren().addAll(mapView, vBoxControl);
      StackPane.setAlignment(vBoxControl, Pos.TOP_LEFT);
      StackPane.setMargin(vBoxControl, new Insets(10, 0, 0, 10));

    } catch (Exception e) {
      // on any error, display the stack trace
      e.printStackTrace();
    }
  }

  /**
   * Stops and releases all resources used in application.
   */
  @Override
  public void stop() throws Exception {

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
