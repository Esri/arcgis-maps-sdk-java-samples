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

package com.esri.samples.unique_value_renderer;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.UniqueValueRenderer;
import com.esri.arcgisruntime.symbology.UniqueValueRenderer.UniqueValue;

public class UniqueValueRendererSample extends Application {

  private MapView mapView;

  // colors for symbols
  private static final int GRAY = 0xFFD3D3D3;
  private static final int RED = 0xFFFF0000;
  private static final int GREEN = 0xFF00FF00;
  private static final int BLUE = 0xFF0000FF;

  @Override
  public void start(Stage stage) {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);

      // set title, size, and add scene to stage
      stage.setTitle("Unique Value Renderer Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // authentication with an API key or named user is required to access basemaps and other location services
      String yourAPIKey = System.getProperty("apiKey");
      ArcGISRuntimeEnvironment.setApiKey(yourAPIKey);

      // create a map with the topographic basemap style
      final ArcGISMap map = new ArcGISMap(BasemapStyle.ARCGIS_TOPOGRAPHIC);

      // create a map view and set the map to it
      mapView = new MapView();
      mapView.setMap(map);

      // set a viewpoint on the map view
      mapView.setViewpoint(new Viewpoint(new Envelope(-13893029.0, 3573174.0, -12038972.0, 5309823.0,
        SpatialReferences.getWebMercator())));

      // create service feature table
      String sampleServiceUrl = "https://sampleserver6.arcgisonline.com/arcgis/rest/services/Census/MapServer/3";
      ServiceFeatureTable serviceFeatureTable = new ServiceFeatureTable(sampleServiceUrl);

      // create the feature layer using the service feature table
      final FeatureLayer featureLayer = new FeatureLayer(serviceFeatureTable);

      // override the feature layer renderer with a new unique value renderer
      UniqueValueRenderer uniqueValueRenderer = new UniqueValueRenderer();
      // field name is a key, in a key/value pair, of a feature's attributes
      // can be a list but only looking for one in this case
      uniqueValueRenderer.getFieldNames().add("STATE_ABBR");

      // create the symbols to be used in the renderer
      SimpleFillSymbol defaultFillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.NULL, 0x00000000,
          new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, GRAY, 2));
      SimpleFillSymbol californiaFillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, RED,
          new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, RED, 2));
      SimpleFillSymbol arizonaFillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, GREEN,
          new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, GREEN, 2));
      SimpleFillSymbol nevadaFillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, BLUE, new SimpleLineSymbol(
          SimpleLineSymbol.Style.SOLID, BLUE, 2));

      // set the default symbol
      uniqueValueRenderer.setDefaultSymbol(defaultFillSymbol);
      uniqueValueRenderer.setDefaultLabel("Other");

      // set value for California, Arizona, and Nevada
      List<Object> californiaValue = new ArrayList<>();
      californiaValue.add("CA");
      uniqueValueRenderer.getUniqueValues().add(new UniqueValue("State of California", "California",
          californiaFillSymbol, californiaValue));

      List<Object> arizonaValue = new ArrayList<>();
      arizonaValue.add("AZ");
      uniqueValueRenderer.getUniqueValues().add(new UniqueValue("State of Arizona", "Arizona", arizonaFillSymbol,
          arizonaValue));

      List<Object> nevadaValue = new ArrayList<>();
      nevadaValue.add("NV");
      uniqueValueRenderer.getUniqueValues().add(new UniqueValue("State of Nevada", "Nevada", nevadaFillSymbol,
          nevadaValue));

      // set the renderer on the feature layer
      featureLayer.setRenderer(uniqueValueRenderer);

      // add the feature layer to the map
      map.getOperationalLayers().add(featureLayer);

      // add the map view and control panel to stack pane
      stackPane.getChildren().add(mapView);
    } catch (Exception e) {
      e.printStackTrace();
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
