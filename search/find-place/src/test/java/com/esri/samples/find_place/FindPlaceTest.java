package com.esri.samples.find_place;

import com.esri.arcgisruntime.mapping.view.DrawStatus;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Pane;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

import static org.junit.Assert.assertEquals;
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

  //  @Override
  //  public void start(Stage stage) throws Exception {
  //    Parent mainNode = FXMLLoader.load(FindPlaceSample.class.getResource("/find_place.fxml"));
  //    stage.setScene(new Scene(mainNode));
  //    stage.show();
  //    stage.toFront();
  //
  //    // wait for initialization
  //    sleep(1000);
  //  }

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

    clickOn(placeBoxArrowRegion).sleep(SLEEP_500MS);
    clickOn("Starbucks").sleep(SLEEP_500MS);

    clickOn(locationBoxArrowRegion).sleep(SLEEP_500MS);
    clickOn("Los Angeles, CA").sleep(SLEEP_500MS);

    clickOn(searchButton);

    // wait for the map view to zoom to the location
    mapView.addDrawStatusChangedListener(drawStatusChangedEvent -> {
      if (drawStatusChangedEvent.getDrawStatus() == DrawStatus.COMPLETED) {
        // get the graphics overlay and assert that the expected number of graphics (location pins) is displayed
        assertEquals("Unexpected number of graphics (location pins) found", 50, graphicsOverlay.getGraphics().size());
      }
    });

    sleep(SLEEP_1000MS);
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

    clickOn(searchButton);

    // wait for the map view to zoom to the location
    mapView.addDrawStatusChangedListener(drawStatusChangedEvent -> {
      if (drawStatusChangedEvent.getDrawStatus() == DrawStatus.COMPLETED) {
        // get the graphics overlay and assert that the expected number of graphics (location pins) is displayed
        assertEquals("Unexpected number of graphics (location pins) found", 4, graphicsOverlay.getGraphics().size());
      }
    });
  }

}