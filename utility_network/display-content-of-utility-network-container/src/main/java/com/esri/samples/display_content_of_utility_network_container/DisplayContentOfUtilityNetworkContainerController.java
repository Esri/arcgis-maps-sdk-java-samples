/*
 * Copyright 2021 Esri.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.esri.samples.display_content_of_utility_network_container;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.ArcGISFeature;
import com.esri.arcgisruntime.data.ArcGISFeatureTable;
import com.esri.arcgisruntime.data.ServiceGeodatabase;
import com.esri.arcgisruntime.geometry.*;
import com.esri.arcgisruntime.layers.SubtypeFeatureLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.*;
import com.esri.arcgisruntime.symbology.*;
import com.esri.arcgisruntime.utilitynetworks.*;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Alert;

import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.security.UserCredential;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.util.*;
import java.util.concurrent.ExecutionException;

public class DisplayContentOfUtilityNetworkContainerController {

  @FXML
  private MapView mapView;
  @FXML
  private ProgressIndicator progressIndicator;

  private ArcGISFeature containerFeature;
  private GraphicsOverlay graphicsOverlay;
  private SimpleLineSymbol boundingBoxSymbol;
  private SimpleLineSymbol attachmentSymbol;
  private SimpleLineSymbol connectivitySymbol;
  private UtilityNetwork utilityNetwork;
  private Viewpoint previousViewpoint;

  public void initialize() {

    try {

      // create a new map from the web map URL (includes ArcGIS Pro subtype group layers with only container features visible)
      ArcGISMap map = new ArcGISMap("https://sampleserver7.arcgisonline.com/portal/home/item.html?id=813eda749a9444e4a9d833a4db19e1c8");

      // create a new graphics overlay to display container view contents
      graphicsOverlay = new GraphicsOverlay();
      mapView.getGraphicsOverlays().add(graphicsOverlay);

      // create three new simple line symbols for displaying container view features
      boundingBoxSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.DASH, ColorUtil.colorToArgb(Color.YELLOW), 3);
      attachmentSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.DOT, ColorUtil.colorToArgb(Color.BLUE), 3);
      connectivitySymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.DOT, ColorUtil.colorToArgb(Color.RED), 3);


      String featureServiceURL =
        "https://sampleserver7.arcgisonline.com/server/rest/services/UtilityNetwork/NapervilleGas/FeatureServer";
      // set user credentials to authenticate with the service
      // NOTE: a licensed user is required to perform utility network operations
      var userCredential = new UserCredential("viewer01", "I68VGU^nMurF");
      // create a new service geodatabase from the feature service url and set the user credential
      var serviceGeodatabase = new ServiceGeodatabase(featureServiceURL);
      serviceGeodatabase.setCredential(userCredential);

      // load the service geodatabase and get tables by their layer IDs
      serviceGeodatabase.loadAsync();
      serviceGeodatabase.addDoneLoadingListener(() -> {
        if (serviceGeodatabase.getLoadStatus() == LoadStatus.LOADED) {

          // create and add the utility network to the map before loading
          utilityNetwork = new UtilityNetwork(featureServiceURL);
          map.getUtilityNetworks().add(utilityNetwork);
          // load the utility network
          utilityNetwork.loadAsync();
          utilityNetwork.addDoneLoadingListener(() -> {
            if (utilityNetwork.getLoadStatus() == LoadStatus.LOADED) {


            } else {
              new Alert(Alert.AlertType.ERROR, "Error loading Utility Network.").show();
            }
          });

        } else {
          new Alert(Alert.AlertType.ERROR, "Failed to load service geodatabase").show();
        }
      });

      mapView.addDrawStatusChangedListener(listener -> {
        if (listener.getDrawStatus() == DrawStatus.COMPLETED) {

          progressIndicator.setVisible(false);


        }
        System.out.println(listener.getDrawStatus());
      });


      mapView.setMap(map);
      mapView.setViewpoint(new Viewpoint(41.801504, -88.163718, 4e3));

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * @param e event registered when the map view is clicked on
   */
  @FXML
  private void handleMapViewClicked(MouseEvent e) {

    if (e.getButton() == MouseButton.PRIMARY && e.isStillSincePress()) {
      // get the clicked map point
      Point2D screenPoint = new Point2D(e.getX(), e.getY());
      Point mapScreenPoint = mapView.screenToLocation(screenPoint);
      System.out.println("clicked the map view");

      // identify the feature to be used
      ListenableFuture<List<IdentifyLayerResult>> identifyLayerResultsFuture =
        mapView.identifyLayersAsync(screenPoint, 10, false);

      identifyLayerResultsFuture.addDoneListener(() -> {
        try {
          // get the result of the query
          List<IdentifyLayerResult> identifyLayerResults = identifyLayerResultsFuture.get();

          // return if no features are identified
          if (!identifyLayerResults.isEmpty()) {

            identifyLayerResults.forEach(layerResult -> {

              if (containerFeature == null && layerResult.getLayerContent() instanceof SubtypeFeatureLayer) {
                var subLayerResult = layerResult.getSublayerResults();
                subLayerResult.forEach(subResult -> {
                  subResult.getElements().forEach(geoElement -> {
                    if (geoElement instanceof ArcGISFeature) {
                      containerFeature = (ArcGISFeature) geoElement;
                    }
                  });
                });
              }
            });

            if (containerFeature == null) {
              System.out.println("container feature is null");
            }

            UtilityElement containerElement = utilityNetwork.createElement(containerFeature);

            // get the containment associations from this element to display its content
            ListenableFuture<List<UtilityAssociation>> containmentAssociationsFuture = utilityNetwork.getAssociationsAsync(containerElement, UtilityAssociationType.CONTAINMENT);
            containmentAssociationsFuture.addDoneListener(() -> {
              try {

                // get and store a list of features from the result of the query (there may be more than one)
                List<UtilityElement> contentElements = new ArrayList<>();
                List<UtilityAssociation> containmentAssociations = containmentAssociationsFuture.get();
                containmentAssociations.forEach(association -> {
                  UtilityElement otherElement = association.getFromElement().getObjectId() == containerElement.getObjectId() ? association.getToElement() : association.getFromElement();
                  contentElements.add(otherElement);
                });
                if (contentElements.size() > 0) {

                  previousViewpoint = mapView.getCurrentViewpoint(Viewpoint.Type.BOUNDING_GEOMETRY);
                  mapView.getMap().getOperationalLayers().forEach(layer -> {
                    layer.setVisible(false);
                  });

                  // make container view visible TODO

                  ListenableFuture<List<ArcGISFeature>> fetchFeaturesFuture = utilityNetwork.fetchFeaturesForElementsAsync(contentElements);

                  fetchFeaturesFuture.addDoneListener(() -> {
                    try {
                      List<ArcGISFeature> contentFeatures = fetchFeaturesFuture.get();
                      contentFeatures.forEach( content -> {
                        Symbol symbol = ((ArcGISFeatureTable) content.getFeatureTable()).getLayerInfo().getDrawingInfo().getRenderer().getSymbol(content);
                        graphicsOverlay.getGraphics().add(new Graphic(content.getGeometry(), symbol));

                      });

                      // The bounding box which defines the container view may be computed using the extent of the features it contain
                      // or centered around it's geometry at the container's view scale.
                      Geometry boundingBox;

                      if (graphicsOverlay.getGraphics().size() == 1 && graphicsOverlay.getGraphics().get(0).getGeometry() instanceof Point) {

                        mapView.setViewpointCenterAsync(mapScreenPoint, containerElement.getAssetType().getContainerViewScale());
                        boundingBox = mapView.getCurrentViewpoint(Viewpoint.Type.BOUNDING_GEOMETRY).getTargetGeometry();

                      } else {
                        boundingBox = GeometryEngine.buffer(graphicsOverlay.getExtent(), 0.05);
                      }

                      graphicsOverlay.getGraphics().add(new Graphic(boundingBox, boundingBoxSymbol));
                      mapView.setViewpointGeometryAsync(GeometryEngine.buffer(graphicsOverlay.getExtent(), 0.05));

                      // Get the associations for this extent to display how content features are attached or connected.
                      ListenableFuture<List<UtilityAssociation>> extentAssociations = utilityNetwork.getAssociationsAsync(graphicsOverlay.getExtent());

                      extentAssociations.addDoneListener(() -> {
                        try {

                          extentAssociations.get().forEach( association -> {
                            Symbol symbol = association.getAssociationType() == UtilityAssociationType.ATTACHMENT ? attachmentSymbol :
                              connectivitySymbol;
                            graphicsOverlay.getGraphics().add(new Graphic(association.getGeometry(), symbol));
                          });

                        } catch (Exception ex) {
                          new Alert(Alert.AlertType.ERROR, "Error getting extent associations").show();
                        }
                      });



                    } catch (Exception ex) {
                      new Alert(Alert.AlertType.ERROR, "Error fetching features for elements.").show();
                    }

                  });
                }

              } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, "Error getting containment associations.").show();
              }
            });

          }


        } catch (InterruptedException | ExecutionException ex) {
          new Alert(Alert.AlertType.ERROR, "Error getting result of the query.").show();
        }

      });

    };

  }

  /**
   * Stops and releases all resources used in application.
   */
  public void terminate() {

    if (mapView != null) {
      mapView.dispose();
    }
  }
}
