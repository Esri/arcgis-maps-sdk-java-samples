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

package com.esri.samples.search.find_place;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.*;
import com.esri.arcgisruntime.mapping.view.Callout.LeaderPosition;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.esri.arcgisruntime.tasks.geocode.*;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class FindPlaceSample extends Application {

  private MapView mapView;
  private LocatorTask locatorTask;
  private Envelope preferredSearchArea = null;
  private GraphicsOverlay graphicsOverlay;
  private PictureMarkerSymbol pinSymbol;
  private ComboBox<String> searchBox;
  private ComboBox<String> locationBox;
  private Button searchButton;
  private Button redoButton;
  private ListenableFuture<IdentifyGraphicsOverlayResult> identifyResults;

  @Override
  public void start(Stage stage) throws Exception {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);
      scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

      // set title, size, and add scene to stage
      stage.setTitle("Find Place Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // create a control panel
      VBox vBoxControl = new VBox(6);
      vBoxControl.setMaxSize(240, 100);
      vBoxControl.getStyleClass().add("panel-region");

      // create search box
      searchBox = new ComboBox<>();
      searchBox.setMaxWidth(Double.MAX_VALUE);
      searchBox.setPromptText("Find a place");
      searchBox.setEditable(true);

      // add example String suggestions
      String[] examples = {
          "Starbucks", "Coffee", "Bank", "Gas station", "Empire State Building"
      };
      searchBox.getItems().addAll(examples);

      // create location box
      locationBox = new ComboBox<>();
      locationBox.setMaxWidth(Double.MAX_VALUE);
      locationBox.setPromptText("Near...");
      locationBox.setEditable(true);

      String[] locations = {
          "Los Angeles, CA", "New York City, New York"
      };
      locationBox.getItems().addAll(locations);

      // create search button
      searchButton = new Button("Search");
      searchButton.setMaxWidth(Double.MAX_VALUE);

      // create redo-search button
      redoButton = new Button("Redo search in this area");
      redoButton.setMaxWidth(260.0);
      redoButton.setDisable(true);

      // add controls to the user interface panel
      vBoxControl.getChildren().addAll(searchBox, locationBox, searchButton);

      // create ArcGISMap with streets basemap
      ArcGISMap map = new ArcGISMap(Basemap.createStreets());

      // create a view and set ArcGISMap to it
      mapView = new MapView();
      mapView.setMap(map);
      mapView.setWrapAroundMode(WrapAroundMode.DISABLED);

      // add a graphics overlay to the map view
      graphicsOverlay = new GraphicsOverlay();
      mapView.getGraphicsOverlays().add(graphicsOverlay);

      // set the callouts default style
      Callout callout = mapView.getCallout();
      callout.setLeaderPosition(LeaderPosition.BOTTOM);
      callout.setTranslateY(-40); // half image height

      // create a locatorTask task
      locatorTask = new LocatorTask("http://geocode.arcgis.com/arcgis/rest/services/World/GeocodeServer");

      // create a pin graphic
      Image img = new Image(getClass().getResourceAsStream("/symbols/pin.png"), 0, 80, true, true);
      pinSymbol = new PictureMarkerSymbol(img);
      pinSymbol.loadAsync();

      // event to get auto-complete suggestions when the user types a query
      searchBox.getEditor().setOnKeyTyped((Event evt) -> {

        // get the search box text for auto-complete suggestions
        String typed = searchBox.getEditor().getText();

        if (!typed.equals("")) {

          // suggest places only
          SuggestParameters geocodeParameters = new SuggestParameters();
          geocodeParameters.getCategories().add("POI");

          // get suggestions from the locatorTask
          ListenableFuture<List<SuggestResult>> suggestions = locatorTask.suggestAsync(typed, geocodeParameters);

          // add a listener to update suggestions list when loaded
          suggestions.addDoneListener(new SuggestionsLoadedListener(suggestions, searchBox));
        }
      });

      // event to get auto-complete suggestions for location when the user types
      // a search location
      locationBox.getEditor().setOnKeyTyped((Event evt) -> {

        // get the search box text for auto-complete suggestions
        String typed = locationBox.getEditor().getText();

        if (!typed.equals("")) {

          // get suggestions from the locatorTask
          ListenableFuture<List<SuggestResult>> suggestions = locatorTask.suggestAsync(typed);

          // add a listener to update suggestions list when loaded
          suggestions.addDoneListener(new SuggestionsLoadedListener(suggestions, locationBox));
        }
      });

      // event to get geocode when query is submitted
      searchButton.setOnAction((ActionEvent evt) -> search(false));

      // when the search location is changed, trigger the change event listener
      // on the search box
      locationBox.setOnAction((ActionEvent evt) -> {
        String locationQuery = locationBox.getEditor().getText();
        if (!locationQuery.equals("")) {
          GeocodeParameters geocodeParameters = new GeocodeParameters();
          geocodeParameters.setOutputSpatialReference(mapView.getSpatialReference());

          // run the locatorTask geocode task
          ListenableFuture<List<GeocodeResult>> results = locatorTask.geocodeAsync(locationQuery, geocodeParameters);
          results.addDoneListener(() -> {
            try {
              List<GeocodeResult> points = results.get();
              if (points.size() > 0) {
                // create a search area envelope around the location
                Point p = points.get(0).getDisplayLocation();
                preferredSearchArea = new Envelope(p.getX() - 10000, p.getY() - 10000, p.getX() + 10000, p.getY() +
                    10000, p.getSpatialReference());
                search(false);
              }
            } catch (Exception e) {
              e.printStackTrace();
            }
          });
        }
      });

      // search again in the map's extent when redo-button pressed
      redoButton.setOnAction((ActionEvent evt) -> search(true));

      // event to display a callout for a selected result
      mapView.setOnMouseClicked(evt -> {
        // check that the primary mouse button was clicked and the user is not
        // panning
        if (evt.isStillSincePress() && evt.getButton() == MouseButton.PRIMARY) {
          // create a point from where the user clicked
          Point2D point = new Point2D(evt.getX(), evt.getY());

          // get layers with elements near the clicked location
          identifyResults = mapView.identifyGraphicsOverlayAsync(graphicsOverlay, point, 10, false);
          identifyResults.addDoneListener(() -> {
            try {
              List<Graphic> graphics = identifyResults.get().getGraphics();
              if (graphics.size() > 0) {
                Graphic marker = graphics.get(0);
                // update the callout
                Platform.runLater(() -> {
                  callout.setTitle(marker.getAttributes().get("title").toString());
                  callout.setDetail(marker.getAttributes().get("detail").toString());
                  callout.showCalloutAt((Point) marker.getGeometry());
                });
              }
            } catch (Exception e) {
              e.printStackTrace();
            }
          });
        }
      });

      // add map view and control panel to stack pane
      stackPane.getChildren().addAll(mapView, vBoxControl, redoButton);
      StackPane.setAlignment(vBoxControl, Pos.TOP_LEFT);
      StackPane.setAlignment(redoButton, Pos.BOTTOM_CENTER);
      StackPane.setMargin(vBoxControl, new Insets(10, 0, 0, 10));
      StackPane.setMargin(redoButton, new Insets(0, 0, 10, 0));

    } catch (Exception e) {
      // on any error, print the stack trace
      e.printStackTrace();
    }
  }

  /**
   * Performs the geocode search based on the search type: user input, dropdown
   * suggestion, or by the view's extent.
   * 
   * @param byExtent whether the search should limit results to the view's
   *          current extent
   */
  private void search(boolean byExtent) {

    // get the user's query
    String query;
    if (searchBox.getSelectionModel().getSelectedIndex() == -1) {
      // user supplied their own query
      query = searchBox.getEditor().getText();
    } else {
      // user chose a suggested query
      query = searchBox.getSelectionModel().getSelectedItem();
    }

    if (!query.equals("")) {
      // hide callout if showing
      mapView.getCallout().dismiss();

      // set the geocode task parameters based on type of query
      GeocodeParameters geocodeParameters = new GeocodeParameters();
      geocodeParameters.getResultAttributeNames().add("*"); // return all attributes
      geocodeParameters.setOutputSpatialReference(SpatialReferences.getWebMercator());
      if (byExtent) {
        geocodeParameters.setSearchArea(mapView.getVisibleArea().getExtent());
      } else if (preferredSearchArea != null) {
        geocodeParameters.setSearchArea(preferredSearchArea);
      }

      // run the locatorTask geocode task
      ListenableFuture<List<GeocodeResult>> results = locatorTask.geocodeAsync(query, geocodeParameters);

      // add a listener to display the results when loaded
      results.addDoneListener(new ResultsLoadedListener(results));
    }
  }

  /**
   * A listener to update a {@link ComboBox} when suggestions from a call to
   * {@link LocatorTask#suggestAsync(String, SuggestParameters)} are loaded.
   */
  private class SuggestionsLoadedListener implements Runnable {

    private final ListenableFuture<List<SuggestResult>> results;
    private ComboBox<String> comboBox;

    /**
     * Constructs a listener to update an auto-complete list for geocode
     * suggestions.
     * 
     * @param results suggestion results from a {@link LocatorTask}
     * @param box the {@link ComboBox} to update with the suggestions
     */
    SuggestionsLoadedListener(ListenableFuture<List<SuggestResult>> results, ComboBox<String> box) {
      this.results = results;
      this.comboBox = box;
    }

    @Override
    public void run() {

      try {
        List<SuggestResult> suggestResult = results.get();
        List<String> suggestions = suggestResult.stream().map(SuggestResult::getLabel).collect(Collectors.toList());

        // update the combo box with suggestions
        Platform.runLater(() -> {
          comboBox.getItems().clear();
          if (suggestions.size() > 0) {
            comboBox.getItems().addAll(suggestions);
            comboBox.show();
          } else {
            comboBox.hide();
          }
        });

      } catch (InterruptedException | ExecutionException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Runnable listener to update marker and callout when new results are loaded.
   */
  private class ResultsLoadedListener implements Runnable {

    private final ListenableFuture<List<GeocodeResult>> results;

    /**
     * Constructs a runnable listener for the geocode results.
     * 
     * @param results results from a {@link LocatorTask#geocodeAsync} task
     */
    ResultsLoadedListener(ListenableFuture<List<GeocodeResult>> results) {
      this.results = results;
    }

    @Override
    public void run() {

      List<Graphic> markers = new ArrayList<>();
      try {
        List<GeocodeResult> geocodes = results.get();
        for (GeocodeResult geocode : geocodes) {

          // get attributes from the result for the callout
          String addrType = geocode.getAttributes().get("Addr_type").toString();
          String placeName = geocode.getAttributes().get("PlaceName").toString();
          String placeAddr = geocode.getAttributes().get("Place_addr").toString();
          String matchAddr = geocode.getAttributes().get("Match_addr").toString();
          String locType = geocode.getAttributes().get("Type").toString();

          // format callout details
          String title;
          String detail;
          switch (addrType) {
            case "POI":
              title = placeName.equals("") ? "" : placeName;
              if (!placeAddr.equals("")) {
                detail = placeAddr;
              } else if (!matchAddr.equals("") && !locType.equals("")) {
                detail = !matchAddr.contains(",") ? locType : matchAddr.substring(matchAddr.indexOf(", ") + 2);
              } else {
                detail = "";
              }
              break;
            case "StreetName":
            case "PointAddress":
            case "Postal":
              if (matchAddr.contains(",")) {
                title = matchAddr.equals("") ? "" : matchAddr.split(",")[0];
                detail = matchAddr.equals("") ? "" : matchAddr.substring(matchAddr.indexOf(", ") + 2);
                break;
              }
            default:
              title = "";
              detail = matchAddr.equals("") ? "" : matchAddr;
              break;
          }

          HashMap<String, Object> attributes = new HashMap<>();
          attributes.put("title", title);
          attributes.put("detail", detail);

          // create the marker
          Graphic marker = new Graphic(geocode.getDisplayLocation(), attributes, pinSymbol);
          markers.add(marker);
        }

        // update the markers
        if (markers.size() > 0) {
          Platform.runLater(() -> {
            // clear out previous results
            graphicsOverlay.getGraphics().clear();
            searchBox.hide();

            // add the markers to the graphics overlay
            graphicsOverlay.getGraphics().addAll(markers);

            // listener to enable the redo-search button the first time the user moves away from the initial search area
            ViewpointChangedListener changedListener = new ViewpointChangedListener() {

              @Override
              public void viewpointChanged(ViewpointChangedEvent arg0) {

                redoButton.setDisable(false);
                mapView.removeViewpointChangedListener(this);
              }
            };

            // zoom to see all results and disable redo-search button
            ListenableFuture<Boolean> changeViewpoint = mapView.setViewpointGeometryAsync(graphicsOverlay.getExtent());
            changeViewpoint.addDoneListener(() -> {
              redoButton.setDisable(true);
              mapView.addViewpointChangedListener(changedListener);
            });
          });
        }

      } catch (InterruptedException | ExecutionException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Stops and releases all resources used in application.
   */
  @Override
  public void stop() throws Exception {

    // release resources when the application closes
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
