/*
 * Copyright 2018 Esri.
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

package com.esri.samples.map.mobile_map_search_and_route;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.TransportationNetworkDataset;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.MobileMapPackage;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.LineSymbol;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.tasks.geocode.GeocodeResult;
import com.esri.arcgisruntime.tasks.geocode.ReverseGeocodeParameters;
import com.esri.arcgisruntime.tasks.networkanalysis.Route;
import com.esri.arcgisruntime.tasks.networkanalysis.RouteParameters;
import com.esri.arcgisruntime.tasks.networkanalysis.RouteResult;
import com.esri.arcgisruntime.tasks.networkanalysis.RouteTask;
import com.esri.arcgisruntime.tasks.networkanalysis.Stop;

public class MobileMapSearchAndRouteSample extends Application {

  private MapView mapView;
  private MobileMapPackage mobileMapPackage;

  @Override
  public void start(Stage stage) {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);

      // set title, size, and add scene to stage
      stage.setTitle("Mobile Map Search and Route Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // create a map view
      mapView = new MapView();

      // create a list view to list the maps in a mobile map package
      ListView<ArcGISMap> mapPackageListView = new ListView<>();
      mapPackageListView.setMaxSize(170, 100);

      // create a file chooser to find local mmpk files
      FileChooser fileChooser = new FileChooser();
      fileChooser.setInitialDirectory(new File("./samples-data/mmpk"));
      fileChooser.setInitialFileName(fileChooser.getInitialDirectory().getAbsolutePath() + "/SanFrancisco.mpk");
      FileChooser.ExtensionFilter mpkFilter = new FileChooser.ExtensionFilter("Map Packages (*.mmpk)", "*.mmpk");
      fileChooser.getExtensionFilters().add(mpkFilter);

      // click a button to open the file chooser
      Button findMmpkButton = new Button("Open mobile map package");
      findMmpkButton.setOnAction(e -> {
        File selectedMmpk = fileChooser.showOpenDialog(mapView.getScene().getWindow());
        if (selectedMmpk != null) {
          // remember the directory of the mmpk for next time
          fileChooser.setInitialDirectory(selectedMmpk.getParentFile());
          fileChooser.setInitialFileName(selectedMmpk.getAbsolutePath());

          // clear the map and list selection from any previous mmpks
          mapPackageListView.getSelectionModel().clearSelection();
          mapPackageListView.getItems().clear();
          mapView.setMap(null);

          // create a mobile map package from the file path and load it
          mobileMapPackage = new MobileMapPackage(selectedMmpk.getAbsolutePath());
          mobileMapPackage.loadAsync();
          mobileMapPackage.addDoneLoadingListener(() -> {
            if (mobileMapPackage.getLoadStatus() == LoadStatus.LOADED && !mobileMapPackage.getMaps().isEmpty()) {
              // show the maps belonging to the mobile map package in the list view
              mobileMapPackage.getMaps().forEach(map -> mapPackageListView.getItems().add(map));
              // default to displaying the first map in the map view
              mapPackageListView.getSelectionModel().select(mobileMapPackage.getMaps().get(0));
            } else {
              new Alert(Alert.AlertType.ERROR, "Failed to load the mobile map package").show();
            }
          });
        }
      });

      // create a custom list item for each of the map package's maps
      mapPackageListView.setCellFactory(list -> new ListCell<ArcGISMap>() {

        @Override
        protected void updateItem(ArcGISMap map, boolean bln) {
          super.updateItem(map, bln);
          if (map != null) {
            HBox hBox = new HBox();
            hBox.setMinWidth(100);

            // show the mobile map package thumbnail image
            ImageView thumbnailImageView = new ImageView();
            hBox.getChildren().add(thumbnailImageView);
            thumbnailImageView.setFitHeight(20);
            thumbnailImageView.setPreserveRatio(true);
            ListenableFuture<byte[]> thumbnailData = mobileMapPackage.getItem().fetchThumbnailAsync();
            thumbnailData.addDoneListener(() -> {
              try {
                thumbnailImageView.setImage(new Image(new ByteArrayInputStream(thumbnailData.get())));
              } catch (Exception ex) {
                ex.printStackTrace();
              }
            });

            // show a location pin symbol in the list item if the map package includes a locator task
            if (mobileMapPackage.getLocatorTask() != null) {
              ImageView locatorImageView = new ImageView();
              locatorImageView.setFitHeight(20);
              locatorImageView.setPreserveRatio(true);
              locatorImageView.setImage(new Image(getClass().getResourceAsStream("/icons/pinOutlineSymbol.png")));
              hBox.getChildren().add(locatorImageView);
            }

            // show a routing symbol in the list item if the map has transportation networks
            if (!map.getTransportationNetworks().isEmpty()) {
              ImageView routingImageView = new ImageView();
              routingImageView.setFitHeight(20);
              routingImageView.setPreserveRatio(true);
              routingImageView.setImage(new Image(getClass().getResourceAsStream("/icons/routingSymbol.png")));
              hBox.getChildren().add(routingImageView);
            }
            setGraphic(hBox);

            // set the list cell's text to the map's index
            setText("Map " + this.getIndex());
          } else {
            setGraphic(null);
            setText(null);
          }
        }
      });

      // create a graphics overlay for showing geocoded locations (for when the mobile map package has a locator task)
      GraphicsOverlay locationsGraphicsOverlay = new GraphicsOverlay();
      mapView.getGraphicsOverlays().add(locationsGraphicsOverlay);
      Image img = new Image(getClass().getResourceAsStream("/symbols/pin.png"), 0, 80, true, true);
      PictureMarkerSymbol pinSymbol = new PictureMarkerSymbol(img);
      pinSymbol.loadAsync();

      // create a graphics overlay for showing routes (for when the map has transportation network datasets)
      GraphicsOverlay routesGraphicsOverlay = new GraphicsOverlay();
      mapView.getGraphicsOverlays().add(routesGraphicsOverlay);
      LineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0xFF0000FF, 3);

      // switch the map in the map view to the one selected in the list view
      mapPackageListView.getSelectionModel().selectedItemProperty().addListener(o -> {
        ArcGISMap selectedMap = mapPackageListView.getSelectionModel().getSelectedItem();
        if (selectedMap != null) {
          mapView.setMap(selectedMap);
          // clear any previous locations/routes
          locationsGraphicsOverlay.getGraphics().clear();
          routesGraphicsOverlay.getGraphics().clear();
          mapView.getCallout().setVisible(false);
        }
      });

      // perform a reverse geocode where the user clicks on the map view if the mobile map package has a locator task
      mapView.setOnMouseClicked(e -> {
        if (e.isStillSincePress() && e.getButton() == MouseButton.PRIMARY && mobileMapPackage.getLocatorTask() != null) {
          Point2D point = new Point2D(e.getX(), e.getY());
          Point mapPoint = mapView.screenToLocation(point);

          // perform a reverse geocode at the clicked location
          ReverseGeocodeParameters reverseGeocodeParameters = new ReverseGeocodeParameters();
          reverseGeocodeParameters.setMaxResults(1);
          ListenableFuture<List<GeocodeResult>> results = mobileMapPackage.getLocatorTask().reverseGeocodeAsync(mapPoint,
              reverseGeocodeParameters);
          results.addDoneListener(() -> {
            try {
              // show a pin graphic and a callout with the geocode's address
              List<GeocodeResult> geocodes = results.get();
              if (geocodes.size() > 0) {
                GeocodeResult geocode = geocodes.get(0);
                Point location = geocode.getDisplayLocation();

                // get attributes from the result for the callout
                String address = geocode.getAttributes().get("Match_addr").toString();
                HashMap<String, Object> attributes = new HashMap<>();
                attributes.put("title", address.split(",")[0]);
                attributes.put("detail", address.substring(address.indexOf(", ") + 2));

                // create a marker for the location
                Graphic marker = new Graphic(geocode.getDisplayLocation(), attributes, pinSymbol);
                locationsGraphicsOverlay.getGraphics().add(marker);

                // display the callout at the location
                Callout callout = mapView.getCallout();
                callout.setTitle(marker.getAttributes().get("title").toString());
                callout.setDetail(marker.getAttributes().get("detail").toString());
                callout.setLeaderPosition(Callout.LeaderPosition.BOTTOM);
                callout.showCalloutAt(location, new Point2D(0, -24), Duration.ZERO);

                // if the map has transportation network datasets, solve for a route between the last two locations
                if (!mapView.getMap().getTransportationNetworks().isEmpty() && locationsGraphicsOverlay.getGraphics().size() > 1) {
                  // use the first transportation network dataset in the map to create a route task
                  TransportationNetworkDataset networkDataset = mapView.getMap().getTransportationNetworks().get(0);
                  RouteTask routeTask = new RouteTask(networkDataset);
                  // create route parameters with the last two locations' stops
                  List<Graphic> locationGraphics = locationsGraphicsOverlay.getGraphics();
                  List<Stop> stops = locationGraphics.subList(Math.max(locationGraphics.size() - 2, 0), locationGraphics.size())
                      .stream()
                      .map(g -> new Stop((Point) g.getGeometry()))
                      .collect(Collectors.toList());
                  RouteParameters routeParameters = routeTask.createDefaultParametersAsync().get();
                  routeParameters.setStops(stops);
                  // solve the route and display the result graphic
                  ListenableFuture<RouteResult> routeResults = routeTask.solveRouteAsync(routeParameters);
                  routeResults.addDoneListener(() -> {
                    try {
                      RouteResult result = routeResults.get();
                      Route route = result.getRoutes().get(0);
                      Graphic routeGraphic = new Graphic(route.getRouteGeometry(), lineSymbol);
                      routesGraphicsOverlay.getGraphics().add(routeGraphic);
                    } catch (InterruptedException | ExecutionException ex) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.initOwner(mapView.getScene().getWindow());
                        alert.setHeaderText(null);
                        alert.setContentText("No path found between stops");
                        alert.show();
                    }
                  });
                }
              }
            } catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.initOwner(mapView.getScene().getWindow());
                alert.setHeaderText(null);
                alert.setContentText("No address found at this location");
                alert.show();
            }
          });
        }
      });

      // add the map view, button, and list view to the stack pane
      stackPane.getChildren().addAll(mapView, findMmpkButton, mapPackageListView);
      StackPane.setAlignment(findMmpkButton, Pos.TOP_LEFT);
      StackPane.setAlignment(mapPackageListView, Pos.TOP_RIGHT);
      StackPane.setMargin(findMmpkButton, new Insets(10));
      StackPane.setMargin(mapPackageListView, new Insets(10));
    } catch (Exception e) {
      // on any error, display the stack trace.
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
