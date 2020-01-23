package com.esri.samples.find_place;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.mapping.view.Callout;
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class FindPlaceTest extends FxRobot {

  public static final int SLEEP_500MS = 500;
  public static final int SLEEP_1000MS = 1000;
  public static final int SLEEP_1500MS = 1500;

  Button searchButton;
  ComboBox<String> locationBox;
  ComboBox<String> placeBox;
  GraphicsOverlay graphicsOverlay;
  MapView mapView;
  Node locationBoxArrowRegion;
  Node placeBoxArrowRegion;

  @Before
  public void setup() throws Exception {
    // launch the sample
    ApplicationTest.launch(FindPlaceSample.class);

    // get a handle on the MapView and GraphicsOverlay
    Node mapViewNode = lookup("#mapView").query();
    if (mapViewNode instanceof MapView) {
      mapView = (MapView) mapViewNode;
      graphicsOverlay = mapView.getGraphicsOverlays().get(0);
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

    // select a default value from the place ComboBox
    clickOn(placeBoxArrowRegion)
        .clickOn("Starbucks")

        // select a default value from the location ComboBox
        .clickOn(locationBoxArrowRegion)
        .clickOn("Los Angeles, CA")

        // perform the search
        .clickOn(searchButton).sleep(SLEEP_1500MS);

    // assert that the expected number of graphics (location pins) is displayed
    assertEquals("Unexpected number of graphics (location pins) found", 50, graphicsOverlay.getGraphics().size());
  }

  /**
   * Test Case 2: When using a custom search query, the app returns the expected number of results
   */
  @Test
  public void customSearchInput() {

    // Type 'Esri' into the place box and select the search result
    clickOn(placeBox).write("esri")
        .sleep(SLEEP_1500MS);

    Node esriEntry = lookup("Esri, 380 New York St, Redlands, CA, 92373, USA").query();
    clickOn(esriEntry);

    // Type 'Redlands' into the location box and select the search result
    clickOn(locationBox).write("Redlands")
        .sleep(SLEEP_1500MS);

    Node redlandsEntry = lookup("Redlands, CA, USA").query();
    clickOn(redlandsEntry);

    // perform the search
    clickOn(searchButton)
        .sleep(SLEEP_1500MS);

    // assert that the expected number of graphics (location pins) is displayed
    assertEquals("Unexpected number of graphics (location pins) found", 4, graphicsOverlay.getGraphics().size());
  }

  /**
   * Test Case 3: Find a callout node in the MapView and confirm the callout text
   */
  @Test
  public void confirmCalloutTest() {

    // Type 'Esri' into the place box and select the search result
    clickOn(placeBox).write("esri")
        .sleep(SLEEP_1500MS);

    Node esriEntry = lookup("Esri, 380 New York St, Redlands, CA, 92373, USA").query();
    clickOn(esriEntry);

    // Type 'Redlands' into the location box and select the search result
    clickOn(locationBox).write("Redlands")
        .sleep(SLEEP_1500MS);

    Node redlandsEntry = lookup("Redlands, CA, USA").query();
    clickOn(redlandsEntry);

    // perform the search
    clickOn(searchButton)
        .sleep(SLEEP_1500MS);

    // get the screen point of pin 1, move the mouse cursor to it
    Graphic pin = mapView.getGraphicsOverlays().get(0).getGraphics().get(1);
    Point2D screenPoint = mapView.locationToScreen((Point) pin.getGeometry());
    moveTo(mapView, Pos.TOP_LEFT, screenPoint, Motion.DIRECT)

        // scroll to zoom in so that more of the pins are visible
        .scroll(12, VerticalDirection.UP).sleep(SLEEP_500MS)

        // move the mouse cursor to pin 1 again, and click it to show the callout
        .moveTo(mapView, Pos.TOP_LEFT, screenPoint, Motion.DIRECT).clickOn(MouseButton.PRIMARY);

    // assert that the callout shows the correct information
    Callout callout = mapView.getCallout();
    assertNotNull(callout);
    assertTrue(callout.isVisible());

    // find the label with the text content matching the expected title of the search result
    Label titleLabel = lookup("380 New York St").query();
    assertNotNull(titleLabel);
    assertEquals(titleLabel.getText(), "380 New York St");

    // find the label with the text content matching the expected detail of the search result
    Label detailLabel = lookup("Redlands, California, 92373").query();
    assertNotNull(detailLabel);
    assertEquals(detailLabel.getText(), "Redlands, California, 92373");
  }

  // test cases:
  // TODO: each result has a corresponding pin shown
  // TODO: clicking on map triggers identify and callout

  /**
   * Test Case 6: Panning the map activates the 'redo search' button
   */
  @Test
  public void panEnablesRedoSearchButton() {

    // get a handle on the Redo Search button
    Button redoButton = lookup("#redoButton").queryButton();

    // perform a search with pre-defined search inputs
    clickOn(placeBoxArrowRegion)
        .clickOn("Starbucks")

        .clickOn(locationBoxArrowRegion)
        .clickOn("Los Angeles, CA")

        .clickOn(searchButton)

        .sleep(SLEEP_1500MS);

    // check that the Redo Search button is disabled, and activates after panning
    assertTrue(redoButton.isDisable());
    drag(mapView).moveBy(100, -100).drop();
    assertFalse(redoButton.isDisable());
  }
}