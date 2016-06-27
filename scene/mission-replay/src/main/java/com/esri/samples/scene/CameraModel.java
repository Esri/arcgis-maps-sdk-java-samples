package com.esri.samples.scene;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;

/**
 * Model bean to bind to camera properties.
 */
public class CameraModel {

  private final BooleanProperty follow;
  private final DoubleProperty distance;
  private final DoubleProperty angle;

  /**
   * Default constructor (needed for FXML injection)
   */
  public CameraModel() {
    this.follow = new SimpleBooleanProperty();
    this.distance = new SimpleDoubleProperty();
    this.angle = new SimpleDoubleProperty();
  }

  /**
   * Constructs the camera model with the specified property values.
   *
   * @param follow if the camera should lock onto and follow the target
   * @param distance following distance for camera to view from
   * @param angle following pitch angle for camera to view from. (0 - 90 degrees)
   */
  public CameraModel(boolean follow, double distance, double angle) {
    this.follow = new SimpleBooleanProperty(follow);
    this.distance = new SimpleDoubleProperty(distance);
    this.angle = new SimpleDoubleProperty(angle);
  }

  public boolean getFollow() {
    return follow.get();
  }

  /**
   * Property tracking the camera's follow mode.
   * @return if the camera is following
   */
  public BooleanProperty followProperty() {
    return follow;
  }

  public void setFollow(boolean follow) {
    this.follow.set(follow);
  }

  public double getDistance() {
    return distance.get();
  }

  /**
   * Property tracking the camera's follow distance.
   * @return following distance
   */
  public DoubleProperty distanceProperty() {
    return distance;
  }

  public void setDistance(double distance) {
    this.distance.set(distance);
  }

  public double getAngle() {
    return angle.get();
  }

  /**
   * Property tracking the following camera's pitch angle.
   * @return following pitch angle.
   */
  public DoubleProperty angleProperty() {
    return angle;
  }

  /**
   * Sets the following camera's pitch angle.
   * @param angle pitch angle (0 - 90 degrees)
   */
  public void setAngle(double angle) {
    this.angle.set(angle);
  }
}
