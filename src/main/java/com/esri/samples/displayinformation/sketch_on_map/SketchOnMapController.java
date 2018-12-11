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

package com.esri.samples.displayinformation.sketch_on_map;

import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.IdentifyGraphicsOverlayResult;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.SketchCreationMode;
import com.esri.arcgisruntime.mapping.view.SketchEditor;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;


public class SketchOnMapController {

  @FXML
  private MapView mapView;
  @FXML
  private Button redoButton;
  @FXML
  private Button undoButton;
  @FXML
  private Button clearButton;
  @FXML
  private Button saveButton;
  @FXML
  private Button editButton;
  @FXML
  private Button stopButton;

  @FXML
  private Button createPointButton;
  @FXML
  private Button createMultiPointButton;
  @FXML
  private Button createPolylineButton;
  @FXML
  private Button createPolygonButton;
  @FXML
  private Button createFreehandPolylineButton;
  @FXML
  private Button createFreehandPolygonButton;

  private SketchEditor sketchEditor;
  private GraphicsOverlay graphicsOverlay;
  private Graphic graphic;
  private SimpleFillSymbol fillSymbol;
  private SimpleLineSymbol lineSymbol;
  private SimpleLineSymbol polygonLineSymbol;
  private SimpleMarkerSymbol pointSymbol;


  public void initialize() {

    // create a map with a basemap and add it to the map view
    ArcGISMap map = new ArcGISMap(Basemap.Type.IMAGERY, 64.3286, -15.5314, 13);
    mapView.setMap(map);

    // create a graphics overlay for the graphics
    graphicsOverlay = new GraphicsOverlay();

    // add the graphics overlay to the map view
    mapView.getGraphicsOverlays().add(graphicsOverlay);

    // create a new sketch editor and add it to the map view
    sketchEditor = new SketchEditor();
    mapView.setSketchEditor(sketchEditor);

    // red square for points
    pointSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.SQUARE, 0xFFFF0000, 20);
    // thin green line for polylines
    lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0xFF64c113, 4);
    // blue outline for polygons
    polygonLineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0xFF1396c1, 4);
    // cross-hatched interior for polygons
    fillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.CROSS, 0x40FFA9A9, polygonLineSymbol);

    // add a listener for when sketch geometry is changed
    sketchEditor.addGeometryChangedListener(SketchGeometryChangedListener -> {
      stopButton.setDisable(false);
      // save button enable depends on if the sketch is valid. If the sketch is valid then set disable opposite of true
      saveButton.setDisable(!sketchEditor.isSketchValid());
      undoButton.setDisable(!sketchEditor.canUndo());
      redoButton.setDisable(!sketchEditor.canUndo());
    });
  }

  /**
   * use sketch editor to edit the geometry of the selected graphic
   */
  @FXML
  private void handleEditButtonClicked() {
    stopButton.setDisable(false);
    saveButton.setDisable(true);

    // if the graphics overlay contains graphics, select the first graphic
    // and start the sketch editor based on that graphic's geometry
    if (!graphicsOverlay.getSelectedGraphics().isEmpty()) {
      graphic = graphicsOverlay.getSelectedGraphics().get(0);
      sketchEditor.start(graphic.getGeometry());
    }
  }

  /**
   * stop the sketch editor
   */
  @FXML
  private void handleStopButtonClicked() {
    sketchEditor.stop();
    graphicsOverlay.clearSelection();
    disableButtons();
    // set text to inform the user the sketch is disabled
    stopButton.setDisable(false);
    // allow graphics to be selected after stopS button is used.
    selectGraphic();
  }

  // start sketch editor with relevant mode for each sketch button
  @FXML
  private void handlePointButtonClicked() {
    graphicsOverlay.clearSelection();
    sketchEditor.start(SketchCreationMode.POINT);
  }

  @FXML
  private void handleMultipointButtonClicked() {
    graphicsOverlay.clearSelection();
    sketchEditor.start(SketchCreationMode.MULTIPOINT);
  }

  @FXML
  private void handlePolylineButtonClicked() {
    graphicsOverlay.clearSelection();
    sketchEditor.start(SketchCreationMode.POLYLINE);
  }

  @FXML
  private void handlePolygonButtonClicked() {
    graphicsOverlay.clearSelection();
    sketchEditor.start(SketchCreationMode.POLYGON);
  }

  @FXML
  private void handleFreehandPolylineButtonClicked() {
    graphicsOverlay.clearSelection();
    sketchEditor.start(SketchCreationMode.FREEHAND_LINE);
  }

  @FXML
  private void handleFreehandPolygonButtonClicked() {
    graphicsOverlay.clearSelection();
    sketchEditor.start(SketchCreationMode.FREEHAND_POLYGON);
  }

  @FXML
  private void handleUndoButtonClicked() {
    if (sketchEditor.canUndo()) {
      sketchEditor.undo();
    }
  }

  @FXML
  private void handleRedoButtonClicked() {
    if (sketchEditor.canRedo()) {
      sketchEditor.redo();
    }
  }

  /**
   * When the done button is clicked, check that sketch is valid. If so, get the geometry from the sketch, set its
   * symbol and add it to the graphics overlay.
   */
  @FXML
  private void handleSaveButtonClicked() {
    // save the graphic in to the graphics overlay
    // if the sketch isn't valid, stop the sketch editor.

    if (!sketchEditor.isSketchValid()) {
      sketchEditor.stop();
      return;
    }

    Geometry sketchGeometry = sketchEditor.getGeometry();
    // if the sketch geometry isn't null, and the graphics overlay isn't empty
    // get the selected graphic, and set its geometry to that of the sketch editor geometry
    // otherwise, create a new graphic based on the sketch editor geometry and give it a symbol
    if (sketchGeometry != null) {
      if (!graphicsOverlay.getSelectedGraphics().isEmpty()) {
        graphic = graphicsOverlay.getSelectedGraphics().get(0);
        graphic.setGeometry(sketchGeometry);
      } else {

        graphic = new Graphic(sketchGeometry);

        switch (sketchGeometry.getGeometryType()) {
          case POLYGON:
            graphic.setSymbol(fillSymbol);
            break;
          case POLYLINE:
            graphic.setSymbol(lineSymbol);
            break;
          case POINT:
          case MULTIPOINT:
            graphic.setSymbol(pointSymbol);
        }
        graphicsOverlay.getGraphics().add(graphic);
      }
    }
    sketchEditor.stop();

    // allow the user to select a graphic from the map view
    selectGraphic();
    graphicsOverlay.clearSelection();
    disableButtons();

    if (!graphicsOverlay.getGraphics().isEmpty()){
      clearButton.setDisable(false);
    }

    stopButton.setDisable(true);

  }

  /**
   * clear the graphics overlay of any saved graphics
   */
  @FXML
  private void handleClearButtonClicked() {
    graphicsOverlay.getGraphics().clear();
    sketchEditor.stop();
    disableButtons();
  }

  /**
   * Allows the user to select a graphic from the graphics overlay
   */

  private void selectGraphic() {

    mapView.setOnMouseClicked(e -> {

      graphicsOverlay.clearSelection();
      Point2D mapViewPoint = new Point2D(e.getX(), e.getY());

      // get graphics near the clicked location
      ListenableFuture<IdentifyGraphicsOverlayResult> identifyGraphics;
      identifyGraphics = mapView.identifyGraphicsOverlayAsync(graphicsOverlay, mapViewPoint, 10, false);

      identifyGraphics.addDoneListener(() -> {

        try {
          if (!identifyGraphics.get().getGraphics().isEmpty()) {
            // store the selected graphic
            graphic = identifyGraphics.get().getGraphics().get(0);
            graphic.setSelected(true);
            editButton.setDisable(false);
          } else {
            editButton.setDisable(true);
          }

        } catch (Exception x) {
          // on any error, display the stack trace
          x.printStackTrace();
        }
      });
    });
  }

  /**
   * Disable all UI buttons
   */
  private void disableButtons() {
    clearButton.setDisable(true);
    redoButton.setDisable(true);
    undoButton.setDisable(true);
    editButton.setDisable(true);
    saveButton.setDisable(true);
    stopButton.setDisable(true);
  }

  /**
   * Disposes of application resources.
   */
  void terminate() {

    if (mapView != null) {
      mapView.dispose();
    }
  }
}
