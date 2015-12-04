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

import java.io.File;

import javax.imageio.ImageIO;

import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Map;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * This sample shows how to create a <@PictureMarkerSymbol> from the different
 * types of picture resources that are available; sourced from a URL, locally on
 * the application, or from storage on disk. How it works, the three
 * PictureMarkerSymbols that you see in the application are all constructed from
 * different types of resources and then added to a <@Graphic> which is then
 * added to a <@GraphicsOverlay>. The campsite icon is constructed from a URL,
 * because this is a remote resource the symbol needs to be loaded before it is
 * added to a Graphic and added to the <@Map>. The blue pin with a star is
 * created from an application resource called a Image, these also need to be
 * loaded before they are added to the map. The orange pin is created from a
 * file path on disk (which is written to disk when the application starts and
 * cleaned up when the application closes).
 */
public class PictureMarkerSymbolSample extends Application {

  private MapView mapView;
  private File orangeSymbolPath;

  private final String CAMPSITE_SYMBOL =
      "http://sampleserver6.arcgisonline.com/arcgis/rest/services/Recreation/FeatureServer/0/images/e82f744ebb069bb35b234b3fea46deae";

  @Override
  public void start(Stage stage) throws Exception {

    // create border pane and application scene
    BorderPane borderPane = new BorderPane();
    Scene scene = new Scene(borderPane);

    // add a title, size the stage, and set scene to stage
    stage.setTitle("Picture Marker Symbol Example");
    stage.setHeight(700);
    stage.setWidth(800);
    stage.setScene(scene);
    stage.show();

    try {
      // create points for displaying graphics
      Point leftPoint = new Point(-228835, 6550763,
          SpatialReferences.getWebMercator()); // Disk
      Point rightPoint = new Point(-223560, 6552021,
          SpatialReferences.getWebMercator()); // URL
      Point localPoint = new Point(-226773, 6550477,
          SpatialReferences.getWebMercator());

      // create view for this map
      mapView = new MapView();

      // place map in border pane
      borderPane.setCenter(mapView);

      // create a map with the topograph basemap
      Map map = new Map(Basemap.createTopographic());

      // create graphics overlay and add it to the mapview
      GraphicsOverlay graphicsOverlay = new GraphicsOverlay();
      mapView.getGraphicsOverlays().add(graphicsOverlay);

      // create campsite picture marker symbol from URL
      PictureMarkerSymbol campsiteSymbol = new PictureMarkerSymbol(
          CAMPSITE_SYMBOL);
      // place campsite picture marker symbol on map
      placePictureMarkerSymbol(graphicsOverlay, campsiteSymbol, rightPoint);

      // create orange picture marker symbol from disk
      if (saveResourceToExternalStorage()) {
        // create orange picture marker symbol
        PictureMarkerSymbol orangeSymbol = new PictureMarkerSymbol(
            orangeSymbolPath.getAbsolutePath());
        // place orange picture marker symbol on map
        placePictureMarkerSymbol(graphicsOverlay, orangeSymbol, leftPoint);
      }

      // create blue picture marker symbol from local
      Image newImage = new Image(
          getClass().getResourceAsStream("resources/blue_symbol.png"));
      PictureMarkerSymbol blueSymbol = new PictureMarkerSymbol(newImage);
      // place blue picture marker symbol on map
      placePictureMarkerSymbol(graphicsOverlay, blueSymbol, localPoint);

      // set map to be displayed in mapview
      mapView.setMap(map);

      // create initial viewpoint using an envelope
      Envelope envelope = new Envelope(leftPoint, rightPoint);

      // set viewpoint on mapview with padding
      mapView.setViewpointGeometryWithPaddingAsync(envelope, 100.0);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Places a PictureMarkerSymbol, that is 40 by 40, on MapView by adding a
   * Graphic to the GraphicsOverlay when the PictureMarkerSymbol is done
   * loading.
   * 
   * @param graphicsOverlay used to place the PictureMarkerSymbol onto the
   *          MapView
   * @param markerSymbol PictureMarkerSymbol that is to be placed
   * @param graphicPoint where the PictureMarkerSymbol is going to be placed
   */
  private void placePictureMarkerSymbol(GraphicsOverlay graphicsOverlay,
      PictureMarkerSymbol markerSymbol, Point graphicPoint) {

    // set size of the image
    markerSymbol.setHeight(40);
    markerSymbol.setWidth(40);

    // add to the graphic overlay once done loading
    markerSymbol.addDoneLoadingListener(() -> {
      Graphic symbolGraphic = new Graphic(graphicPoint, markerSymbol);
      graphicsOverlay.getGraphics().add(symbolGraphic);
    });

    // load symbol asynchronously
    markerSymbol.loadAsync();
  }

  /**
   * Writes a resource image to a file.
   * 
   * @return true if successful
   */
  private boolean saveResourceToExternalStorage() {

    try {
      // create a file that will be deleted automatically
      orangeSymbolPath = File.createTempFile("orange_symbol", ".png");

      // save image from resources folder to computer's disk
      Image orangeImage = new Image(
          getClass().getResourceAsStream("resources/orange_symbol.png"));

      ImageIO.write(SwingFXUtils.fromFXImage(orangeImage, null), "png",
          orangeSymbolPath.getAbsoluteFile());

    } catch (Exception e) {
      e.printStackTrace();
    }

    // check to see if the resource was created on disk
    return orangeSymbolPath.exists();
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
