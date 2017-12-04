package com.esri.samples.analysis.line_of_sight_location;

import java.util.concurrent.ExecutionException;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.geoanalysis.LocationLineOfSight;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.ArcGISTiledElevationSource;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Surface;
import com.esri.arcgisruntime.mapping.view.AnalysisOverlay;
import com.esri.arcgisruntime.mapping.view.Camera;
import com.esri.arcgisruntime.mapping.view.SceneView;

public class LineOfSightLocationSample extends Application {

  private SceneView sceneView;

  @Override
  public void start(Stage stage) throws Exception {

    try {

      // create stack pane and JavaFX app scene
      StackPane stackPane = new StackPane();
      Scene fxScene = new Scene(stackPane);

      // set title, size, and add JavaFX scene to stage
      stage.setTitle("Line of Sight Location Sample");
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
      stackPane.getChildren().addAll(sceneView);

      // add base surface for elevation data
      Surface surface = new Surface();
      surface.getElevationSources().add(new ArcGISTiledElevationSource( "http://elevation3d.arcgis" +
          ".com/arcgis/rest/services/WorldElevation3D/Terrain3D/ImageServer"));
      scene.setBaseSurface(surface);

      // create an analysis overlay for the line of sight
      AnalysisOverlay analysisOverlay = new AnalysisOverlay();
      sceneView.getAnalysisOverlays().add(analysisOverlay);

      // initialize a line of sight analysis and add it to the analysis overlay
      Point observerLocation = new Point(-73.06958032962375,-49.253112971555446,2000, SpatialReferences.getWgs84());
      Point targetLocation = new Point(-73.079266999709162, -49.300457676730559, 1312, SpatialReferences.getWgs84());
      LocationLineOfSight lineOfSight = new LocationLineOfSight(observerLocation, targetLocation);
      analysisOverlay.getAnalyses().add(lineOfSight);

      Camera camera = new Camera(new Point(-73.0815, -49.3272, 4059, SpatialReferences.getWgs84()), 11, 62, 0);
      sceneView.setViewpointCamera(camera);

      // remove default mouse move handler
      sceneView.setOnMouseMoved(null);

      // update the target location when the mouse moves
      EventHandler<MouseEvent> mouseMoveEventHandler = event -> {
        Point2D point2D = new Point2D(event.getX(), event.getY());
        ListenableFuture<Point> pointFuture = sceneView.screenToLocationAsync(point2D);
        pointFuture.addDoneListener(() -> {
          try {
            Point point = pointFuture.get();
            lineOfSight.setTargetLocation(point);
          } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
          }
        });
      };

      // mouse click to start/stop moving target location
      sceneView.setOnMouseClicked(event -> {
        if (event.isStillSincePress() && event.getButton() == MouseButton.PRIMARY) {
          if (sceneView.getOnMouseMoved() == null) {
            sceneView.setOnMouseMoved(mouseMoveEventHandler);
          } else {
            sceneView.setOnMouseMoved(null);
          }
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
