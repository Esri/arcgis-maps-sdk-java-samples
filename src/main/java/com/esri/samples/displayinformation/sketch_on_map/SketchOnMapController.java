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
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;

public class SketchOnMapController {

  private SketchEditor sketchEditor;
  private GraphicsOverlay graphicsOverlay;
  private Graphic graphic;
  private SimpleFillSymbol fillSymbol;
  private SimpleLineSymbol lineSymbol;
  private SimpleLineSymbol polygonLineSymbol;
  private SimpleMarkerSymbol pointSymbol;

  @FXML private MapView mapView;
  @FXML private Button redoButton;
  @FXML private Button undoButton;
  @FXML private Button clearButton;
  @FXML private Button saveButton;
  @FXML private Button editButton;
  @FXML private Button stopSketchButton;
  @FXML private Button createPointButton;
  @FXML private Button createMultiPointButton;
  @FXML private Button createPolylineButton;
  @FXML private Button createPolygonButton;
  @FXML private Button createFreehandPolylineButton;
  @FXML private Button createFreehandPolygonButton;


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


    // disable all buttons except sketch buttons when starting application
    disableButtons();

    // define symbols for graphics
    // red square for points
    pointSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.SQUARE, 0xFFFF0000, 20);
    // thin green line for polylines
    lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0xFF64c113, 4);
    // blue outline for polygons
    polygonLineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0xFF1396c1, 4);
    // cross-hatched interior for polygons
    fillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.CROSS, 0x40FFA9A9,polygonLineSymbol);

    // start sketch editor with relevant mode for each sketch button
    createPointButton.setOnAction(event -> {
      graphicsOverlay.clearSelection();
      sketchEditor.start(SketchCreationMode.POINT);
    });

    createMultiPointButton.setOnAction(event -> {
      graphicsOverlay.clearSelection();
      sketchEditor.start(SketchCreationMode.MULTIPOINT);
    });

    createPolylineButton.setOnAction(event -> {
      graphicsOverlay.clearSelection();
      sketchEditor.start(SketchCreationMode.POLYLINE);
    });

    createPolygonButton.setOnAction(event -> {
      graphicsOverlay.clearSelection();
      sketchEditor.start(SketchCreationMode.POLYGON);
    });

    createFreehandPolylineButton.setOnAction(event -> {
      graphicsOverlay.clearSelection();
      sketchEditor.start(SketchCreationMode.FREEHAND_LINE);
    });

    createFreehandPolygonButton.setOnAction(event -> {
      graphicsOverlay.clearSelection();
      sketchEditor.start(SketchCreationMode.FREEHAND_POLYGON);
    });


    // if possible, undo the last change made whilst sketching graphic
    undoButton.setOnAction(event -> {
      if (sketchEditor.canUndo()) {
        sketchEditor.undo();
      }
    });

    // if possible, redo the last change made whilst sketching graphic
    redoButton.setOnAction(event -> {
      if (sketchEditor.canRedo()) {
        sketchEditor.redo();
      }
    });

    // clear the graphics overlay, and disable the clear button
    clearButton.setOnAction(event -> {
      graphicsOverlay.getGraphics().clear();
      sketchEditor.stop();
      disableButtons();
    });

    // add a listener for when sketch geometry is changed
    sketchEditor.addGeometryChangedListener(SketchGeometryChangedListener -> {

      // if sketch is valid, enable save and stop sketch buttons
      stopSketchButton.setText("Cancel sketching");
      stopSketchButton.setDisable(false);

      if (sketchEditor.isSketchValid()) {
        saveButton.setText("Save sketch and stop sketching");
        saveButton.setDisable(false);
      } else
      {saveButton.setDisable(true);}

      // if the sketch editor can undo, enable the undo button otherwise disable it
      if (sketchEditor.canUndo()) {
        undoButton.setDisable(false);
      } else {
        undoButton.setDisable(true);
      }

      // if the sketch editor can redo, enable the redo button otherwise disable it
      if (sketchEditor.canRedo()) {
        redoButton.setDisable(false);
      } else {
        redoButton.setDisable(true);
      }
    });
  }

  /**
   * When the done button is clicked, check that sketch is valid. If so, get the geometry from the sketch, set its
   * symbol and add it to the graphics overlay.
   */
  private void storeGraphicInGraphicOverlay() {

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
  }

  /**
   * use sketch editor to edit the geometry of the selected graphic
   */
  @FXML
  private void editSketch() {

    stopSketchButton.setDisable(false);
    saveButton.setDisable(true);
    saveButton.setText("Save edits");
    stopSketchButton.setText("Stop sketching");
    editButton.setText("Edit Sketch (active)");

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
  private void stopSketch () {

    sketchEditor.stop();
    graphicsOverlay.clearSelection();
    disableButtons();
    // set text to inform the user the sketch is disabled
    stopSketchButton.setDisable(false);
    stopSketchButton.setText("Select a sketch geometry");
    saveButton.setText("Save sketch");

    if (!graphicsOverlay.getGraphics().isEmpty()) {
      editButton.setText("Select graphic to edit");
      clearButton.setDisable(false);
    }

    // allow graphics to be selected after stopSketch button is used.
    selectGraphic();
  }

  /**
   * save the sketch as a graphic, and store graphic to the graphics overlay
   */
  @FXML
  private void saveGraphic () {
      // save the graphic in to the graphics overlay
      storeGraphicInGraphicOverlay();

      // allow the user to select a graphic from the map view
      selectGraphic();
      graphicsOverlay.clearSelection();
      disableButtons();

      if (!graphicsOverlay.getGraphics().isEmpty()){
        clearButton.setDisable(false);
      }

      stopSketchButton.setDisable(false);
      editButton.setText("Stop sketching to edit");

      // set text on the disabled save button to show user what geometry is active
      if (saveButton.isDisabled()) {
        saveButton.setText("Sketch saved ");
      }

      stopSketchButton.setText("Choose new sketch or select saved one to edit");
      stopSketchButton.setDisable(false);
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
            editButton.setText("Edit Sketch");
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
    stopSketchButton.setDisable(true);
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
