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

package com.esri.samples.scene;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;

/**
 * Model bean to bind to camera properties.
 */
public class CameraModel {

  private final BooleanProperty following;
  private final DoubleProperty distance;
  private final DoubleProperty angle;

  /**
   * Default constructor (needed for FXML injection).
   */
  public CameraModel() {
    this.following = new SimpleBooleanProperty();
    this.distance = new SimpleDoubleProperty();
    this.angle = new SimpleDoubleProperty();
  }

  /**
   * Constructs the camera model with the specified property values.
   *
   * @param following if the camera should lock onto and following the target
   * @param distance  following distance for camera to view from
   * @param angle     following pitch angle for camera to view from. (0 - 90 degrees)
   */
  public CameraModel(boolean following, double distance, double angle) {
    this.following = new SimpleBooleanProperty(following);
    this.distance = new SimpleDoubleProperty(distance);
    this.angle = new SimpleDoubleProperty(angle);
  }

  /**
   * Checks if the camera is in follow mode.
   *
   * @return true if the camera is in follow mode, false otherwise
   */
  public boolean isFollowing() {
    return following.get();
  }

  /**
   * Property tracking the camera's following mode.
   *
   * @return following property
   */
  public BooleanProperty followingProperty() {
    return following;
  }

  /**
   * Sets the camera follow mode.
   *
   * @param following if the camera should be in follow mode
   */
  public void setFollowing(boolean following) {
    this.following.set(following);
  }

  /**
   * Gets the distance from the camera to the target.
   *
   * @return distance from camera to target
   */
  public double getDistance() {
    return distance.get();
  }

  /**
   * Property tracking the camera's following distance.
   *
   * @return following distance property
   */
  public DoubleProperty distanceProperty() {
    return distance;
  }

  /**
   * Sets the follow distance between the camera and the target.
   *
   * @param distance follow distance between camera and target
   */
  public void setDistance(double distance) {
    this.distance.set(distance);
  }

  /**
   * Gets the pitch angle the camera views the target from.
   *
   * @return pitch angle between camera and target
   */
  public double getAngle() {
    return angle.get();
  }

  /**
   * Property tracking the following camera's pitch angle.
   *
   * @return following pitch angle property
   */
  public DoubleProperty angleProperty() {
    return angle;
  }

  /**
   * Sets the following camera's pitch angle.
   *
   * @param angle pitch angle (0 - 90 degrees)
   */
  public void setAngle(double angle) {
    this.angle.set(angle);
  }
}
