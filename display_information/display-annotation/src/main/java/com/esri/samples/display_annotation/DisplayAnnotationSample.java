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

package com.esri.samples.display_annotation;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.layers.AnnotationLayer;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.MapView;

public class DisplayAnnotationSample extends Application {

    private MapView mapView;

    @Override
    public void start(Stage stage) {
        try {
            // create stack pane and application scene
            StackPane stackPane = new StackPane();
            Scene scene = new Scene(stackPane);

            // set title, size, and add scene to stage
            stage.setTitle("Display Annotation Sample");
            stage.setWidth(800);
            stage.setHeight(700);
            stage.setScene(scene);
            stage.show();

            // create a map
            ArcGISMap map = new ArcGISMap(Basemap.Type.LIGHT_GRAY_CANVAS_VECTOR, 55.882436, -2.725610, 13);

            // create a map view and set the map to it
            mapView = new MapView();
            mapView.setMap(map);

            // create a feature layer from a feature service
            FeatureLayer riverFeatureLayer = new FeatureLayer(new ServiceFeatureTable("https://services1.arcgis.com/6677msI40mnLuuLr/arcgis/rest/services/East_Lothian_Rivers/FeatureServer/0"));

            // add the feature layer to the map
            map.getOperationalLayers().add(riverFeatureLayer);

            // create an annotation layer from a feature service
            AnnotationLayer annotationLayer = new AnnotationLayer("https://sampleserver6.arcgisonline.com/arcgis/rest/services/RiversAnnotation/FeatureServer/0");

            // add the annotation layer to the map
            map.getOperationalLayers().add(annotationLayer);

            // add a done loading listener, with a runnable that gets triggered asynchronously when the feature layer has loaded
            // check for the load status of the layer and if it hasn't loaded, report an error
            riverFeatureLayer.addDoneLoadingListener(() -> {
                if (riverFeatureLayer.getLoadStatus() != LoadStatus.LOADED) {
                    new Alert(Alert.AlertType.ERROR, "Error loading Feature Layer.").show();
                }
            });

            // add a done loading listener, with a runnable that gets triggered asynchronously when the feature layer has loaded
            // check for the load status of the layer and if it hasn't loaded, report an error
            annotationLayer.addDoneLoadingListener(()->{
                if (annotationLayer.getLoadStatus() != LoadStatus.LOADED) {
                    new Alert(Alert.AlertType.ERROR, "Error loading Annotation Layer.").show();
                }
            });

            // add the map view to stack pane
            stackPane.getChildren().add(mapView);

        } catch (Exception e) {
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
