/*
 * Copyright 2018 Esri.
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

package com.esri.samples.identify_kml_features;

import java.util.concurrent.ExecutionException;

import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.KmlLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.GeoElement;
import com.esri.arcgisruntime.mapping.view.IdentifyLayerResult;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.ogc.kml.KmlDataset;
import com.esri.arcgisruntime.ogc.kml.KmlPlacemark;

public class IdentifyKMLFeaturesSample extends Application {

  private MapView mapView;

  @Override
  public void start(Stage stage) {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);

      // set title, size, and add scene to stage
      stage.setTitle("Identify KML Features Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // authentication with an API key or named user is required to access basemaps and other location services
      String yourAPIKey = System.getProperty("apiKey");
      ArcGISRuntimeEnvironment.setApiKey(yourAPIKey);

      // create a map with a basemap style
      ArcGISMap map = new ArcGISMap(BasemapStyle.ARCGIS_DARK_GRAY);

      // create a map view and set its map
      mapView = new MapView();
      mapView.setMap(map);

      // start zoomed in over the US
      mapView.setViewpointGeometryAsync(new Envelope(-19195297.778679, 512343.939994, -3620418.579987, 8658913.035426, 0.0, 0.0, SpatialReferences.getWebMercator()));

      // create a KML dataset of weather forecasts
      KmlDataset forecastDataset = new KmlDataset("https://www.wpc.ncep.noaa.gov/kml/noaa_chart/WPC_Day1_SigWx.kml");

      // create a KML layer and add it as an operational layer
      KmlLayer forecastLayer = new KmlLayer(forecastDataset);
      map.getOperationalLayers().add(forecastLayer);

      // add a click listener to identify clicked features
      mapView.setOnMouseClicked(e -> {
        // hide the callout if it's showing
        mapView.getCallout().dismiss();

        if (e.isStillSincePress() && e.getButton() == MouseButton.PRIMARY) {
          // get the identified geoelements at the clicked location
          Point2D screenPoint = new Point2D(e.getX(), e.getY());
          ListenableFuture<IdentifyLayerResult> identify = mapView.identifyLayerAsync(forecastLayer, screenPoint, 15, false);
          identify.addDoneListener(() -> {
            try {
              IdentifyLayerResult result = identify.get();
              // find the first geoElement that is a KML placemark
              for (GeoElement geoElement : result.getElements()) {
                if (geoElement instanceof KmlPlacemark) {
                  // show a callout at the placemark with custom content using the placemark's "balloon content"
                  KmlPlacemark placemark = (KmlPlacemark) geoElement;
                  VBox vBox = new VBox();
                  WebView webView = new WebView();
                  webView.setMaxSize(400, 100);
                  webView.getEngine().loadContent(placemark.getBalloonContent());
                  vBox.getChildren().add(webView);
                  mapView.getCallout().setCustomView(vBox);
                  Point interactionPoint = mapView.screenToLocation(screenPoint);
                  mapView.getCallout().showCalloutAt(geoElement, interactionPoint);
                  break;
                }
              }
            } catch (InterruptedException | ExecutionException ex) {
              new Alert(Alert.AlertType.ERROR, "Error identifying features in layer").show();
            }
          });
        }
      });

      // add the map view to stack pane
      stackPane.getChildren().add(mapView);
    } catch (Exception e) {
      // on any error, display the stack trace.
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
