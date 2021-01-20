/*
 * Copyright 2017 Esri.
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

package com.esri.samples.manage_bookmarks;

import java.util.Optional;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.Bookmark;
import com.esri.arcgisruntime.mapping.BookmarkList;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.MapView;

public class ManageBookmarksSample extends Application {

  private MapView mapView;
  private BookmarkList bookmarkList;
  private Bookmark bookmark;

  @Override
  public void start(Stage stage) {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);
      scene.getStylesheets().add(getClass().getResource("/manage_bookmarks/style.css").toExternalForm());

      // set title, size, and add scene to stage
      stage.setTitle("Manage Bookmarks Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // authentication with an API key or named user is required to access basemaps and other location services
      String yourAPIKey = System.getProperty("apiKey");
      ArcGISRuntimeEnvironment.setApiKey(yourAPIKey);

      // create a control panel
      VBox controlsVBox = new VBox(6);
      controlsVBox.setBackground(new Background(new BackgroundFill(Paint.valueOf("rgba(0,0,0,0.3)"), CornerRadii.EMPTY,
          Insets.EMPTY)));
      controlsVBox.setPadding(new Insets(10.0));
      controlsVBox.setMaxSize(220, 240);
      controlsVBox.getStyleClass().add("panel-region");

      // create label for bookmarks
      Label bookmarkLabel = new Label("Bookmarks");
      bookmarkLabel.getStyleClass().add("panel-label");

      // create a list to hold the names of the bookmarks
      ListView<String> bookmarkNames = new ListView<>();
      bookmarkNames.setMaxHeight(190);

      // when user clicks on a bookmark change to that location
      bookmarkNames.getSelectionModel().selectedItemProperty().addListener((ov, old_val, new_val) -> {
        int index = bookmarkNames.getSelectionModel().getSelectedIndex();
        mapView.setBookmarkAsync(bookmarkList.get(index));
      });

      // create button to add a bookmark
      Button addBookmarkButton = new Button("Add Bookmark");
      addBookmarkButton.setMaxWidth(Double.MAX_VALUE);

      // show dialog to user and add a bookmark if text is entered
      addBookmarkButton.setOnAction(e -> {
        TextInputDialog dialog = new TextInputDialog();
        dialog.initOwner(mapView.getScene().getWindow());
        dialog.setHeaderText("Add Bookmark");
        dialog.setContentText("Bookmark Name");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(text -> {
          if (text.trim().length() > 0 && !bookmarkNames.getItems().contains(text)) {
            bookmark = new Bookmark(text, mapView.getCurrentViewpoint(Viewpoint.Type.BOUNDING_GEOMETRY));
            bookmarkList.add(bookmark);
            bookmarkNames.getItems().add(bookmark.getName());
          } else {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.initOwner(mapView.getScene().getWindow());
            alert.setHeaderText("Text Error");
            alert.setContentText("Text name already exist or no text was entered.");
            alert.showAndWait();
          }
        });
      });

      // add label and bookmarks to the control panel
      controlsVBox.getChildren().addAll(bookmarkLabel, bookmarkNames, addBookmarkButton);

      // create a map with the standard imagery basemap style
      ArcGISMap map = new ArcGISMap(BasemapStyle.ARCGIS_IMAGERY_STANDARD);

      // create a map view and set the map to it
      mapView = new MapView();
      mapView.setMap(map);

      // get all the bookmarks from the map
      bookmarkList = map.getBookmarks();

      // add default bookmarks
      Viewpoint viewpoint;

      viewpoint = new Viewpoint(27.3805833, 33.6321389, 6e3);
      bookmark = new Bookmark("Mysterious Desert Pattern", viewpoint);
      bookmarkList.add(bookmark);
      bookmarkNames.getItems().add(bookmark.getName());
      // set this bookmark as the map's initial viewpoint
      mapView.setBookmarkAsync(bookmarkList.get(0));

      viewpoint = new Viewpoint(37.401573, -116.867808, 6e3);
      bookmark = new Bookmark("Strange Symbol", viewpoint);
      bookmarkNames.getItems().add(bookmark.getName());
      bookmarkList.add(bookmark);

      viewpoint = new Viewpoint(-33.867886, -63.985, 4e4);
      bookmark = new Bookmark("Guitar-Shaped Trees", viewpoint);
      bookmarkNames.getItems().add(bookmark.getName());
      bookmarkList.add(bookmark);

      viewpoint = new Viewpoint(44.525049, -110.83819, 6e3);
      bookmark = new Bookmark("Grand Prismatic Spring", viewpoint);
      bookmarkNames.getItems().add(bookmark.getName());
      bookmarkList.add(bookmark);

      // add the map view and control panel to stack pane
      stackPane.getChildren().addAll(mapView, controlsVBox);
      StackPane.setAlignment(controlsVBox, Pos.TOP_LEFT);
      StackPane.setMargin(controlsVBox, new Insets(10, 0, 0, 10));

    } catch (Exception e) {
      // on any error, display the stack trace
      e.printStackTrace();
    }
  }

  /**
   * Stops and releases all resources used in application.
   */
  @Override
  public void stop() {

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
