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

package com.esri.samples.wfs_xml_query;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.ogc.wfs.OgcAxisOrder;
import com.esri.arcgisruntime.ogc.wfs.WfsFeatureTable;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class WfsXmlQuerySample extends Application {

  private MapView mapView;
  private ListenableFuture<FeatureQueryResult> featureTableResult; // keeps loadable in scope to avoid garbage collection

  @Override
  public void start(Stage stage) throws IOException {

    // create stack pane and JavaFX app scene
    StackPane stackPane = new StackPane();
    Scene scene = new Scene(stackPane);

    // set title, size, and add JavaFX scene to stage
    stage.setTitle("Load WFS with XML Query");
    stage.setWidth(800);
    stage.setHeight(700);
    stage.setScene(scene);
    stage.show();

    // create a progress indicator
    ProgressIndicator progressIndicator = new ProgressIndicator();
    progressIndicator.setVisible(true);

    // create an ArcGISMap with topographic basemap and set it to the map view
    ArcGISMap map = new ArcGISMap(Basemap.createNavigationVector());
    mapView = new MapView();
    mapView.setMap(map);

     // create a FeatureTable from the WFS service URL and layer name
    WfsFeatureTable wfsFeatureTable = new WfsFeatureTable(
            "https://dservices2.arcgis.com/ZQgQTuoyBrtmoGdP/arcgis/services/Seattle_Downtown_Features/WFSServer?service=wfs&request=getcapabilities",
            "Seattle_Downtown_Features:Trees");

    // set the feature request mode and axis order
    wfsFeatureTable.setAxisOrder(OgcAxisOrder.NO_SWAP);
    wfsFeatureTable.setFeatureRequestMode(ServiceFeatureTable.FeatureRequestMode.MANUAL_CACHE);

    // create a feature layer to visualize the WFS features
    FeatureLayer wfsFeatureLayer = new FeatureLayer(wfsFeatureTable);

    // add the layer to the map's operational layers
    map.getOperationalLayers().add(wfsFeatureLayer);

    // create an XML query to retrieve trees of genus Tilia.
    // To learn more about specifying filters in OGC technologies, see https://www.opengeospatial.org/standards/filter.
    String xmlQuery = IOUtils.toString(WfsXmlQuerySample.class.getResourceAsStream("/SeattleTreeQuery.xml"), StandardCharsets.UTF_8.name());

    // populate the WFS feature table with XML query
    featureTableResult = wfsFeatureTable.populateFromServiceAsync(xmlQuery, true);
    featureTableResult.addDoneListener(() -> {
      // set the viewpoint of the map view to the extent reported by the feature layer
      mapView.setViewpointGeometryAsync(wfsFeatureLayer.getFullExtent(), 50);
      progressIndicator.setVisible(false);
    });

    // add the mapview to the stackpane
    stackPane.getChildren().addAll(mapView, progressIndicator);
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
