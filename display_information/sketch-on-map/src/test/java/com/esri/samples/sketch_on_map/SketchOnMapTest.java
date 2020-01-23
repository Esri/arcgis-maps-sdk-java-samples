package com.esri.samples.sketch_on_map;

import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryType;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.util.ListenableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.Before;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.robot.Motion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class SketchOnMapTest extends ApplicationTest {

  private MapView mapView;
  private GraphicsOverlay graphicsOverlay;

  private Button editButton;
  private Button stopButton;

  private Button pointButton;
  private Button multipointButton;
  private Button polylineButton;
  private Button polygonButton;
  private Button freehandPolylineButton;
  private Button freehandPolygonButton;

  private Button undoButton;
  private Button redoButton;
  private Button saveButton;
  private Button clearButton;

  private float mapViewHeight = 700;
  private float mapViewWidth = 1000;

  @Override
  public void start(Stage stage) throws Exception {
    // set up the scene
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/sketch_on_map.fxml"));
    Parent root = loader.load();
    Scene scene = new Scene(root);

    stage.setScene(scene);
    stage.setTitle("Sketch on Map Sample");
    stage.setWidth(mapViewWidth);
    stage.setHeight(mapViewHeight);
    stage.show();

    // wait for initialization
    sleep(1500);
  }

  @Before
  public void setup() {

    // get a handle on the MapView and GraphicsOverlay
    Node mapViewNode = lookup("#mapView").query();
    if (mapViewNode instanceof MapView) {
      mapView = (MapView) mapViewNode;
      graphicsOverlay = mapView.getGraphicsOverlays().get(0);
    }

    // find the middle button group and get each button
    VBox vbox = lookup(".panel-region").queryAs(VBox.class);
    GridPane middleButtonGroup = (GridPane) vbox.getChildren().get(2);
    pointButton = (Button) middleButtonGroup.getChildren().get(0);
    multipointButton = (Button) middleButtonGroup.getChildren().get(1);
    polylineButton = (Button) middleButtonGroup.getChildren().get(2);
    polygonButton = (Button) middleButtonGroup.getChildren().get(3);
    freehandPolylineButton = (Button) middleButtonGroup.getChildren().get(4);
    freehandPolygonButton = (Button) middleButtonGroup.getChildren().get(5);

    editButton = lookup("#editButton").queryButton();
    stopButton = lookup("#stopButton").queryButton();
    undoButton = lookup("#undoButton").queryButton();
    redoButton = lookup("#redoButton").queryButton();
    saveButton = lookup("#saveButton").queryButton();
    clearButton = lookup("#clearButton").queryButton();
  }

  /**
   * Test Case 1: Use the SketchEditor to draw a point on the map, and verify that it is added to the GraphicsOverlay.
   */
  @Test
  public void drawPoint() {
    // define a screen point in the centre of the map view (relative to the top left corner)
    Point2D screenPoint = new Point2D(Math.round(mapViewWidth / 2), Math.round(mapViewHeight / 2));

    clickOn(pointButton)
        .moveTo(mapView, Pos.TOP_LEFT, screenPoint, Motion.DIRECT)
        .clickOn(MouseButton.PRIMARY)
        .clickOn(saveButton);

    ListenableList<Graphic> graphics = graphicsOverlay.getGraphics();
    assertEquals(1, graphics.size());

    Geometry pointGeometry = graphics.get(0).getGeometry();
    assertEquals(GeometryType.POINT, pointGeometry.getGeometryType());

    // check that the Geometry was created at the screen point clicked
    assertEquals(screenPoint, mapView.locationToScreen((Point) pointGeometry));
  }

  /**
   * Test Case 2: Use the SketchEditor to draw a multipoint graphic on the map, and verify that it is added to the GraphicsOverlay.
   */
  @Test
  public void drawMultipoint() {
    clickOn(multipointButton);
    clickOnFourPoints();
    clickOn(saveButton);

    ListenableList<Graphic> graphics = graphicsOverlay.getGraphics();
    assertEquals(1, graphics.size());
    assertEquals(GeometryType.MULTIPOINT, graphics.get(0).getGeometry().getGeometryType());
  }

  /**
   * Test Case 3: Use the SketchEditor to draw a polyline on the map, and verify that it is added to the GraphicsOverlay.
   */
  @Test
  public void drawPolyline() {
    clickOn(polylineButton);
    clickOnFourPoints();
    clickOn(saveButton);

    ListenableList<Graphic> graphics = graphicsOverlay.getGraphics();
    assertEquals(1, graphics.size());
    assertEquals(GeometryType.POLYLINE, graphics.get(0).getGeometry().getGeometryType());
  }

  /**
   * Test Case 4: Use the SketchEditor to draw a polygon on the map, and verify that it is added to the GraphicsOverlay.
   */
  @Test
  public void drawPolygon() {
    clickOn(polygonButton);
    clickOnFourPoints();
    clickOn(saveButton);

    ListenableList<Graphic> graphics = graphicsOverlay.getGraphics();
    assertEquals(1, graphics.size());
    assertEquals(GeometryType.POLYGON, graphics.get(0).getGeometry().getGeometryType());
  }

  /**
   * Test Case 5: Use the SketchEditor to draw a freehand polyline on the map, and verify that it is added to the GraphicsOverlay.
   */
  @Test
  public void drawFreehandPolyline() {
    clickOn(freehandPolylineButton);
    clickAndDragFourPoints();
    clickOn(saveButton);

    ListenableList<Graphic> graphics = graphicsOverlay.getGraphics();
    assertEquals(1, graphics.size());
    assertEquals(GeometryType.POLYLINE, graphics.get(0).getGeometry().getGeometryType());
  }

  /**
   * Test Case 5: Use the SketchEditor to draw a freehand polygon on the map, and verify that it is added to the GraphicsOverlay.
   */
  @Test
  public void drawFreehandPolygon() {
    clickOn(freehandPolygonButton);
    clickAndDragFourPoints();
    clickOn(saveButton);

    ListenableList<Graphic> graphics = graphicsOverlay.getGraphics();
    assertEquals(1, graphics.size());
    assertEquals(GeometryType.POLYGON, graphics.get(0).getGeometry().getGeometryType());
  }

  /**
   * Clicks on four different points in the map view in succession, starting from the center.
   */
  private void clickOnFourPoints() {
    clickOn(mapView)
        .moveBy(-100, 0)
        .clickOn(MouseButton.PRIMARY)
        .moveBy(0, 100)
        .clickOn(MouseButton.PRIMARY)
        .moveBy(100, 0)
        .clickOn(MouseButton.PRIMARY);
  }

  /**
   * Clicks and drags the mouse to four different points, starting from the center of the map view.
   */
  private void clickAndDragFourPoints() {
    drag(mapView)
        .moveBy(-150, 0)
        .moveBy(0, 150)
        .moveBy(150, 0)
        .drop();
  }

  /**
   * Test Case 6: Create a point and edit it.
   */
  @Test
  public void editPoint() {
    // create a point sketch
    clickOn(pointButton)
        .clickOn(mapView)
        .clickOn(saveButton);

    // click on the point and edit it
    Graphic point = graphicsOverlay.getGraphics().get(0);
    Point2D originalLocation = mapView.locationToScreen((Point) point.getGeometry());
    moveTo(mapView, Pos.TOP_LEFT, originalLocation, Motion.DIRECT)
        .clickOn(MouseButton.PRIMARY)

        // start editing
        .clickOn(editButton)

        // move to the vertex, select it, and click somewhere else to have the SketchEditor move it
        .moveTo(mapView, Pos.TOP_LEFT, originalLocation, Motion.DIRECT)
        .moveBy(100, -100)
        .clickOn(MouseButton.PRIMARY)
        .clickOn(saveButton);

    Point2D newLocation = mapView.locationToScreen((Point) point.getGeometry());
    assertNotEquals(originalLocation, newLocation);
  }

  /**
   * Test Case 7: Create a polyline and edit it by moving a vertex
   */
  @Test
  public void editPolylineMoveVertex() {
    // create a polyline sketch
    clickOn(polylineButton);
    clickOnFourPoints();
    clickOn(saveButton);

    // move to the bottom right corner of the polygon and select it
    Graphic graphic = graphicsOverlay.getGraphics().get(0);
    Geometry oldGeometry = graphic.getGeometry();
    double xMin = graphic.getGeometry().getExtent().getXMin();
    double yMin = graphic.getGeometry().getExtent().getYMin();
    Point2D originaBottomLeftCorner = mapView.locationToScreen(new Point(xMin, yMin, mapView.getSpatialReference()));
    moveTo(mapView, Pos.TOP_LEFT, originaBottomLeftCorner, Motion.DIRECT)
        .clickOn(MouseButton.PRIMARY)

        // start editing
        .clickOn(editButton)

        // move to the bottom left vertex, select it, and click somewhere else to have the SketchEditor move it
        .moveTo(mapView, Pos.TOP_LEFT, originaBottomLeftCorner, Motion.DIRECT)
        .drag(MouseButton.PRIMARY)
        .moveBy(-100, 100)
        .drop()
        .clickOn(saveButton);

    Geometry newGeometry = graphic.getGeometry();
    assertNotEquals(newGeometry, oldGeometry);
  }

  /**
   * Test Case 8: Create a polyline and edit it by removing a vertex
   */
  @Test
  public void editPolylineRemoveVertex() {
    // create a polyline sketch
    clickOn(polylineButton);
    clickOnFourPoints();
    clickOn(saveButton);

    // move to the bottom right corner of the polygon and select it
    Graphic graphic = graphicsOverlay.getGraphics().get(0);
    Geometry oldGeometry = graphic.getGeometry();
    double xMin = graphic.getGeometry().getExtent().getXMin();
    double yMin = graphic.getGeometry().getExtent().getYMin();
    Point2D originaBottomLeftCorner = mapView.locationToScreen(new Point(xMin, yMin, mapView.getSpatialReference()));
    moveTo(mapView, Pos.TOP_LEFT, originaBottomLeftCorner, Motion.DIRECT)
        .clickOn(MouseButton.PRIMARY)

        // start editing
        .clickOn(editButton)

        // move to the bottom left vertex, select it, and remove it
        .moveTo(mapView, Pos.TOP_LEFT, originaBottomLeftCorner, Motion.DIRECT)
        .clickOn(MouseButton.SECONDARY)
        .moveBy(10, 10)
        .clickOn(MouseButton.PRIMARY)
        .clickOn(saveButton);

    Geometry newGeometry = graphic.getGeometry();
    assertNotEquals(newGeometry, oldGeometry);
  }

  /**
   * Test Case 9: Create a polyline and edit it by adding a vertex between two existing vertices
   */
  @Test
  public void editPolylineAddVertex() {
    // create a polyline sketch
    clickOn(polylineButton);
    clickOnFourPoints();
    clickOn(saveButton);

    // move to the bottom right corner of the polygon and select it
    Graphic graphic = graphicsOverlay.getGraphics().get(0);
    Geometry oldGeometry = graphic.getGeometry();
    double xMin = graphic.getGeometry().getExtent().getXMin();
    double yMin = graphic.getGeometry().getExtent().getYMin();
    Point2D originalBottomLeftCorner = mapView.locationToScreen(new Point(xMin, yMin, mapView.getSpatialReference()));
    moveTo(mapView, Pos.TOP_LEFT, originalBottomLeftCorner, Motion.DIRECT)
        .clickOn(MouseButton.PRIMARY)

        // start editing
        .clickOn(editButton)

        // move to the point between the two leftmost vertices, and drag the line to add a vertex
        .moveTo(mapView, Pos.TOP_LEFT, originalBottomLeftCorner, Motion.DIRECT)
        .clickOn(MouseButton.SECONDARY)
        .moveBy(0, -50)
        .drag(MouseButton.PRIMARY).moveBy(-50, 0).drop()
        .clickOn(saveButton);

    Geometry newGeometry = graphic.getGeometry();
    assertNotEquals(newGeometry, oldGeometry);
  }

  /**
   * Test Case 10: Delete created features
   */
  @Test
  public void deleteSketches() {
    clickOn(pointButton)
        .clickOn(mapView)
        .clickOn(saveButton);

    ListenableList<Graphic> graphics = graphicsOverlay.getGraphics();
    assertEquals(1, graphics.size());
    assertEquals(GeometryType.POINT, graphics.get(0).getGeometry().getGeometryType());

    clickOn(clearButton);
    assertEquals(0, graphicsOverlay.getGraphics().size());
  }

  //TODO:
  // * add vertex to polyline/polygon while creating

}
