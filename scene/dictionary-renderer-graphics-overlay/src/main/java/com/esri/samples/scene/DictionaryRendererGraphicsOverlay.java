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
package com.esri.samples.scene;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.MultipartBuilder;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PolygonBuilder;
import com.esri.arcgisruntime.geometry.PolylineBuilder;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.ArcGISTiledElevationSource;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Surface;
import com.esri.arcgisruntime.mapping.view.Camera;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.SceneView;
import com.esri.arcgisruntime.symbology.DictionaryRenderer;
import com.esri.arcgisruntime.symbology.SymbolDictionary;

public class DictionaryRendererGraphicsOverlay extends Application {

  // displays 3D view
  private SceneView sceneView;
  // bounding geometry for all graphics
  private Geometry graphicsBoundary;
  // holds all graphics displayed on the view
  private GraphicsOverlay graphicsOverlay;

  private static final String ELEVATION_IMAGE_SERVICE =
      "http://elevation3d.arcgis.com/arcgis/rest/services/WorldElevation3D/Terrain3D/ImageServer";

  @Override
  public void start(Stage stage) throws Exception {
    sceneView = new SceneView();
    StackPane appWindow = new StackPane(sceneView);
    Scene sceneFX = new Scene(appWindow);

    // set title, size, and add scene to stage
    stage.setTitle("3D Dictionary Renderer Graphics Overlay Sample");
    stage.setWidth(800);
    stage.setHeight(700);
    stage.setScene(sceneFX);
    stage.show();

    ArcGISScene scene = new ArcGISScene(Basemap.createImagery());
    sceneView.setArcGISScene(scene);

    // add base surface for elevation data
    Surface surface = new Surface();
    surface.getElevationSources().add(new ArcGISTiledElevationSource(ELEVATION_IMAGE_SERVICE));
    scene.setBaseSurface(surface);

    graphicsOverlay = new GraphicsOverlay();
    sceneView.getGraphicsOverlays().add(graphicsOverlay);

    // set specification for symbol dictionary using local resource path
    File specificationFile = new File(getClass().getResource("/mil2525d.stylx").getPath());
    SymbolDictionary symbolDictionary = new SymbolDictionary("mil2525d", specificationFile.getAbsolutePath());

    // tells graphics overlay how to render graphics with symbol dictionary attributes set
    DictionaryRenderer renderer = new DictionaryRenderer(symbolDictionary);
    graphicsOverlay.setRenderer(renderer);

    parseXMLFile();

    // set geometry's spatial reference to be the same as the scene view
    graphicsBoundary = GeometryEngine.project(graphicsBoundary, sceneView.getSpatialReference());
    // set the view to the center of the geometry
    Camera camera = new Camera(graphicsBoundary.getExtent().getCenter(), 15000, 0.0, 50.0, 0.0);
    sceneView.setViewpointCamera(camera);
  }

  /**
   * Parses through a xml file and creates a graphic for each block of attributes found. Each block of attributes is then
   * assigned to that graphic.
   */
  private void parseXMLFile() {
    // parse through XML file containing symbol dictionary attributes
    Map<String, Object> attributes = new HashMap<>();
    try {
      File symbolData = new File(getClass().getResource("/Mil2525DMessages.xml").getPath());
      FileInputStream file = new FileInputStream(symbolData.getAbsolutePath());
      XMLStreamReader xmlReader = XMLInputFactory.newInstance().createXMLStreamReader(file);

      // skip first element tag
      xmlReader.next();
      while (xmlReader.hasNext()) {
        // if next thing read is an element from the xml file
        if (xmlReader.next() == XMLStreamConstants.START_ELEMENT) {
          String attributeName = xmlReader.getLocalName().trim();
          if (attributeName.equals("message") && attributes.size() > 0) {
            createGraphic(attributes);
            attributes = new HashMap<>();
          } else {
            xmlReader.next();
            String attributeValue = xmlReader.getText().trim();
            attributes.put(attributeName, attributeValue);
          }
        }
      }
      // create graphic for last block of attributes
      createGraphic(attributes);
      if (file != null)
        file.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Creates a graphic using a symbol dictionary and the attributes that were passed.
   * 
   * @param attributes tells symbol dictionary what symbol to apply to graphic
   */
  private void createGraphic(Map<String, Object> attributes) {

    Geometry geometry = null;
    int wkid = Integer.parseInt((String) attributes.get("_wkid"));
    SpatialReference sr = SpatialReference.create(wkid);
    // get points that make up the graphic's geometry
    String[] geometryPoints = ((String) attributes.get("_control_points")).split(";");

    // geometry is made up of a single point
    if (geometryPoints.length == 1) {
      // split into x,y coordinates
      String[] coordinates = geometryPoints[0].split(",");
      geometry = new Point(Double.parseDouble(coordinates[0]), Double.parseDouble(coordinates[1]), sr);
    } else {
      geometry = createBuilderFromPoints(geometryPoints, sr).toGeometry();
    }

    if (!geometry.isEmpty()) {
      Graphic graphic = new Graphic(geometry, attributes);
      graphicsOverlay.getGraphics().add(graphic);

      // update boundary geometry so all graphics are within
      graphicsBoundary = graphicsBoundary == null ? geometry : GeometryEngine.union(graphicsBoundary, geometry);
    }
  }

  /**
   * Builds a polygon or polyline from the points that are passed.
   * 
   * @param points used to build a polygon or polyline geometry
   * @param sr spatial reference of the geometry to be built
   * @return geometry that was built
   */
  private MultipartBuilder createBuilderFromPoints(String[] points, SpatialReference sr) {

    MultipartBuilder builder = null;
    if (points.length >= 3 && points[0] == points[points.length - 1]) {
      // if there are at least 3 points and the first and last point are the same, assume it's a polygon
      builder = new PolygonBuilder(sr);
    } else {
      builder = new PolylineBuilder(sr);
    }

    // add each point to the geometry   
    for (String point : points) {
      // split into x,y coordinates 
      String[] coordinates = point.split(",");
      if (coordinates.length >= 2) {
        builder.addPoint(new Point(Double.parseDouble(coordinates[0]), Double.parseDouble(coordinates[1]), sr));
      }
    }

    return builder;
  }

  /**
   * Stops and releases all resources used in application.
   */
  @Override
  public void stop() {

    if (sceneView != null) {
      sceneView.dispose();
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
