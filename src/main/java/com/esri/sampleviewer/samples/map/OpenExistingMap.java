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

import com.esri.arcgisruntime.mapping.Map;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.portal.Portal;
import com.esri.arcgisruntime.portal.PortalItem;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.StringConverter;

/**
 * The Open Existing Map application demonstrates how to open an existing
 * {@link Map} as a {@PortalItem} from a{@Portal}. The application opens with a
 * web map from a portal displayed. Select any of the web maps from the drop
 * down list to open it up in the MapView.
 */
public class OpenExistingMap extends Application {

  private MapView mapView;
  private Map map;
  private Portal portal;

  // arcgis online portal url
  private static final String PORTAL_URL = "http://www.arcgis.com/";

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

    // creates a stack pane and application scene
    StackPane stackPane = new StackPane();
    Scene scene = new Scene(stackPane);

    // size the stage and add a title
    stage.setTitle("Open Existing Map");
    stage.setWidth(700);
    stage.setHeight(800);
    stage.setScene(scene);
    stage.show();

    ObservableList<WebmapEntry> webmapData =
        FXCollections.observableArrayList();

    // Init the webmap items.
    webmapData.add(new WebmapEntry(WEBMAP_HOUSES_WITH_MORTAGES_TITLE,
        WEBMAP_HOUSES_WITH_MORTAGES_ID));
    webmapData.add(new WebmapEntry(WEBMAP_USA_TAPESTRY_SEGMENTATION_TITLE,
        WEBMAP_USA_TAPESTRY_SEGMENTATION_ID));
    webmapData.add(new WebmapEntry(WEBMAP_USA_POP_DENSITY_TITLE,
        WEBMAP_USA_POP_DENSITY_ID));

    // Creates ComboBox
    ComboBox<WebmapEntry> webmapComboBox = new ComboBox<>(webmapData);
    webmapComboBox.setEditable(false);
    webmapComboBox.setValue(webmapData.get(0));

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
      WebmapEntry selectedWebMap =
          webmapComboBox.getSelectionModel().getSelectedItem();
      if (map != null) {
        mapView.setMap(null);
        map.dispose();
      }
      PortalItem portalItem = new PortalItem(portal, selectedWebMap.getId());
      map = new Map(portalItem);
      mapView.setMap(map);
    });

    FlowPane flowPane =
        new FlowPane(Orientation.HORIZONTAL, 0, 0, webmapComboBox);
    flowPane.setPadding(new Insets(5, 0, 0, 5));
    flowPane.setColumnHalignment(HPos.LEFT);
    flowPane.setLayoutX(20);
    flowPane.setLayoutY(40);

    // creates a Map which defines the layers of data to view
    try {
      portal = new Portal(PORTAL_URL, null);
      PortalItem portalItem = new PortalItem(portal, webmapData.get(0).getId());
      map = new Map(portalItem);

      // creates the MapView JavaFX control and assign its map
      mapView = new MapView();
      mapView.setMap(map);

      // adds the MapView
      stackPane.getChildren().addAll(mapView, flowPane);

    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
    }
  }

  @Override
  public void stop() throws Exception {

    // releases resources when the application closes
    mapView.dispose();
    map.dispose();
    Platform.exit();
    System.exit(0);
  }

  /**
   * Starting point of this application.
   * 
   * @args arguments to this application.
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

    /**
     * Constructor
     * 
     * @param name
     * @param id
     */
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
