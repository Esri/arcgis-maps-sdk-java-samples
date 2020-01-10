package com.esri.samples.find_place;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.DrawStatus;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.VerticalDirection;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.robot.Motion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class FindPlaceTest extends FxRobot {

  public static final int SLEEP_500MS = 500;
  public static final int SLEEP_1000MS = 1000;
  public static final int SLEEP_1500MS = 1500;

  MapView mapView;
  GraphicsOverlay graphicsOverlay;
  ComboBox<String> placeBox;
  ComboBox<String> locationBox;
  Node locationBoxArrowRegion;
  Node placeBoxArrowRegion;
  Button searchButton;

  @Before
  public void setup() throws Exception {
    // launch the sample
    ApplicationTest.launch(FindPlaceSample.class);

    // get a handle on the MapView and GraphicsOverlay
    Node mapViewNode = lookup("#mapView").query();
    if (mapViewNode instanceof MapView) {
      mapView = (MapView) mapViewNode;
      graphicsOverlay = mapView.getGraphicsOverlays().get(0);

    } else {
      fail("MapView Node could not be found");
    }

    // get a handle on the ComboBoxes
    placeBox = lookup("#placeBox").queryComboBox();
    locationBox = lookup("#locationBox").queryComboBox();

    // get a handle on the ComboBoxes' arrow buttons
    for (Node child : locationBox.getChildrenUnmodifiable()) {
      if (child.getStyleClass().contains("arrow-button")) {
        locationBoxArrowRegion = ((Pane) child).getChildren().get(0);
      }
    }

    for (Node child : placeBox.getChildrenUnmodifiable()) {
      if (child.getStyleClass().contains("arrow-button")) {
        placeBoxArrowRegion = ((Pane) child).getChildren().get(0);
      }
    }

    // get a handle on the search button
    searchButton = lookup("#searchButton").queryButton();

    // wait for initialization
    sleep(SLEEP_1000MS);
  }

  @After
  public void tearDown() throws Exception {
    FxToolkit.cleanupStages();
    // wait before the next test
    sleep(SLEEP_1000MS);
  }

  /**
   * Test Case 1: When using one of the pre-defined search inputs, the app returns the expected number of results
   */
  @Test
  public void defaultSearchInput() {

    clickOn(placeBoxArrowRegion);
    clickOn("Starbucks");

    clickOn(locationBoxArrowRegion);
    clickOn("Los Angeles, CA");

    clickOn(searchButton).sleep(SLEEP_1500MS);

    // assert that the expected number of graphics (location pins) is displayed
    assertEquals("Unexpected number of graphics (location pins) found", 50, graphicsOverlay.getGraphics().size());
  }

  /**
   * Test Case 2: When using a custom search query, the app returns the expected number of results
   */
  @Test
  public void customSearchInput() {

    clickOn(placeBox).write("esri").sleep(SLEEP_1500MS);

    Node esriEntry = lookup("Esri, 380 New York St, Redlands, CA, 92373, USA").query();
    if (esriEntry != null) {
      clickOn(esriEntry).sleep(SLEEP_1000MS);
    } else {
      fail("No search result found for custom query \"esri\" ");
    }

    clickOn(locationBox).write("Redlands").sleep(SLEEP_1500MS);

    Node redlandsEntry = lookup("Redlands, CA, USA").query();
    if (redlandsEntry != null) {
      clickOn(redlandsEntry).sleep(SLEEP_1000MS);
    } else {
      fail("No search result found for custom query \"Redlands\" ");
    }

    clickOn(searchButton).sleep(SLEEP_1500MS);

    // assert that the expected number of graphics (location pins) is displayed
    assertEquals("Unexpected number of graphics (location pins) found", 4, graphicsOverlay.getGraphics().size());
  }

  /**
   * Test Case 3: Find a callout node in the MapView and confirm the callout text
   */
  @Test
  public void confirmCalloutTest() {

    clickOn(placeBox).write("esri").sleep(SLEEP_1500MS);
    // get a handle on the search result and click on it
    Node esriEntry = lookup("Esri, 380 New York St, Redlands, CA, 92373, USA").query();
    if (esriEntry != null) {
      clickOn(esriEntry);
    } else {
      fail("No search result found for custom query \"esri\" ");
    }

    clickOn(locationBox).write("Redlands").sleep(SLEEP_1500MS);
    // get a handle on the search result, and click on it
    Node redlandsEntry = lookup("Redlands, CA, USA").query();
    if (redlandsEntry != null) {
      clickOn(redlandsEntry);
    } else {
      fail("No search result found for custom query \"Redlands\" ");
    }

    clickOn(searchButton).sleep(SLEEP_1500MS);

    // get the screen point of pin 1, move the mouse cursor to it
    Graphic pin = mapView.getGraphicsOverlays().get(0).getGraphics().get(1);
    Point2D screenPoint = mapView.locationToScreen((Point) pin.getGeometry());
    moveTo(mapView, Pos.TOP_LEFT, screenPoint, Motion.DIRECT);

    // scroll to zoom in so that more of the pins are visible
    scroll(12, VerticalDirection.UP).sleep(SLEEP_500MS);

    // move the mouse cursor to pin 1 again, and click it to show the callout
    moveTo(mapView, Pos.TOP_LEFT, screenPoint, Motion.DIRECT).clickOn(MouseButton.PRIMARY);

    // assert that the callout shows the correct information
    Callout callout = mapView.getCallout();
    assertNotNull(callout);
    assertTrue(callout.isVisible());

    Label titleLabel = lookup("380 New York St").query();
    assertNotNull(titleLabel);
    assertEquals(titleLabel.getText(), "380 New York St");

    Label detailLabel = lookup("Redlands, California, 92373").query();
    assertNotNull(detailLabel);
    assertEquals(detailLabel.getText(), "Redlands, California, 92373");

  }

  // test cases:
  // TODO: each result has a corresponding pin shown
  // TODO: clicking on map triggers identify and callout
  // TODO: panning map triggers prompt for new results
}