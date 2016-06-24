package com.esri.samples.scene;

import javafx.beans.property.*;

public class PlaneModel {

  private final DoubleProperty altitude;
  private final DoubleProperty heading;
  private final DoubleProperty pitch;
  private final DoubleProperty roll;

  /**
   * Default constructor
   */
  public PlaneModel() {
    this.altitude = new SimpleDoubleProperty();
    this.heading = new SimpleDoubleProperty();
    this.pitch = new SimpleDoubleProperty();
    this.roll = new SimpleDoubleProperty();
  }

  /**
   * Creates a model of plane properties.
   *
   * @param altitude
   * @param heading
   * @param pitch
   * @param roll
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

  public DoubleProperty altitudeProperty() {
    return altitude;
  }

  public void setAltitude(double altitude) {
    this.altitude.set(altitude);
  }

  public double getHeading() {
    return heading.get();
  }

  public DoubleProperty headingProperty() {
    return heading;
  }

  public void setHeading(double heading) {
    this.heading.set(heading);
  }

  public double getPitch() {
    return pitch.get();
  }

  public DoubleProperty pitchProperty() {
    return pitch;
  }

  public void setPitch(double pitch) {
    this.pitch.set(pitch);
  }

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
