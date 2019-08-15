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

import java.io.File;
import java.util.Arrays;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
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

  @FXML private Button completeSketchBtn;
  @FXML private ColorPicker colorPicker;
  @FXML private ComboBox<String> iconPicker;
  @FXML private HBox geometrySelectionHBox;
  @FXML private HBox saveResetHBox;
  @FXML private AnchorPane stylePickersAnchorPane;
  @FXML private Label instructionsText;
  @FXML private MapView mapView;
  @FXML private VBox styleOptionsVBox;

  private ArcGISMap map;
  private FileChooser fileChooser;
  private KmlDocument kmlDocument;
  private KmlPlacemark currentKmlPlacemark;
  private SketchEditor sketchEditor;
  private SketchCreationMode sketchCreationMode;

  @FXML
  public void initialize() {

    // create a map and add it to the map view
    map = new ArcGISMap(Basemap.createDarkGrayCanvasVector());
    mapView.setMap(map);

    // create a sketch editor and add it to the map view
    sketchEditor = new SketchEditor();
    mapView.setSketchEditor(sketchEditor);

    // create a sketch creation mode
    sketchCreationMode = null;

    // enable the 'Complete Sketch' button depending on whether the sketch is valid
    sketchEditor.addGeometryChangedListener(sketchGeometryChangedListener ->
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
    fileChooser.setTitle("Save KMZ file:");

    // set up a new KML document and layer
    resetKmlDocument();

    // wait for the map to finish loading to enable the UI
    map.addDoneLoadingListener(() -> toggleUI());
  }

  /**
   * Clears all operational layers and creates a blank KML document with a dataset and layer.
   */
  @FXML
  private void resetKmlDocument() {
    // clear any existing layers from the map
    map.getOperationalLayers().clear();

    // reset the most recently placed placemark
    currentKmlPlacemark = null;

    // create a new KML document
    kmlDocument = new KmlDocument();
    kmlDocument.setName("KML Sample Document");

    // create a KML dataset using the KML document
    KmlDataset kmlDataset = new KmlDataset(kmlDocument);

    // create the KML layer using the KML dataset
    KmlLayer kmlLayer = new KmlLayer(kmlDataset);

    // add the KML layer to the map
    map.getOperationalLayers().add(kmlLayer);

    // disable the save/reset buttons
    saveResetHBox.getChildren().forEach(node -> node.setDisable(true));
  }

  /**
   * Sets the sketch creation mode to the selected geometry and starts the sketch editor.
   *
   * @param event used to identify which button was pressed to select the corresponding geometry.
   */
  @FXML
  private void resolveSelectGeometryClick(ActionEvent event) {
    // disable and hide the geometry buttons
    geometrySelectionHBox.getChildren().forEach(node -> node.setDisable(true));
    geometrySelectionHBox.setVisible(false);

    // disable the save/reset buttons
    saveResetHBox.getChildren().forEach(node -> node.setDisable(true));

    // show the 'Complete Sketch' button
    completeSketchBtn.setVisible(true);

    // set the sketch creation mode and UI based on which button called this method
    switch (((Button) event.getSource()).getText()) {
      case "Point":
        sketchCreationMode = SketchCreationMode.POINT;
        instructionsText.setText("Click to add a point.");
        // enable the icon picker to allow applying icons after completion
        iconPicker.setVisible(true);
        break;

      case "Polyline":
        sketchCreationMode = SketchCreationMode.POLYLINE;
        instructionsText.setText("Click to add a vertex.");
        // enable the color picker to allow applying colors after completion
        colorPicker.setVisible(true);
        break;

      case "Polygon":
        sketchCreationMode = SketchCreationMode.POLYGON;
        instructionsText.setText("Click to add a vertex.");
        // enable the color picker to allow applying colors after completion
        colorPicker.setVisible(true);
        break;
    }

    // start the sketch editor to capture the user input
    if (sketchCreationMode != null) {
      sketchEditor.start(sketchCreationMode);
    }

  }

  /**
   * Completes the sketch in progress, creates a KML placemark from the resulting geometry, and adds it to the KML document.
   */
  @FXML
  private void resolveCompleteSketchClick() {

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

    // hide the 'Complete Sketch' button and show the geometry selection buttons
    completeSketchBtn.setVisible(false);
    geometrySelectionHBox.setVisible(true);

    // stop the sketch editor
    sketchEditor.stop();

    // update the instructions text
    instructionsText.setText("Select a style for the geometry.");
  }

  /**
   * Applies the selected style to the KML placemark depending on the type of geometry.
   */
  @FXML
  private void resolveApplyStyleClick() {

    KmlStyle kmlStyle = new KmlStyle();
    currentKmlPlacemark.setStyle(kmlStyle);

    // set the selected style for the placemark
    switch (currentKmlPlacemark.getGeometries().get(0).getType().toString()) {

      // create a KML icon style using the selected icon
      case ("POINT"):
        if (iconPicker.getSelectionModel().getSelectedItem() != null) {
          String iconURI = iconPicker.getSelectionModel().getSelectedItem();
          KmlIcon kmlIcon = new KmlIcon(iconURI);
          KmlIconStyle kmlIconStyle = new KmlIconStyle(kmlIcon, 1);
          kmlStyle.setIconStyle(kmlIconStyle);
        }
        break;

      // create a KML line style using the selected color
      case ("POLYLINE"):
        if (colorPicker.valueProperty().get() != null) {
          Color color = colorPicker.valueProperty().get();
          KmlLineStyle kmlLineStyle = new KmlLineStyle(ColorUtil.colorToArgb(color), 8);
          kmlStyle.setLineStyle(kmlLineStyle);
        }
        break;

      // create a KML polygon style using the selected color as a fill
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

    // re-enables the UI to allow creating another KML element, saving the document, or resetting the sample
    toggleUI();
  }

  /**
   * Toggles the UI when making a style selection for the recently created KML element, to allow choosing a new KML element to create.
   */
  @FXML
  private void toggleUI() {
    // enable the geometry buttons
    geometrySelectionHBox.getChildren().forEach(node -> node.setDisable(false));

    // hide the style editing UI
    styleOptionsVBox.setVisible(false);

    // hide both pickers within the style editing box
    stylePickersAnchorPane.getChildren().forEach(node -> node.setVisible(false));

    // reset the instructions text
    instructionsText.setText("Select a geometry to create:");

    // enable or disable the save/reset buttons, depending on whether there are kml elements in the document
    saveResetHBox.getChildren().forEach(node -> node.setDisable(kmlDocument.getChildNodes().isEmpty()));
  }

  /**
   * Opens a FileChooser and saves a KMZ file to the selected path.
   */
  @FXML
  private void handleSaveClick() {

    try {
      // get a path from the file chooser
      File kmzFile = fileChooser.showSaveDialog(mapView.getScene().getWindow());
      // write the KMZ file to the path chosen
      if (kmzFile != null) {
        kmlDocument.saveAsAsync(kmzFile.getPath());
      } else {
        new Alert(Alert.AlertType.WARNING, "KMZ file not saved.").show();
      }
    } catch (Exception e) {
      new Alert(Alert.AlertType.ERROR, "Error saving KMZ file.").show();
    }
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
