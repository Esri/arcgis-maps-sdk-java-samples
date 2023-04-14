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
import com.esri.arcgisruntime.geometry.GeometryType;
import com.esri.arcgisruntime.geometry.MultipointBuilder;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointBuilder;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.PolylineBuilder;
import com.esri.arcgisruntime.geometry.PolygonBuilder;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.IdentifyGraphicsOverlayResult;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.geometryeditor.FreehandTool;
import com.esri.arcgisruntime.mapping.view.geometryeditor.GeometryEditor;
import com.esri.arcgisruntime.mapping.view.geometryeditor.GeometryEditorTool;
import com.esri.arcgisruntime.mapping.view.geometryeditor.VertexTool;
import com.esri.arcgisruntime.mapping.view.geometryeditor.GeometryEditorElement;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;

import java.util.Arrays;

public class CreateAndEditGeometriesController {


  @FXML
  private MapView mapView;
  @FXML
  private Button pointButton;
  @FXML
  private Button multipointButton;
  @FXML
  private Button redoButton;
  @FXML
  private Button undoButton;
  @FXML
  public Button deleteSelectedElementButton;
  @FXML
  private Button stopAndSaveButton;
  @FXML
  private Button stopAndDiscardButton;
  @FXML
  public Button deleteAllGeometriesButton;
  @FXML
  private ComboBox<GeometryEditorTool> toolComboBox;

  private GeometryEditor geometryEditor;
  private VertexTool vertexTool;
  private FreehandTool freehandTool;

  private GraphicsOverlay graphicsOverlay;
  private Graphic selectedGraphic;

  private SimpleFillSymbol fillSymbol;
  private SimpleLineSymbol lineSymbol;
  private SimpleMarkerSymbol pointSymbol;
  private SimpleMarkerSymbol multipointSymbol;

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

    // create vertex and freehand tools for the geometry editor and add to combo box - vertex tool selected by default
    vertexTool = new VertexTool();
    freehandTool = new FreehandTool();
    toolComboBox.setConverter(new ComboBoxStringConverter());
    toolComboBox.getItems().addAll(vertexTool, freehandTool);
    toolComboBox.getSelectionModel().select(0);

    // bind the geometry editor tool to the tool chosen in the combo box
    toolComboBox.valueProperty().bindBidirectional(geometryEditor.toolProperty());

    createGraphicsMarkers();
    createInitialGraphics();
    bindButtonsToGeometryEditor();
    selectGraphic(); // allow user to immediately select and edit graphics
  }

  /**
   * Bind button status to geometry editor properties.
   */
  private void bindButtonsToGeometryEditor() {
    // Point and multipoint disabled when freehand tool selected
    pointButton.disableProperty().bind(geometryEditor.toolProperty().isEqualTo(freehandTool));
    multipointButton.disableProperty().bind(geometryEditor.toolProperty().isEqualTo(freehandTool));

    // Undo/Redo enabled when the geometry editor can undo/redo
    undoButton.disableProperty().bind(geometryEditor.canUndoProperty().not());
    redoButton.disableProperty().bind(geometryEditor.canRedoProperty().not());

    // Delete selected element enabled when an element is selected, and it's deletable
    // Using a listener here avoids NullPointerException when trying to init binding
    ChangeListener<GeometryEditorElement> selectedElementChangedListener = (observable, oldValue, newValue) -> {
      if (newValue == null){
        deleteSelectedElementButton.disableProperty().bind(geometryEditor.selectedElementProperty().isNull());
      } else {
        deleteSelectedElementButton.disableProperty().bind(geometryEditor.getSelectedElement().canDeleteProperty().not());
      }
    };
    geometryEditor.selectedElementProperty().addListener(selectedElementChangedListener);
    // Old and less accurate working binding
    // deleteSelectedElementButton.disableProperty().bind(geometryEditor.selectedElementProperty().isNull());

    // Stop buttons enabled when geometry editor has started
    stopAndSaveButton.disableProperty().bind(geometryEditor.startedProperty().not());
    stopAndDiscardButton.disableProperty().bind(geometryEditor.startedProperty().not());

  }

  private void createGraphicsMarkers() {
    // orange-red square for points
    pointSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.SQUARE, Color.ORANGERED, 10);
    //yellow circle for multipoints
    multipointSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.YELLOW, 5);
    // thin blue line (problematic) for polylines
    lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLUE, 2);
    // black outline for polygons
    SimpleLineSymbol polygonLineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.DASH, Color.BLACK, 1);
    // translucent red interior for polygons
    fillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, Color.rgb(255,0,0,0.3), polygonLineSymbol);
  }

  /**
   * Clear selection of any graphic in the graphics overlay and start a new point sketch.
   */
  @FXML
  private void handlePointButtonClicked() {
    graphicsOverlay.clearSelection();
    geometryEditor.start(GeometryType.POINT);
  }

  /**
   * Clear selection of any graphic in the graphics overlay and start a new multipoint sketch.
   */
  @FXML
  private void handleMultipointButtonClicked() {
    graphicsOverlay.clearSelection();
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
   * Undo the last change made to the geometry while editing is active.
   */
  @FXML
  private void handleUndoButtonClicked() {
    if (geometryEditor.getCanUndo()) {
      geometryEditor.undo();
    }
  }

  /**
   * Redo the last change made to the geometry while editing is active.
   */
  @FXML
  private void handleRedoButtonClicked() {
    if (geometryEditor.getCanRedo()) {
      geometryEditor.redo();
    }
  }

  /**
   * Delete the currently selected element of the geometry.
   */
  @FXML
  private void handleDeleteSelectedElementButtonClicked() {
    if (geometryEditor.getSelectedElement().getCanDelete()) {
      geometryEditor.deleteSelectedElement();
    }
  }

  /**
   * Save the geometry in the editor to the graphics overlay, and set its symbol to the type relevant for the geometry.
   */
  @FXML
  private void handleStopAndSaveButtonClicked() {
    if (geometryEditor.getGeometry().isEmpty()) {
      System.err.println("Geometry is empty. Unable to save geometry.");
    }

    if (selectedGraphic != null){
      updateSelectedGraphic();
    } else {
      createNewGraphic();
    }
  }

  private void createNewGraphic() {
    var geometry = geometryEditor.stop();
    var graphic = new Graphic();

    switch (geometry.getGeometryType()){
      case POINT:
        graphic.setSymbol(pointSymbol);
        break;
      case MULTIPOINT:
        graphic.setSymbol(multipointSymbol);
        break;
      case POLYLINE:
        graphic.setSymbol(lineSymbol);
        break;
      case POLYGON:
        graphic.setSymbol(fillSymbol);
        break;
      default:
      {}
    }

    graphic.setGeometry(geometry);
    graphicsOverlay.getGraphics().add(graphic);
  }

  private void updateSelectedGraphic() {
    selectedGraphic.setGeometry(geometryEditor.stop());
    selectedGraphic.setVisible(true);
    selectedGraphic = null;
  }

  /**
   * Stop the geometry editor without saving the geometry stored within.
   */
  @FXML
  private void handleStopAndDiscardButtonClicked() {
    if (selectedGraphic != null) {
      geometryEditor.stop();
      selectedGraphic.setVisible(true);
      selectedGraphic = null;
    } else {
      geometryEditor.stop();
    }
  }

  /**
   * Removes all geometries from the graphics overlay.
   */
  @FXML
  private void handleDeleteAllGeometriesButtonClicked() {
    graphicsOverlay.getGraphics().clear();
  }

  /**
   * Allows the user to select a graphic from the graphics overlay.
   */
  private void selectGraphic() {
    // todo needs to not delete any currently selected graphic upon selecting a new one

    mapView.setOnMouseClicked(e -> {
      graphicsOverlay.clearSelection();
      if (e.isStillSincePress() && e.getButton() == MouseButton.PRIMARY){
      Point2D mapViewPoint = new Point2D(e.getX(), e.getY());

      // get graphics near the clicked location
      ListenableFuture<IdentifyGraphicsOverlayResult> identifyGraphics;
      identifyGraphics = mapView.identifyGraphicsOverlayAsync(graphicsOverlay, mapViewPoint, 10, false);

      identifyGraphics.addDoneListener(() -> {
        try {
          if (!identifyGraphics.get().getGraphics().isEmpty()) { // todo can we keep this?

            // store the selected graphic
            selectedGraphic = identifyGraphics.get().getGraphics().get(0);
            selectedGraphic.setSelected(true);

            // Need to use vertex tool if selected geometry is a point or multipoint
            if (selectedGraphic.getGeometry().getGeometryType().equals(GeometryType.POINT) ||
                    selectedGraphic.getGeometry().getGeometryType().equals(GeometryType.MULTIPOINT)) {
              geometryEditor.setTool(vertexTool);
              toolComboBox.getSelectionModel().select(0);
            }

            geometryEditor.start(selectedGraphic.getGeometry());
            selectedGraphic.setGeometry(null); // todo this can't be right
            //graphic.setVisible(false);
          } else {
            selectedGraphic = null;
          }
        } catch (Exception x) {
          // on any error, display the stack trace
          x.printStackTrace();
        }
      });
    }});
  }

  void createInitialGraphics() {
    double[] houseCoords = {-9.59309629, 53.0830063};
    double[][] outbuildingCoords = {
                {-9.59386587, 53.08289651},
                {-9.59370896, 53.08234917},
                {-9.59330546, 53.082564},
                {-9.59341755, 53.08286662},
                {-9.59326997, 53.08304595},
                {-9.59246485, 53.08294507},
                {-9.59250034, 53.08286101},
                {-9.59241815, 53.08284607},
                {-9.59286835, 53.08311506},
                {-9.59307943, 53.08234731}
            };
    
    double[][] road1Coords = {
                {-9.59486423, 53.08169453},
                {-9.5947812, 53.08175431},
                {-9.59475464, 53.08189379},
                {-9.59494393, 53.08213622},
                {-9.59464173, 53.08240521},
                {-9.59413694, 53.08260115},
                {-9.59357903, 53.0829266},
                {-9.59335984, 53.08311589},
                {-9.59318051, 53.08316903},
                {-9.59301779, 53.08322216},
                {-9.59264252, 53.08370038},
                {-9.59250636, 53.08383986}
            };

    double[][] road2Coords = {
                {-9.59400079, 53.08136244},
                {-9.59395761, 53.08149528},
                {-9.59368862, 53.0817045},
                {-9.59358235, 53.08219267},
                {-9.59331667, 53.08290335},
                {-9.59314398, 53.08314246},
                {-9.5930676, 53.08330519},
                {-9.59303439, 53.08351109},
                {-9.59301447, 53.08363728},
                {-9.59293809, 53.08387307}
            };
    
    double[][] boundaryCoords = {
                {-9.59350122, 53.08320723},
                {-9.59345177, 53.08333534},
                {-9.59309789, 53.08327198},
                {-9.59300344, 53.08317992},
                {-9.59221827, 53.08304034},
                {-9.59220706, 53.08287782},
                {-9.59229486, 53.08280871},
                {-9.59236398, 53.08268915},
                {-9.59255263, 53.08256769},
                {-9.59265165, 53.08237906},
                {-9.59287552, 53.08241478},
                {-9.59292812, 53.0823012},
                {-9.5932294, 53.08235022},
                {-9.59342188, 53.08260009},
                {-9.59354382, 53.08238728},
                {-9.59365852, 53.08203535},
                {-9.59408443, 53.08210446},
                {-9.59448232, 53.08224456},
                {-9.5943609, 53.08243697},
                {-9.59458319, 53.08245939},
                {-9.59439639, 53.08264619},
                {-9.59433288, 53.0827975},
                {-9.59404707, 53.08323649},
                {-9.59350122, 53.08320723}
            };

    createPolygonAndAddToGraphics(boundaryCoords);
    createLineAndAddToGraphics(road1Coords);
    createLineAndAddToGraphics(road2Coords);
    createMultipointAndAddToGraphics(outbuildingCoords);
    createPointAndAddToGraphics(houseCoords);
  }

  private void createPointAndAddToGraphics(double[] houseCoords) {
    var pointBuilder = new PointBuilder(houseCoords[0], houseCoords[1], SpatialReferences.getWgs84());
    var pointGeometry = pointBuilder.toGeometry();
    var pointGraphic = new Graphic(pointGeometry);
    pointGraphic.setSymbol(pointSymbol);
    graphicsOverlay.getGraphics().add(pointGraphic);
  }

  private void createMultipointAndAddToGraphics(double[][] outbuildingCoords) {
    Point[] points = new Point[outbuildingCoords.length];
    for (int i = 0; i < points.length; i++) {
      points[i] = new Point(outbuildingCoords[i][0], outbuildingCoords[i][1]);
    }
    var multipointBuilder = new MultipointBuilder(Arrays.asList(points), SpatialReferences.getWgs84());
    var multipointGeometry = multipointBuilder.toGeometry();
    var multipointGraphic = new Graphic(multipointGeometry);
    multipointGraphic.setSymbol(multipointSymbol);
    graphicsOverlay.getGraphics().add(multipointGraphic);
  }

  private void createLineAndAddToGraphics(double[][] roadCoords) {
    Point[] points = new Point[roadCoords.length];
    for (int i = 0; i < points.length; i++) {
      points[i] = new Point(roadCoords[i][0], roadCoords[i][1]);
    }
    var polylineBuilder = new PolylineBuilder(new PointCollection(Arrays.asList(points)), SpatialReferences.getWgs84());
    var polylineGeometry = polylineBuilder.toGeometry();
    var polylineGraphic = new Graphic(polylineGeometry);
    polylineGraphic.setSymbol(lineSymbol);
    graphicsOverlay.getGraphics().add(polylineGraphic);
  }

  private void createPolygonAndAddToGraphics(double[][] boundaryCoords) {
    Point[] points = new Point[boundaryCoords.length];
    for (int i = 0; i < points.length; i++) {
      points[i] = new Point(boundaryCoords[i][0], boundaryCoords[i][1]);
    }
    var polygonBuilder = new PolygonBuilder(new PointCollection(Arrays.asList(points)), SpatialReferences.getWgs84());
    var polygonGeometry = polygonBuilder.toGeometry();
    var polygonGraphic = new Graphic(polygonGeometry);
    polygonGraphic.setSymbol(fillSymbol);
    graphicsOverlay.getGraphics().add(polygonGraphic);
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
