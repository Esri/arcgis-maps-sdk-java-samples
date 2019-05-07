/*
 * Copyright 2019 Esri.
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

package com.esri.samples.na.closest_facility_static;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.FeatureTable;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.esri.arcgisruntime.tasks.networkanalysis.ClosestFacilityTask;
import com.esri.arcgisruntime.util.ListenableList;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.Arrays;

public class ClosestFacilityStaticSample extends Application {

    private MapView mapView;

    @Override
    public void start(Stage stage) throws Exception {

        try {
            // create stack pane and application scene
            StackPane stackPane = new StackPane();
            Scene scene = new Scene(stackPane);

            // set title, size, and add scene to stage
            stage.setTitle("Closest Facility (Static) Sample");
            stage.setWidth(800);
            stage.setHeight(700);
            stage.setScene(scene);
            stage.show();

            // create a ArcGISMap with a Basemap instance with an Imagery base layer
            ArcGISMap map = new ArcGISMap(Basemap.createStreetsWithReliefVector());

            // set the map to be displayed in this view
            mapView = new MapView();
            mapView.setMap(map);

            // create Symbols for displaying facilities
            PictureMarkerSymbol facilitySymbol = new PictureMarkerSymbol("https://static.arcgis.com/images/Symbols/SafetyHealth/FireStation.png");
            PictureMarkerSymbol incidentSymbol = new PictureMarkerSymbol("https://static.arcgis.com/images/Symbols/SafetyHealth/esriCrimeMarker_56_Gradient.png");


            // create a ClosestFacilityTask
            ClosestFacilityTask closestFacilityTask = new ClosestFacilityTask("https://sampleserver6.arcgisonline.com/arcgis/rest/services/NetworkAnalysis/SanDiego/NAServer/ClosestFacility");

            // Create a table for facilities using the FeatureServer.
            FeatureTable facilitiesFeatureTable = new ServiceFeatureTable("https://services2.arcgis.com/ZQgQTuoyBrtmoGdP/ArcGIS/rest/services/San_Diego_Facilities/FeatureServer/0");

            // Create a feature layer from the table.
            FeatureLayer facilitiesFeatureLayer = new FeatureLayer(facilitiesFeatureTable);

            // Create a table for incidents using the FeatureServer.
            FeatureTable incidentsFeatureTable = new ServiceFeatureTable("https://services2.arcgis.com/ZQgQTuoyBrtmoGdP/ArcGIS/rest/services/San_Diego_Incidents/FeatureServer/0");

            // Create a feature layer from the table.
            FeatureLayer incidentsFeatureLayer = new FeatureLayer(incidentsFeatureTable);

            // Add the layers to the map.
            map.getOperationalLayers().addAll(Arrays.asList(facilitiesFeatureLayer, incidentsFeatureLayer));

            // Wait for both layers to load.
            facilitiesFeatureLayer.loadAsync();
            incidentsFeatureLayer.loadAsync();

            // TODO: Is there any possibility to combine the two Tasks and only use one listener?
            facilitiesFeatureLayer.addDoneLoadingListener(()->{
                incidentsFeatureLayer.addDoneLoadingListener(()->{
                    if (facilitiesFeatureLayer.getLoadStatus() == LoadStatus.LOADED && incidentsFeatureLayer.getLoadStatus() == LoadStatus.LOADED){



                        Envelope fullFeatureLayerExtent = GeometryEngine.combineExtents(facilitiesFeatureLayer.getFullExtent(), incidentsFeatureLayer.getFullExtent());
                        mapView.setViewpointGeometryAsync(fullFeatureLayerExtent, 90);
                    }
                });
            });



            // Zoom to the combined extent of both layers.
//            Envelope fullExtent = incidentsFeatureLayer.getFullExtent();
//            mapView.setViewpointGeometryAsync(fullExtent, 50);

            // Enable the solve button.






            stackPane.getChildren().add(mapView);
        } catch (Exception e){
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
