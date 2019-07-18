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

package com.esri.samples.line_of_sight_geoelement;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.util.Duration;

import com.esri.arcgisruntime.geoanalysis.GeoElementLineOfSight;
import com.esri.arcgisruntime.geoanalysis.LineOfSight;
import com.esri.arcgisruntime.geometry.AngularUnit;
import com.esri.arcgisruntime.geometry.AngularUnitId;
import com.esri.arcgisruntime.geometry.GeodeticCurveType;
import com.esri.arcgisruntime.geometry.GeodeticDistanceResult;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.LinearUnit;
import com.esri.arcgisruntime.geometry.LinearUnitId;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointBuilder;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.ArcGISSceneLayer;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.ArcGISTiledElevationSource;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Surface;
import com.esri.arcgisruntime.mapping.view.AnalysisOverlay;
import com.esri.arcgisruntime.mapping.view.Camera;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.LayerSceneProperties;
import com.esri.arcgisruntime.mapping.view.SceneView;
import com.esri.arcgisruntime.symbology.ModelSceneSymbol;
import com.esri.arcgisruntime.symbology.Renderer;
import com.esri.arcgisruntime.symbology.SceneSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;

public class LineOfSightGeoElementSample extends Application {

  private SceneView sceneView;
  private List<Point> waypoints;
  private int waypointIndex = 0;
  private Timeline animation;
  private Graphic taxi;

  private static final LinearUnit METERS = new LinearUnit(LinearUnitId.METERS);
  private static final AngularUnit DEGREES = new AngularUnit(AngularUnitId.DEGREES);

  @Override
  public void start(Stage stage) {

    try {

      // create stack pane and JavaFX app scene
      StackPane stackPane = new StackPane();
      Scene fxScene = new Scene(stackPane);
      fxScene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

      // set title, size, and add JavaFX scene to stage
      stage.setTitle("Line of Sight GeoElement Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(fxScene);
      stage.show();

      // create a scene and add a basemap to it
      ArcGISScene scene = new ArcGISScene();
      scene.setBasemap(Basemap.createTopographic());

      // add the SceneView to the stack pane
      sceneView = new SceneView();
      sceneView.setArcGISScene(scene);
      stackPane.getChildren().add(sceneView);

      // add base surface for elevation data
      Surface surface = new Surface();
      surface.getElevationSources().add(new ArcGISTiledElevationSource("http://elevation3d.arcgis" +
          ".com/arcgis/rest/services/WorldElevation3D/Terrain3D/ImageServer"));
      scene.setBaseSurface(surface);

      // add buildings from New York City
      String buildingsURL = "https://tiles.arcgis.com/tiles/z2tnIkrLQ2BRzr6P/arcgis/rest/services/New_York_LoD2_3D_Buildings/SceneServer/layers/0";
      ArcGISSceneLayer buildings = new ArcGISSceneLayer(buildingsURL);
      scene.getOperationalLayers().add(buildings);

      // create a graphics overlay for the graphics
      GraphicsOverlay graphicsOverlay = new GraphicsOverlay();
      graphicsOverlay.getSceneProperties().setSurfacePlacement(LayerSceneProperties.SurfacePlacement.RELATIVE);
      sceneView.getGraphicsOverlays().add(graphicsOverlay);

      // set up a heading expression to handle graphic rotation
      SimpleRenderer renderer3D = new SimpleRenderer();
      Renderer.SceneProperties renderProperties = renderer3D.getSceneProperties();
      renderProperties.setHeadingExpression("[HEADING]");
      graphicsOverlay.setRenderer(renderer3D);

      // create a point graph near the Empire State Building to be the observer
      Point observationPoint = new Point(-73.9853, 40.7484, 200, SpatialReferences.getWgs84());
      Graphic observer = new Graphic(observationPoint, new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, 0xFFFF0000, 5));
      graphicsOverlay.getGraphics().add(observer);

      // create a slider to change the observer's Z value
      Slider heightSlider = new Slider(150, 300, 200);
      heightSlider.setMaxSize(30, 150);
      heightSlider.setShowTickLabels(true);
      heightSlider.setOrientation(Orientation.VERTICAL);
      heightSlider.valueProperty().addListener(e -> {
        PointBuilder pointBuilder = new PointBuilder((Point) observer.getGeometry());
        pointBuilder.setZ(heightSlider.getValue());
        observer.setGeometry(pointBuilder.toGeometry());
      });

      // show a background behind the slider
      VBox controlsVBox = new VBox(6);
      controlsVBox.setBackground(new Background(new BackgroundFill(Paint.valueOf("rgba(0,0,0,0.3)"), CornerRadii.EMPTY,
          Insets.EMPTY)));
      controlsVBox.setPadding(new Insets(10.0));
      controlsVBox.setMaxSize(50, 160);
      controlsVBox.getStyleClass().add("panel-region");
      controlsVBox.getChildren().add(heightSlider);
      stackPane.getChildren().add(controlsVBox);
      StackPane.setAlignment(controlsVBox, Pos.TOP_LEFT);
      StackPane.setMargin(controlsVBox, new Insets(10, 0, 0, 10));

      // create waypoints around a block for the taxi to drive to
      waypoints = Arrays.asList(
          new Point(-73.984513, 40.748469, SpatialReferences.getWgs84()),
          new Point(-73.985068, 40.747786, SpatialReferences.getWgs84()),
          new Point(-73.983452, 40.747091, SpatialReferences.getWgs84()),
          new Point(-73.982961, 40.747762, SpatialReferences.getWgs84())
      );

      // create a graphic of a taxi to be the target
      String modelURI = new File("./samples-data/dolmus_3ds/dolmus.3ds").getAbsolutePath();
      ModelSceneSymbol taxiSymbol = new ModelSceneSymbol(modelURI, 1.0);
      taxiSymbol.setAnchorPosition(SceneSymbol.AnchorPosition.BOTTOM);
      taxiSymbol.loadAsync();
      taxi = new Graphic(waypoints.get(0), taxiSymbol);
      taxi.getAttributes().put("HEADING", 0.0);
      graphicsOverlay.getGraphics().add(taxi);

      // create an analysis overlay to hold the line of sight
      AnalysisOverlay analysisOverlay = new AnalysisOverlay();
      sceneView.getAnalysisOverlays().add(analysisOverlay);

      // create a line of sight between the two graphics and add it to the analysis overlay
      GeoElementLineOfSight lineOfSight = new GeoElementLineOfSight(observer, taxi);
      analysisOverlay.getAnalyses().add(lineOfSight);

      // select (highlight) the taxi when the line of sight target visibility changes to visible
      lineOfSight.addTargetVisibilityChangedListener(targetVisibilityChangedEvent ->
        taxi.setSelected(targetVisibilityChangedEvent.getTargetVisibility() == LineOfSight.TargetVisibility.VISIBLE)
      );

      // create a timeline to animate the taxi driving around the block
      animation = new Timeline();
      animation.setCycleCount(-1);
      animation.getKeyFrames().add(new KeyFrame(Duration.millis(100), e -> animate()));
      animation.play();

      // zoom to show the observer
      Camera camera = new Camera((Point) observer.getGeometry(), 700, -30, 45, 0);
      sceneView.setViewpointCamera(camera);

    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
    }
  }

  /**
   * Moves the taxi toward the current waypoint a short distance.
   */
  private void animate() {
    Point waypoint = waypoints.get(waypointIndex);
    // get current location and distance from waypoint
    Point location = (Point) taxi.getGeometry();
    GeodeticDistanceResult distance = GeometryEngine.distanceGeodetic(location, waypoint, METERS, DEGREES,
        GeodeticCurveType.GEODESIC);

    // move toward waypoint a short distance
    location = GeometryEngine.moveGeodetic(location, 1.0, METERS, distance.getAzimuth1(), DEGREES,
        GeodeticCurveType.GEODESIC);
    taxi.setGeometry(location);

    // rotate to the waypoint
    taxi.getAttributes().put("HEADING", distance.getAzimuth1());

    // reached waypoint, move to next waypoint
    if (distance.getDistance() <= 2) {
      waypointIndex = (waypointIndex + 1) % waypoints.size();
    }
  }

  /**
   * Stops and releases all resources used in application.
   */
  @Override
  public void stop() {

    // stop the animation
    animation.stop();

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
