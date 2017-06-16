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

package com.esri.samples.search.geocode_online;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.Callout.LeaderPosition;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.esri.arcgisruntime.tasks.geocode.GeocodeParameters;
import com.esri.arcgisruntime.tasks.geocode.GeocodeResult;
import com.esri.arcgisruntime.tasks.geocode.LocatorTask;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class GeocodeOnlineSample extends Application {

  private MapView mapView;
  private LocatorTask locatorTask;
  private GraphicsOverlay graphicsOverlay;
  private PictureMarkerSymbol pinSymbol;
  private ComboBox<String> searchBox;

  @Override
  public void start(Stage stage) throws Exception {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);

      // set title, size, and add scene to stage
      stage.setTitle("Geocode Online Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // create search box
      searchBox = new ComboBox<>();
      searchBox.setPromptText("Search");
      searchBox.setEditable(true);
      searchBox.setMaxWidth(260.0);

      // add example locations
      String[] examples = {
          "277 N Avenida Caballeros, Palm Springs, CA", "380 New York St, Redlands, CA 92373"
      };
      searchBox.getItems().addAll(examples);

      // create ArcGISMap with imagery basemap
      ArcGISMap map = new ArcGISMap(Basemap.Type.IMAGERY, 48.354406, -99.998267, 2);

      // create a view and set ArcGISMap to it
      mapView = new MapView();
      mapView.setMap(map);

      // add a graphics overlay
      graphicsOverlay = new GraphicsOverlay();
      mapView.getGraphicsOverlays().add(graphicsOverlay);

      // set the callout's default style
      Callout callout = mapView.getCallout();
      callout.setLeaderPosition(LeaderPosition.BOTTOM);

      // create a locatorTask
      locatorTask = new LocatorTask("http://geocode.arcgis.com/arcgis/rest/services/World/GeocodeServer");

      // create geocode task parameters
      GeocodeParameters geocodeParameters = new GeocodeParameters();
      // return all attributes
      geocodeParameters.getResultAttributeNames().add("*");
      geocodeParameters.setMaxResults(1); // get closest match
      geocodeParameters.setOutputSpatialReference(mapView.getSpatialReference());

      // create a pin graphic
      Image img = new Image(getClass().getResourceAsStream("/symbols/pin.png"), 0, 80, true, true);
      pinSymbol = new PictureMarkerSymbol(img);
      pinSymbol.loadAsync();

      // event to get geocode when query is submitted
      searchBox.setOnAction((ActionEvent evt) -> {

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

      // add map view and control panel to stack pane
      stackPane.getChildren().addAll(mapView, searchBox);
      StackPane.setAlignment(searchBox, Pos.TOP_LEFT);
      StackPane.setMargin(searchBox, new Insets(10, 0, 0, 10));

    } catch (Exception e) {
      // on any error, print the stack trace
      e.printStackTrace();
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

      try {
        List<GeocodeResult> geocodes = results.get();
        if (geocodes.size() > 0) {
          // get the top result
          GeocodeResult geocode = geocodes.get(0);

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
          }

          HashMap<String, Object> attributes = new HashMap<>();
          attributes.put("title", title);
          attributes.put("detail", detail);

          // create the marker
          Graphic marker = new Graphic(geocode.getDisplayLocation(), attributes, pinSymbol);

          // set the viewpoint to the marker
          Point location = geocodes.get(0).getDisplayLocation();
          mapView.setViewpointCenterAsync(location, 10000);

          // update the marker
          Platform.runLater(() -> {
            // clear out previous results
            graphicsOverlay.getGraphics().clear();
            searchBox.hide();

            // add the marker to the graphics overlay
            graphicsOverlay.getGraphics().add(marker);

            // display the callout
            Callout callout = mapView.getCallout();
            callout.setTitle(marker.getAttributes().get("title").toString());
            callout.setDetail(marker.getAttributes().get("detail").toString());
            callout.showCalloutAt(location, new Point2D(0, -24), Duration.ZERO);
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
