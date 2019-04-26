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

package com.esri.samples.scene.get_elevation_at_a_point;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.ArcGISTiledElevationSource;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Surface;
import com.esri.arcgisruntime.mapping.view.Camera;
import com.esri.arcgisruntime.mapping.view.SceneView;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;

public class GetElevationAtAPointSample extends Application {

    private SceneView sceneView;
    private static final String ELEVATION_IMAGE_SERVICE =
            "http://elevation3d.arcgis.com/arcgis/rest/services/WorldElevation3D/Terrain3D/ImageServer";

    @Override
    public void start(Stage stage){

        try {

            // create stack pane and JavaFX app scene
            StackPane stackPane = new StackPane();
            Scene fxScene = new Scene(stackPane);

            // set title, size, and add JavaFX scene to stage
            stage.setTitle("Get Elevation At A Point Sample");
            stage.setWidth(800);
            stage.setHeight(700);
            stage.setScene(fxScene);
            stage.show();

            // create a scene and add a basemap to it
            ArcGISScene scene = new ArcGISScene();
            scene.setBasemap(Basemap.createImagery());

            // add the SceneView to the stack pane
            sceneView = new SceneView();
            sceneView.setArcGISScene(scene);
            stackPane.getChildren().addAll(sceneView);

            // add base surface for elevation data
            Surface surface = new Surface();
            surface.getElevationSources().add(new ArcGISTiledElevationSource("http://elevation3d.arcgis.com/arcgis/rest/services/WorldElevation3D/Terrain3D/ImageServer"));
            scene.setBaseSurface(surface);

            // create a point symbol to mark where elevation is being measured
            SimpleMarkerSymbol circleSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, 0xFFFF0000, 10);

            // create a graphics overlay
            GraphicsOverlay graphicsOverlay = new GraphicsOverlay(GraphicsOverlay.RenderingMode.DYNAMIC);
            sceneView.getGraphicsOverlays().add(graphicsOverlay);

            // add a camera and initial camera position
            Camera camera = new Camera(28.42, 83.9, 10000.0, 10.0, 80.0, 0.0);
            sceneView.setViewpointCamera(camera);

            // create listener to handle clicked
            sceneView.setOnMouseClicked(event -> {

                // clear any existing graphics from the graphics overlay
                graphicsOverlay.getGraphics().clear();

                // get the clicked scene point
                Point2D screenpoint = new Point2D(event.getX(), event.getY());

                // convert the screen point to a point on the surface
                Point surfacePoint = sceneView.screenToBaseSurface(screenpoint);

                

            });
        }

        catch (Exception e){
            // on any error, display the stack trace.
            e.printStackTrace();
        }

    }


    /**
     * Stops and releases all resources used in application.
     */
    @Override
    public void stop() {

        if (sceneView != null) {
            sceneView.dispose();
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
