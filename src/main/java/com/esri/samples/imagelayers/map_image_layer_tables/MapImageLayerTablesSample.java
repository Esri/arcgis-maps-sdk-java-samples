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

package com.esri.samples.imagelayers.map_image_layer_tables;

import java.util.List;
import java.util.concurrent.ExecutionException;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.arcgisservices.RelationshipInfo;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.ArcGISFeature;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.RelatedFeatureQueryResult;
import com.esri.arcgisruntime.data.RelatedQueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.layers.ArcGISMapImageLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;

public class MapImageLayerTablesSample extends Application {

  private MapView mapView;
  private GraphicsOverlay graphicsOverlay;
  private ServiceFeatureTable commentsTable;
  private ListView<Feature> commentsListView;

  /**
   * Starting point of this application.
   *
   * @param args arguments to this application.
   */
  public static void main(String[] args) {

    Application.launch(args);
  }

  @Override
  public void start(Stage stage) {

    try {
      // create a stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);
      scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

      // size the stage and add a title
      stage.setTitle("Map Image Layer Tables Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // create a map with a basemap
      ArcGISMap map = new ArcGISMap(Basemap.createStreetsVector());

      // create and add a map image layer to the map
      // the map image layer contains a feature table with related spatial and non-spatial comment features
      ArcGISMapImageLayer imageLayer = new ArcGISMapImageLayer(
          "https://sampleserver6.arcgisonline.com/arcgis/rest/services/ServiceRequest/MapServer");
      map.getOperationalLayers().add(imageLayer);

      // create a map view and set the map to it
      mapView = new MapView();
      mapView.setMap(map);

      // create a graphics overlay to show the related spatial features in
      graphicsOverlay = new GraphicsOverlay();
      mapView.getGraphicsOverlays().add(graphicsOverlay);

      // show the related graphics as cyan circles
      SimpleRenderer renderer = new SimpleRenderer();
      renderer.setSymbol(new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, 0xFF00FFFF, 14));
      graphicsOverlay.setRenderer(renderer);

      // create a list view to show the non-spatial comment features
      commentsListView = new ListView<>();
      commentsListView.setMaxSize(200.0, 150.0);
      // show the comments attribute of the feature in the list
      commentsListView.setCellFactory(listView -> new ListCell<>() {
        @Override
        protected void updateItem(Feature item, boolean empty) {
          super.updateItem(item, empty);
          if (item != null) {
            ArcGISFeature feature = (ArcGISFeature) item;
            setText((String) feature.getAttributes().get("comments"));
          }
        }
      });

      // when a comment is selected, query its related spatial features and show the first result on the map
      commentsListView.getSelectionModel().selectedItemProperty().addListener(observable -> showRelatedRequests());

      // when the layer is loaded, get the comment features
      imageLayer.addDoneLoadingListener(() -> {
        if (imageLayer.getLoadStatus() == LoadStatus.LOADED) {
          // zoom to the layer's extent
          mapView.setViewpoint(new Viewpoint(imageLayer.getFullExtent()));

          // get the comments feature table
          commentsTable = imageLayer.getTables().get(0);

          // create query parameters to get features that have non-empty comments
          QueryParameters queryParameters = new QueryParameters();
          queryParameters.setWhereClause("requestid <> '' AND comments <> ''");

          // query the comments table for features
          ListenableFuture<FeatureQueryResult> featureQuery = commentsTable.queryFeaturesAsync(queryParameters);
          featureQuery.addDoneListener(() -> {
            try {
              // add the returned features to the list view
              FeatureQueryResult results = featureQuery.get();
              for (Feature f : results) {
                commentsListView.getItems().addAll(f);
              }
            } catch (InterruptedException | ExecutionException ex) {
              new Alert(Alert.AlertType.ERROR, "Error querying comment features");
            }
          });
        } else {
          new Alert(Alert.AlertType.ERROR, imageLayer.getLoadError().getMessage()).show();
        }
      });

      // add the mapview and controls to the stack pane
      stackPane.getChildren().addAll(mapView, commentsListView);
      StackPane.setAlignment(commentsListView, Pos.TOP_LEFT);
      StackPane.setMargin(commentsListView, new Insets(10, 0, 0, 10));

    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
    }
  }

  /**
   * Queries for spatial features related to the selected comment in the list view and shows the first result on the
   * map as a graphic.
   */
  private void showRelatedRequests() {
    // clear any previous results
    graphicsOverlay.getGraphics().clear();

    // get the selected comment feature from the list view
    Feature selectedCommentFeature = commentsListView.getSelectionModel().getSelectedItem();
    if (selectedCommentFeature != null) {

      // get the relationships info between layers in the table
      ArcGISFeature feature = (ArcGISFeature) selectedCommentFeature;
      List<RelationshipInfo> relationshipInfos = commentsTable.getLayerInfo().getRelationshipInfos();
      if (!relationshipInfos.isEmpty()) {

        // use the first relationship for the related query parameters
        RelationshipInfo commentsRelationshipInfo = relationshipInfos.get(0);
        RelatedQueryParameters relatedQueryParameters = new RelatedQueryParameters(commentsRelationshipInfo);
        relatedQueryParameters.setReturnGeometry(true);

        // query the table for related features using the parameters
        ListenableFuture<List<RelatedFeatureQueryResult>> relatedFeaturesRequest = commentsTable
            .queryRelatedFeaturesAsync(feature, relatedQueryParameters);
        relatedFeaturesRequest.addDoneListener(() -> {
          try {
            // loop through the returned related features
            List<RelatedFeatureQueryResult> results = relatedFeaturesRequest.get();
            if (!results.isEmpty()) {
              RelatedFeatureQueryResult relatedResult = results.get(0);
              if (relatedResult.iterator().hasNext()) {
                // get the first related feature
                ArcGISFeature relatedFeature = (ArcGISFeature) relatedResult.iterator().next();
                // load the feature and get its geometry to show as a graphic on the map
                relatedFeature.loadAsync();
                relatedFeature.addDoneLoadingListener(() -> {
                  if (relatedFeature.getLoadStatus() == LoadStatus.LOADED) {
                    Point point = (Point) relatedFeature.getGeometry();
                    Graphic graphic = new Graphic(point);
                    graphicsOverlay.getGraphics().add(graphic);
                    // zoom to the graphic
                    mapView.setViewpointCenterAsync(point, 40000);
                  }
                });
              }
            } else {
              new Alert(Alert.AlertType.INFORMATION, "No related features found").show();
            }
          } catch (InterruptedException | ExecutionException ex) {
            new Alert(Alert.AlertType.ERROR, "Failed to query relationships").show();
          }
        });
      }
    }

  }

  @Override
  public void stop() {

    // releases resources when the application closes
    if (mapView != null) {
      mapView.dispose();
    }
  }
}
