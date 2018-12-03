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
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

public class SketchOnMapSample_Old extends Application {

  private MapView mapView;
  private SketchEditor sketchEditor;
  private Button redoButton;
  private Button undoButton;
  private Button clearButton;
  private Button saveButton;
  private Button editButton;
  private Button stopSketchButton;


  private Button createPointButton;
  private Button createMultiPointButton;
  private Button createPolylineButton;
  private Button createPolygonButton;
  private Button createFreehandPolylineButton;
  private Button createFreehandPolygonButton;


  private GraphicsOverlay graphicsOverlay;
  private SimpleFillSymbol fillSymbol;
  private SimpleLineSymbol lineSymbol;
  private SimpleLineSymbol polygonLineSymbol;
  private SimpleMarkerSymbol pointSymbol;
  private Graphic graphic;

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

      // define symbols for graphics
      // red square for points
      pointSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.SQUARE, 0xFFFF0000, 20);
      // thin green line for polylines
      lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0xFF64c113, 4);
      // blue outline for polygons
      polygonLineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0xFF1396c1, 4);
      // cross-hatched interior for polygons
      fillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.CROSS, 0x40FFA9A9, polygonLineSymbol);

      // create buttons for user interaction
      undoButton = new Button("Undo sketch");
      redoButton = new Button("Redo sketch");
      clearButton = new Button("Clear saved sketches");
      saveButton = new Button("Save sketch and stop sketching");
      editButton = new Button("Edit sketch");
      stopSketchButton = new Button("Sketch is disabled");
      buttonsSetMaxWidth();

      // create buttons for creating sketches
      createPointButton = new Button("");
      createMultiPointButton = new Button("");
      createPolylineButton = new Button("");
      createPolygonButton = new Button("");
      createFreehandPolylineButton = new Button("");
      createFreehandPolygonButton = new Button("");

      // create tooltips for sketch buttons
      createPointButton.setTooltip(new Tooltip("Sketch single point"));
      createMultiPointButton.setTooltip(new Tooltip("Sketch multipoints"));
      createPolylineButton.setTooltip(new Tooltip("Sketch polyline"));
      createPolygonButton.setTooltip(new Tooltip("Sketch Polygon"));
      createFreehandPolylineButton.setTooltip(new Tooltip("Sketch freehand polyline"));
      createFreehandPolygonButton.setTooltip(new Tooltip("Sketch freehand polygon"));

      // set icon for each sketch button
      Image pointIcon = new Image(getClass().getResourceAsStream("/icons/pointSketch.png"));
      createPointButton.setGraphic(new ImageView(pointIcon));

      Image multiPointIcon = new Image(getClass().getResourceAsStream("/icons/multiPointSketch.png"));
      createMultiPointButton.setGraphic(new ImageView(multiPointIcon));

      Image polylineIcon = new Image(getClass().getResourceAsStream("/icons/polylineSketch.png"));
      createPolylineButton.setGraphic(new ImageView(polylineIcon));

      Image polygonIcon = new Image(getClass().getResourceAsStream("/icons/polygonSketch.png"));
      createPolygonButton.setGraphic(new ImageView(polygonIcon));

      Image freehandLineIcon = new Image(getClass().getResourceAsStream("/icons/freehandPolylineSketch.png"));
      createFreehandPolylineButton.setGraphic(new ImageView(freehandLineIcon));

      Image freehandPolygonIcon = new Image(getClass().getResourceAsStream("/icons/freehandPolygonSketch.png"));
      createFreehandPolygonButton.setGraphic(new ImageView(freehandPolygonIcon));

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

      // disable all buttons except sketch buttons when starting application
      disableButtons();

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

      // save the sketch as a graphic, and store graphic to the graphics overlay
      saveButton.setOnAction(event -> {

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

      });

      // stop the sketch editor
      stopSketchButton.setOnAction(event -> {
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

      });

      // use sketch editor to edit the geometry of the selected graphic
      editButton.setOnAction(event -> {
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

      // TODO this is in FXML
      // create a control panel
      VBox controlsVBox = new VBox(6);
      controlsVBox.setBackground(new Background(new BackgroundFill(Paint.valueOf("rgba(0, 0, 0, 0.3)"), CornerRadii.EMPTY, Insets.EMPTY)));
      controlsVBox.setPadding(new Insets(10));
      controlsVBox.setMaxSize(255, 110);
      controlsVBox.getStyleClass().add("panel-region");

      // create a HBox
      HBox sketchHBox = new HBox(6);
      sketchHBox.setMaxSize(200, 50);
      sketchHBox.getChildren().addAll(createPointButton, createMultiPointButton, createPolylineButton, createPolygonButton, createFreehandPolylineButton, createFreehandPolygonButton);

      // create a flow pane for placing buttons side by side within the control box
      HBox undoRedoHBox = new HBox(6);
      undoRedoHBox.getChildren().addAll(undoButton, redoButton);
      undoRedoHBox.setAlignment(Pos.CENTER);

      controlsVBox.getChildren().addAll(sketchHBox, stopSketchButton, undoRedoHBox, saveButton, editButton, clearButton);

      // add the map view to the stack pane
      stackPane.getChildren().addAll(mapView, controlsVBox);
      stackPane.setAlignment(controlsVBox, Pos.TOP_RIGHT);
      stackPane.setMargin(controlsVBox, new Insets(10, 10, 0, 10));

    } catch (Exception e) {
      // on any error, display the stack trace
      e.printStackTrace();
    }
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
   * Set the clear, save, edit and stop sketch buttons to max width
   */
  private void buttonsSetMaxWidth() {
    clearButton.setMaxWidth(Double.MAX_VALUE);
    saveButton.setMaxWidth(Double.MAX_VALUE);
    editButton.setMaxWidth(Double.MAX_VALUE);
    stopSketchButton.setMaxWidth(Double.MAX_VALUE);
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