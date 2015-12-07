/* Copyright 2015 Esri.
 
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
 
    http://www.apache.org/licenses/LICENSE-2.0
 
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
limitations under the License.  */

package com.esri.sampleviewer.samples.map;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextArea;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import com.esri.arcgisruntime.mapping.Map;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.portal.Portal;
import com.esri.arcgisruntime.portal.PortalItem;
import com.esri.arcgisruntime.security.Credential;

/**
 * This sample demonstrates how to open an existing Map from a PortalItem.
 * <p>
 * A {@link Portal} collects information from different sources and creates a
 * way to access that information.
 * <p>
 * A {@link PortalItem} contains a section of data from a Portal, example being
 * a tiled package.
 * <h4>How it Works</h4>
 * 
 * A Portal is created using a URL and some {@link Credential}s, this can be
 * null if a password is not needed. A PortalItem is then created using that
 * Portal and a ID associated with some data inside that Portal. This
 * PortialItem can then be set to the {@link Map} and displayed in the
 * {@link MapView}.
 */
public class OpenExistingMap extends Application {

  private MapView mapView;
  private Map map;
  private Portal portal;

  private static final String SAMPLES_THEME_PATH =
      "../resources/SamplesTheme.css";

  // arcgis online portal url
  private static final String ARCGIS_URL = "http://www.arcgis.com/";

  // webmap portal item id's
  private static final String WEBMAP_HOUSES_WITH_MORTAGES_ID =
      "2d6fa24b357d427f9c737774e7b0f977";
  private static final String WEBMAP_USA_TAPESTRY_SEGMENTATION_ID =
      "01f052c8995e4b9e889d73c3e210ebe3";
  private static final String WEBMAP_USA_POP_DENSITY_ID =
      "85c92f2a6e5b49f894fb72988d87551f";

  // webmap titles
  private static final String WEBMAP_HOUSES_WITH_MORTAGES_TITLE =
      "Houses with mortgages";
  private static final String WEBMAP_USA_TAPESTRY_SEGMENTATION_TITLE =
      "USA tapestry segmentation";
  private static final String WEBMAP_USA_POP_DENSITY_TITLE =
      "2015 Population Density in the US";

  @Override
  public void start(Stage stage) throws Exception {

    // create stack pane and application scene
    StackPane stackPane = new StackPane();
    Scene scene = new Scene(stackPane);
    scene.getStylesheets().add(getClass().getResource(SAMPLES_THEME_PATH)
        .toExternalForm());

    // set title, size, and add scene to stage
    stage.setTitle("Open Existing Map Sample");
    stage.setWidth(800);
    stage.setHeight(700);
    stage.setScene(scene);
    stage.show();

    // create a control panel
    VBox vBoxControl = new VBox(6);
    vBoxControl.setMaxSize(250, 190);
    vBoxControl.getStyleClass().add("panel-region");

    // create sample description
    Label descriptionLabel = new Label("Sample Description:");
    descriptionLabel.getStyleClass().add("panel-label");
    TextArea description = new TextArea("This sample shows how to open a "
        + "Map from a PortalItem. Select an item from the combo box"
        + " to fetch an existing Map and load it to the view.");
    description.setWrapText(true);
    description.autosize();
    description.setEditable(false);

    // create a list of web map entries
    ObservableList<WebmapEntry> webmapEntries = FXCollections
        .observableArrayList();
    webmapEntries.add(new WebmapEntry(WEBMAP_HOUSES_WITH_MORTAGES_TITLE,
        WEBMAP_HOUSES_WITH_MORTAGES_ID));

    webmapEntries.add(new WebmapEntry(WEBMAP_USA_TAPESTRY_SEGMENTATION_TITLE,
        WEBMAP_USA_TAPESTRY_SEGMENTATION_ID));

    webmapEntries.add(new WebmapEntry(WEBMAP_USA_POP_DENSITY_TITLE,
        WEBMAP_USA_POP_DENSITY_ID));

    // create a combo box of web map entries
    ComboBox<WebmapEntry> webmapComboBox = new ComboBox<>(webmapEntries);
    webmapComboBox.setEditable(false);
    webmapComboBox.setValue(webmapEntries.get(0));

    // Define rendering of the list of values in ComboBox drop down.
    webmapComboBox.setCellFactory((comboBox) -> {
      return new ListCell<WebmapEntry>() {

        @Override
        protected void updateItem(WebmapEntry item, boolean empty) {

          super.updateItem(item, empty);

          if (item == null || empty) {
            setText(null);
          } else {
            setText(item.getName());
          }
        }
      };
    });

    // Define rendering of selected value shown in ComboBox.
    webmapComboBox.setConverter(new StringConverter<WebmapEntry>() {

      @Override
      public String toString(WebmapEntry webmap) {

        if (webmap == null) {
          return null;
        }
        return webmap.getName();
      }

      @Override
      public WebmapEntry fromString(String webmapString) {

        return null; // No conversion fromString needed.
      }
    });

    // Handle ComboBox event.
    webmapComboBox.setOnAction((event) -> {
      WebmapEntry selectedWebMap = webmapComboBox.getSelectionModel()
          .getSelectedItem();
      if (map != null) {
        mapView.setMap(null);
        map.dispose();
      }
      PortalItem portalItem = new PortalItem(portal, selectedWebMap.getId());
      map = new Map(portalItem);
      mapView.setMap(map);
    });

    // create flow pane for combo box
    FlowPane flowPane = new FlowPane(Orientation.HORIZONTAL, 0, 0,
        webmapComboBox);
    flowPane.setPadding(new Insets(5, 0, 0, 5));
    flowPane.setColumnHalignment(HPos.LEFT);
    flowPane.setLayoutX(20);
    flowPane.setLayoutY(40);

    // add label and sample description to the control panel
    vBoxControl.getChildren().addAll(descriptionLabel, description, flowPane);

    try {
      // create a portal using the arcgis url
      portal = new Portal(ARCGIS_URL, null);
      PortalItem portalItem = new PortalItem(portal, webmapEntries.get(0)
          .getId());

      // create a map with the portal item
      map = new Map(portalItem);

      // create a map view and set map to it
      mapView = new MapView();
      mapView.setMap(map);

      // add the map view and control panel to stack pane
      stackPane.getChildren().addAll(mapView, vBoxControl);
      StackPane.setAlignment(vBoxControl, Pos.TOP_LEFT);
      StackPane.setMargin(vBoxControl, new Insets(10, 0, 0, 10));
    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
    }
  }

  /**
   * Stops and releases all resources used in application.
   *
   * @throws Exception if security manager doesn't allow JVM to exit with
   *           current status
   */
  @Override
  public void stop() throws Exception {

    if (mapView != null) {
      mapView.dispose();
    }
    Platform.exit();
    System.exit(0);
  }

  /**
   * Opens and runs application.
   *
   * @param args arguments passed to this application
   */
  public static void main(String[] args) {

    Application.launch(args);
  }

  /**
   * Defines class for the webmap entries.
   */
  private class WebmapEntry {

    private final String name;
    private final String id;

    public WebmapEntry(String name, String id) {
      this.name = name;
      this.id = id;
    }

    public String getName() {

      return name;
    }

    public String getId() {

      return id;
    }

  }

}
