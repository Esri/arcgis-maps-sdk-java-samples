package com.esri.samples.scene;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

/**
 * Model bean to bind to plane properties.
 */
public class PlaneModel {

  private final DoubleProperty altitude;
  private final DoubleProperty heading;
  private final DoubleProperty pitch;
  private final DoubleProperty roll;

  /**
   * Default constructor (needed for FXML injection)
   */
  public PlaneModel() {
    this.altitude = new SimpleDoubleProperty();
    this.heading = new SimpleDoubleProperty();
    this.pitch = new SimpleDoubleProperty();
    this.roll = new SimpleDoubleProperty();
  }

  /**
   * Constructs a plane model with the specified values.
   *
   * @param altitude plane altitude
   * @param heading  plane heading
   * @param pitch    plane pitch
   * @param roll     plane roll
   */
  public PlaneModel(double altitude, double heading, double pitch, double roll) {
    this.altitude = new SimpleDoubleProperty(altitude);
    this.heading = new SimpleDoubleProperty(heading);
    this.pitch = new SimpleDoubleProperty(pitch);
    this.roll = new SimpleDoubleProperty(roll);
  }

  /**
   * Gets the plane's altitude.
   *
   * @return plane's altitude in meters
   */
  public double getAltitude() {
    return altitude.get();
  }

  /**
   * Property tracking plane altitude.
   *
   * @return plane altitude property
   */
  public DoubleProperty altitudeProperty() {
    return altitude;
  }

  /**
   * Sets the plane's altitude.
   *
   * @param altitude altitude in meters
   */
  public void setAltitude(double altitude) {
    this.altitude.set(altitude);
  }

  /**
   * Gets the plane's heading.
   *
   * @return plane's heading angle in degrees
   */
  public double getHeading() {
    return heading.get();
  }

  /**
   * Property tracking plane heading.
   *
   * @return plane heading property
   */
  public DoubleProperty headingProperty() {
    return heading;
  }

  /**
   * Sets the plane's heading.
   *
   * @param heading plane's heading in degrees
   */
  public void setHeading(double heading) {
    this.heading.set(heading);
  }

  /**
   * Gets the plane's pitch angle.
   *
   * @return plane's pitch angle in degrees
   */
  public double getPitch() {
    return pitch.get();
  }

  /**
   * Property tracking plane pitch.
   *
   * @return plane pitch property
   */
  public DoubleProperty pitchProperty() {
    return pitch;
  }

  /**
   * Sets the plane's pitch angle.
   *
   * @param pitch pitch angle in degrees
   */
  public void setPitch(double pitch) {
    this.pitch.set(pitch);
  }

  /**
   * Gets the plane's roll angle.
   *
   * @return plane's roll angle
   */
  public double getRoll() {
    return roll.get();
  }

  /**
   * Property tracking plane roll.
   *
   * @return plane roll property
   */
  public DoubleProperty rollProperty() {
    return roll;
  }

  /**
   * Set's the plane's roll angle.
   *
   * @param roll plane's roll angle in degrees
   */
  public void setRoll(double roll) {
    this.roll.set(roll);
  }
}
