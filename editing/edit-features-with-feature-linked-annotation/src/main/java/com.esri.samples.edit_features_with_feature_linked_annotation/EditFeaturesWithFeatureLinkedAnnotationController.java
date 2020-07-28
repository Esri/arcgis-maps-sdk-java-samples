/*
 * Copyright 2020 Esri.
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

package com.esri.samples.edit_features_with_feature_linked_annotation;

import java.util.List;

import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseButton;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.geometry.Part;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.Geodatabase;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.GeometryType;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.layers.AnnotationLayer;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.IdentifyLayerResult;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geometry.PolylineBuilder;
import com.esri.arcgisruntime.geometry.ProximityResult;

public class EditFeaturesWithFeatureLinkedAnnotationController {
  @FXML private MapView mapView;
  private FeatureLayer addressPointFeatureLayer;
  private FeatureLayer parcelLinesFeatureLayer;
  private AnnotationLayer addressPointsAnnotationLayer;
  private AnnotationLayer parcelLinesAnnotationLayer;
  private Feature selectedFeature = null;
  private boolean selectedFeatureIsPolyline = false;

  @FXML private void initialize() {

    try {
      // create the map with a light gray canvas basemap centered on Loudoun, Virginia
      ArcGISMap map = new ArcGISMap(Basemap.Type.LIGHT_GRAY_CANVAS_VECTOR, 39.0204, -77.4159, 18);

      // add a map to the map view
      mapView.setMap(map);

      // create the geodatabase from the local .geodatabase file
      Geodatabase geodatabase = new Geodatabase("loudoun_anno.geodatabase");
      geodatabase.addDoneLoadingListener(() -> {
        //load the geodatabase
        if (geodatabase.getLoadStatus() == LoadStatus.LOADED) {
          // create feature layers from tables in the geodatabase
          addressPointFeatureLayer = new FeatureLayer(geodatabase.getGeodatabaseFeatureTable("Loudoun_Address_Points_1"));
          parcelLinesFeatureLayer = new FeatureLayer(geodatabase.getGeodatabaseFeatureTable("ParcelLines_1"));

          // create annotation layers from tables in the geodatabase
          addressPointsAnnotationLayer = new AnnotationLayer(geodatabase.getGeodatabaseAnnotationTable("Loudoun_Address_PointsAnno_1"));
          parcelLinesAnnotationLayer = new AnnotationLayer(geodatabase.getGeodatabaseAnnotationTable("ParcelLinesAnno_1"));

          // add the feature layers to the map
          map.getOperationalLayers().add(addressPointFeatureLayer);
          map.getOperationalLayers().add(parcelLinesFeatureLayer);

          // add the annotation layers to the map
          map.getOperationalLayers().add(addressPointsAnnotationLayer);
          map.getOperationalLayers().add(parcelLinesAnnotationLayer);
        } else {
          // show alert if geodatabase fails to load
          new Alert(Alert.AlertType.ERROR, "Error loading Geodatabase.").show();
        }
      });
      geodatabase.loadAsync();

      // set on click behaviour
      mapView.setOnMouseClicked(event -> {
        // check that the primary mouse button was clicked
        if (event.isStillSincePress() && event.getButton() == MouseButton.PRIMARY) {
          // create a point where the user clicked
          Point2D screenpoint = new Point2D(event.getX(), event.getY());
          // call select or move method to move to the point
          selectOrMove(screenpoint);
        }
      });
    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
    }
  }

  /**
   * Select the nearest feature, or move the point or polyline vertex to the given screen point.
   */
  private void selectOrMove(Point2D screenPoint) {

    // if a feature hasn't been selected, select the feature
    if (selectedFeature == null) {
      selectFeature(screenPoint);
    } else {
      // convert the screen point to a map point
      Point mapPoint = mapView.screenToLocation(screenPoint);
      // if the feature is a polyline, move the polyline
      if (selectedFeatureIsPolyline) {
        movePolylineVertex(mapPoint);
      } else {
        // if the feature is a point, move the point
        movePoint(mapPoint);
      }
    }
  }

  /**
   * Select a feature near the given screen point using identify and, for a point feature, show a
   * dialog to edit attributes. Future clicks will call move functions.
   */
  private void selectFeature(Point2D screenPoint) {

    // clear any previously selected features
    clearSelection();
    selectedFeature = null;

    // identify across all layers
    ListenableFuture<List<IdentifyLayerResult>> identifyLayerResultFuture = mapView.identifyLayersAsync(screenPoint, 1, false, 20);

    // add a listener to the future
    identifyLayerResultFuture.addDoneListener(() -> {
      try {
        // get the list of results from the future
        List<IdentifyLayerResult> identifyLayersResults = identifyLayerResultFuture.get();
        // iterate all the layers in the identify results list
        identifyLayersResults.forEach(result -> {
          // iterate each result and check for Feature results
          result.getElements().forEach(identifiedElement -> {
            if (identifiedElement instanceof Feature) {
              // get a reference to the identified feature
              selectedFeature = (Feature) identifiedElement;
            }
            // if the selected feature is a polyline
            if (selectedFeature.getGeometry().getGeometryType() == GeometryType.POLYLINE) {
              // create a poly line builder from the selected feature
              PolylineBuilder polylineBuilder = new PolylineBuilder((Polyline) selectedFeature.getGeometry());
              // get a list of parts of the selected polyline
              List<Part> parts = polylineBuilder.getParts();
              parts.forEach(part -> {
                // if the selected feature is a polyline with any part containing more than one segment
                // (i.e. a curve)
                if (part.getPointCount() > 2) {
                  // set the selected feature to null
                  selectedFeature = null;
                  // show message reminding user to select straight (single segment) polylines only
                  new Alert(Alert.AlertType.WARNING, "Select straight (single segment) polylines only.").show();
                  // return early, effectively disallowing selection of multi segmented polylines
                  return;
                } else {
                  // select the identified straight polyline
                  ((FeatureLayer) result.getLayerContent()).selectFeature(selectedFeature);
                  selectedFeatureIsPolyline = true;
                }
              });
            }
            // if the selected feature is a point, select the feature
            else if (selectedFeature.getGeometry().getGeometryType() == GeometryType.POINT) {
              ((FeatureLayer) result.getLayerContent()).selectFeature(selectedFeature);
              // open a dialog to edit the feature's attributes
              showEditableAttributes(selectedFeature);
            } else {
              new Alert(Alert.AlertType.WARNING, "Feature of unexpected geometry type selected.").show();
            }
          });
        });
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }

  /**
   * Create an alert dialog with edit texts to allow editing of the given feature's 'AD_ADDRESS' and
   * 'ST_STR_NAM' attributes.
   */
  private void showEditableAttributes(Feature selectedFeature) {

    // create a dialog to edit the selected feature
    EditAttributesDialog editAttributesDialog = new EditAttributesDialog(selectedFeature);

    // show the dialog and wait for the user response
    editAttributesDialog.showAndWait();
  }

  /**
   * Move the currently selected point feature to the given map point by updating the selected
   * feature's geometry and feature table.
   */
  private void movePoint(Point mapPoint) {

    // set the selected features' geometry to a new map point
    selectedFeature.setGeometry(mapPoint);

    // update the selected feature's feature table
    selectedFeature.getFeatureTable().updateFeatureAsync(selectedFeature);

    // clear selection of the point
    clearSelection();
    selectedFeature = null;
  }

  /**
   * Move the last of the vertex point of the currently selected polyline to the given map point, by updating the
   * selected feature's geometry and feature table.
   */
  private void movePolylineVertex(Point mapPoint) {

    // get the selected feature's geometry as a polyline
    Polyline polyline = (Polyline) selectedFeature.getGeometry();

    // get the nearest vertex to the map point on the polyline
    ProximityResult nearestVertex = GeometryEngine.nearestVertex(polyline, (Point) GeometryEngine.project(mapPoint, polyline.getSpatialReference()));

    // create a polyline builder to add and remove parts from the polyline
    PolylineBuilder polylineBuilder = new PolylineBuilder(polyline);

    // get the part of the polyline nearest to the map point
    Part part = polylineBuilder.getParts().get((int) nearestVertex.getPartIndex());

    // remove the nearest vertex to the map point from the part
    part.removePoint((int) nearestVertex.getPointIndex());

    // add the new map point to the part
    part.addPoint((Point) GeometryEngine.project(mapPoint, polyline.getSpatialReference()));

    // add the part to the polyline
    polylineBuilder.getParts().add(part);

    // set the selected feature's geometry to the new polyline
    selectedFeature.setGeometry(polylineBuilder.toGeometry());

    // update the selected feature's feature table
    selectedFeature.getFeatureTable().updateFeatureAsync(selectedFeature);

    // clear selection of the polyline
    clearSelection();
    selectedFeatureIsPolyline = false;
    selectedFeature = null;
  }

  /**
   * Clear any previously selected feature layers.
   */
  private void clearSelection() {

    mapView.getMap().getOperationalLayers().forEach(layer -> {
      if (layer instanceof FeatureLayer) {
        ((FeatureLayer) layer).clearSelection();
      }
    });
  }

  /**
   * Disposes of application resources.
   */
  void terminate() {

    if (mapView != null) {
      mapView.dispose();
    }
  }
}
