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

package com.esri.samples.displayinformation.dictionary_renderer_graphics_overlay;

import static org.joox.JOOX.$;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.geometry.Multipoint;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.DictionaryRenderer;
import com.esri.arcgisruntime.symbology.DictionarySymbolStyle;

public class DictionaryRendererGraphicsOverlaySample extends Application {

  private MapView mapView;
  private GraphicsOverlay graphicsOverlay;

  @Override
  public void start(Stage stage) throws Exception {
    mapView = new MapView();
    StackPane appWindow = new StackPane(mapView);
    Scene scene = new Scene(appWindow);

    // set title, size, and add scene to stage
    stage.setTitle("Dictionary Renderer Graphics Overlay Sample");
    stage.setWidth(800);
    stage.setHeight(700);
    stage.setScene(scene);
    stage.show();

    ArcGISMap map = new ArcGISMap(Basemap.createTopographic());
    mapView.setMap(map);

    graphicsOverlay = new GraphicsOverlay();
    // graphics no longer show after zooming passed this scale
    graphicsOverlay.setMinScale(1000000);
    mapView.getGraphicsOverlays().add(graphicsOverlay);

    // create symbol dictionary from specification
    DictionarySymbolStyle symbolDictionary = new DictionarySymbolStyle("mil2525d");

    // tells graphics overlay how to render graphics with symbol dictionary attributes set
    DictionaryRenderer renderer = new DictionaryRenderer(symbolDictionary);
    graphicsOverlay.setRenderer(renderer);

    // parse graphic attributes from a XML file
    List<Map<String, Object>> messages = parseMessages();

    // create graphics with attributes and add to graphics overlay
    messages.stream()
        .map(DictionaryRendererGraphicsOverlaySample::createGraphic)
        .collect(Collectors.toCollection(() -> graphicsOverlay.getGraphics()));

    // once view has loaded
    mapView.addSpatialReferenceChangedListener(e -> {
      // set initial viewpoint
      mapView.setViewpointGeometryAsync(graphicsOverlay.getExtent());
    });
  }

  /**
   * Parses a XML file and creates a message for each block of attributes found.
   */
  private List<Map<String, Object>> parseMessages() throws Exception {
    final List<Map<String, Object>> messages = new ArrayList<>();
    $(getClass().getResourceAsStream("/symbols/Mil2525DMessages.xml")) // $ reads the file
        .find("message")
        .each()
        .forEach(message -> {
          Map<String, Object> attributes = new HashMap<>();
          message.children().forEach(attr -> attributes.put(attr.getNodeName(), attr.getTextContent()));
          messages.add(attributes);
        });

    return messages;
  }

  /**
   * Creates a graphic using a symbol dictionary and the attributes that were passed.
   * 
   * @param attributes tells symbol dictionary what symbol to apply to graphic
   */
  private static Graphic createGraphic(Map<String, Object> attributes) {

    // get spatial reference
    int wkid = Integer.parseInt((String) attributes.get("_wkid"));
    SpatialReference sr = SpatialReference.create(wkid);

    // get points from coordinates' string
    PointCollection points = new PointCollection(sr);
    String[] coordinates = ((String) attributes.get("_control_points")).split(";");
    Stream.of(coordinates)
        .map(cs -> cs.split(","))
        .map(c -> new Point(Double.valueOf(c[0]), Double.valueOf(c[1]), sr))
        .collect(Collectors.toCollection(() -> points));

    // return a graphic with multipoint geometry
    return new Graphic(new Multipoint(points), attributes);
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
