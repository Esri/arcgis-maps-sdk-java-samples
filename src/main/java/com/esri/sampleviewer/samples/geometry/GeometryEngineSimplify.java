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
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
 * This sample demonstrates how to use the {@link GeometryEngine} to perform the
 * simplify geometry operation on a polygon.
 * <h4>How it Works</h4>
 * 
 * First the {@link Graphic} polygon is created via {@link PointCollection} and
 * {@link PartCollection}. Next, the Simplify Geometry Engine operation is
 * performed and the {@link Geometry} result is displayed.
 * 
 * Note: Simplify geometry operation can also be perform on polylines.
 */
public class GeometryEngineSimplify extends Application {

  private MapView mapView;
  private GraphicsOverlay geomLayer;
  private GraphicsOverlay resultGeomLayer;
  private Graphic polygon;

  // UI 
  private RgbColor red = new RgbColor(233, 31, 31, 255);
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
    stage.setTitle("Geometry Engine Simplify");
    stage.setWidth(800);
    stage.setHeight(700);
    stage.setScene(scene);
    stage.show();

    // create a control panel
    VBox vBoxControl = new VBox(6);
    vBoxControl.setMaxSize(220, 220);
    vBoxControl.getStyleClass().add("panel-region");

    // create sample description
    Label descriptionLabel = new Label("Sample Description:");
    descriptionLabel.getStyleClass().add("panel-label");
    TextArea description = new TextArea(
        "Click the Simplify button to perform the geometry operation on the polygon. Press reset to start again.");
    description.setWrapText(true);
    description.autosize();
    description.setEditable(false);

    // create simplify button
    Button simplifyButton = new Button("Simplify");
    simplifyButton.setMaxWidth(Double.MAX_VALUE);
    simplifyButton.setDisable(true);

    // create reset button
    Button resetButton = new Button("Reset");
    resetButton.setMaxWidth(Double.MAX_VALUE);
    resetButton.setDisable(true);

    // perform the simplify geometry operation
    simplifyButton.setOnAction(e -> {
      Geometry resultPolygon = GeometryEngine.simplify(polygon.getGeometry());

      // update result as a red geometry 
      SimpleFillSymbol redSymbol = new SimpleFillSymbol(red,
          SimpleFillSymbol.Style.SOLID, line, 1.0f);
      resultGeomLayer.getGraphics().add(new Graphic(resultPolygon, redSymbol));

      resetButton.setDisable(false);
      simplifyButton.setDisable(true);
    });

    // clear result layer
    resetButton.setOnAction(e -> {
      resultGeomLayer.getGraphics().clear();
      simplifyButton.setDisable(false);
    });

    // add label, sample description, and buttons to the control panel
    vBoxControl.getChildren().addAll(descriptionLabel, description,
        simplifyButton, resetButton);

    try {

      Map map = new Map(Basemap.createLightGrayCanvas());

      // enable geometry operations when map is done loading
      map.addDoneLoadingListener(() -> {
        simplifyButton.setDisable(false);
      });

      mapView = new MapView();
      mapView.setMap(map);

      // set the map views's viewpoint centred on London and scaled
      Point viewPoint = new Point(-13500, 6710327, SpatialReferences
          .getWebMercator());
      mapView.setViewpointCenterWithScaleAsync(viewPoint, 25000);

      // create geometry layers
      geomLayer = new GraphicsOverlay();
      mapView.getGraphicsOverlays().add(geomLayer);

      resultGeomLayer = new GraphicsOverlay();
      mapView.getGraphicsOverlays().add(resultGeomLayer);

      // create sample polygon
      createPolygon();
      geomLayer.getGraphics().add(polygon);

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
   * Creates the polygon.
   * 
   */
  private void createPolygon() {

    // part one
    PointCollection partSegmentCollectionOne = new PointCollection(
        SpatialReferences.getWebMercator());
    partSegmentCollectionOne.add(new Point(-13020, 6710130));
    partSegmentCollectionOne.add(new Point(-14160, 6710130));
    partSegmentCollectionOne.add(new Point(-14160, 6709300));
    partSegmentCollectionOne.add(new Point(-13020, 6709300));
    partSegmentCollectionOne.add(new Point(-13020, 6710130));
    Part partOne = new Part(partSegmentCollectionOne);

    // part two
    PointCollection partSegmentCollectionTwo = new PointCollection(
        SpatialReferences.getWebMercator());
    partSegmentCollectionTwo.add(new Point(-12160, 6710730));
    partSegmentCollectionTwo.add(new Point(-13160, 6710730));
    partSegmentCollectionTwo.add(new Point(-13160, 6709100));
    partSegmentCollectionTwo.add(new Point(-12160, 6709100));
    partSegmentCollectionTwo.add(new Point(-12160, 6710730));
    Part partTwo = new Part(partSegmentCollectionTwo);

    // part three
    PointCollection partSegmentCollectionThree = new PointCollection(
        SpatialReferences.getWebMercator());
    partSegmentCollectionThree.add(new Point(-12560, 6710030));
    partSegmentCollectionThree.add(new Point(-13520, 6710030));
    partSegmentCollectionThree.add(new Point(-13520, 6709000));
    partSegmentCollectionThree.add(new Point(-12560, 6709000));
    partSegmentCollectionThree.add(new Point(-12560, 6710030));
    Part partThree = new Part(partSegmentCollectionThree);

    PartCollection polygonParts = new PartCollection(partOne);
    polygonParts.add(partTwo);
    polygonParts.add(partThree);

    SimpleFillSymbol fillSymbol = new SimpleFillSymbol(red,
        SimpleFillSymbol.Style.SOLID, line, 0.0f);
    polygon = new Graphic(new Polygon(polygonParts), fillSymbol);

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
