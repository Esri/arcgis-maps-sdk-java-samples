package com.esri.samples.statistical_query_group_and_sort;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Convenience bean class for representing a group-by field. The grouping property can be bound to a CheckBoxListCell
 * to choose whether the field should be grouped by with a CheckBox.
 */
class GroupField {

  private final SimpleStringProperty fieldName;
  private final SimpleBooleanProperty grouping;

  GroupField(String fieldName, Boolean grouping) {
    this.fieldName = new SimpleStringProperty(fieldName);
    this.grouping = new SimpleBooleanProperty(grouping);
  }

  String getFieldName() {
    return fieldName.get();
  }

  public SimpleStringProperty fieldNameProperty() {
    return fieldName;
  }

  public void setFieldName(String fieldName) {
    this.fieldName.set(fieldName);
  }

  boolean isGrouping() {
    return grouping.get();
  }

  SimpleBooleanProperty groupingProperty() {
    return grouping;
  }

  void setGrouping(boolean grouping) {
    this.grouping.set(grouping);
  }

  @Override
  public String toString() {
    return getFieldName();
  }
}