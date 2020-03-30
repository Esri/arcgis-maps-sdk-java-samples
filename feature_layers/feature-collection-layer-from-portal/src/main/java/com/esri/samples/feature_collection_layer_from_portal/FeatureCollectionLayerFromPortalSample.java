/*
 * Copyright 2020 Esri.
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

package com.esri.samples.feature_collection_layer_from_portal;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.application.Application;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.data.FeatureCollection;
import com.esri.arcgisruntime.layers.FeatureCollectionLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.portal.Portal;
import com.esri.arcgisruntime.portal.PortalItem;

public class FeatureCollectionLayerFromPortalSample extends Application {

    private MapView mapView;
    private ArcGISMap map;
    private Portal portal;
    private FeatureCollectionLayer featureCollectionLayer;
    private FeatureCollection featureCollection;
    private TextField input;

    @Override
    public void start(Stage stage) {
        // set up the application scene
        try {
            // create stack pane and application scene
            StackPane stackPane = new StackPane();
            Scene scene = new Scene(stackPane);

            // set title, size, and add scene to stage
            stage.setTitle("Feature Collection Layer From Portal");
            stage.setWidth(800);
            stage.setHeight(700);
            stage.setScene(scene);
            stage.show();


            // create a new ArcGISMap with an oceans Basemap
            map = new ArcGISMap(Basemap.createOceans());
            
            // create a map view and set the ArcGISMap to it
            mapView = new MapView();
            mapView.setMap(map);

            // create text field to input user's own portal item ID
            input = new TextField();
            input.setMaxWidth(250);
            input.setText("32798dfad17942858d5eef82ee802f0b");

            // create button to perform action
            Button fetchFromPortal = new Button("Open from portal item");
            fetchFromPortal.setMaxWidth(250);

             // verify the input and fetch the portal item
            fetchFromPortal.setOnAction(e -> {

                if (!"".equals(input.getText())) {
                    fetchFeatureCollectionFromPortal();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Portal Item ID is empty. Please enter a Portal Item ID.").show();
                }

            });

            fetchFeatureCollectionFromPortal();

            // add feature layer to ArcGISMap
            map.getOperationalLayers().add(featureCollectionLayer);

            // add the map view and control panel to stack pane
            stackPane.getChildren().addAll(mapView, input, fetchFromPortal);
            StackPane.setAlignment(input, Pos.TOP_LEFT);
            StackPane.setMargin(input, new Insets(10, 0, 0, 10));
            StackPane.setAlignment(fetchFromPortal, Pos.TOP_LEFT);
            StackPane.setMargin(fetchFromPortal, new Insets(40, 0, 0, 10));

        } catch (Exception e) {
            // on any error, display the stack trace
            e.printStackTrace();
        }
    }

    /**
     * Fetch feature collection from portal and load as feature layer.
     */
    private void fetchFeatureCollectionFromPortal() {

        // create portal and portal item
        portal = new Portal("https://www.arcgis.com/");
        PortalItem portalItem = new PortalItem(portal, input.getText());

        // create feature collection and add to the map as a layer
        featureCollection = new FeatureCollection(portalItem);
        featureCollectionLayer = new FeatureCollectionLayer(featureCollection);

        featureCollectionLayer.loadAsync();
        featureCollectionLayer.addDoneLoadingListener(() -> {
            if (portalItem.getType() != PortalItem.Type.FEATURE_COLLECTION) {
                new Alert(Alert.AlertType.ERROR, "This is not valid Feature Collection.").show();
            }

        });

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
