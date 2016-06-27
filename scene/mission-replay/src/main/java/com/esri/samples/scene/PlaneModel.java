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
   * @param heading plane heading
   * @param pitch plane pitch
   * @param roll plane roll
   */
  public PlaneModel(double altitude, double heading, double pitch, double roll) {
    this.altitude = new SimpleDoubleProperty(altitude);
    this.heading = new SimpleDoubleProperty(heading);
    this.pitch = new SimpleDoubleProperty(pitch);
    this.roll = new SimpleDoubleProperty(roll);
  }

  public double getAltitude() {
    return altitude.get();
  }

  /**
   * Property tracking plane altitude.
   * @return plane altitude property
   */
  public DoubleProperty altitudeProperty() {
    return altitude;
  }

  public void setAltitude(double altitude) {
    this.altitude.set(altitude);
  }

  public double getHeading() {
    return heading.get();
  }

  /**
   * Property tracking plane heading.
   * @return plane heading property
   */
  public DoubleProperty headingProperty() {
    return heading;
  }

  public void setHeading(double heading) {
    this.heading.set(heading);
  }

  public double getPitch() {
    return pitch.get();
  }

  /**
   * Property tracking plane pitch.
   * @return plane pitch property
   */
  public DoubleProperty pitchProperty() {
    return pitch;
  }

  public void setPitch(double pitch) {
    this.pitch.set(pitch);
  }

  /**
   * Property tracking plane roll.
   * @return plane roll property
   */
  public double getRoll() {
    return roll.get();
  }

  public DoubleProperty rollProperty() {
    return roll;
  }

  public void setRoll(double roll) {
    this.roll.set(roll);
  }
}
