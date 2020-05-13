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

package com.esri.samples.picture_marker_symbol;

import java.io.File;

import javax.imageio.ImageIO;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;

public class PictureMarkerSymbolSample extends Application {

  private MapView mapView;
  private File orangeSymbolPath;
  private GraphicsOverlay graphicsOverlay;

  // keep loadables in scope to avoid garbage collection
  private ArcGISMap map;
  private PictureMarkerSymbol campsiteSymbol;
  private PictureMarkerSymbol blueSymbol;
  private PictureMarkerSymbol orangeSymbol;

  private static final String CAMPSITE_SYMBOL =
      "https://sampleserver6.arcgisonline.com/arcgis/rest/services/Recreation/FeatureServer/0/images/e82f744ebb069bb35b234b3fea46deae";

  @Override
  public void start(Stage stage) {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);

      // set title, size, and add scene to stage
      stage.setTitle("Picture Marker Symbol Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // create a ArcGISMap with the topographic basemap
      map = new ArcGISMap(Basemap.createTopographic());

      // create view for this map
      mapView = new MapView();

      // create graphics overlay and add it to the mapview
      graphicsOverlay = new GraphicsOverlay();

      mapView.getGraphicsOverlays().add(graphicsOverlay);

      // create points for displaying graphics
      Point leftPoint = new Point(-228835, 6550763, SpatialReferences.getWebMercator()); // Disk
      Point rightPoint = new Point(-223560, 6552021, SpatialReferences.getWebMercator()); // URL
      Point middlePoint = new Point(-226773, 6550477, SpatialReferences.getWebMercator());

      // create orange picture marker symbol from disk
      if (saveResourceToExternalStorage()) {
        // create orange picture marker symbol
        orangeSymbol = new PictureMarkerSymbol(orangeSymbolPath.getAbsolutePath());
        // place orange picture marker symbol on ArcGISMap
        placePictureMarkerSymbol(orangeSymbol, leftPoint);
      }

      // create blue picture marker symbol from local
      Image newImage = new Image("/blue_symbol.png");
      blueSymbol = new PictureMarkerSymbol(newImage);
      // place blue picture marker symbol on ArcGISMap
      placePictureMarkerSymbol(blueSymbol, middlePoint);

      // create campsite picture marker symbol from URL
      campsiteSymbol = new PictureMarkerSymbol(CAMPSITE_SYMBOL);

      // place campsite picture marker symbol on ArcGISMap
      map.addDoneLoadingListener(() -> {
        if (map.getLoadStatus() == LoadStatus.LOADED) {
          placePictureMarkerSymbol(campsiteSymbol, rightPoint);
        } else {
          Alert alert = new Alert(Alert.AlertType.ERROR, "Map Failed to Load!");
          alert.show();
        }
      });

      // set ArcGISMap to be displayed in mapview
      mapView.setMap(map);

      // set viewpoint on mapview with padding
      Envelope envelope = new Envelope(leftPoint, rightPoint);
      mapView.setViewpointGeometryAsync(envelope, 100.0);

      // add the map view and control panel to stack pane
      stackPane.getChildren().add(mapView);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Adds a Graphic to the Graphics Overlay using a Point and a Picture Marker
   * Symbol.
   * 
   * @param markerSymbol PictureMarkerSymbol to be used
   * @param graphicPoint where the Graphic is going to be placed
   */
  private void placePictureMarkerSymbol(PictureMarkerSymbol markerSymbol, Point graphicPoint) {

    // set size of the image
    markerSymbol.setHeight(40);
    markerSymbol.setWidth(40);

    // load symbol asynchronously
    markerSymbol.loadAsync();

    // add to the graphic overlay once done loading
    markerSymbol.addDoneLoadingListener(() -> {
      if (markerSymbol.getLoadStatus() == LoadStatus.LOADED) {
        Graphic symbolGraphic = new Graphic(graphicPoint, markerSymbol);
        graphicsOverlay.getGraphics().add(symbolGraphic);
      } else {
        Alert alert = new Alert(Alert.AlertType.ERROR, "Picture Marker Symbol Failed to Load!");
        alert.show();
      }
    });

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
      Image orangeImage = new Image("orange_symbol.png");

      ImageIO.write(SwingFXUtils.fromFXImage(orangeImage, null), "png", orangeSymbolPath.getAbsoluteFile());

    } catch (Exception e) {
      e.printStackTrace();
    }

    // check to see if the resource was created on disk
    return orangeSymbolPath.exists();
  }

  /**
   * Stops and releases all resources used in application.
   */
  @Override
  public void stop() {

    // release resources when the application closes
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
