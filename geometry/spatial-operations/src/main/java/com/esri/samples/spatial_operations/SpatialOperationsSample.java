/*
 * Copyright 2017 Esri.
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

package com.esri.samples.spatial_operations;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Part;
import com.esri.arcgisruntime.geometry.PartCollection;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.Polygon;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;

public class SpatialOperationsSample extends Application {

  private ArcGISMap map; // keep loadable in scope to avoid garbage collection
  private MapView mapView;
  private GraphicsOverlay resultGeomOverlay;
  private Graphic polygon1;
  private Graphic polygon2;

  // geometry operations
  private enum OPERATION_TYPE {
    NONE, UNION, DIFFERENCE, SYMMETRIC_DIFFERENCE, INTERSECTION
  }

  // simple black (0xFF000000) line symbol
  private final SimpleLineSymbol line = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0xFF000000, 1);

  @Override
  public void start(Stage stage) {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);
      scene.getStylesheets().add(getClass().getResource("/spatial_operations/style.css").toExternalForm());

      // set title, size, and add scene to stage
      stage.setTitle("Spatial Operations Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // authentication with an API key or named user is required to access basemaps and other location services
      String yourAPIKey = System.getProperty("apiKey");
      ArcGISRuntimeEnvironment.setApiKey(yourAPIKey);

      // initialise comboBox items
      ComboBox<OPERATION_TYPE> geomOperationBox = new ComboBox<>(FXCollections.observableArrayList(OPERATION_TYPE
          .values()));
      geomOperationBox.getSelectionModel().select(OPERATION_TYPE.NONE);
      geomOperationBox.setMaxWidth(180);
      geomOperationBox.setDisable(true);

      // handle ComboBox event to perform the selected geometry operation
      geomOperationBox.setOnAction(e -> {
        resultGeomOverlay.getGraphics().clear();
        Geometry resultPolygon;
        switch (geomOperationBox.getSelectionModel().getSelectedItem()) {
          case UNION:
            resultPolygon = GeometryEngine.union(polygon1.getGeometry(), polygon2.getGeometry());
            break;
          case DIFFERENCE:
            resultPolygon = GeometryEngine.difference(polygon1.getGeometry(), polygon2.getGeometry());
            break;
          case SYMMETRIC_DIFFERENCE:
            resultPolygon = GeometryEngine.symmetricDifference(polygon1.getGeometry(), polygon2.getGeometry());
            break;
          case INTERSECTION:
            resultPolygon = GeometryEngine.intersection(polygon1.getGeometry(), polygon2.getGeometry());
            break;
          case NONE:
          default:
            return;
        }

        // update result as a red (0xFFE91F1F) geometry
        SimpleFillSymbol redSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, 0xFFE91F1F, line);
        resultGeomOverlay.getGraphics().add(new Graphic(resultPolygon, redSymbol));
      });

      // create a map with the light gray basemap style
      map = new ArcGISMap(BasemapStyle.ARCGIS_LIGHT_GRAY);

      // enable geometry operations when the map is done loading
      map.addDoneLoadingListener(() -> {
        if (map.getLoadStatus() == LoadStatus.LOADED) {
          geomOperationBox.setDisable(false);
        } else {
          Alert alert = new Alert(Alert.AlertType.ERROR, "Map Failed to Load!");
          alert.show();
        }
      });

      // create a map view and set the map to it
      mapView = new MapView();
      mapView.setMap(map);

      // set a viewpoint on the map view centered on London
      mapView.setViewpointCenterAsync(new Point(-14153, 6710527, SpatialReferences.getWebMercator()), 30000);

      // create geometry overlays
      GraphicsOverlay geomOverlay = new GraphicsOverlay();
      mapView.getGraphicsOverlays().add(geomOverlay);

      resultGeomOverlay = new GraphicsOverlay();
      mapView.getGraphicsOverlays().add(resultGeomOverlay);

      // create sample polygons
      createPolygons();

      geomOverlay.getGraphics().add(polygon1);
      geomOverlay.getGraphics().add(polygon2);

      // add the map view and control panel to stack pane
      stackPane.getChildren().addAll(mapView, geomOperationBox);
      StackPane.setAlignment(geomOperationBox, Pos.TOP_LEFT);
      StackPane.setMargin(geomOperationBox, new Insets(10, 0, 0, 10));

    } catch (Exception e) {
      // on any error, display the stack trace
      e.printStackTrace();
    }
  }

  /**
   * Creates the two intersecting polygons used in the application.
   */
  private void createPolygons() {

    // create blue (0xFF0000CC) polygon
    PointCollection pointsPoly = new PointCollection(SpatialReferences.getWebMercator());
    pointsPoly.add(new Point(-13960, 6709400));
    pointsPoly.add(new Point(-14660, 6710000));
    pointsPoly.add(new Point(-13760, 6710730));
    pointsPoly.add(new Point(-13300, 6710500));
    pointsPoly.add(new Point(-13160, 6710100));

    SimpleFillSymbol fillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, 0xFF0000CC, line);
    Polygon polygonSymbol = new Polygon(pointsPoly);
    polygon1 = new Graphic(polygonSymbol, fillSymbol);

    // create green (0xFF009900) polygon
    // outer ring
    PointCollection outerRingSegmentCollection = new PointCollection(SpatialReferences.getWebMercator());
    outerRingSegmentCollection.add(new Point(-13060, 6711030));
    outerRingSegmentCollection.add(new Point(-12160, 6710730));
    outerRingSegmentCollection.add(new Point(-13160, 6709700));
    outerRingSegmentCollection.add(new Point(-14560, 6710730));
    Part outerRing = new Part(outerRingSegmentCollection);

    // inner ring
    PointCollection innerRingSegmentCollection = new PointCollection(SpatialReferences.getWebMercator());
    innerRingSegmentCollection.add(new Point(-13060, 6710910));
    innerRingSegmentCollection.add(new Point(-14160, 6710630));
    innerRingSegmentCollection.add(new Point(-13160, 6709900));
    innerRingSegmentCollection.add(new Point(-12450, 6710660));
    Part innerRing = new Part(innerRingSegmentCollection);

    PartCollection polygonParts = new PartCollection(outerRing);
    polygonParts.add(innerRing);
    fillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, 0xFF009900, line);
    polygon2 = new Graphic(new Polygon(polygonParts), fillSymbol);
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
