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

package com.esri.samples.identify_raster_cell;

import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.layers.RasterLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.GeoElement;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.IdentifyLayerResult;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.raster.Raster;
import com.esri.arcgisruntime.raster.RasterCell;

public class IdentifyRasterCellSample extends Application {

    private MapView mapView;
    private RasterLayer rasterLayer;

    @Override
    public void start(Stage stage) {

        try {
            // create stack pane and application scene
            StackPane stackPane = new StackPane();
            Scene scene = new Scene(stackPane);

            // set title, size, and add scene to stage
            stage.setTitle("Identify Raster Cell Sample");
            stage.setWidth(800);
            stage.setHeight(700);
            stage.setScene(scene);
            stage.show();

            // create a map view
            mapView = new MapView();

            // create a Map with imagery basemap
            ArcGISMap map = new ArcGISMap(Basemap.createImagery());

            // add the map to a map view
            mapView.setMap(map);

            // create a raster from a local raster file
            Raster raster = new Raster(new File(System.getProperty("data.dir"), "./samples-data/raster/Shasta_Elevation.tif").getAbsolutePath());

            // create a raster layer
            rasterLayer = new RasterLayer(raster);

            // add the raster as an operational layer
            map.getOperationalLayers().add(rasterLayer);

            // set viewpoint on the raster
            rasterLayer.addDoneLoadingListener(() -> {
                if (map.getLoadStatus() == LoadStatus.LOADED) {
                    mapView.setViewpointGeometryAsync(rasterLayer.getFullExtent(), 150);
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Raster Layer Failed to Load!");
                    alert.show();
                }
            });

            mapView.setOnMouseClicked(mouseEvent -> {
                if (mouseEvent.getButton() == MouseButton.PRIMARY && mouseEvent.isStillSincePress()) {
                    try {
                        // get the map point where the user clicked
                        Point2D point = new Point2D(mouseEvent.getX(), mouseEvent.getY());
                        Point mapPoint = mapView.screenToLocation(point);

                        // identify the layers at the clicked location
                        ListenableFuture<IdentifyLayerResult> identifyLayerResultFuture
                                = mapView.identifyLayerAsync(rasterLayer, point, 10, false, 1);

                        identifyLayerResultFuture.addDoneListener(() -> {
                            try {
                                // get the result of the query
                                IdentifyLayerResult identifyLayerResult = identifyLayerResultFuture.get();

                                // Get the read only list of geo-elements (they contain RasterCell's)
                                List<GeoElement> geoElements = identifyLayerResult.getElements();

                                // Create a StringBuilder to display information to the user
                                StringBuilder stringBuilder = new StringBuilder();

                                // Loop through each RasterCell
                                for (GeoElement geoElement : geoElements) {

                                    if (geoElement instanceof RasterCell) {
                                        RasterCell rasterCell = (RasterCell) geoElement;

                                        // Loop through the attributes (key/value pairs)
                                        rasterCell.getAttributes().forEach((key, value) -> {
                                            // Add the key/value pair to the string builder
                                            stringBuilder.append(key).append(": ").append(value).append("\n");
                                        });

                                        // get and format the X and Y values for the cell
                                        double x = rasterCell.getGeometry().getExtent().getXMin();
                                        double y = rasterCell.getGeometry().getExtent().getYMin();
                                        String string = "X: " + Math.round(x) + " Y: " + Math.round(y);

                                        // add the X & Y coordinates where the user clicked raster cell to the string builder
                                        stringBuilder.append(string);

                                        // Define a callout based on the string builder
                                        Callout callout = mapView.getCallout();
                                        callout.setDetail(stringBuilder.toString());
                                        callout.showCalloutAt(mapPoint);
                                    }
                                }
                            } catch (InterruptedException | ExecutionException ex) {
                                ex.printStackTrace();
                            }
                        });


                    } catch (Exception e) {

                    }
                }
            });

            // add the map view to stack pane
            stackPane.getChildren().addAll(mapView);
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
