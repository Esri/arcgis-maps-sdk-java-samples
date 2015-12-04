/* Copyright 2015 Esri.
 
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
 
    http://www.apache.org/licenses/LICENSE-2.0
 
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
limitations under the License.  */

package com.esri.sampleviewer.samples.symbology;

import java.util.ArrayList;
import java.util.List;

import com.esri.arcgisruntime.datasource.arcgis.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Map;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.Viewpoint;
import com.esri.arcgisruntime.symbology.RgbColor;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.UniqueValue;
import com.esri.arcgisruntime.symbology.UniqueValueRenderer;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * This sample demonstrates how to use a <@UniqueValueRenderer> to style
 * different <@Feature>s in a <@FeatureLayer> with different symbols. Features
 * do not have a symbol property for you to set, renderers should be used to
 * define the symbol for Features in FeatureLayers. The UniqueValueRenderer
 * allows for separate symbols to be used for Features that have specific
 * attribute values in a defined field. How it works, first a
 * <@ServiceFeatureTable> and FeatureLayer are constructed and added to the
 * <@Map>. Then a UniqueValueRender is created and the field name to be used as
 * the renderer field is set ("STATE_ABBR"). You can use multiple fields, this
 * sample only uses one. Multiple <@SimpleFillSymbol>s are defined for each type
 * of feature we want to render differently (in this case, different states of
 * the USA). SimpleFillSymbols can be applied to polygon Features, these are the
 * types of Features found in the Feature service used for this
 * ServiceFeatureTable. A default symbol is also created, this will be used for
 * all other Features that don't match the <@UniqueValue>s defined. Separate
 * UniqueValue objects are created which define the values in the renderer field
 * and what symbol should be used for Features that match. These are added to
 * the UniqueValues collection. The renderer is set on the layer and is rendered
 * in the <@MapView> accordingly.
 */
public class UniqueValueRendererSample extends Application {

  private MapView mapView;

  private final String SAMPLE_SERVICE_URL =
      "https://sampleserver6.arcgisonline.com/arcgis/rest/services/Census/MapServer/3";

  @Override
  public void start(Stage stage) throws Exception {

    // create border pane and application scene
    BorderPane borderPane = new BorderPane();
    Scene scene = new Scene(borderPane);

    // set size of stage, add a title, and set scene to stage
    stage.setTitle("Unique Value Renderer Sample");
    stage.setHeight(700);
    stage.setWidth(800);
    stage.setScene(scene);
    stage.show();

    try {
      // create view for this map
      mapView = new MapView();

      // create map with topographic basemap
      Map map = new Map(Basemap.createTopographic());

      // set map to be displayed in mapview
      mapView.setMap(map);

      // set map in border pane
      borderPane.setCenter(mapView);

      // create service feature table
      ServiceFeatureTable serviceFeatureTable = new ServiceFeatureTable(
          SAMPLE_SERVICE_URL);
      // ensure that the fields used in the renderer are specified as outfields
      // (by default when creating a ServiceFeatureTable, only the minimal set
      // of fields required for rendering are requested.)
      serviceFeatureTable.getOutFields().add(0, "STATE_ABBR");

      // create the feature layer using the service feature table
      FeatureLayer featureLayer = new FeatureLayer(serviceFeatureTable);

      // override the feature layer renderer with a new unique value renderer
      UniqueValueRenderer uniqueValueRenderer = new UniqueValueRenderer();
      // get field names you can add multiple fields to be used for the renderer
      // in the form of a list, in this case we are only adding a single field
      uniqueValueRenderer.getFieldNames().add("STATE_ABBR");

      // create the symbols to be used in the renderer
      SimpleFillSymbol defaultFillSymbol = new SimpleFillSymbol(
          new RgbColor(0, 0, 0, 0), SimpleFillSymbol.Style.NULL,
          new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID,
              new RgbColor(211, 211, 211, 255), 2, 1f),
          1f);
      SimpleFillSymbol californiaFillSymbol = new SimpleFillSymbol(
          new RgbColor(255, 0, 0, 255), SimpleFillSymbol.Style.SOLID,
          new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID,
              new RgbColor(255, 0, 0, 255), 2, 1f),
          1f);
      SimpleFillSymbol arizonaFillSymbol = new SimpleFillSymbol(
          new RgbColor(0, 255, 0, 255), SimpleFillSymbol.Style.SOLID,
          new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID,
              new RgbColor(0, 255, 0, 255), 2, 1f),
          1f);
      SimpleFillSymbol nevadaFillSymbol = new SimpleFillSymbol(
          new RgbColor(0, 0, 255, 255), SimpleFillSymbol.Style.SOLID,
          new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID,
              new RgbColor(0, 0, 255, 255), 2, 1f),
          1f);

      // set the default symbol
      uniqueValueRenderer.setDefaultSymbol(defaultFillSymbol);
      uniqueValueRenderer.setDefaultLabel("Other");

      // set value for California, Arizona, and Nevada
      List<Object> californiaValue = new ArrayList<>();
      californiaValue.add("CA");
      uniqueValueRenderer.getUniqueValues().add(new UniqueValue("California",
          "State of California", californiaFillSymbol, californiaValue));

      List<Object> arizonaValue = new ArrayList<>();
      arizonaValue.add("AZ");
      uniqueValueRenderer.getUniqueValues().add(new UniqueValue("Arizona",
          "State of Arizona", arizonaFillSymbol, arizonaValue));

      List<Object> nevadaValue = new ArrayList<>();
      nevadaValue.add("NV");
      uniqueValueRenderer.getUniqueValues().add(new UniqueValue("Nevada",
          "State of Nevada", nevadaFillSymbol, nevadaValue));

      // set the renderer on the feature layer
      featureLayer.setRenderer(uniqueValueRenderer);

      // add the layer to the map
      map.getOperationalLayers().add(featureLayer);

      // create initial viewpoint using a envelope
      Envelope envelope = new Envelope(-13893029.0, 3573174.0, -12038972.0,
          5309823.0, 0, 0, 0, 0, SpatialReferences.getWebMercator());

      // set viewpoint on map
      Viewpoint viewpoint = new Viewpoint(envelope);
      map.setInitialViewpoint(viewpoint);

    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  /**
   * Stops and releases all resources used in application.
   * 
   * @throws Exception if security manager doesn't allow JVM to exit with
   *           current status
   */
  @Override
  public void stop() throws Exception {

    // release resources when the application closes
    if (mapView != null) {
      mapView.dispose();
    }
    Platform.exit();
    System.exit(0);
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
