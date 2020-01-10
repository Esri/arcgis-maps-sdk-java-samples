package com.esri.samples.find_place;

import com.esri.arcgisruntime.mapping.view.DrawStatus;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import javafx.scene.Node;
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

  Object mapView;
  ComboBox<String> placeBox;
  ComboBox<String> locationBox;
  Node locationBoxArrowRegion;
  Node placeBoxArrowRegion;

  @Before
  public void setup() throws Exception {
    // launch the sample
    ApplicationTest.launch(FindPlaceSample.class);

    // get a handle on the MapView
    Node mapViewNode = lookup("#mapView").query();
    if (mapViewNode instanceof MapView) {
      mapView = mapViewNode;
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

    // wait for initialization
    sleep(1000);
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
    sleep(1000);
  }

  /**
   * Test Case 1: When using one of the pre-defined search inputs, the app returns the expected number of results
   */
  @Test
  public void defaultSearchInput() {

    clickOn(placeBoxArrowRegion);
    sleep(500);
    clickOn("Starbucks");
    sleep(500);

    clickOn(locationBoxArrowRegion);
    sleep(500);
    clickOn("Los Angeles, CA");
    sleep(500);

    clickOn("Search");

    // wait for the map view to zoom to the location
    if (mapView instanceof MapView) {
      ((MapView) mapView).addDrawStatusChangedListener((drawStatusChangedEvent) -> {
        if (drawStatusChangedEvent.getDrawStatus() == DrawStatus.COMPLETED) {
          // get the graphics overlay and assert that the expected number of graphics (location pins) is displayed
          GraphicsOverlay graphicsOverlay = ((MapView) mapView).getGraphicsOverlays().get(0);
          assertEquals("Unexpected number of graphics (location pins) found",50, graphicsOverlay.getGraphics().size());
        }
      });
    }

    sleep(1000);
  }



  // test cases:
  // TODO: find callout node in map view and confirm text (probably something you'll be doing in our unit tests)
  // TODO: each result has a corresponding pin shown
  // TODO: clicking on map triggers identify and callout
  // TODO: panning map triggers prompt for new results

}