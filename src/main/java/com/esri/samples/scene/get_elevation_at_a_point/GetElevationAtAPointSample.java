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

import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.mapping.view.*;
import com.esri.arcgisruntime.symbology.MarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.TextSymbol;
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

            // add the SceneView and controlsVBox to the stack pane
            sceneView = new SceneView();
            sceneView.setArcGISScene(scene);
            stackPane.getChildren().add(sceneView);

            // add base surface for elevation data
            Surface surface = new Surface();
            surface.getElevationSources().add(new ArcGISTiledElevationSource("http://elevation3d.arcgis.com/arcgis/rest/services/WorldElevation3D/Terrain3D/ImageServer"));
            scene.setBaseSurface(surface);

            // create a point symbol to mark where elevation is being measured
            SimpleMarkerSymbol circleSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, 0xFFFF0000, 10);

            // create a text symbol to display the elevation of selected points
            TextSymbol elevationTextSymbol = new TextSymbol(20,"",0xFFFF0000,TextSymbol.HorizontalAlignment.CENTER,TextSymbol.VerticalAlignment.MIDDLE);

            // create a graphics overlay
            GraphicsOverlay graphicsOverlay = new GraphicsOverlay(GraphicsOverlay.RenderingMode.DYNAMIC);
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

                    // get the surface elevation at the surface point
                    ListenableFuture<Double> elevationFuture = scene.getBaseSurface().getElevationAsync(relativeSurfacePoint);
                    elevationFuture.addDoneListener(() -> {
                            try {
                                // get the surface elevation
                                Double elevation = elevationFuture.get();

                                Point trueSurfacePoint = new Point(relativeSurfacePoint.getX(),relativeSurfacePoint.getY(), relativeSurfacePoint.getZ()-elevation);
                                // create a new graphic at the surface point and add it to the graphics overlay
                                Graphic surfacePointGraphic = new Graphic(trueSurfacePoint, circleSymbol);
                                graphicsOverlay.getGraphics().add(surfacePointGraphic);

                                // create a point of the end of the polyline
                                Point endOfPolyline = new Point(trueSurfacePoint.getX(), trueSurfacePoint.getY(), (trueSurfacePoint.getZ()+500), trueSurfacePoint.getSpatialReference());

                                Graphic eolPointGraphic = new Graphic(endOfPolyline, circleSymbol);
                                graphicsOverlay.getGraphics().add(eolPointGraphic);

                                // create a polyline symbol between the surface point and the end of the polyline
                    //                PointCollection polyLineStartAndEnd = new PointCollection
                    //                polyLineStartAndEnd.add(surfacePoint);
                    //                polyLineStartAndEnd.add(endOfPolyline)
                    //                Polyline markerPolyline = new Polyline(polyLineStartAndEnd);
                    //                Graphic polyLineGraphic = new Graphic(markerPolyline);
                    //                graphicsOverlay.getGraphics().add(polyLineGraphic);



                                // prepare and place the marker text symbol
                                elevationTextSymbol.setText((Math.round(elevation * 10d)/10d) + " m");
                                Graphic elevationTextGraphic = new Graphic(endOfPolyline, elevationTextSymbol);
                                graphicsOverlay.getGraphics().add(elevationTextGraphic);

                            } catch (InterruptedException | ExecutionException e) {
                                new Alert(Alert.AlertType.ERROR, e.getCause().getMessage()).show();
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
