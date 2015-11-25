/*
 * Copyright 2015 Esri.
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

package com.esri.sampleviewer.samples.map;

import java.util.Optional;

import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Bookmark;
import com.esri.arcgisruntime.mapping.BookmarkList;
import com.esri.arcgisruntime.mapping.Map;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.Viewpoint;
import com.esri.arcgisruntime.mapping.view.ViewpointType;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * This sample demonstrates how to access and add Bookmarks to a Map.
 * <h4>How it Works</h4>
 * 
 * A {@link BookmarkList} is created using the {@link Map#getBookmarks} method
 * and then some {@link Bookmark}s are added to that list, each with a name and
 * a {@link Viewpoint}.
 * <p>
 * To set the Map's Viewpoint to that of a Bookmark, select a Bookmark from the
 * BookmarkList, call the {@link Bookmark#getViewpoint} method, and pass that to
 * {@link MapView#setViewpoint}.
 */
public class ManageBookmarks extends Application {

  private MapView mapView;
  private BookmarkList bookmarkList;
  private Bookmark bookmark;

  @Override
  public void start(Stage stage) throws Exception {

    // create stack pane and application scene
    StackPane stackPane = new StackPane();
    Scene scene = new Scene(stackPane);
    scene.getStylesheets().add(getClass()
        .getResource("../resources/SamplesTheme.css").toExternalForm());

    // set title, size, and add scene to stage
    stage.setTitle("Manage Bookmark Sample");
    stage.setWidth(800);
    stage.setHeight(700);
    stage.setScene(scene);
    stage.show();

    // create a control panel
    VBox vBoxControl = new VBox(6);
    vBoxControl.setMaxSize(250, 440);
    vBoxControl.getStyleClass().add("panel-region");

    // create sample description
    Label descriptionLabel = new Label("Sample Description:");
    descriptionLabel.getStyleClass().add("panel-label");
    TextArea description = new TextArea(
        "This samples shows how to access and add bookmarks to a Map. "
            + "Select a Bookmark from the list to change the Map's Viewpoint to"
            + " that location. To add a Bookmark, pan or zoom to a location "
            + "and hit the add bookmark button.");
    description.setWrapText(true);
    description.autosize();
    description.setEditable(false);

    // create label for bookmarks
    Label bookmarkLabel = new Label("Bookmarks");
    bookmarkLabel.getStyleClass().add("panel-label");

    // create a list to hold the names of the bookmarks
    ListView<String> bookmarkNames = new ListView<>();
    bookmarkNames.setMaxHeight(190);

    // when user clicks on a bookmark change to that location
    bookmarkNames.getSelectionModel().selectedItemProperty()
        .addListener((ov, old_val, new_val) -> {
          int index = bookmarkNames.getSelectionModel().getSelectedIndex();
          mapView.setViewpoint(bookmarkList.get(index).getViewpoint());
        });

    // create button to add a bookmark
    Button addBookmarkButton = new Button("Add Bookmark");
    addBookmarkButton.setMaxWidth(Double.MAX_VALUE);

    // show dialog to user and add a bookmark if text is entered
    addBookmarkButton.setOnAction(e -> {
      TextInputDialog dialog = new TextInputDialog();
      dialog.setHeaderText("Add Bookmark");
      dialog.setContentText("Bookmark Name");

      Optional<String> result = dialog.showAndWait();
      result.ifPresent(text -> {
        if (text.trim().length() > 0) {
          bookmark = new Bookmark(text,
              mapView.getCurrentViewpoint(ViewpointType.BOUNDING_GEOMETRY));
          bookmarkList.add(bookmark);
          bookmarkNames.getItems().add(bookmark.getName());
        }
      });
    });

    // add label and sample description to the control panel
    vBoxControl.getChildren().addAll(descriptionLabel, description,
        bookmarkLabel, bookmarkNames, addBookmarkButton);

    try {
      Map map = new Map(Basemap.createImageryWithLabels());

      // create a view for this map and set map to it
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
      mapView.setViewpointAsync(viewpoint);

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
      stackPane.getChildren().addAll(mapView, vBoxControl);
      StackPane.setAlignment(vBoxControl, Pos.TOP_LEFT);
      StackPane.setMargin(vBoxControl, new Insets(10, 0, 0, 10));

    } catch (Exception e) {
      // on any error, display the stack trace
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

}
