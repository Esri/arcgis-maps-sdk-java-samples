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

package com.esri.samples.geometry.spatial_operations;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Part;
import com.esri.arcgisruntime.geometry.PartCollection;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.Polygon;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;

public class SpatialOperationsSample extends Application {

  private MapView mapView;
  private GraphicsOverlay geomLayer;
  private GraphicsOverlay resultGeomLayer;
  private Graphic polygon1;
  private Graphic polygon2;

  // geometry operations
  private enum OPERATION_TYPE {
    UNION, DIFFERENCE, SYMMETRIC_DIFFERENCE, INTERSECTION
  }

  // simple black (0xFF000000) line symbol
  private SimpleLineSymbol line = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0xFF000000, 1);

  @Override
  public void start(Stage stage) throws Exception {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);
      scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

      // set title, size, and add scene to stage
      stage.setTitle("Spatial Operations Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // create a control panel
      VBox vBoxControl = new VBox(6);
      vBoxControl.setMaxSize(180, 120);
      vBoxControl.getStyleClass().add("panel-region");

      // create section for combo box
      Label geomOperationLabel = new Label("Select operation:");
      geomOperationLabel.getStyleClass().add("panel-label");

      // initialise comboBox items
      ComboBox<OPERATION_TYPE> geomOperationBox = new ComboBox<>(FXCollections.observableArrayList(OPERATION_TYPE
          .values()));
      geomOperationBox.getSelectionModel().selectLast();
      geomOperationBox.setMaxWidth(Double.MAX_VALUE);
      geomOperationBox.setDisable(true);

      // create button for reset the sample
      Button resetButton = new Button("Reset operation");
      resetButton.setMaxWidth(Double.MAX_VALUE);
      resetButton.setDisable(true);

      // handle ComboBox event to perform the selected geometry operation
      geomOperationBox.setOnAction(e -> {
        Geometry resultPolygon;
        OPERATION_TYPE operation = geomOperationBox.getSelectionModel().getSelectedItem();
        switch (operation) {
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
          default:
            resultPolygon = GeometryEngine.intersection(polygon1.getGeometry(), polygon2.getGeometry());
        }

        // update result as a red (0xFFE91F1F) geometry
        SimpleFillSymbol redSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, 0xFFE91F1F, line);
        resultGeomLayer.getGraphics().add(new Graphic(resultPolygon, redSymbol));

        resetButton.setDisable(false);
        geomOperationBox.setDisable(true);
      });

      // clear result layer
      resetButton.setOnAction(e -> {
        resultGeomLayer.getGraphics().clear();
        geomOperationBox.setDisable(false);
      });

      // add label and buttons to the control panel
      vBoxControl.getChildren().addAll(geomOperationLabel, geomOperationBox, resetButton);

      // create ArcGISMap with topograohic basemap
      ArcGISMap map = new ArcGISMap(Basemap.createLightGrayCanvas());

      // enable geometry operations when ArcGISMap is done loading
      map.addDoneLoadingListener(() -> geomOperationBox.setDisable(false));

      mapView = new MapView();
      mapView.setMap(map);

      // set the map views's viewpoint centred on London and scaled
      Point viewPoint = new Point(-14153, 6710527, SpatialReferences.getWebMercator());
      mapView.setViewpointCenterAsync(viewPoint, 30000);

      // create geometry layers
      geomLayer = new GraphicsOverlay();
      mapView.getGraphicsOverlays().add(geomLayer);

      resultGeomLayer = new GraphicsOverlay();
      mapView.getGraphicsOverlays().add(resultGeomLayer);

      // create sample polygons
      createPolygons();

      geomLayer.getGraphics().add(polygon1);
      geomLayer.getGraphics().add(polygon2);

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
  public void stop() throws Exception {

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
