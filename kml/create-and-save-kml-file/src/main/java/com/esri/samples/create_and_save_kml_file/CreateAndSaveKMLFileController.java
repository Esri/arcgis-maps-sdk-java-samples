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

import java.util.Arrays;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.KmlLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.SketchCreationMode;
import com.esri.arcgisruntime.mapping.view.SketchEditor;
import com.esri.arcgisruntime.ogc.kml.KmlAltitudeMode;
import com.esri.arcgisruntime.ogc.kml.KmlDataset;
import com.esri.arcgisruntime.ogc.kml.KmlDocument;
import com.esri.arcgisruntime.ogc.kml.KmlGeometry;
import com.esri.arcgisruntime.ogc.kml.KmlIcon;
import com.esri.arcgisruntime.ogc.kml.KmlIconStyle;
import com.esri.arcgisruntime.ogc.kml.KmlLineStyle;
import com.esri.arcgisruntime.ogc.kml.KmlPlacemark;
import com.esri.arcgisruntime.ogc.kml.KmlPolygonStyle;
import com.esri.arcgisruntime.ogc.kml.KmlStyle;
import com.esri.arcgisruntime.symbology.ColorUtil;

public class CreateAndSaveKMLFileController {

  @FXML private MapView mapView;
  @FXML private Label instructionsText;
  @FXML private HBox geometrySelectionHBox;
  @FXML private HBox saveResetHBox;
  @FXML private VBox styleOptionsVBox;
  @FXML private Button completeSketchBtn;
  @FXML private ColorPicker colorPicker;
  @FXML private ComboBox<String> iconPicker;
  @FXML private HBox stylePickersHBox;

  private ArcGISMap map;
  private KmlDocument kmlDocument;
  private KmlPlacemark currentKmlPlacemark;
  private SketchEditor sketchEditor;
  private SketchCreationMode sketchCreationMode;
  private FileChooser fileChooser;

  @FXML
  public void initialize() {

    // create a map and add it to the map view
    map = new ArcGISMap(Basemap.createDarkGrayCanvasVector());
    mapView.setMap(map);

    sketchCreationMode = null;
    sketchEditor = new SketchEditor();
    mapView.setSketchEditor(sketchEditor);

    sketchEditor.addGeometryChangedListener(sketchGeometryChangedListener ->
      // save button enable depends on if the sketch is valid
      completeSketchBtn.setDisable(!sketchEditor.isSketchValid())
    );

    // set the images for the point icon picker
    List<String> iconLinks = Arrays.asList(
            "http://static.arcgis.com/images/Symbols/Shapes/BlueCircleLargeB.png",
            "http://static.arcgis.com/images/Symbols/Shapes/BlueDiamondLargeB.png",
            "http://static.arcgis.com/images/Symbols/Shapes/BluePin1LargeB.png",
            "http://static.arcgis.com/images/Symbols/Shapes/BluePin2LargeB.png",
            "http://static.arcgis.com/images/Symbols/Shapes/BlueSquareLargeB.png",
            "http://static.arcgis.com/images/Symbols/Shapes/BlueStarLargeB.png");
    iconPicker.getItems().addAll(iconLinks);
    iconPicker.setCellFactory(comboBox -> new IconListCell());
    iconPicker.setButtonCell(new IconListCell());
    iconPicker.getSelectionModel().select(0);

    // create a file chooser to get a path for saving the KMZ file
    fileChooser = new FileChooser();
    FileChooser.ExtensionFilter kmzFilter = new FileChooser.ExtensionFilter("KMZ files (*.kmz)", "*.kmz");
    fileChooser.getExtensionFilters().add(kmzFilter);

    // set up a new KML document and layer
    resetKmlDocument();
  }

  /**
   *
   */
  @FXML
  private void resetKmlDocument() {
    // clear any existing layers from the map
    map.getOperationalLayers().clear();

    // reset the most recently placed placemark
    currentKmlPlacemark = null;

    // create a new KmlDocument
    kmlDocument = new KmlDocument();
    kmlDocument.setName("KML Sample Document");

    // create a kml dataset using the kml document
    KmlDataset kmlDataset = new KmlDataset(kmlDocument);

    // create the kml layer using the kml dataset
    KmlLayer kmlLayer = new KmlLayer(kmlDataset);

    // add the kml layer to the map
    map.getOperationalLayers().add(kmlLayer);

    // disable the save/reset buttons
    saveResetHBox.getChildren().forEach(node -> node.setDisable(true));
  }

  @FXML
  private void resolveSelectGeometryClick(ActionEvent event) {
    // disable and hide the geometry buttons
    geometrySelectionHBox.getChildren().forEach(node -> node.setDisable(true));
    geometrySelectionHBox.setVisible(false);

    // disable the save/reset buttons while sketching
    saveResetHBox.getChildren().forEach(node -> node.setDisable(true));

    // show the 'Complete Sketch' button
    completeSketchBtn.setVisible(true);

    // create variables for the sketch creation mode and color

    // set the creation mode and UI based on which button called this method
    switch (((Button) event.getSource()).getText()) {
      case "Point":
        sketchCreationMode = SketchCreationMode.POINT;
        instructionsText.setText("Click to add a point.");
        iconPicker.setDisable(false);
        break;

      case "Polyline":
        sketchCreationMode = SketchCreationMode.POLYLINE;
        instructionsText.setText("Click to add a vertex.");
        colorPicker.setDisable(false);
        break;

      case "Polygon":
        sketchCreationMode = SketchCreationMode.POLYGON;
        instructionsText.setText("Click to add a vertex.");
        colorPicker.setDisable(false);
        break;
    }

    if (sketchCreationMode != null) {
      sketchEditor.start(sketchCreationMode);
    }

  }

  @FXML
  private void resolveCompleteSketchClick() {
    geometrySelectionHBox.setVisible(true);
    completeSketchBtn.setVisible(false);

    // get the user-drawn geometry
    Geometry sketchGeometry = sketchEditor.getGeometry();

    if (sketchGeometry != null) {

      // project the geometry to WGS84 to comply with the KML standard
      Geometry projectedGeometry = GeometryEngine.project(sketchGeometry, SpatialReferences.getWgs84());
      // create a KML geometry
      KmlGeometry kmlGeometry = new KmlGeometry(projectedGeometry, KmlAltitudeMode.CLAMP_TO_GROUND);

      // create a new placemark
      currentKmlPlacemark = new KmlPlacemark(kmlGeometry);

      // add the placemark to the kml document
      kmlDocument.getChildNodes().add(currentKmlPlacemark);

      // show the style editing UI
      styleOptionsVBox.setVisible(true);
    }

    sketchEditor.stop();
  }

  @FXML
  private void resolveApplyStyleClick() {
    // toggle the UI
    toggleUI();

    KmlStyle kmlStyle = new KmlStyle();
    currentKmlPlacemark.setStyle(kmlStyle);

    // set the selected style for the placemark
    switch (currentKmlPlacemark.getGeometries().get(0).getType().toString()) {
      // create a KmlIconStyle using the selected icon
      case ("POINT"):
        if (iconPicker.getSelectionModel().getSelectedItem() != null) {
          String iconURI = iconPicker.getSelectionModel().getSelectedItem();
          KmlIcon kmlIcon = new KmlIcon(iconURI);
          KmlIconStyle kmlIconStyle = new KmlIconStyle(kmlIcon, 1);
          kmlStyle.setIconStyle(kmlIconStyle);
        }
        break;
      case ("POLYLINE"):
        if (colorPicker.valueProperty().get() != null) {
          Color color = colorPicker.valueProperty().get();
          KmlLineStyle kmlLineStyle = new KmlLineStyle(ColorUtil.colorToArgb(color), 8);
          kmlStyle.setLineStyle(kmlLineStyle);
        }
        break;
      case ("POLYGON"):
        if (colorPicker.valueProperty().get() != null) {
          Color color = colorPicker.valueProperty().get();
          KmlPolygonStyle kmlPolygonStyle = new KmlPolygonStyle(ColorUtil.colorToArgb(color));
          kmlPolygonStyle.setFilled(true);
          kmlPolygonStyle.setOutlined(false);
          kmlStyle.setPolygonStyle(kmlPolygonStyle);
        }
        break;
    }
  }

  @FXML
  private void resolveNoStyleClick() {
    // toggle the UI
    toggleUI();
  }

  /**
   * Toggles the UI when making a style selection for the recently created KML element, to allow choosing a new KML element to create.
   */
  private void toggleUI() {
    // enable the geometry buttons
    geometrySelectionHBox.getChildren().forEach(node -> node.setDisable(false));

    // hide the style editing UI
    styleOptionsVBox.setVisible(false);

    // disable both pickers within the style editing box
    stylePickersHBox.getChildren().forEach(node -> node.setDisable(true));

    // enable or disable the save/reset buttons, depending on whether there are kml elements in the document
    saveResetHBox.getChildren().forEach(node -> node.setDisable(kmlDocument.getChildNodes().isEmpty()));
  }

  @FXML
  private void handleSaveClick() {
    // write the KMZ file to the path chosen in the file chooser
    kmlDocument.saveAsAsync(fileChooser.showSaveDialog(mapView.getScene().getWindow()).getPath());
  }

  /**
   * Stops and releases all resources used in application.
   */
  public void terminate() {

    if (mapView != null) {
      mapView.dispose();
    }
  }
}
