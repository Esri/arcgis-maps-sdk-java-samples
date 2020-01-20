package com.esri.samples.sketch_on_map;

import com.esri.arcgisruntime.geometry.GeometryType;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.util.ListenableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.Before;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class SketchOnMapTest extends ApplicationTest {

  MapView mapView;

  GraphicsOverlay graphicsOverlay;

  Button pointButton;

  Button multipointButton;

  Button polylineButton;

  Button polygonButton;

  Button freehandPolylineButton;

  Button freehandPolygonButton;

  Button editButton;

  Button stopButton;

  Button undoButton;

  Button redoButton;

  Button saveButton;

  Button clearButton;

  @Override
  public void start(Stage stage) throws Exception {
    // set up the scene
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/sketch_on_map.fxml"));
    Parent root = loader.load();
    Scene scene = new Scene(root);

    stage.setScene(scene);
    stage.setTitle("Sketch on Map Sample");
    stage.setWidth(1000);
    stage.setHeight(700);
    stage.show();

    // wait for initialization
    sleep(1000);
  }

  @Before
  public void setup() {

    // get a handle on the MapView and GraphicsOverlay
    Node mapViewNode = lookup("#mapView").query();
    if (mapViewNode instanceof MapView) {
      mapView = (MapView) mapViewNode;
      graphicsOverlay = mapView.getGraphicsOverlays().get(0);

    } else {
      fail("MapView Node could not be found");
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
   * Test Case 1: Draw a point
   */
  @Test
  public void test() {
    clickOn(pointButton);
    clickOn(mapView);
    clickOn(saveButton);

    ListenableList<Graphic> graphics = graphicsOverlay.getGraphics();
    assertEquals(1, graphics.size());
    assertEquals(GeometryType.POINT, graphics.get(0).getGeometry().getGeometryType());
  }
}
