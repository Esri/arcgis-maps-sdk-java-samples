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

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import com.esri.arcgisruntime.datasource.arcgis.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Point;
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

/**
 * This sample demonstrates how to use an UniqueValueRenderer to style Features
 * from a FeatureLayer with symbols which represent an attribute value of a
 * Feature.
 * <p>
 * {@link UniqueValueRenderer} displays UniqueValues on a Map.
 * <p>
 * {@link UniqueValue} stores a label, description, symbol, and attribute values
 * to which the symbol is applied.
 * <h4>How it Works</h4>
 * 
 * An UniqueValueRenderer is created and set to the FeatureLayer. Some
 * UniqueValues are then added to the renderer using the
 * {@link UniqueValueRenderer#getUniqueValues} method. When the FeatureLayer is
 * loaded, the UniqueValueRenderer will display those Features using the symbols
 * described from it's list of UniqueValues.
 */
public class UniqueValueRendererSample extends Application {

  private MapView mapView;

  private static final String SERVICE_FEATURE_URL =
      "https://sampleserver6.arcgisonline.com/arcgis/rest/services/Census/MapServer/3";
  private static final String SAMPLES_THEME_PATH =
      "../resources/SamplesTheme.css";

  @Override
  public void start(Stage stage) throws Exception {

    // create stack pane and application scene
    StackPane stackPane = new StackPane();
    Scene scene = new Scene(stackPane);
    scene.getStylesheets().add(getClass().getResource(SAMPLES_THEME_PATH)
        .toExternalForm());

    // set size of stage, add a title, and set scene to stage
    stage.setTitle("Unique Value Renderer Sample");
    stage.setWidth(800);
    stage.setHeight(700);
    stage.setScene(scene);
    stage.show();

    // create a control panel
    VBox vBoxControl = new VBox(6);
    vBoxControl.setMaxSize(250, 190);
    vBoxControl.getStyleClass().add("panel-region");

    // create sample description
    Label descriptionLabel = new Label("Sample Description:");
    descriptionLabel.getStyleClass().add("panel-label");
    TextArea description = new TextArea("This sample demonstrates how to use a "
        + "Unique Value Renderer to style Features from a Feature Layer with "
        + "symbols which represent an attribute value of a Feature.");
    description.setWrapText(true);
    description.autosize();
    description.setEditable(false);

    // add label and sample description to the control panel
    vBoxControl.getChildren().addAll(descriptionLabel, description);
    try {

      // create map with topographic basemap
      final Map map = new Map(Basemap.createTopographic());

      // create a view and set map to it
      mapView = new MapView();
      mapView.setMap(map);

      // create service feature table from URL
      final ServiceFeatureTable serviceFeatureTable =
          new ServiceFeatureTable(SERVICE_FEATURE_URL);

      // set name for all fields added to this table
      serviceFeatureTable.getOutFields().add(0, "STATE_ABBR");

      // create the feature layer using the service feature table
      final FeatureLayer featureLayer = new FeatureLayer(serviceFeatureTable);

      // override the feature layer renderer with a new unique value renderer
      UniqueValueRenderer uniqueValueRenderer = new UniqueValueRenderer();
      // get all fields from the renderer
      uniqueValueRenderer.getFieldNames().add("STATE_ABBR");

      // create the symbols to be used in the renderer
      SimpleFillSymbol defaultFillSymbol = new SimpleFillSymbol(
          new RgbColor(0, 0, 0, 0), SimpleFillSymbol.Style.NULL,
          new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID,
              new RgbColor(211, 211, 211, 255), 2, 1f), 1f);

      SimpleFillSymbol californiaFillSymbol = new SimpleFillSymbol(
          new RgbColor(255, 0, 0, 255), SimpleFillSymbol.Style.SOLID,
          new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID,
              new RgbColor(255, 0, 0, 255), 2, 1f), 1f);

      SimpleFillSymbol arizonaFillSymbol = new SimpleFillSymbol(
          new RgbColor(0, 255, 0, 255), SimpleFillSymbol.Style.SOLID,
          new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID,
              new RgbColor(0, 255, 0, 255), 2, 1f), 1f);

      SimpleFillSymbol nevadaFillSymbol = new SimpleFillSymbol(
          new RgbColor(0, 0, 255, 255), SimpleFillSymbol.Style.SOLID,
          new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID,
              new RgbColor(0, 0, 255, 255), 2, 1f), 1f);

      // set the default symbol
      uniqueValueRenderer.setDefaultSymbol(defaultFillSymbol);
      uniqueValueRenderer.setDefaultLabel("Other");

      // set value for California, Arizona, and Nevada
      List<Object> californiaValue = new ArrayList<>();
      californiaValue.add("CA");
      uniqueValueRenderer.getUniqueValues()
          .add(new UniqueValue("California", "State of California",
              californiaFillSymbol, californiaValue));

      List<Object> arizonaValue = new ArrayList<>();
      arizonaValue.add("AZ");
      uniqueValueRenderer.getUniqueValues()
          .add(new UniqueValue("Arizona", "State of Arizona", arizonaFillSymbol,
              arizonaValue));

      List<Object> nevadaValue = new ArrayList<>();
      nevadaValue.add("NV");
      uniqueValueRenderer.getUniqueValues()
          .add(new UniqueValue("Nevada", "State of Nevada", nevadaFillSymbol,
              nevadaValue));

      // set the renderer on the feature layer
      featureLayer.setRenderer(uniqueValueRenderer);

      // add the layer to the map
      map.getOperationalLayers().add(featureLayer);

      // create initial viewpoint using a envelope
      Point leftPoint = new Point(-13893029.0, 3573174.0,
          SpatialReferences.getWebMercator());
      Point rightPoint = new Point(-12038972.0, 5309823.0,
          SpatialReferences.getWebMercator());
      Envelope envelope = new Envelope(leftPoint, rightPoint);

      // set viewpoint on map
      Viewpoint viewpoint = new Viewpoint(envelope);
      map.setInitialViewpoint(viewpoint);

      // add the map view and control panel to stack pane
      stackPane.getChildren().addAll(mapView, vBoxControl);
      StackPane.setAlignment(vBoxControl, Pos.TOP_LEFT);
      StackPane.setMargin(vBoxControl, new Insets(10, 0, 0, 10));
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
