/*
 * Copyright 2016 Esri.
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

package com.esri.samples.map.open_existing_map;

import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.portal.Portal;
import com.esri.arcgisruntime.portal.PortalItem;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class OpenExistingMapSample extends Application {

  private MapView mapView;
  private ArcGISMap map;
  private Portal portal;

  // arcgis online portal url
  private static final String ARCGIS_URL = "http://www.arcgis.com/";

  // webmap portal item id's
  private static final String WEBMAP_HOUSES_WITH_MORTAGES_ID = "2d6fa24b357d427f9c737774e7b0f977";
  private static final String WEBMAP_USA_TAPESTRY_SEGMENTATION_ID = "01f052c8995e4b9e889d73c3e210ebe3";
  private static final String WEBMAP_USA_SOIL_SURVEY_ID = "0edea1c7bbb84ba5842d20483af11679";

  // webmap titles
  private static final String WEBMAP_HOUSES_WITH_MORTAGES_TITLE = "Houses with mortgages";
  private static final String WEBMAP_USA_TAPESTRY_SEGMENTATION_TITLE = "USA tapestry segmentation";
  private static final String WEBMAP_USA_SOIL_SURVEY_TITLE = "USA soil survey";

  @Override
  public void start(Stage stage) throws Exception {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);

      // set title, size, and add scene to stage
      stage.setTitle("Open Existing Map Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // create a list of web map entries
      ObservableList<WebmapEntry> webmapEntries = FXCollections.observableArrayList();
      webmapEntries.add(new WebmapEntry(WEBMAP_HOUSES_WITH_MORTAGES_TITLE, WEBMAP_HOUSES_WITH_MORTAGES_ID));
      webmapEntries.add(new WebmapEntry(WEBMAP_USA_TAPESTRY_SEGMENTATION_TITLE, WEBMAP_USA_TAPESTRY_SEGMENTATION_ID));
      webmapEntries.add(new WebmapEntry(WEBMAP_USA_SOIL_SURVEY_TITLE, WEBMAP_USA_SOIL_SURVEY_ID));

      // create a combo box of web map entries
      ComboBox<WebmapEntry> webmapComboBox = new ComboBox<>(webmapEntries);
      webmapComboBox.setEditable(false);
      webmapComboBox.setValue(webmapEntries.get(0));

      // define rendering of the list of values in ComboBox drop down.
      webmapComboBox.setCellFactory((comboBox) -> new ListCell<WebmapEntry>() {

        @Override
        protected void updateItem(WebmapEntry item, boolean empty) {

          super.updateItem(item, empty);

          if (item == null || empty) {
            setText(null);
          } else {
            setText(item.getName());
          }
        }
      });

      // define rendering of selected value shown in ComboBox.
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

      // handle ComboBox event.
      webmapComboBox.setOnAction((event) -> {
        WebmapEntry selectedWebMap = webmapComboBox.getSelectionModel().getSelectedItem();
        if (map != null) {
          mapView.setMap(null);
        }
        PortalItem portalItem = new PortalItem(portal, selectedWebMap.getId());
        map = new ArcGISMap(portalItem);
        mapView.setMap(map);
      });

      // create flow pane for combo box
      FlowPane flowPane = new FlowPane(Orientation.HORIZONTAL, 0, 0, webmapComboBox);
      flowPane.setPadding(new Insets(5, 0, 0, 5));
      flowPane.setColumnHalignment(HPos.LEFT);
      flowPane.setLayoutX(20);
      flowPane.setLayoutY(40);

      // create a portal using the arcgis url
      portal = new Portal(ARCGIS_URL);
      PortalItem portalItem = new PortalItem(portal, webmapEntries.get(0).getId());

      // create a ArcGISMap with the portal item
      map = new ArcGISMap(portalItem);

      // create a ArcGISMap view and set ArcGISMap to it
      mapView = new MapView();
      mapView.setMap(map);

      // add the map view and flow panel to stack pane
      stackPane.getChildren().addAll(mapView, flowPane);
      StackPane.setAlignment(flowPane, Pos.TOP_LEFT);

    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
    }
  }

  /**
   * Defines class for the webmap entries.
   */
  private class WebmapEntry {

    private final String name;
    private final String id;

    WebmapEntry(String name, String id) {
      this.name = name;
      this.id = id;
    }

    String getName() {

      return name;
    }

    String getId() {

      return id;
    }

  }

  /**
   * Stops and releases all resources used in application.
   */
  @Override
  public void stop() throws Exception {

    if (mapView != null) {
      mapView.dispose();
    }
  }

  /**
   * Opens and runs application.
   *
   * @param args arguments passed to this application
   */
  public static void main(String[] args) {

    Application.launch(args);
  }

}
