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

package com.esri.sampleviewer.samples.geometry;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
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
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Map;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.RgbColor;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;

/**
 * This sample demonstrates how to use the {@link GeometryEngine} to perform
 * some of the operations: Union, Difference, Intersection and Buffer.
 * <h4>How it Works</h4>
 * 
 * First two {@link Graphic} polygons are created via {@link PointCollection}
 * and {@link PartCollection}. Next, the selected Geometry Engine operation is
 * performed and the {@link Geometry} result is displayed.
 */
public class GeometryEngineSample extends Application {

  private MapView mapView;
  private GraphicsOverlay geomLayer;
  private GraphicsOverlay resultGeomLayer;
  private Graphic polygon1;
  private Graphic polygon2;

  // Geometry operations
  enum OPERATION_TYPE {
    UNION, DIFFERENCE, BUFFER, INTERSECTION
  }

  // UI 
  private RgbColor red = new RgbColor(233, 31, 31, 255);
  private RgbColor green = new RgbColor(0, 153, 0, 255);
  private RgbColor blue = new RgbColor(0, 0, 204, 255);
  private RgbColor black = new RgbColor(0, 0, 0, 255);
  private SimpleLineSymbol line = new SimpleLineSymbol(
      SimpleLineSymbol.Style.SOLID, black, 1, 1.0f);

  @Override
  public void start(Stage stage) throws Exception {

    // create stack pane and application scene
    StackPane stackPane = new StackPane();
    Scene scene = new Scene(stackPane);
    scene.getStylesheets().add(getClass().getResource(
        "../resources/SamplesTheme.css").toExternalForm());

    // set title, size, and add scene to stage
    stage.setTitle("Geometry Engine Sample");
    stage.setWidth(800);
    stage.setHeight(700);
    stage.setScene(scene);
    stage.show();

    // create a control panel
    VBox vBoxControl = new VBox(6);
    vBoxControl.setMaxSize(210, 230);
    vBoxControl.getStyleClass().add("panel-region");

    // create sample description
    Label descriptionLabel = new Label("Sample Description:");
    descriptionLabel.getStyleClass().add("panel-label");
    TextArea description = new TextArea(
        "Select a geometry engine operation to perform on the two polygons");
    description.setWrapText(true);
    description.autosize();
    description.setEditable(false);

    // create section for combo box
    Label geomOperationLabel = new Label("Select geometry operation");
    geomOperationLabel.getStyleClass().add("panel-label");

    // initialise comboBox items
    ComboBox<OPERATION_TYPE> geomOperationBox = new ComboBox<>(FXCollections
        .observableArrayList(OPERATION_TYPE.values()));
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
      if (geomOperationBox.getSelectionModel().getSelectedItem().equals(
          OPERATION_TYPE.UNION)) {
        resultPolygon = GeometryEngine.union(polygon1.getGeometry(), polygon2
            .getGeometry());
      } else if (geomOperationBox.getSelectionModel().getSelectedItem().equals(
          OPERATION_TYPE.DIFFERENCE)) {
        resultPolygon = GeometryEngine.difference(polygon1.getGeometry(),
            polygon2.getGeometry());
      } else if (geomOperationBox.getSelectionModel().getSelectedItem().equals(
          OPERATION_TYPE.BUFFER)) {
        resultPolygon = GeometryEngine.buffer(polygon2.getGeometry(), 50);
      } else {
        resultPolygon = GeometryEngine.intersection(polygon1.getGeometry(),
            polygon2.getGeometry());
      }

      // update result as a red geometry 
      SimpleFillSymbol redSymbol = new SimpleFillSymbol(red,
          SimpleFillSymbol.Style.SOLID, line, 1.0f);
      resultGeomLayer.getGraphics().add(new Graphic(resultPolygon, redSymbol));

      resetButton.setDisable(false);
      geomOperationBox.setDisable(true);
    });

    // clear result layer
    resetButton.setOnAction(e -> {
      resultGeomLayer.getGraphics().clear();
      geomOperationBox.setDisable(false);
    });

    // add label, sample description, and buttons to the control panel
    vBoxControl.getChildren().addAll(descriptionLabel, description,
        geomOperationLabel, geomOperationBox, resetButton);

    try {

      // create map with topograohic basemap
      Map map = new Map(Basemap.createLightGrayCanvas());

      // enable geometry operations when map is done loading
      map.addDoneLoadingListener(() -> {
        geomOperationBox.setDisable(false);
      });

      mapView = new MapView();
      mapView.setMap(map);

      // set the map views's viewpoint centred on London and scaled
      Point viewPoint = new Point(-14153, 6710527, SpatialReferences
          .getWebMercator());
      mapView.setViewpointCenterWithScaleAsync(viewPoint, 30000);

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
   * 
   */
  private void createPolygons() {

    // create blue polygon 
    PointCollection pointsPoly = new PointCollection(SpatialReferences
        .getWebMercator());
    pointsPoly.add(new Point(-13160, 6710100));
    pointsPoly.add(new Point(-13300, 6710500));
    pointsPoly.add(new Point(-13760, 6710730));
    pointsPoly.add(new Point(-14660, 6710000));
    pointsPoly.add(new Point(-13960, 6709400));

    SimpleFillSymbol fillSymbol = new SimpleFillSymbol(blue,
        SimpleFillSymbol.Style.SOLID, line, 1.0f);
    Polygon polygonSymbol = new Polygon(pointsPoly);
    polygon1 = new Graphic(polygonSymbol, fillSymbol);

    // create green polygon 
    // outer ring
    PointCollection outerRingSegmentCollection = new PointCollection(
        SpatialReferences.getWebMercator());
    outerRingSegmentCollection.add(new Point(-13060, 6711030));
    outerRingSegmentCollection.add(new Point(-12160, 6710730));
    outerRingSegmentCollection.add(new Point(-13160, 6709700));
    outerRingSegmentCollection.add(new Point(-14560, 6710730));
    outerRingSegmentCollection.add(new Point(-13060, 6711030));
    Part outerRing = new Part(outerRingSegmentCollection);

    // inner ring
    PointCollection innerRingSegmentCollection = new PointCollection(
        SpatialReferences.getWebMercator());
    innerRingSegmentCollection.add(new Point(-13060, 6710910));
    innerRingSegmentCollection.add(new Point(-12450, 6710660));
    innerRingSegmentCollection.add(new Point(-13160, 6709900));
    innerRingSegmentCollection.add(new Point(-14160, 6710630));
    innerRingSegmentCollection.add(new Point(-13060, 6710910));
    Part innerRing = new Part(innerRingSegmentCollection);

    PartCollection polygonParts = new PartCollection(outerRing);
    polygonParts.add(innerRing);
    fillSymbol = new SimpleFillSymbol(green, SimpleFillSymbol.Style.SOLID, line,
        1.0f);
    polygon2 = new Graphic(new Polygon(polygonParts), fillSymbol);
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
