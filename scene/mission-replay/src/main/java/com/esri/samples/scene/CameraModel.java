package com.esri.samples.scene;

import javafx.beans.property.*;

public class CameraModel {

  private final BooleanProperty follow;
  private final DoubleProperty distance;
  private final DoubleProperty angle;

  /**
   * Default constructor
   */
  public CameraModel() {
    this.follow = new SimpleBooleanProperty();
    this.distance = new SimpleDoubleProperty();
    this.angle = new SimpleDoubleProperty();
  }

  /**
   * Creates a model of camera properties.
   *
   * @param follow
   * @param distance
   * @param angle
   */
  public CameraModel(boolean follow, double distance, double angle) {
    this.follow = new SimpleBooleanProperty(follow);
    this.distance = new SimpleDoubleProperty(distance);
    this.angle = new SimpleDoubleProperty(angle);
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

  public double getDistance() {
    return distance.get();
  }

  public DoubleProperty distanceProperty() {
    return distance;
  }

  public void setDistance(double distance) {
    this.distance.set(distance);
  }

  public double getAngle() {
    return angle.get();
  }

  public DoubleProperty angleProperty() {
    return angle;
  }

  public void setAngle(double angle) {
    this.angle.set(angle);
  }
}
