package com.esri.samples.featurelayers.statistical_query_group_and_sort;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import com.esri.arcgisruntime.data.QueryParameters;

/**
 * Convenience bean class for representing OrderBy in a TableView row. The sortOrder property can be bound to a
 * ComboBoxTableCell for changing the sortOrder with a ComboBox.
 */
public class OrderByField {

  private final SimpleStringProperty fieldName;
  private final SimpleObjectProperty<QueryParameters.SortOrder> sortOrder;
  private final SimpleObjectProperty<QueryParameters.OrderBy> orderBy;

  OrderByField(QueryParameters.OrderBy orderBy) {
    this.fieldName = new SimpleStringProperty(orderBy.getFieldName());
    this.sortOrder = new SimpleObjectProperty<>(orderBy.getSortOrder());
    this.orderBy = new SimpleObjectProperty<>();
    this.orderBy.bind(Bindings.createObjectBinding(() -> new QueryParameters.OrderBy(this.fieldName.get(), this
        .sortOrder.get()), this.fieldName, this.sortOrder));
  }

  public String getFieldName() {
    return fieldName.get();
  }

  public SimpleStringProperty fieldNameProperty() {
    return fieldName;
  }

  public void setFieldName(String fieldName) {
    this.fieldName.set(fieldName);
  }

  public QueryParameters.SortOrder getSortOrder() {
    return sortOrder.get();
  }

  public SimpleObjectProperty<QueryParameters.SortOrder> sortOrderProperty() {
    return sortOrder;
  }

  public void setSortOrder(QueryParameters.SortOrder sortOrder) {
    this.sortOrder.set(sortOrder);
  }

  public QueryParameters.OrderBy getOrderBy() {
    return orderBy.get();
  }

  public SimpleObjectProperty<QueryParameters.OrderBy> orderByProperty() {
    return orderBy;
  }

  public void setOrderBy(QueryParameters.OrderBy orderBy) {
    this.orderBy.set(orderBy);
  }
}
