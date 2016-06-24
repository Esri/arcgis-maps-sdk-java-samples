package com.esri.samples.scene;

import javafx.beans.property.*;

public class CameraModel {

  private final BooleanProperty follow;
  private final DoubleProperty cameraDistance;
  private final DoubleProperty cameraAngle;

  /**
   * Default constructor
   */
  public CameraModel() {
    this.follow = new SimpleBooleanProperty();
    this.cameraDistance = new SimpleDoubleProperty();
    this.cameraAngle = new SimpleDoubleProperty();
  }

  /**
   * Creates a model of camera properties.
   *
   * @param follow
   * @param cameraDistance
   * @param cameraAngle
   */
  public CameraModel(boolean follow, double cameraDistance, double cameraAngle) {
    this.follow = new SimpleBooleanProperty(follow);
    this.cameraDistance = new SimpleDoubleProperty(cameraDistance);
    this.cameraAngle = new SimpleDoubleProperty(cameraAngle);
  }

  public boolean getFollow() {
    return follow.get();
  }

  public BooleanProperty followProperty() {
    return follow;
  }

  public void setFollow(boolean follow) {
    this.follow.set(follow);
  }

  public double getCameraDistance() {
    return cameraDistance.get();
  }

  public DoubleProperty cameraDistanceProperty() {
    return cameraDistance;
  }

  public void setCameraDistance(double cameraDistance) {
    this.cameraDistance.set(cameraDistance);
  }

  public double getCameraAngle() {
    return cameraAngle.get();
  }

  public DoubleProperty cameraAngleProperty() {
    return cameraAngle;
  }

  public void setCameraAngle(double cameraAngle) {
    this.cameraAngle.set(cameraAngle);
  }
}
