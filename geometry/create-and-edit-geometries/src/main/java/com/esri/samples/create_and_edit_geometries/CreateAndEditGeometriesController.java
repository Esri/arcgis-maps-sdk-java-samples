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

package com.esri.samples.create_and_edit_geometries;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryType;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.*;
import com.esri.arcgisruntime.mapping.view.geometryeditor.FreehandTool;
import com.esri.arcgisruntime.mapping.view.geometryeditor.GeometryEditor;
import com.esri.arcgisruntime.mapping.view.geometryeditor.GeometryEditorTool;
import com.esri.arcgisruntime.mapping.view.geometryeditor.VertexTool;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;

public class CreateAndEditGeometriesController {


  @FXML
  private MapView mapView;
  @FXML
  public Label editLabel;
  @FXML
  private Button redoButton;
  @FXML
  private Button undoButton;
  @FXML
  public Button deleteSelectedElementButton;
  @FXML
  private Button saveButton;
  @FXML
  private Button stopButton;
  @FXML
  public Button deleteAllGeometriesButton;
  @FXML
  private ComboBox<GeometryEditorTool> toolComboBox;

  private GeometryEditor geometryEditor;

  private VertexTool vertexTool;

  private FreehandTool freehandTool;
  private GraphicsOverlay graphicsOverlay;
  private Graphic graphic;
  private SimpleFillSymbol fillSymbol;
  private SimpleLineSymbol lineSymbol;
  private SimpleMarkerSymbol pointSymbol;

  public void initialize() {

    // authentication with an API key or named user is required to access basemaps and other location services
    String yourAPIKey = System.getProperty("apiKey");
    ArcGISRuntimeEnvironment.setApiKey(yourAPIKey);

    // create a map with the standard imagery basemap style
    ArcGISMap map = new ArcGISMap(BasemapStyle.ARCGIS_IMAGERY_STANDARD);

    // set the map to the map view
    mapView.setMap(map);

    // set a viewpoint on the map view
    mapView.setViewpoint(new Viewpoint(53.08230, -9.5920, 5000));

    // create a graphics overlay for the graphics
    graphicsOverlay = new GraphicsOverlay();

    // add the graphics overlay to the map view
    mapView.getGraphicsOverlays().add(graphicsOverlay);

    // create a new sketch editor and add it to the map view
    geometryEditor = new GeometryEditor();
    mapView.setGeometryEditor(geometryEditor);

    // create vertex and freehand tools for the geometry editor
    vertexTool = new VertexTool();
    freehandTool = new FreehandTool();

    // add geometry editor tools to the combo box
    toolComboBox.getItems().addAll(vertexTool, freehandTool);

    // show the name of the tools in the combo box
    toolComboBox.setConverter(new ComboBoxStringConverter());

    // bind the geometry editor tool to the tool chosen in the combo box
    toolComboBox.valueProperty().bindBidirectional(geometryEditor.toolProperty());

    // launch the app with the vertex tool selected by default
    toolComboBox.getSelectionModel().select(0);

    // red square for points
    pointSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.SQUARE, Color.RED, 20);
    // thin green line for polylines
    lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.GREENYELLOW, 4);
    // blue outline for polygons
    SimpleLineSymbol polygonLineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.STEELBLUE, 4);
    // cross-hatched interior for polygons
    fillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.CROSS, Color.rgb(255, 169, 169, 0.4), polygonLineSymbol);

    // bind button status to geometry editor properties

    undoButton.disableProperty().bind(geometryEditor.canUndoProperty().not());
    redoButton.disableProperty().bind(geometryEditor.canRedoProperty().not());
    saveButton.disableProperty().bind(geometryEditor.startedProperty().not());
    deleteSelectedElementButton.disableProperty().bind(geometryEditor.selectedElementProperty().isNull());
    stopButton.disableProperty().bind(geometryEditor.startedProperty().not());
  }

  /**
   * Stop the geometry editor without saving the geometry stored within.
   */
  @FXML
  private void handleStopButtonClicked() {
    Geometry newGeometry = geometryEditor.stop();
    Graphic newGraphic = new Graphic(newGeometry);
    graphicsOverlay.getGraphics().add(newGraphic);
    graphicsOverlay.clearSelection();
    selectGraphic();//todo what is this doing?
  }

  /**
   * Clear selection of any graphic in the graphics overlay and start a new point sketch.
   */
  @FXML
  private void handlePointButtonClicked() {
    graphicsOverlay.clearSelection();
    geometryEditor.setTool(vertexTool); // points can only be created with the vertex tool, so we need to activate it
    geometryEditor.start(GeometryType.POINT);
  }

  /**
   * Clear selection of any graphic in the graphics overlay and start a new multipoint sketch.
   */
  @FXML
  private void handleMultipointButtonClicked() {
    graphicsOverlay.clearSelection();
    geometryEditor.setTool(vertexTool); // multipoints can only be created with the vertex tool, so we need to activate it
    geometryEditor.start(GeometryType.MULTIPOINT);
  }

  /**
   * Clear selection of any graphic in the graphics overlay and start a new polyline sketch.
   */
  @FXML
  private void handlePolylineButtonClicked() {
    graphicsOverlay.clearSelection();
    geometryEditor.start(GeometryType.POLYLINE);
  }

  /**
   * Clear selection of any graphic in the graphics overlay and start a new polygon sketch.
   */
  @FXML
  private void handlePolygonButtonClicked() {
    graphicsOverlay.clearSelection();
    geometryEditor.start(GeometryType.POLYGON);
  }

  /**
   * Undo the last change made to the sketch, whilst sketching is active.
   */
  @FXML
  private void handleUndoButtonClicked() {
    if (geometryEditor.getCanUndo()) {
      geometryEditor.undo();
    }
  }

  /**
   * Redo the last change made to the sketch, whilst sketching is active.
   */
  @FXML
  private void handleRedoButtonClicked() {
    if (geometryEditor.getCanRedo()) {
      geometryEditor.redo();
    }
  }

  /**
   * Save the geometry in the editor to the graphics overlay, and set its symbol to the type relevant for the geometry.
   */
  @FXML
  private void handleSaveButtonClicked() {

    // stop the geometry editor and get the new geometry from it
    Geometry geometryFromEditor = geometryEditor.stop();

    // if an existing graphic is being edited: get the selected graphic, set its geometry to that of the editor geometry
    // if a new graphic: create a new graphic based on the editor geometry and set symbol depending on geometry type
    if (geometryFromEditor != null) {
      if (!graphicsOverlay.getSelectedGraphics().isEmpty()) {
        graphic = graphicsOverlay.getSelectedGraphics().get(0);
        graphic.setGeometry(geometryFromEditor);
      } else {
        graphic = new Graphic(geometryFromEditor);

        switch (geometryFromEditor.getGeometryType()) {
          case POLYGON -> graphic.setSymbol(fillSymbol);
          case POLYLINE -> graphic.setSymbol(lineSymbol);
          case POINT, MULTIPOINT -> graphic.setSymbol(pointSymbol);
        }
        graphicsOverlay.getGraphics().add(graphic);
      }
    }
    // allow the user to select a graphic from the map view
    selectGraphic();
    graphicsOverlay.clearSelection();

    if (!graphicsOverlay.getGraphics().isEmpty()) {
      deleteAllGeometriesButton.setDisable(false);
    }
  }

  /**
   * Removes all geometries from the graphics overlay.
   */
  @FXML
  private void handleDeleteAllGeometriesButtonClicked() {
    graphicsOverlay.getGraphics().clear();
  }

  @FXML
  private void handleDeleteSelectedElementButtonClicked() {
    geometryEditor.deleteSelectedElement();
  }

  /**
   * Allows the user to select a graphic from the graphics overlay.
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
            if (graphic.getGeometry() != null) {
              geometryEditor.start(graphic.getGeometry());
              graphic.setGeometry(null); // todo update the graphic w/ geometryEditor.stop, not whatever this is
            }
          }
        } catch (Exception x) {
          // on any error, display the stack trace
          x.printStackTrace();
        }
      });
    });
  }

  private class ComboBoxStringConverter extends StringConverter<GeometryEditorTool> {

    @Override
    public String toString(GeometryEditorTool geometryEditorTool) {
      if (geometryEditorTool != null) {
        if (geometryEditorTool instanceof VertexTool) return "Vertex Tool";
        else if (geometryEditorTool instanceof FreehandTool) return "Freehand Tool";
      }
      return "";
    }

    @Override
    public GeometryEditorTool fromString(String string) {
      return null;
    }
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
