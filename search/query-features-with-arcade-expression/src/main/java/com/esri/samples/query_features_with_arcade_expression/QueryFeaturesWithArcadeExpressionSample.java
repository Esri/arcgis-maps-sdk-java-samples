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

package com.esri.samples.query_features_with_arcade_expression;

import java.util.HashMap;

import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.arcade.ArcadeEvaluator;
import com.esri.arcgisruntime.arcade.ArcadeExpression;
import com.esri.arcgisruntime.arcade.ArcadeProfile;
import com.esri.arcgisruntime.data.ArcGISFeature;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.layers.Layer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.portal.Portal;
import com.esri.arcgisruntime.portal.PortalItem;

public class QueryFeaturesWithArcadeExpressionSample extends Application {

  private MapView mapView;
  private ArcGISMap map;
  private Layer policeBeatsLayer;
  private final String rpdBeatsLayerName = "RPD Beats  - City_Beats_Border_1128-4500";
  private final String calloutText = "Crimes in the last 60 days: ";

  @Override
  public void start(Stage stage) {
    try {
      // create stack pane and application scene
      var stackPane = new StackPane();
      var scene = new Scene(stackPane);

      // set title, size, and add scene to stage
      stage.setTitle("Query Features With Arcade Expression Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // authentication with an API key or named user is required to access basemaps and other location services
      String yourAPIKey = System.getProperty("apiKey");
      ArcGISRuntimeEnvironment.setApiKey(yourAPIKey);

      // create a portal item and use it to create a map
      var portal = new Portal("https://www.arcgis.com/");
      var portalItem = new PortalItem(portal, "539d93de54c7422f88f69bfac2aebf7d");
      map = new ArcGISMap(portalItem);

      // create a map view and set the map to it
      mapView = new MapView();
      mapView.setMap(map);

      // set callout properties that never change
      mapView.getCallout().setLeaderPosition(Callout.LeaderPosition.BOTTOM);
      mapView.getCallout().setTitle("RPD Beats");

      // wait for the map to finish loading data from portal before accessing its layers
      map.addDoneLoadingListener(() -> {
        if (map.getLoadStatus() == LoadStatus.LOADED) {
          // hide all layers other than the RPD Beats Layer to reduce clutter
          map.getOperationalLayers().forEach(layer -> layer.setVisible(layer.getName().equals(rpdBeatsLayerName)));

          // get the RPD beats layer.
          policeBeatsLayer = map.getOperationalLayers().stream().filter(
            layer -> layer.getName().equals(rpdBeatsLayerName)).toList().get(0);

          // if map clicked, evaluate an arcade expression at that point
          mapView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.isStillSincePress()) {
              try {
                evaluateArcadeExpression(new Point2D(event.getX(), event.getY()));
              } catch (Exception e) {
                throw new RuntimeException(e);
              }
            }});
        } else if (map.getLoadStatus() == LoadStatus.FAILED_TO_LOAD) {
          new Alert(Alert.AlertType.ERROR, "Map failed to load").show();
        }
      });

      // add the map view to the stack pane
      stackPane.getChildren().addAll(mapView);
    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
    }
  }

  /**
   * Evaluates an arcade expression at the point on the map that was clicked.
   *
   * @param point The point on the map which was clicked, in screen (rather than map) coordinates.
   * @throws RuntimeException If unable to get results from futures due to interruption or concurrent execution.
   */
  private void evaluateArcadeExpression(Point2D point) throws RuntimeException {
    // convert the passed point from screen coordinates to map coordinates, for use in displaying the callout
    Point mapPoint = mapView.screenToLocation(point);

    var identifyLayerResultFuture = mapView.identifyLayerAsync(policeBeatsLayer, point,
      0, false);
    identifyLayerResultFuture.addDoneListener(() -> {
      if (identifyLayerResultFuture.isDone()) {
        try {
          var identifyLayerResult = identifyLayerResultFuture.get();

          // only execute query if the police beats layer has data at the selected point
          if (identifyLayerResult.getElements().size() != 0) {
            ArcGISFeature feature = (ArcGISFeature) identifyLayerResult.getElements().get(0);

            // show callout and text without data
            mapView.getCallout().setDetail(calloutText);
            mapView.getCallout().showCalloutAt(mapPoint);
            mapView.getCallout().setVisible(true);

            //setup arcade expression and evaluator
            String expressionString = "var crimes = FeatureSetByName($map, 'Crime in the last 60 days');\n" +
              "return Count(Intersects($feature, crimes));";
            var arcadeExpression = new ArcadeExpression(expressionString);
            var arcadeEvaluator = new ArcadeEvaluator(arcadeExpression, ArcadeProfile.FORM_CALCULATION);

            // instantiate key/value pairs
            var hashMap = new HashMap<String, Object>();
            hashMap.put("$feature", feature);
            hashMap.put("$map", mapView.getMap());

            // evaluate the arcade expression asynchronously
            var resultFuture = arcadeEvaluator.evaluateAsync(hashMap);

            // wait for the expression to finish evaluating before attempting to access it
            resultFuture.addDoneListener(() -> {
              if (resultFuture.isDone()) {
                try {
                  // get result from async method and cast to int
                  var arcadeEvaluationResult = resultFuture.get();
                  var crimesCount = (int) ((double) arcadeEvaluationResult.getResult());

                  // add data from arcade evaluation to callout
                  mapView.getCallout().setDetail(calloutText + crimesCount);
                } catch (Exception e) {
                  throw new RuntimeException(e);
                }
              }});
          } else {
            mapView.getCallout().setVisible(false);
          }
        } catch (Exception e) {
          // resend any exceptions from the method body to be dealt with further up the call stack
          throw new RuntimeException(e);
        }
      }});
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
