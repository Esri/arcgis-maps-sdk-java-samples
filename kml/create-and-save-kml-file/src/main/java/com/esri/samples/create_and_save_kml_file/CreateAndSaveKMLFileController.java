/*
 * Copyright 2019 Esri.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.esri.samples.create_and_save_kml_file;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.KmlLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.SketchCreationMode;
import com.esri.arcgisruntime.mapping.view.SketchEditor;
import com.esri.arcgisruntime.ogc.kml.*;
import com.esri.arcgisruntime.symbology.ColorUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class CreateAndSaveKMLFileController {

  @FXML private StackPane stackPane;
  @FXML private ColorPicker colorPicker;
  @FXML private ComboBox<String> pointSymbolComboBox;
  @FXML private ComboBox<SketchCreationMode> sketchCreationModeComboBox;
  @FXML private MapView mapView;

  private ArcGISMap map; // keep loadable in scope to avoid garbage collection
  private KmlDocument kmlDocument;
  private SketchEditor sketchEditor;
  private FileChooser fileChooser;

  @FXML
  public void initialize() {

    // authentication with an API key or named user is required to access basemaps and other location services
    String yourAPIKey = System.getProperty("apiKey");
    ArcGISRuntimeEnvironment.setApiKey(yourAPIKey);

    // create a map and add it to the map view
    map = new ArcGISMap(BasemapStyle.ARCGIS_DARK_GRAY);
    mapView.setMap(map);

    // create a sketch editor and add it to the map view
    sketchEditor = new SketchEditor();
    mapView.setSketchEditor(sketchEditor);

    // add geometry options for KML placemarks
    sketchCreationModeComboBox.getItems().addAll(SketchCreationMode.POINT, SketchCreationMode.POLYLINE, SketchCreationMode.POLYGON);

    // restart the sketch editor whenever the selected creation mode changes
    sketchCreationModeComboBox.getSelectionModel().selectedItemProperty().addListener(o -> startSketch());

    // start with POINT selected
    map.addDoneLoadingListener(() -> sketchCreationModeComboBox.getSelectionModel().select(0));

    // show style controls relevant to the selected sketch creation mode
    colorPicker.visibleProperty().bind(sketchCreationModeComboBox.getSelectionModel().selectedItemProperty().isNotEqualTo(SketchCreationMode.POINT));
    pointSymbolComboBox.visibleProperty().bind(sketchCreationModeComboBox.getSelectionModel().selectedItemProperty().isEqualTo(SketchCreationMode.POINT));

    // set the images for the icon selection combo box
    List<String> iconLinks = Arrays.asList(
            null, // for the default symbol
            "https://static.arcgis.com/images/Symbols/Shapes/BlueCircleLargeB.png",
            "https://static.arcgis.com/images/Symbols/Shapes/BlueDiamondLargeB.png",
            "https://static.arcgis.com/images/Symbols/Shapes/BluePin1LargeB.png",
            "https://static.arcgis.com/images/Symbols/Shapes/BluePin2LargeB.png",
            "https://static.arcgis.com/images/Symbols/Shapes/BlueSquareLargeB.png",
            "https://static.arcgis.com/images/Symbols/Shapes/BlueStarLargeB.png");
    pointSymbolComboBox.getItems().addAll(iconLinks);
    pointSymbolComboBox.setCellFactory(comboBox -> new ImageURLListCell());
    pointSymbolComboBox.setButtonCell(new ImageURLListCell());
    pointSymbolComboBox.getSelectionModel().select(0);

    // create a KML layer from a blank KML document and add it to the map
    kmlDocument = new KmlDocument();
    KmlDataset kmlDataset = new KmlDataset(kmlDocument);
    KmlLayer kmlLayer = new KmlLayer(kmlDataset);
    map.getOperationalLayers().add(kmlLayer);

    // create a file chooser to get a path for saving the KMZ file
    fileChooser = new FileChooser();
    FileChooser.ExtensionFilter kmzFilter = new FileChooser.ExtensionFilter("KMZ files (*.kmz)", "*.kmz");
    fileChooser.getExtensionFilters().add(kmzFilter);
    fileChooser.setTitle("Save KMZ file:");
  }

  /**
   * Starts the sketch editor based on the selected sketch creation mode.
   */
  @FXML
  private void startSketch() {
    // stop the sketch editor
    sketchEditor.stop();

    // request focus on stack pane to receive key events
    stackPane.requestFocus();

    // start the sketch editor with the selected creation mode
    sketchEditor.start(sketchCreationModeComboBox.getSelectionModel().getSelectedItem());
  }

  /**
   * Discard or commit the current sketch to a KML placemark if ESCAPE or ENTER are pressed while sketching.
   *
   * @param keyEvent the key event
   */
  @FXML
  private void handleKeyReleased(KeyEvent keyEvent) {
    if (keyEvent.getCode() == KeyCode.ESCAPE) {
      // clear the current sketch and start a new sketch
      startSketch();
    } else if (keyEvent.getCode() == KeyCode.ENTER && sketchEditor.isSketchValid()) {
      // project the sketched geometry to WGS84 to comply with the KML standard
      Geometry sketchGeometry = sketchEditor.getGeometry();
      Geometry projectedGeometry = GeometryEngine.project(sketchGeometry, SpatialReferences.getWgs84());

      // create a new KML placemark
      KmlGeometry kmlGeometry = new KmlGeometry(projectedGeometry, KmlAltitudeMode.CLAMP_TO_GROUND);
      KmlPlacemark currentKmlPlacemark = new KmlPlacemark(kmlGeometry);

      // update the style of the current KML placemark
      KmlStyle kmlStyle = new KmlStyle();
      currentKmlPlacemark.setStyle(kmlStyle);

      // set the selected style for the placemark
      switch (sketchGeometry.getGeometryType()) {
        case POINT:
          if (pointSymbolComboBox.getSelectionModel().getSelectedItem() != null) {
            String iconURI = pointSymbolComboBox.getSelectionModel().getSelectedItem();
            KmlIcon kmlIcon = new KmlIcon(iconURI);
            KmlIconStyle kmlIconStyle = new KmlIconStyle(kmlIcon, 1);
            kmlStyle.setIconStyle(kmlIconStyle);
          }
          break;
        case POLYLINE:
          Color polylineColor = colorPicker.getValue();
          if (polylineColor != null) {
            KmlLineStyle kmlLineStyle = new KmlLineStyle(ColorUtil.colorToArgb(polylineColor), 8);
            kmlStyle.setLineStyle(kmlLineStyle);
          }
          break;
        case POLYGON:
          Color polygonColor = colorPicker.getValue();
          if (polygonColor != null) {
            KmlPolygonStyle kmlPolygonStyle = new KmlPolygonStyle(ColorUtil.colorToArgb(polygonColor));
            kmlPolygonStyle.setFilled(true);
            kmlPolygonStyle.setOutlined(false);
            kmlStyle.setPolygonStyle(kmlPolygonStyle);
          }
          break;
      }

      // add the placemark to the kml document
      kmlDocument.getChildNodes().add(currentKmlPlacemark);

      // start a new sketch
      startSketch();
    }
  }

  /**
   * Open the file chooser to save the KML Document to a KMZ file.
   */
  @FXML
  private void handleSaveAction() {

    // get a path from the file chooser
    File kmzFile = fileChooser.showSaveDialog(mapView.getScene().getWindow());
    if (kmzFile != null) {
      // save the KML document to the file
      kmlDocument.saveAsAsync(kmzFile.getPath()).addDoneListener(() ->
          new Alert(Alert.AlertType.INFORMATION, "KMZ file saved.").show()
      );
    }
  }

  /**
   * Stops and releases all resources used in application.
   */
  void terminate() {

    if (mapView != null) {
      mapView.dispose();
    }
  }
}
