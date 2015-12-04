/* Copyright 2015 Esri.
 
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
 
    http://www.apache.org/licenses/LICENSE-2.0
 
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
limitations under the License.  */

package com.esri.sampleviewer.samples.symbology;

import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Map;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.Color;
import com.esri.arcgisruntime.symbology.RgbColor;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * This sample demonstrates how to create a <@SimpleRenderer> and add it to a
 * <@GraphicsOverlay>. Renderers define the symbology for all graphics in a
 * GraphicsOverlay (unless they are overridden by setting the symbol directly on
 * the graphic). SimpleRenderers can also be defined on <@FeatureLayer> using
 * the same code. How it works, a SimpleRenderer is created using a
 * <@SimpleMarkerSymbol> (red cross) and set onto a GraphicsOverlay. The three
 * points are created and each added to a <@Graphic> which are then added to the
 * GraphicsOverlay. The GraphicsOverlay is added to the <@MapView> to make it
 * visible, then all Graphics are styled with the same symbol that was defined
 * in the renderer.
 */
public class SimpleRendererSample extends Application {

  private MapView mapView;

  @Override
  public void start(Stage stage) throws Exception {

    // create a border pane and a application scene
    BorderPane borderPane = new BorderPane();
    Scene scene = new Scene(borderPane);

    // size the stage, add a title, and set scene to stage
    stage.setTitle("Simple Renderer Sample");
    stage.setHeight(700);
    stage.setWidth(800);
    stage.setScene(scene);
    stage.show();

    try {
      // create SpatialReference for points
      SpatialReference wgs84 = SpatialReferences.getWgs84();

      // create points for displaying graphics
      Point oldFaithfullPoint = new Point(-110.828140, 44.460458, wgs84);
      Point cascadeGeyserPoint = new Point(-110.829004, 44.462438, wgs84);
      Point plumeGeyserPoint = new Point(-110.829381, 44.462735, wgs84);

      // create view for this map
      mapView = new MapView();

      // create a map with the imagery basemap
      Map map = new Map(Basemap.createImageryWithLabels());

      // set map to be displayed in mapview
      mapView.setMap(map);

      // place map in border pane
      borderPane.setCenter(mapView);

      // create initial viewpoint using an envelope
      Envelope envelope = new Envelope(oldFaithfullPoint, plumeGeyserPoint);

      // set viewpoint on mapview with padding
      mapView.setViewpointGeometryWithPaddingAsync(envelope, 100.0);

      // create a graphics overlay and add it to the mapview
      GraphicsOverlay graphicsOverlay = new GraphicsOverlay();
      mapView.getGraphicsOverlays().add(graphicsOverlay);

      // create a simple symbol for use in a simple renderer
      Color color = new RgbColor(255, 0, 0, 255); // red, fully opaque
      // size12, style ofcross
      SimpleMarkerSymbol symbol = new SimpleMarkerSymbol(color, 12,
          SimpleMarkerSymbol.Style.CROSS);
      SimpleRenderer renderer = new SimpleRenderer(symbol);

      // apply the renderer to the graphics overlay (so all graphics will use
      // the same symbol from the renders)
      graphicsOverlay.setRenderer(renderer);

      // create graphics from the geyser location points. NOTE: no need to set
      // the symbol on the graphic because the renderer takes care of it.
      // The points are in WGS84, but graphics get reprojected automatically, so
      // they work fine in a map with a spatial reference of web mercator.
      Graphic oldFaithfullGraphic = new Graphic(oldFaithfullPoint);
      Graphic cascadeGeyserGraphic = new Graphic(cascadeGeyserPoint);
      Graphic plumeGeyserGraphic = new Graphic(plumeGeyserPoint);
      graphicsOverlay.getGraphics().add(oldFaithfullGraphic);
      graphicsOverlay.getGraphics().add(cascadeGeyserGraphic);
      graphicsOverlay.getGraphics().add(plumeGeyserGraphic);

    } catch (Exception e) {
      // on any error, display stack trace
      e.printStackTrace();
    }
  }

  /**
   * Stops and releases all resources used in application.
   * 
   * @throws Exception if security manager doesn't allow JVM to exit with
   *           current status
   */
  @Override
  public void stop() throws Exception {

    // release resources when the application closes
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
