package com.esri.samples.find_place;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.loadui.testfx.GuiTest;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

import static org.junit.Assert.*;

public class FindPlaceTest extends ApplicationTest {

  @Override
  public void start (Stage stage) throws Exception {
    Parent mainNode = FXMLLoader.load(FindPlaceSample.class.getResource("/find_place.fxml"));
    stage.setScene(new Scene(mainNode));
    stage.show();
    stage.toFront();
  }

  @Before
  public void setUp () throws Exception {
  }

  @After
  public void tearDown () throws Exception {
    FxToolkit.hideStage();
    release(new KeyCode[]{});
    release(new MouseButton[]{});
  }

  @Test
  public void testClickButton () throws InterruptedException {

    ComboBox<String> placeBox = lookup("#placeBox").query();

    // Find and click only on arrow button. This is important for editable combo-boxes.
    for (Node child : placeBox.getChildrenUnmodifiable()) {
      if (child.getStyleClass().contains("arrow-button")) {
        Node arrowRegion = ((Pane) child).getChildren().get(0);
        clickOn(arrowRegion);
        Thread.sleep(100); // try/catch were skipped for shorter code.
        clickOn("Starbucks");
      }
    }

    clickOn("Search");
  }
}