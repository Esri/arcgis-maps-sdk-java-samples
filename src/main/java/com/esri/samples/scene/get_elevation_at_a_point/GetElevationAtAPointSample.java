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
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geometry.PolylineBuilder;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.ArcGISTiledElevationSource;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Surface;
import com.esri.arcgisruntime.mapping.view.Camera;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.LayerSceneProperties;
import com.esri.arcgisruntime.mapping.view.SceneView;
import com.esri.arcgisruntime.symbology.TextSymbol;

public class GetElevationAtAPointSample extends Application {

    private SceneView sceneView;
    private GraphicsOverlay graphicsOverlay;

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

            // add the SceneView to the stack pane
            sceneView = new SceneView();
            sceneView.setArcGISScene(scene);
            stackPane.getChildren().add(sceneView);

            // add base surface for elevation data
            Surface surface = new Surface();
            surface.getElevationSources().add(new ArcGISTiledElevationSource("http://elevation3d.arcgis.com/arcgis/rest/services/WorldElevation3D/Terrain3D/ImageServer"));
            scene.setBaseSurface(surface);

            // create a point symbol to mark where elevation is being measured
            SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0xFFFF0000, 3.0f);

            // create a text symbol to display the elevation of selected points
            TextSymbol elevationTextSymbol = new TextSymbol(20,null,0xFFFF0000,TextSymbol.HorizontalAlignment.CENTER,TextSymbol.VerticalAlignment.BOTTOM);

            // create a graphics overlay
            graphicsOverlay = new GraphicsOverlay(GraphicsOverlay.RenderingMode.DYNAMIC);
            graphicsOverlay.getSceneProperties().setSurfacePlacement(LayerSceneProperties.SurfacePlacement.RELATIVE);
            sceneView.getGraphicsOverlays().add(graphicsOverlay);

            // add a camera and initial camera position
            Camera camera = new Camera(28.42, 83.9, 10000.0, 10.0, 80.0, 0.0);
            sceneView.setViewpointCamera(camera);

            // create listener to handle clicked
            sceneView.setOnMouseClicked(event -> {

                // get the clicked screenPoint
                Point2D screenPoint = new Point2D(event.getX(), event.getY());
                // convert the screen point to a point on the surface
                Point relativeSurfacePoint = sceneView.screenToBaseSurface(screenPoint);

                // check that the point is on the surface, and primary button was clicked
                if (relativeSurfacePoint != null && event.getButton() == MouseButton.PRIMARY && event.isStillSincePress()) {

                    // clear any existing graphics from the graphics overlay
                    graphicsOverlay.getGraphics().clear();

                    // construct a polyline to use as a marker
                    PolylineBuilder polylineBuilder = new PolylineBuilder(relativeSurfacePoint.getSpatialReference());
                    Point baseOfPolyline = new Point(relativeSurfacePoint.getX(), relativeSurfacePoint.getY(), 0);
                    polylineBuilder.addPoint(baseOfPolyline);
                    Point topOfPolyline = new Point(baseOfPolyline.getX(), baseOfPolyline.getY(), 750);
                    polylineBuilder.addPoint(topOfPolyline);
                    Polyline markerPolyline = polylineBuilder.toGeometry();
                    Graphic polylineGraphic = new Graphic(markerPolyline, lineSymbol);
                    graphicsOverlay.getGraphics().add(polylineGraphic);

                    // get the surface elevation at the surface point
                    ListenableFuture<Double> elevationFuture = scene.getBaseSurface().getElevationAsync(relativeSurfacePoint);
                    elevationFuture.addDoneListener(() -> {
                        try {
                            // get the surface elevation
                            Double elevation = elevationFuture.get();

                            // update the text in the elevation marker
                            elevationTextSymbol.setText((Math.round(elevation * 10d)/10d) + " m");
                            Graphic elevationTextGraphic = new Graphic(topOfPolyline, elevationTextSymbol);
                            graphicsOverlay.getGraphics().add(elevationTextGraphic);

                        } catch (InterruptedException | ExecutionException e) {
                            new Alert(Alert.AlertType.ERROR, e.getCause().getMessage()).show();
                        }
                    });
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
