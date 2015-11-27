/*
 * Copyright 2015 Esri.
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

package com.esri.sampleviewer.samples.featurelayers;

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

import com.esri.arcgisruntime.datasource.Feature;
import com.esri.arcgisruntime.datasource.arcgis.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Map;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.Viewpoint;

/**
 * This sample demonstrates how to use the ServiceFeatureTable's
 * on-interaction-no-cache mode. How it works: a {@link ServiceFeatureTable} is
 * created from a URL and its {@link ServiceFeatureTable.FeatureRequestMode} is
 * set to ON_INTERACTION_NO_CACHE before the ServiceFeatureTable is loaded. A
 * {@link FeatureLayer} is then created from this table and added to the Map so
 * its {@link Feature}s can be displayed in the MapView.
 * <p>
 * On-interaction-no-cache mode will always fetch Features from the server and
 * doesn't cache any Features on the client's side. This meaning that Features
 * will be fetched whenever the Map pans, zooms, selects, or queries.
 * <p>
 * To fetch all Features from the server, pan or zoom the MapView. This will
 * cause all the current Features to be destroyed and a request sent to the
 * server to re-populate all Features on the MapView.
 */
public class ServiceFeatureTableNoCache extends Application {

  private static final String SERVICE_FEATURE_URL =
      "http://sampleserver6.arcgisonline.com/arcgis/rest/services/PoolPermits/FeatureServer/0";

  private MapView mapView;

  private ServiceFeatureTable featureTable;

  @Override
  public void start(Stage stage) throws Exception {

    // create stack pane and application scene
    StackPane stackPane = new StackPane();
    Scene scene = new Scene(stackPane);
    scene.getStylesheets().add(getClass().getResource(
        "../resources/SamplesTheme.css").toExternalForm());

    // set title, size, and add scene to stage
    stage.setTitle("Service Feature Table No Cache Sample");
    stage.setWidth(800);
    stage.setHeight(700);
    stage.setScene(scene);
    stage.show();

    // create a control panel
    VBox vBoxControl = new VBox(6);
    vBoxControl.setMaxSize(250, 170);
    vBoxControl.getStyleClass().add("panel-region");

    // create sample description
    Label descriptionLabel = new Label("Sample Description:");
    descriptionLabel.getStyleClass().add("panel-label");
    TextArea description = new TextArea("This sample shows how to use " +
        "a Service Feature Table in on-interaction-no-cache mode. Pan or zoom" +
        " to request all Features from the server.");
    description.setWrapText(true);
    description.autosize();
    description.setEditable(false);

    // add label and sample description to the control panel
    vBoxControl.getChildren().addAll(descriptionLabel, description);

    try {
      Map map = new Map(Basemap.createTopographic());

      // create starting viewpoint for map
      SpatialReference spatialReference = SpatialReferences.getWebMercator();
      Point leftPoint = new Point(-1.30758164047166E7, 4014771.46954516,
          spatialReference);
      Point rightPoint = new Point(-1.30730056797177E7, 4016869.78617381,
          spatialReference);
      Envelope envelope = new Envelope(leftPoint, rightPoint);
      Viewpoint viewpoint = new Viewpoint(envelope);

      // set starting viewpoint for map
      map.setInitialViewpoint(viewpoint);

      // create service feature table from URL
      featureTable = new ServiceFeatureTable(SERVICE_FEATURE_URL);

      // set cache mode for table to no caching
      featureTable.setFeatureRequestMode(
          ServiceFeatureTable.FeatureRequestMode.ON_INTERACTION_NO_CACHE);
      FeatureLayer featureLayer = new FeatureLayer(featureTable);

      // add feature layer to map
      map.getOperationalLayers().add(featureLayer);

      // create a view for this map and set map to it
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
   *
   * @throws Exception if security manager doesn't allow JVM to exit with
   *           current status
   */
  @Override
  public void stop() throws Exception {

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
