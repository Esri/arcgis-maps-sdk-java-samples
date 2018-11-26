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
import com.esri.arcgisruntime.geometry.GeometryType;

import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.IdentifyGraphicsOverlayResult;
import com.esri.arcgisruntime.mapping.view.MapView;

import com.esri.arcgisruntime.mapping.view.SketchCreationMode;
import com.esri.arcgisruntime.mapping.view.SketchEditor;
import com.esri.arcgisruntime.mapping.view.SketchGeometryChangedListener;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

public class SketchOnMapSample extends Application {

  private MapView mapView;
  private SketchEditor sketchEditor;
  private Button redoButton;
  private Button undoButton;
  private Button clearButton;
  private Button saveButton;
  private Button editButton;
  private Button cancelButton;
  private GraphicsOverlay graphicsOverlay;
  private SimpleFillSymbol fillSymbol;
  private SimpleFillSymbol editedFillSymbol;
  private SimpleLineSymbol editedLineSymbol;
  private SimpleLineSymbol lineSymbol;
  private SimpleMarkerSymbol pointSymbol;
  private Graphic graphic;
  private ComboBox<SketchEditor> sketchOptionsDropDown;

  @Override
  public void start(Stage stage) {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);
      scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

      // set title, size, and add scene to stage
      stage.setTitle("Sketch on Map Sample");
      stage.setWidth(1000);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // create a map with a basemap and add it to the map view
      ArcGISMap map = new ArcGISMap(Basemap.Type.IMAGERY, 64.3286, -15.5314, 13);
      mapView = new MapView();
      mapView.setMap(map);

      // create a graphics overlay for the graphics
      graphicsOverlay = new GraphicsOverlay();

      // add the graphics overlay to the map view
      mapView.getGraphicsOverlays().add(graphicsOverlay);

      // create a new sketch editor and add it to the map view
      sketchEditor = new SketchEditor();
      mapView.setSketchEditor(sketchEditor);

      // define symbols
      pointSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.SQUARE, 0xFFFF0000, 20);
      lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0xFFFF8800, 4);
      fillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.CROSS, 0x40FFA9A9, lineSymbol);
      editedLineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.DASH, 0x40000000, 4);
      editedFillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.CROSS, 0x40FFA9A9, editedLineSymbol);

      // create a combo box for graphic options
      ComboBox<SketchCreationMode> sketchOptionsDropDown = new ComboBox<>();
      sketchOptionsDropDown.getItems().setAll(SketchCreationMode.values());
      sketchOptionsDropDown.setMaxWidth(Double.MAX_VALUE);

      // create buttons for user interaction
      undoButton = new Button("Undo sketch");
      redoButton = new Button("Redo sketch");
      clearButton = new Button("Clear graphics overlay");
      saveButton = new Button("Save sketch to graphics overlay");
      editButton = new Button("Edit sketch");
      cancelButton = new Button("Cancel sketch");

      clearButton.setMaxWidth(Double.MAX_VALUE);
      saveButton.setMaxWidth(Double.MAX_VALUE);
      editButton.setMaxWidth(Double.MAX_VALUE);

      // disable clear, undo, redo and edit buttons when starting application
      resetButtons(true);
      sketchOptionsDropDown.setPromptText("    -- Select a graphic to sketch --");

      // save the sketch as a graphic, and store graphic to the graphics overlay
      saveButton.setOnAction(event -> {
        System.out.println(sketchEditor.isSketchValid());

        storeGraphicInGraphicOverlay();
        selectGraphic();
        graphicsOverlay.clearSelection();
        resetButtons(true);
        clearButton.setDisable(false);
        saveButton.setText("-- Select an available option --");

        // logic to force the drop down box to change to the next item after save button has been clicked.

        //sketchOptionsDropDown.getSelectionModel().selectNext();

//
//          if (sketchOptionsDropDown.getSelectionModel().getSelectedItem() == SketchCreationMode.FREEHAND_POLYGON) {
//            sketchOptionsDropDown.getSelectionModel().select(0);
//        }


      });

      // clear the graphics overlay, and disable the clear button
      clearButton.setOnAction(event -> {
        graphicsOverlay.getGraphics().clear();
        sketchEditor.stop();
        resetButtons(true);
      });

      // undo the last change made whilst sketching graphic
      undoButton.setOnAction(event -> {
        if (sketchEditor.canUndo()) {
          sketchEditor.undo();
        }
      });

      // redo the last change made whilst sketching graphic
      redoButton.setOnAction(event -> {
        if (sketchEditor.canRedo()) {
          sketchEditor.redo();
        }
      });

      // use sketch editor to edit the geometry of the selected graphic
      editButton.setOnAction(event -> {
        cancelButton.setDisable(false);
        saveButton.setDisable(true);
        saveButton.setText("Save edits to graphics overlay");

        // if the graphics overlay contains graphics, select the first graphic
        // and start the sketch editor based on that graphic's geometry
        if (!graphicsOverlay.getSelectedGraphics().isEmpty()) {
          graphic = graphicsOverlay.getSelectedGraphics().get(0);
          sketchEditor.start(graphic.getGeometry());
        }
      });

      cancelButton.setOnAction(event -> {

        sketchEditor.stop();
        selectGraphic();
        graphicsOverlay.clearSelection();
        cancelButton.setDisable(true);
        undoButton.setDisable(true);
        saveButton.setDisable(true);
        saveButton.setText("-- Select an available option --");
      });

      // when an item is selected from the drop down box

      sketchOptionsDropDown.getSelectionModel().selectedItemProperty().addListener(o -> {

        graphicsOverlay.clearSelection();
        saveButton.setText("Save sketch to graphics overlay");
        System.out.println(sketchEditor.isSketchValid());

        // create a graphics draw option based on source type
        resetButtons(true);
        if (!graphicsOverlay.getGraphics().isEmpty()){
          clearButton.setDisable(false);
        }

        SketchCreationMode sketchCreationMode = sketchOptionsDropDown.getSelectionModel().getSelectedItem();

        try {
          switch (sketchCreationMode) {
            case POLYLINE:
              createPolyline();
              break;
            case POLYGON:
              createPolygon();
              break;
            case POINT:
              createPoint();
              break;
            case MULTIPOINT:
              createMultiPoint();
              break;
            case FREEHAND_LINE:
              createFreeHandLine();
              break;
            case FREEHAND_POLYGON:
              createFreeHandPolygon();
              break;
          }

        } catch (Exception e) {
          new Alert(Alert.AlertType.ERROR, "Error finding draw style").show();
        }


      });


      // add a listener for when geometry is changed, to enable cancelling the button
      sketchEditor.addGeometryChangedListener(SketchGeometryChangedListener -> {

        // if the geometry being listened to is not empty, then enable the cancel button
        if (!SketchGeometryChangedListener.getGeometry().isEmpty()) {
          cancelButton.setDisable(false);
        }

        if (sketchEditor.isSketchValid()) {
          saveButton.setDisable(false);
        }

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


      // create a control panel
      VBox controlsVBox = new VBox(6);
      controlsVBox.setBackground(new Background(new BackgroundFill(Paint.valueOf("rgba(0, 0, 0, 0.3)"),
              CornerRadii.EMPTY, Insets.EMPTY)));
      controlsVBox.setPadding(new Insets(10));
      controlsVBox.setMaxSize(240, 110);
      controlsVBox.getStyleClass().add("panel-region");

      // create a flow pane for placing buttons side by side within the control box
      FlowPane flowPaneUndoRedo = new FlowPane(Orientation.HORIZONTAL, 55, 10, undoButton, redoButton);
      flowPaneUndoRedo.setAlignment(Pos.CENTER);
      FlowPane flowPaneEditCancel = new FlowPane(Orientation.HORIZONTAL, 55, 10, editButton, cancelButton);

      controlsVBox.getChildren().addAll(sketchOptionsDropDown, flowPaneUndoRedo, saveButton, flowPaneEditCancel, clearButton);

      // add the map view to the stack pane
      stackPane.getChildren().addAll(mapView, controlsVBox);
      stackPane.setAlignment(controlsVBox, Pos.TOP_RIGHT);
      stackPane.setMargin(controlsVBox, new Insets(10, 10, 0, 10));

    } catch (Exception e) {

      e.printStackTrace();
    }
  }

  private void createPolyline() {
    sketchEditor.start(SketchCreationMode.POLYLINE);
//    sketchEditor.addGeometryChangedListener(SketchGeometryChangedListener -> {
//      if (sketchEditor.isSketchValid()) {
//        saveButton.setDisable(false);
//      }
//    });
  }

  private void createPolygon() {
    sketchEditor.start(SketchCreationMode.POLYGON);
  }

  private void createPoint() {
    sketchEditor.start(SketchCreationMode.POINT);
  }

  private void createMultiPoint() {
    sketchEditor.start(SketchCreationMode.MULTIPOINT);
  }

  private void createFreeHandLine() {
    sketchEditor.start(SketchCreationMode.FREEHAND_LINE);
  }

  private void createFreeHandPolygon() {
    sketchEditor.start(SketchCreationMode.FREEHAND_POLYGON);
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

    // get the geometry from sketch editor
    Geometry sketchGeometry = sketchEditor.getGeometry();

    // if there is something in the sketch geometry
    if (sketchGeometry != null) {

      // and if there any number of graphics within the graphics overlay, get the first selected graphic, and set its geometry
      // to that of the sketch geometry

      if (graphicsOverlay.getSelectedGraphics().size() != 0) {
        graphic = graphicsOverlay.getSelectedGraphics().get(0);
        graphic.setGeometry(sketchGeometry);
      } else {

        // create a graphic from the sketch editor geometry
        graphic = new Graphic(sketchGeometry);

        // assign a symbol based on geometry type
        if (graphic.getGeometry().getGeometryType() == GeometryType.POLYGON) {
          graphic.setSymbol(fillSymbol);
        } else if (graphic.getGeometry().getGeometryType() == GeometryType.POLYLINE) {
          graphic.setSymbol(lineSymbol);
        } else if (graphic.getGeometry().getGeometryType() == GeometryType.POINT ||
                graphic.getGeometry().getGeometryType() == GeometryType.MULTIPOINT) {
          graphic.setSymbol(pointSymbol);
        }

        // add the graphic to the graphics overlay
        graphicsOverlay.getGraphics().add(graphic);

      }

    }

    sketchEditor.stop();
//    selectGraphic();

  }

  /**
   * Select a graphic from the graphics overlay
   */
  private void selectGraphic() {
    // click on a graphic on the map view

    mapView.setOnMouseClicked(e -> {

      graphicsOverlay.clearSelection();

      Point2D mapViewPoint = new Point2D(e.getX(), e.getY());

      ListenableFuture<IdentifyGraphicsOverlayResult> identifyGraphics;

      identifyGraphics = mapView.identifyGraphicsOverlayAsync(graphicsOverlay, mapViewPoint, 10, false);

      identifyGraphics.addDoneListener(() -> {

        try {

          Graphic selectedGraphic = null;

          if (!identifyGraphics.get().getGraphics().isEmpty()) {
            // store the selected graphic
            graphic = identifyGraphics.get().getGraphics().get(0);
            graphic.setSelected(true);
            editButton.setDisable(false);
          } else if (identifyGraphics.get().getGraphics().isEmpty()) {
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
   * Reset all UI buttons to a chosen boolean value
   */

  private void resetButtons(Boolean bool) {
    clearButton.setDisable(bool);
    redoButton.setDisable(bool);
    undoButton.setDisable(bool);
    editButton.setDisable(bool);
    saveButton.setDisable(bool);
    cancelButton.setDisable(bool);
  }

  /**
   * Stops and releases all resources used in application
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
