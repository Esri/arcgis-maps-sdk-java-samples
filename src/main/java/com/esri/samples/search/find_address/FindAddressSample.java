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

package com.esri.samples.search.find_address;

import java.util.HashMap;
import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.TileCache;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.layers.ArcGISTiledLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.Callout.LeaderPosition;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.IdentifyGraphicsOverlayResult;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.esri.arcgisruntime.tasks.geocode.GeocodeParameters;
import com.esri.arcgisruntime.tasks.geocode.GeocodeResult;
import com.esri.arcgisruntime.tasks.geocode.LocatorTask;
import com.esri.arcgisruntime.tasks.geocode.ReverseGeocodeParameters;

public class FindAddressSample extends Application {

  private MapView mapView;
  private ArcGISMap map;
  private ArcGISTiledLayer tiledLayer;
  private LocatorTask locatorTask;
  private GeocodeParameters geocodeParameters;
  private ReverseGeocodeParameters reverseGeocodeParameters;
  private GraphicsOverlay graphicsOverlay;
  private PictureMarkerSymbol pinSymbol;
  private ComboBox<String> searchBox;
  private ListenableFuture<IdentifyGraphicsOverlayResult> identifyResults;
  private boolean realtimeMode = false;

  @Override
  public void start(Stage stage) throws Exception {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);

      // set title, size, and add scene to stage
      stage.setTitle("Find Address Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // create search box
      searchBox = new ComboBox<>();
      searchBox.setPromptText("Search");
      searchBox.setEditable(true);
      searchBox.setMaxWidth(260.0);

      // add example address suggestions
      String[] recent = {
          "1455 Market St, San Francisco, CA 94103", "2011 Mission St, San Francisco  CA  94110",
          "820 Bryant St, San Francisco  CA  94103", "1 Zoo Rd, San Francisco, 944132",
          "1201 Mason Street, San Francisco, CA 94108", "151 Third Street, San Francisco, CA 94103",
          "1050 Lombard Street, San Francisco, CA 94109"
      };
      searchBox.getItems().addAll(recent);

      // event to get geocode when query is submitted
      searchBox.setOnAction((e) -> {

        // get the user's query
        String query = "";
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

          // run the locatorTask geocode task
          ListenableFuture<List<GeocodeResult>> results = locatorTask.geocodeAsync(query, geocodeParameters);

          // add a listener to display the result when loaded
          results.addDoneListener(new ResultsLoadedListener(results));
        }
      });

      // create a basemap from a local tile package
      TileCache tileCache = new TileCache(System.getProperty("user.dir") +
          "/samples-data/sanfrancisco/SanFrancisco.tpk");
      tiledLayer = new ArcGISTiledLayer(tileCache);
      Basemap basemap = new Basemap(tiledLayer);

      // create ArcGISMap with imagery basemap
      map = new ArcGISMap(basemap);

      // create a view and set ArcGISMap to it
      mapView = new MapView();
      mapView.setMap(map);

      // add a graphics overlay
      graphicsOverlay = new GraphicsOverlay();
      graphicsOverlay.setSelectionColor(0xFF00FFFF);
      mapView.getGraphicsOverlays().add(graphicsOverlay);

      // create a pin graphic
      Image img = new Image(getClass().getResourceAsStream("/symbols/pin.png"), 0, 80, true, true);
      pinSymbol = new PictureMarkerSymbol(img);
      pinSymbol.loadAsync();

      tiledLayer.addDoneLoadingListener(() -> {
        if (tiledLayer.getLoadStatus() == LoadStatus.LOADED) {
          Platform.runLater(() -> {
            // center the view over the tiled layer's extent
            mapView.setViewpointGeometryAsync(tiledLayer.getFullExtent());
          });
        } else {
          Alert alert = new Alert(Alert.AlertType.ERROR, "Tiled Layer Failed to Load!");
          alert.show();
        }
      });

      // set the callout's default style
      Callout callout = mapView.getCallout();
      callout.setLeaderPosition(LeaderPosition.BOTTOM);

      // create a locator task
      locatorTask = new LocatorTask(System.getProperty("user.dir") +
          "/samples-data/sanfrancisco/SanFranciscoLocator.loc");

      // set geocode task parameters
      geocodeParameters = new GeocodeParameters();
      geocodeParameters.getResultAttributeNames().add("*"); // return all attributes
      geocodeParameters.setMaxResults(1); // get closest match

      // set reverse geocode task parameters
      reverseGeocodeParameters = new ReverseGeocodeParameters();
      reverseGeocodeParameters.getResultAttributeNames().add("*"); // return all
                                                                  // attributes
      reverseGeocodeParameters.setOutputSpatialReference(mapView.getSpatialReference());

      // create mouse moved event handler
      MouseMovedHandler handler = new MouseMovedHandler();

      // start or stop real-time geocoding mode
      mapView.setOnMouseClicked(event -> {

        if (event.isStillSincePress() && event.getButton() == MouseButton.PRIMARY) {
          Point2D clickLocation = new Point2D(event.getX(), event.getY());
          Point point = mapView.screenToLocation(clickLocation);

          if (realtimeMode) {
            // disable the real-time geocoding mode
            realtimeMode = false;
            // clear graphic selections
            graphicsOverlay.clearSelection();
            // remove the mouse moved handler
            mapView.setOnMouseMoved(null);
          } else {
            // identify the selected graphic
            identifyResults = mapView.identifyGraphicsOverlayAsync(graphicsOverlay, clickLocation, 10, false);
            identifyResults.addDoneListener(() -> {
              try {
                List<Graphic> graphics = identifyResults.get().getGraphics();
                if (graphics.size() > 0) {
                  // set the graphic as selected
                  Graphic graphic = graphics.get(0);
                  graphic.setSelected(true);
                  // start real-time mode
                  realtimeMode = true;
                  // add a mouse move event handler to get real-time geocodes
                  mapView.setOnMouseMoved(handler);
                } else {
                  // reverse geocode the selected point
                  ListenableFuture<List<GeocodeResult>> results = locatorTask.reverseGeocodeAsync(point,
                      reverseGeocodeParameters);
                  results.addDoneListener(new ResultsLoadedListener(results));
                }
              } catch (Exception e) {
                e.printStackTrace();
              }
            });
          }
        }
      });

      // add map view and searchBox to stack pane
      stackPane.getChildren().addAll(mapView, searchBox);
      StackPane.setAlignment(searchBox, Pos.TOP_LEFT);
      StackPane.setMargin(searchBox, new Insets(10, 0, 0, 10));

    } catch (Exception e) {
      // on any error, print the stack trace
      e.printStackTrace();
    }
  }

  /**
   * Performs reverse geocode based on mouse location and display the result in
   * real-time.
   */
  private class MouseMovedHandler implements EventHandler<MouseEvent> {

    @Override
    public void handle(MouseEvent event) {

      // get the mouse location coordinates
      Point point = mapView.screenToLocation(new Point2D(event.getX(), event.getY()));

      // disable the move event listener to reduce unnecessary geocode calls
      mapView.setOnMouseMoved(null);

      // run the locator task
      ListenableFuture<List<GeocodeResult>> results = locatorTask.reverseGeocodeAsync(point, reverseGeocodeParameters);
      results.addDoneListener(() -> {
        try {
          // get the geocode from the result
          List<GeocodeResult> geocodes = results.get();
          GeocodeResult geocode = geocodes.get(0);

          // update the marker's position
          graphicsOverlay.getGraphics().get(0).setGeometry(geocode.getDisplayLocation());

          // format result's attributes for callout
          String street = geocode.getAttributes().get("Street").toString();
          String city = geocode.getAttributes().get("City").toString();
          String state = geocode.getAttributes().get("State").toString();
          String zip = geocode.getAttributes().get("ZIP").toString();

          // update the callout
          Platform.runLater(() -> {
            Callout callout = mapView.getCallout();
            callout.setTitle(street);
            callout.setDetail(city + ", " + state + " " + zip);
            callout.showCalloutAt(geocode.getDisplayLocation(), new Point2D(0, -24), Duration.ZERO);
          });

        } catch (Exception e) {
          // mouse is out of bounds
        }
        // re-enable the mouse move listener
        mapView.setOnMouseMoved(this);
      });
    }
  }

  /**
   * Updates marker and callout when new results are loaded.
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

      try {
        List<GeocodeResult> geocodes = results.get();
        if (geocodes.size() > 0) {
          // get the top result
          GeocodeResult geocode = geocodes.get(0);

          // set the viewpoint to the marker
          Point location = geocode.getDisplayLocation();
          mapView.setViewpointCenterAsync(location, 10000);

          // get attributes from the result for the callout
          String title;
          String detail;
          Object matchAddr = geocode.getAttributes().get("Match_addr");
          if (matchAddr != null) {
            // attributes from a query-based search
            title = matchAddr.toString().split(",")[0];
            detail = matchAddr.toString().substring(matchAddr.toString().indexOf(",") + 1);
          } else {
            // attributes from a click-based search
            String street = geocode.getAttributes().get("Street").toString();
            String city = geocode.getAttributes().get("City").toString();
            String state = geocode.getAttributes().get("State").toString();
            String zip = geocode.getAttributes().get("ZIP").toString();
            title = street;
            detail = city + ", " + state + " " + zip;
          }

          // get attributes from the result for the callout
          HashMap<String, Object> attributes = new HashMap<>();
          attributes.put("title", title);
          attributes.put("detail", detail);

          // create the marker
          Graphic marker = new Graphic(geocode.getDisplayLocation(), attributes, pinSymbol);

          // update the callout
          Platform.runLater(() -> {
            // clear out previous results
            graphicsOverlay.getGraphics().clear();

            // add the markers to the graphics overlay
            graphicsOverlay.getGraphics().add(marker);

            // display the callout
            Callout callout = mapView.getCallout();
            callout.setTitle(marker.getAttributes().get("title").toString());
            callout.setDetail(marker.getAttributes().get("detail").toString());
            callout.showCalloutAt(location, new Point2D(0, -24), Duration.ZERO);
          });
        }

      } catch (Exception e) {
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
