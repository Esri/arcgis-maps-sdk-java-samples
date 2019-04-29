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

import java.util.concurrent.ExecutionException;

import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.ArcGISTiledElevationSource;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.SceneView;
import com.esri.arcgisruntime.mapping.view.Camera;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.Surface;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;

public class GetElevationAtAPointSample extends Application {

    private SceneView sceneView;

    @Override
    public void start(Stage stage) {

        try {
            // create stack pane and application scene
            StackPane stackPane = new StackPane();
            Scene fxScene = new Scene(stackPane);
            fxScene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

            // set title, size, and add JavaFX scene to stage
            stage.setTitle("Get Elevation At A Point Sample");
            stage.setWidth(800);
            stage.setHeight(700);
            stage.setScene(fxScene);
            stage.show();

            // create a scene and add a basemap to it
            ArcGISScene scene = new ArcGISScene();
            scene.setBasemap(Basemap.createImagery());

            // create a control panel
            VBox controlsVBox = new VBox(6);
            controlsVBox.setBackground(new Background(new BackgroundFill(Paint.valueOf("rgba(0,0,0,0.6)"), CornerRadii.EMPTY, Insets.EMPTY)));
            controlsVBox.setPadding(new Insets(10.0));
            controlsVBox.setMaxSize(220, 90);
            controlsVBox.getStyleClass().add("panel-region");

            // add the SceneView and controlsVBox to the stack pane
            sceneView = new SceneView();
            sceneView.setArcGISScene(scene);
            stackPane.getChildren().addAll(sceneView, controlsVBox);
            StackPane.setAlignment(controlsVBox, Pos.BOTTOM_CENTER);
            StackPane.setMargin(controlsVBox, new Insets(10, 0, 0, 10));

            // create label to display the elevation
            Label elevationLabel = new Label("Elevation:");
            elevationLabel.setFont(new Font(22));
            elevationLabel.getStyleClass().add("panel-label");

            // add label to the control panel
            controlsVBox.getChildren().add(elevationLabel);

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

                // get the clicked screenPoint
                Point2D screenPoint = new Point2D(event.getX(), event.getY());

                // convert the screen point to a point on the surface
                Point surfacePoint = sceneView.screenToBaseSurface(screenPoint);

                // check that the point is on the surface, and primary button was clicked
                if (surfacePoint != null && event.getButton() == MouseButton.PRIMARY && event.isStillSincePress()) {

                    // clear any existing graphics from the graphics overlay
                    graphicsOverlay.getGraphics().clear();

                    // create a new graphic at the surface point and add it to the graphics overlay
                    Graphic surfacePointGraphic = new Graphic(surfacePoint, circleSymbol);
                    graphicsOverlay.getGraphics().add(surfacePointGraphic);

                    // get the surface elevation at the surface point
                    ListenableFuture<Double> elevationFuture = scene.getBaseSurface().getElevationAsync(surfacePoint);
                    elevationFuture.addDoneListener(() -> {
                            try {
                                Double elevation = elevationFuture.get();

                                // update the label text with the new elevation
                                elevationLabel.setText("Elevation: " + Math.round(elevation) + " m");

                            } catch (InterruptedException | ExecutionException e) {
                                new Alert(Alert.AlertType.ERROR, e.getCause().getMessage()).show();
//                                Alert alert = new Alert(Alert.AlertType.ERROR, "Error: Could not retrieve elevation at this point. Please make sure you selected a valid surface.");
//                                alert.show();
                            }
                        }
                    );
                }
            });
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
