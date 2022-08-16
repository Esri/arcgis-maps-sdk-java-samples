package com.esri.samples.create_mobile_geodatabase;

import javafx.beans.property.SimpleStringProperty;

/**
 * Create a new class as data model to hold feature data.
 * Instances of this class are used to populate the TableView.
 */
public class FeatureAttributeField {

  private final SimpleStringProperty oid;
  private final SimpleStringProperty timestamp;

  public FeatureAttributeField(String oid, String timestamp) {
    this.oid = new SimpleStringProperty(oid);
    this.timestamp = new SimpleStringProperty(timestamp);
  }

  public String getOid() {
    return oid.get();
  }

  public String getTimestamp() {
    return timestamp.get();
  }
}
