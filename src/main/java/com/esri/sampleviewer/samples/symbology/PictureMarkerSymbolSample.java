/*
+ * Copyright 2015 Esri.
+ *
+ * Licensed under the Apache License, Version 2.0 (the "License");
+ * you may not use this file except in compliance with the License.
+ * You may obtain a copy of the License at
+ *
+ * http://www.apache.org/licenses/LICENSE-2.0
+ *
+ * Unless required by applicable law or agreed to in writing, software
+ * distributed under the License is distributed on an "AS IS" BASIS,
+ * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
+ * See the License for the specific language governing permissions and
+ * limitations under the License.
+ */

package com.esri.sampleviewer.samples.symbology;

import java.io.File;

import javax.imageio.ImageIO;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Map;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;

/**
 * This sample demonstrates how to create a PictureMarkerSymbol from a URL,
 * locally on the application, or from disk storage.
 * <p>
 * The {@link GraphicsOverlay} will hold our {@link PictureMarkerSymbol}s so
 * they can be displayed on the MapView.
 * <p>
 * For loading a file store on a disk. The application stores a temporary file
 * onto your machine and then gets the address of where that file was stored.
 * <h4>How it Works</h4>
 * 
 * First a {@link GraphicsOverlay} is created and added to the
 * {@link MapView#getGraphicsOverlays} method. The PictureMarkerSymbol(String
 * uri) constructor creates a symbol from a specified URI. This means that a
 * symbol can be created from a URL, a web page, or one can enter a path
 * location to a file stored on their machine.
 * <p>
 * The new PictureMarkerSymbol(Image) constructor creates a symbol from a Javafx
 * Image. The Image used for this demo is stored in the resource folder that
 * comes with the application.
 * <p>
 * Lastly once a symbol is created it will need to be added to a {@link Graphic}
 * along with a Point and then pass that graphic to the
 * {@link GraphicsOverlay#getGraphics} method.
 */
public class PictureMarkerSymbolSample extends Application {

  private MapView mapView;
  private File orangeSymbolPath;

  private static final String CAMPSITE_SYMBOL =
      "http://sampleserver6.arcgisonline.com/arcgis/rest/services/Recreation/FeatureServer/0/images/e82f744ebb069bb35b234b3fea46deae";
  private static final String SAMPLES_THEME_PATH =
      "../resources/SamplesTheme.css";

  @Override
  public void start(Stage stage) throws Exception {

    // create stack pane and application scene
    StackPane stackPane = new StackPane();
    Scene scene = new Scene(stackPane);
    scene.getStylesheets().add(getClass().getResource(SAMPLES_THEME_PATH)
        .toExternalForm());

    // set title, size, and add scene to stage
    stage.setTitle("Picture Marker Symbol Sample");
    stage.setWidth(800);
    stage.setHeight(700);
    stage.setScene(scene);
    stage.show();

    // create a control panel
    VBox vBoxControl = new VBox(6);
    vBoxControl.setMaxSize(250, 190);
    vBoxControl.getStyleClass().add("panel-region");

    // create sample description
    Label descriptionLabel = new Label("Sample Description:");
    descriptionLabel.getStyleClass().add("panel-label");
    TextArea description = new TextArea(
        "This sample shows how to create a Picture Marker Symbol from a URL, locally on"
            + " the application, or from disk storage");
    description.setWrapText(true);
    description.autosize();
    description.setEditable(false);

    // add label and sample description to the control panel
    vBoxControl.getChildren().addAll(descriptionLabel, description);
    try {

      // create a map with the topograph basemap
      final Map map = new Map(Basemap.createTopographic());

      // create view for this map
      mapView = new MapView();

      // create graphics overlay and add it to the mapview
      GraphicsOverlay graphicsOverlay = new GraphicsOverlay();

      mapView.getGraphicsOverlays().add(graphicsOverlay);

      // create points for displaying graphics
      Point leftPoint = new Point(-228835, 6550763, SpatialReferences
          .getWebMercator()); // Disk
      Point rightPoint = new Point(-223560, 6552021, SpatialReferences
          .getWebMercator()); // URL
      Point middlePoint = new Point(-226773, 6550477, SpatialReferences
          .getWebMercator());

      // create orange picture marker symbol from disk
      if (saveResourceToExternalStorage()) {
        // create orange picture marker symbol
        PictureMarkerSymbol orangeSymbol = new PictureMarkerSymbol(
            orangeSymbolPath
                .getAbsolutePath());
        // place orange picture marker symbol on map
        placePictureMarkerSymbol(graphicsOverlay, orangeSymbol, leftPoint);
      }

      // create blue picture marker symbol from local
      Image newImage = new Image(getClass()
          .getResourceAsStream("resources/blue_symbol.png"));
      PictureMarkerSymbol blueSymbol = new PictureMarkerSymbol(newImage);
      // place blue picture marker symbol on map
      placePictureMarkerSymbol(graphicsOverlay, blueSymbol, middlePoint);

      // create campsite picture marker symbol from URL
      PictureMarkerSymbol campsiteSymbol = new PictureMarkerSymbol(
          CAMPSITE_SYMBOL);
      // place campsite picture marker symbol on map
      placePictureMarkerSymbol(graphicsOverlay, campsiteSymbol, rightPoint);

      campsiteSymbol.addDoneLoadingListener(() -> {
        Platform.runLater(() -> {
          stackPane.getChildren().add(vBoxControl);
          StackPane.setAlignment(vBoxControl, Pos.TOP_LEFT);
          StackPane.setMargin(vBoxControl, new Insets(10, 0, 0, 10));
        });
      });

      // set map to be displayed in mapview
      mapView.setMap(map);

      // set viewpoint on mapview with padding
      Envelope envelope = new Envelope(leftPoint, rightPoint);
      mapView.setViewpointGeometryWithPaddingAsync(envelope, 100.0);

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
   * @param graphicsOverlay holds information about Graphics
   * @param markerSymbol PictureMarkerSymbol to be used
   * @param graphicPoint where the Graphic is going to be placed
   */
  private void placePictureMarkerSymbol(GraphicsOverlay graphicsOverlay,
      PictureMarkerSymbol markerSymbol, Point graphicPoint) {

    // set size of the image
    markerSymbol.setHeight(40);
    markerSymbol.setWidth(40);

    // load symbol asynchronously
    markerSymbol.loadAsync();

    // add to the graphic overlay once done loading
    markerSymbol.addDoneLoadingListener(() -> {
      Graphic symbolGraphic = new Graphic(graphicPoint, markerSymbol);
      graphicsOverlay.getGraphics().add(symbolGraphic);
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
      Image orangeImage = new Image(getClass().getResourceAsStream(
          "resources/orange_symbol.png"));

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
