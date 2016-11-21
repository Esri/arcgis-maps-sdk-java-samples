/*
 * Copyright 2016 Esri.
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

package com.esri.samples.scene.calculate_distance_3d;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.fxml.FXML;
import javafx.geometry.Point3D;
import javafx.scene.control.Label;
import javafx.util.Duration;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.ArcGISTiledElevationSource;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Surface;
import com.esri.arcgisruntime.mapping.view.Camera;
import com.esri.arcgisruntime.mapping.view.DrawStatus;
import com.esri.arcgisruntime.mapping.view.DrawStatusChangedEvent;
import com.esri.arcgisruntime.mapping.view.DrawStatusChangedListener;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.LayerSceneProperties.SurfacePlacement;
import com.esri.arcgisruntime.mapping.view.SceneView;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;

public class CalculateDistance3dController {

  @FXML private Label txtDistance;
  private LongProperty distance;
  private Timeline animation;
  // distance to move graphics each key frame
  private double xOffset = -0.1;

  @FXML private SceneView sceneView;
  private Point redPoint;
  private Point greenPoint;
  private SimpleMarkerSymbol redSymbol;
  private SimpleMarkerSymbol greenSymbol;
  private Graphic redGraphic;
  private Graphic greenGraphic;
  private SpatialReference sr = SpatialReferences.getWgs84();

  private static final String ELEVATION_IMAGE_SERVICE =
      "http://elevation3d.arcgis.com/arcgis/rest/services/WorldElevation3D/Terrain3D/ImageServer";

  /**
   * Called after FXML loads. Sets up scene and map and configures property bindings.
   */
  public void initialize() {

    try {
      // create a scene and add to view
      ArcGISScene scene = new ArcGISScene();
      scene.setBasemap(Basemap.createImagery());
      sceneView.setArcGISScene(scene);

      // adds elevation to surface
      Surface surface = new Surface();
      surface.getElevationSources().add(new ArcGISTiledElevationSource(ELEVATION_IMAGE_SERVICE));
      scene.setBaseSurface(surface);

      createGraphics();

      // set viewpoint of camera above graphics
      Camera camera = new Camera(39, -101, 10000000, 10.0, 0.0, 0.0);
      sceneView.setViewpointCamera(camera);

      setupAnimation();

      // automatically updates distance between graphics to view
      distance = new SimpleLongProperty();
      txtDistance.textProperty().bind(distance.asString());
      // set beginning distance of two graphics
      distance.set(Math.round(calculateDirectLinearDistance(redPoint, greenPoint)));

    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
    }
  }

  /**
   * Create red and green triangle graphics and displays them to the view.
   */
  private void createGraphics() {

    // applies graphics to the view who's altitude is increased by surface's elevation
    GraphicsOverlay graphicsOverlay = new GraphicsOverlay();
    graphicsOverlay.getSceneProperties().setSurfacePlacement(SurfacePlacement.ABSOLUTE);
    sceneView.getGraphicsOverlays().add(graphicsOverlay);

    // creating graphics for view
    redPoint = new Point(-77.69531409620706, 40.25390707699415, 900, sr);
    redSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.TRIANGLE, 0xFFFF0000, 20);
    redGraphic = new Graphic(redPoint, redSymbol);
    graphicsOverlay.getGraphics().add(redGraphic);

    greenPoint = new Point(-120.05859621653715, 38.847657048103514, 1000, sr);
    greenSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.TRIANGLE, 0xFF00FF00, 20);
    greenSymbol.setAngle(30);
    greenGraphic = new Graphic(greenPoint, greenSymbol);
    graphicsOverlay.getGraphics().add(greenGraphic);
  }

  /**
   * Sets graphic animation to keep running every 100 milliseconds.
   * <p>
   * The animation will play once the view is done loading.
   */
  private void setupAnimation() {

    animation = new Timeline(new KeyFrame(Duration.millis(100), e -> animate()));
    animation.setCycleCount(Animation.INDEFINITE);

    // listener for the view to stop loading
    DrawStatusChangedListener listener = new DrawStatusChangedListener() {

      @Override
      public void drawStatusChanged(DrawStatusChangedEvent drawStatusChangedEvent) {
        if (drawStatusChangedEvent.getDrawStatus() == DrawStatus.COMPLETED) {
          // start animation
          animation.play();
          // stop listening for the view to load
          sceneView.removeDrawStatusChangedListener(this);
        }
      }
    };
    sceneView.addDrawStatusChangedListener(listener);
  }

  /**
   * Moves graphics along the X axis and calculates distance between them.
   */
  private void animate() {

    // changes direction of graphics once they reach their boundary
    if (redPoint.getX() <= -120) {
      xOffset = 0.1;
      redSymbol.setAngle(180);
      greenSymbol.setAngle(210);
    } else if (redPoint.getX() >= -77) {
      xOffset = -0.1;
      redSymbol.setAngle(0);
      greenSymbol.setAngle(30);
    }

    // update red graphic's position
    redPoint = new Point(redPoint.getX() + xOffset, redPoint.getY(), redPoint.getZ(), sr);
    redGraphic.setGeometry(redPoint);

    //update green graphic's position
    greenPoint = new Point(greenPoint.getX() - xOffset, greenPoint.getY(), greenPoint.getZ(), sr);
    greenGraphic.setGeometry(greenPoint);

    // updates distance between graphics to view
    distance.set(Math.round(calculateDirectLinearDistance(redPoint, greenPoint)));
  }

  /**
   * Calculates the distance, in meters, between two Points in 3D space.
   * 
   * @param point1 first point
   * @param point2 second point
   * @return distance, in meters, between the two points
   */
  private double calculateDirectLinearDistance(Point point1, Point point2) {

    return convertToCartesianPoint(point1).distance(convertToCartesianPoint(point2));
  }

  /**
   * Converts a Point to the Cartesian coordinate system.
   * 
   * @param point point to convert
   * @return a 3D point in Cartesian coordinates
   */
  private Point3D convertToCartesianPoint(Point point) {

    double x = convertToRadians(point.getY());
    double y = convertToRadians(point.getX());
    double z = point.getZ();

    // in meters
    int earthRadius = 6371000;
    double radius = z + earthRadius;
    double radCosLat = radius * Math.cos(y);

    double p1 = radCosLat * Math.sin(x);
    double p2 = radCosLat * Math.cos(x);
    double p3 = radius * Math.sin(y);

    return new Point3D(p1, p2, p3);
  }

  /**
   * Converts degrees to radians.
   * 
   * @param degrees degree to convert
   * @return the converted degrees
   */
  private double convertToRadians(double degrees) {

    return degrees * (Math.PI / 180);
  }

  /**
   * Stops the animation and disposes of application resources.
   */
  void terminate() {

    if (sceneView != null) {
      sceneView.dispose();
    }
  }
}
