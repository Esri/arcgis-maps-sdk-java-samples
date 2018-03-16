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

package com.esri.samples.geometry.spatial_relationships;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.GeometryType;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.Polygon;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.IdentifyGraphicsOverlayResult;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;

public class SpatialRelationshipsSample extends Application {

  private MapView mapView;

  private enum SpatialRelationship {
    CROSSES, CONTAINS, DISJOINT, INTERSECTS, OVERLAPS, TOUCHES, WITHIN
  }

  @Override
  public void start(Stage stage) throws Exception {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);

      // set title, size, and add scene to stage
      stage.setTitle("Spatial Relationships Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // create a map and set it to a map view
      ArcGISMap map = new ArcGISMap(Basemap.createTopographic());
      mapView = new MapView();
      mapView.setMap(map);

      // create a graphics overlay
      GraphicsOverlay graphicsOverlay = new GraphicsOverlay();
      mapView.getGraphicsOverlays().add(graphicsOverlay);
      graphicsOverlay.setSelectionColor(0xFFFFFF00);

      // create a polygon graphic
      PointCollection polygonPoints = new PointCollection(SpatialReferences.getWebMercator());
      polygonPoints.add(new Point(-5991501.677830, 5599295.131468));
      polygonPoints.add(new Point(-6928550.398185, 2087936.739807));
      polygonPoints.add(new Point(-3149463.800709, 1840803.011362));
      polygonPoints.add(new Point(-1563689.043184, 3714900.452072));
      polygonPoints.add(new Point(-3180355.516764, 5619889.608838));
      Polygon polygon = new Polygon(polygonPoints);
      SimpleFillSymbol polygonSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.FORWARD_DIAGONAL, 0xFF00FF00,
          new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0xFF00FF00, 2));
      Graphic polygonGraphic = new Graphic(polygon, polygonSymbol);
      graphicsOverlay.getGraphics().add(polygonGraphic);

      // create a polyline graphic
      PointCollection polylinePoints = new PointCollection(SpatialReferences.getWebMercator());
      polylinePoints.add(new Point(-4354240.726880, -609939.795721));
      polylinePoints.add(new Point(-3427489.245210, 2139422.933233));
      polylinePoints.add(new Point(-2109442.693501, 4301843.057130));
      polylinePoints.add(new Point(-1810822.771630, 7205664.366363));
      Polyline polyline = new Polyline(polylinePoints);
      Graphic polylineGraphic = new Graphic(polyline, new SimpleLineSymbol(SimpleLineSymbol.Style.DASH, 0xFFFF0000,
          4));
      graphicsOverlay.getGraphics().add(polylineGraphic);

      // create a point graphic
      Point point = new Point(-4487263.495911, 3699176.480377, SpatialReferences.getWebMercator());
      SimpleMarkerSymbol locationMarker = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, 0xFF0000FF, 10);
      Graphic pointGraphic = new Graphic(point, locationMarker);
      graphicsOverlay.getGraphics().add(pointGraphic);

      // create a tree view to show the spatial relationships between each graphic and the selected graphic
      TreeView<String> relationships = new TreeView<>();
      relationships.setMaxSize(200, 300);
      TreeItem<String> rootItem = new TreeItem<>("Relationships");
      rootItem.setExpanded(true);
      relationships.setRoot(rootItem);
      TreeItem<String> pointRelationships = new TreeItem<>("Point");
      TreeItem<String> polylineRelationships = new TreeItem<>("Polyline");
      TreeItem<String> polygonRelationships = new TreeItem<>("Polygon");
      rootItem.getChildren().addAll(Arrays.asList(pointRelationships, polylineRelationships, polygonRelationships));

      // and a mouse click listener to identify the selected graphic
      mapView.setOnMouseClicked(e -> {
        if (e.isStillSincePress() && e.getButton() == MouseButton.PRIMARY) {
          // identify the clicked graphic(s)
          Point2D point2D = new Point2D(e.getX(), e.getY());
          ListenableFuture<IdentifyGraphicsOverlayResult> identifyGraphics = mapView.identifyGraphicsOverlayAsync(graphicsOverlay,
              point2D, 1, false);
          identifyGraphics.addDoneListener(() -> {
            try {
              // get the first identified graphic
              IdentifyGraphicsOverlayResult result = identifyGraphics.get();
              List<Graphic> identifiedGraphics = result.getGraphics();
              if (identifiedGraphics.size() > 0) {
                // clear previous results
                pointRelationships.getChildren().clear();
                polylineRelationships.getChildren().clear();
                polygonRelationships.getChildren().clear();
                
                // select the identified graphic
                graphicsOverlay.clearSelection();
                Graphic identifiedGraphic = identifiedGraphics.get(0);
                identifiedGraphic.setSelected(true);
                Geometry selectedGeometry = identifiedGraphic.getGeometry();
                GeometryType selectedGeometryType = selectedGeometry.getGeometryType();
                
                // populate the tree view with the spatial relationships the selected graphic has to the other graphics
                // ignore testing relationships between the geometry and itself
                if (selectedGeometryType != GeometryType.POINT) {
                  getSpatialRelationships(selectedGeometry, pointGraphic.getGeometry()).forEach(relationship ->
                      pointRelationships.getChildren().add(new TreeItem<>(relationship.toString())));
                }
                if (selectedGeometryType != GeometryType.POLYLINE) {
                  getSpatialRelationships(selectedGeometry, polylineGraphic.getGeometry()).forEach(relationship ->
                      polylineRelationships.getChildren().add(new TreeItem<>(relationship.toString())));
                }
                if (selectedGeometryType != GeometryType.POLYGON) {
                  getSpatialRelationships(selectedGeometry, polygonGraphic.getGeometry()).forEach(relationship ->
                      polygonRelationships.getChildren().add(new TreeItem<>(relationship.toString())));
                }
              }
            } catch (InterruptedException | ExecutionException ex) {
              ex.printStackTrace();
            }
          });
        }
      });

      // add the scene view and label box to the stack pane
      stackPane.getChildren().addAll(mapView, relationships);
      StackPane.setMargin(relationships, new Insets(10, 0, 0, 10));
      StackPane.setAlignment(relationships, Pos.TOP_LEFT);
    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
    }
  }

  /**
   * Gets a list of spatial relationships that the first geometry has to the second geometry.
   * 
   * @param a first geometry
   * @param b second geometry
   * @return list of relationships a has to b
   */
  private List<SpatialRelationship> getSpatialRelationships(Geometry a, Geometry b) {
    List<SpatialRelationship> relationships = new ArrayList<>();
    if (GeometryEngine.crosses(a, b)) relationships.add(SpatialRelationship.CROSSES);
    if (GeometryEngine.contains(a, b)) relationships.add(SpatialRelationship.CONTAINS);
    if (GeometryEngine.disjoint(a, b)) relationships.add(SpatialRelationship.DISJOINT);
    if (GeometryEngine.intersects(a, b)) relationships.add(SpatialRelationship.INTERSECTS);
    if (GeometryEngine.overlaps(a, b)) relationships.add(SpatialRelationship.OVERLAPS);
    if (GeometryEngine.touches(a, b)) relationships.add(SpatialRelationship.TOUCHES);
    if (GeometryEngine.within(a, b)) relationships.add(SpatialRelationship.WITHIN);
    return relationships;
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
