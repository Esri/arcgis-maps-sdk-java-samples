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

package com.esri.samples.display_route_layer;


import com.esri.arcgisruntime.data.*;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.FeatureCollectionLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.SceneView;
import com.esri.arcgisruntime.portal.Portal;
import com.esri.arcgisruntime.portal.PortalItem;
import com.esri.arcgisruntime.symbology.SymbolStyle;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.application.Platform;

import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.view.MapView;
import javafx.scene.text.TextAlignment;
import javafx.collections.ObservableList;


import java.util.ArrayList;
import java.util.List;

public class DisplayRouteLayerSample extends Application {

    private MapView mapView;
    private Portal portal;
    private TextField inputTextField;
    private PortalItem portalItem; // keep loadable in scope to avoid garbage collection
    private FeatureCollection featureCollection;
    private VBox vBox;
    private GridPane legendGridPane;
    private SymbolStyle symbolStyle;


    @Override
    public void start(Stage stage) {

        try {
            // create stack pane and application scene
            StackPane stackPane = new StackPane();
            Scene scene = new Scene(stackPane);

            // set title, size, and add scene to stage
            stage.setTitle("Display Route Layer Sample");
            stage.setWidth(800);
            stage.setHeight(700);
            stage.setScene(scene);
            stage.show();

            // authentication with an API key or named user is required to access basemap and other location services
            String yourAPIKey = System.getProperty("apiKey");
            ArcGISRuntimeEnvironment.setApiKey(yourAPIKey);

            // create a map with the topographical basemap style
            ArcGISMap map = new ArcGISMap(BasemapStyle.ARCGIS_TOPOGRAPHIC);

            // create a map view and set the map to it
            mapView = new MapView();
            mapView.setMap(map);

            // set a viewpoint on the map view
            mapView.setViewpoint(new Viewpoint(45.2281, -122.8309, 57e4));

            // create text field to input user's own portal item ID
            inputTextField = new TextField("0e3c8e86b4544274b45ecb61c9f41336");
            inputTextField.setMaxWidth(250);

            //Create a VBox
            vBox = new VBox();
            vBox.setMaxSize(250, 700);
            vBox.setPadding(new Insets(10.0));
            vBox.setAlignment(Pos.TOP_CENTER);
            vBox.setSpacing(10);
            //vBox.setStyle("-fx-background-color: white;");
            vBox.setBackground(new Background(new BackgroundFill(Paint.valueOf("rgba(255, 255, 255 , 0.7)"), CornerRadii.EMPTY,
                    Insets.EMPTY)));

            // create portal and portalItem
            portal = new Portal("https://www.arcgis.com/");

            portalItem = new PortalItem(portal, inputTextField.getText());
            //var portalItem = new PortalItem(portal, "0e3c8e86b4544274b45ecb61c9f41336");

            portalItem.loadAsync();
            portalItem.addDoneLoadingListener(() -> {

                if (portalItem.getLoadStatus() == LoadStatus.LOADED) {
                    var featureCollection = new FeatureCollection(portalItem);

                    // Create a feature collection layer using the feature collection.
                    FeatureCollectionLayer featureCollectionLayer = new FeatureCollectionLayer(featureCollection);
                    // add the feature collection layer to the map's operational layers
                    mapView.getMap().getOperationalLayers().add(featureCollectionLayer);

                    featureCollectionLayer.addDoneLoadingListener(() -> {
                        if (featureCollection.getLoadStatus() == LoadStatus.LOADED) {
                            for (FeatureCollectionTable table : featureCollection.getTables()) {
                                if (table.getTitle().equals("DirectionPoints")) {
                                    for (Feature feature : table) {
                                        String text = (String) feature.getAttributes().get("DisplayText");
                                        //Creating a Label
                                        Label label = new Label(text);
                                        //wrapping the label
                                        label.setWrapText(true);
                                        //Setting the alignment to the label
                                        label.setTextAlignment(TextAlignment.LEFT);
                                        //Setting the maximum width of the label
                                        label.setMaxWidth(200);
                                        //Setting the position of the label
                                        label.setTranslateX(5);
                                        label.setTranslateY(5);


                                        // create a scroll pane to contain the legend
                                        //ScrollPane scrollPane = new ScrollPane();
                                        //scrollPane.setPrefSize(595, 200);
                                        //scrollPane.setContent(label);
                                        Platform.runLater(() -> vBox.getChildren().add(label));
                                        //Platform.runLater(() ->list.setItems(items));
                                    }
                                }
                            }
                        }
                    });
                }
            });

//          ScrollPane scrollPane = new ScrollPane();
//          scrollPane.setPrefSize(595, 200);
//          scrollPane.setContent(label);


            // add the map view to the stack pane
            stackPane.getChildren().addAll(mapView, vBox);
            StackPane.setAlignment(vBox, Pos.TOP_RIGHT);
            StackPane.setMargin(vBox, new Insets(10, 10, 0, 0));

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
