package com.esri.samples.featurelayers.statistical_query_group_and_sort;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Field;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.data.StatisticDefinition;
import com.esri.arcgisruntime.data.StatisticRecord;
import com.esri.arcgisruntime.data.StatisticType;
import com.esri.arcgisruntime.data.StatisticsQueryParameters;
import com.esri.arcgisruntime.data.StatisticsQueryResult;
import com.esri.arcgisruntime.loadable.LoadStatus;

public class StatisticalQueryGroupAndSortController {

  public ComboBox<String> fieldNameComboBox;
  public ComboBox<String> statisticTypeComboBox;
  public TableView<StatisticDefinition> statisticDefinitionsTableView;
  public TreeView<String> statisticRecordTreeView;
  public ListView<GroupField> groupFieldsListView;
  public TableColumn<StatisticDefinition, String> statisticDefinitionFieldNameTableColumn;
  public TableColumn<StatisticDefinition, StatisticType> statisticDefinitionStatisticTypeTableColumn;
  public Button removeStatisticDefinitionButton;
  public TableView<OrderByField> orderByTableView;
  public TableColumn<OrderByField, String> orderByFieldNameTableColumn;
  public TableColumn<OrderByField, QueryParameters.SortOrder> orderBySortOrderTableColumn;
  public Button addOrderByFieldButton;
  public Button removeOrderByFieldButton;

  private ServiceFeatureTable featureTable;

  public void initialize() {

    setUpButtonBindings();
    initializeStatisticDefinitionsTableView();
    initializeGroupByListView();
    initializeOrderByTableView();

    // load the service feature table
    String USStatesServiceUri = "https://sampleserver6.arcgisonline.com/arcgis/rest/services/Census/MapServer/3";
    featureTable = new ServiceFeatureTable(USStatesServiceUri);
    featureTable.loadAsync();
    featureTable.addDoneLoadingListener(() -> {
      if (featureTable.getLoadStatus() == LoadStatus.LOADED) {
        // populate the field name combo box with the feature table's field names
        List<String> fieldNames = featureTable.getFields().stream().map(Field::getName).collect(Collectors.toList());
        fieldNameComboBox.getItems().addAll(fieldNames);

        // populate the statistic type combo box with the different StatisticType values
        statisticTypeComboBox.getItems().addAll(Arrays.stream(StatisticType.values()).map(Enum::toString).collect(Collectors.toList()));

        // populate the Group By list with all of the field names with none of the check boxes selected
        groupFieldsListView.getItems().addAll(fieldNames.stream().map(f -> new GroupField(f, false)).collect
            (Collectors.toList()));

        // set some default selections
        fieldNameComboBox.getSelectionModel().select("AGE_5_17");
        statisticTypeComboBox.getSelectionModel().select("MINIMUM");
        statisticDefinitionsTableView.getItems().addAll(
            new StatisticDefinition("POP2007", StatisticType.SUM),
            new StatisticDefinition("POP2007", StatisticType.AVERAGE),
            new StatisticDefinition("AGE_5_17", StatisticType.MINIMUM)
        );
        groupFieldsListView.getItems().stream().filter(f -> f.getFieldName().equals("SUB_REGION")).collect(Collectors
            .toList()).get(0).setGrouping(true);
        orderByTableView.getItems().add(new OrderByField(new QueryParameters.OrderBy("SUB_REGION", QueryParameters.SortOrder
            .ASCENDING)));
      }
    });
  }

  /**
   * Add a CheckBox to each Group By field which sets whether that field should be grouped by.
   */
  private void initializeGroupByListView() {
    groupFieldsListView.setCellFactory(CheckBoxListCell.forListView(groupField -> {
      // if the user deselects a field to be grouped by, remove it from order-by table too
      groupField.groupingProperty().addListener((observable, wasGrouping, isNowGrouping) -> {
        List<OrderByField> orderByFieldsMatchingGroupByField = orderByTableView.getItems().stream().filter(row -> row
            .getFieldName().equals(groupField.getFieldName())).collect(Collectors.toList());
        if (!isNowGrouping && !orderByFieldsMatchingGroupByField.isEmpty()) {
          orderByTableView.getItems().removeAll(orderByFieldsMatchingGroupByField);
        }
      });
      // bind the checkbox selection to the grouping property
      return groupField.groupingProperty();
    }));
  }

  /**
   * Initializes the Statistics Definition table view. Configures the columns to show the correct field values.
   */
  private void initializeStatisticDefinitionsTableView() {
    // make the table columns stretch to fill the width of the table
    statisticDefinitionsTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

    // show the field name and statistic type values in the column cells
    statisticDefinitionFieldNameTableColumn.setCellValueFactory(new PropertyValueFactory<>("fieldName"));
    statisticDefinitionStatisticTypeTableColumn.setCellValueFactory(new PropertyValueFactory<>("statisticType"));
  }

  /**
   * Initializes the Order By table view. Configures the table columns to show the correct field values and allow the
   * sort direction of each field to be changed with a ComboBox.
   */
  private void initializeOrderByTableView() {
    // make the table columns stretch to fill the width of the table
    orderByTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

    // show the field name and sort order values in the column cells
    orderByFieldNameTableColumn.setCellValueFactory(cellData -> cellData.getValue().fieldNameProperty());
    orderBySortOrderTableColumn.setCellValueFactory(cellData -> cellData.getValue().sortOrderProperty());

    // show an editable ComboBox in the sort order column cells to change the sort order
    orderBySortOrderTableColumn.setCellFactory(col -> {
      ComboBoxTableCell<OrderByField, QueryParameters.SortOrder> ct = new ComboBoxTableCell<>();
      ct.getItems().addAll(QueryParameters.SortOrder.values());
      ct.setEditable(true);
      return ct;
    });

    // switch to edit mode when the user selects the Sort Direction column
    orderByTableView.getSelectionModel().selectedItemProperty().addListener(e -> {
      List<TablePosition> tablePositions = orderByTableView.getSelectionModel().getSelectedCells();
      if (!tablePositions.isEmpty()) {
        tablePositions.forEach(tp -> orderByTableView.edit(tp.getRow(), orderBySortOrderTableColumn));
      }
    });
  }

  /**
   * Sets up button bindings.
   */
  private void setUpButtonBindings() {
    // disable the "Remove" button when no statistic definitions are selected
    removeStatisticDefinitionButton.disableProperty().bind(statisticDefinitionsTableView.getSelectionModel()
        .selectedItemProperty().isNull());
    // disable the ">>" button when no Group By fields are selected
    addOrderByFieldButton.disableProperty().bind(groupFieldsListView.getSelectionModel().selectedItemProperty().isNull());
    // disable the "<<" button when no Order By fields are selected
    removeOrderByFieldButton.disableProperty().bind(orderByTableView.getSelectionModel().selectedItemProperty().isNull());
  }

  /**
   * Called when the "Add" button is clicked. Adds a statistic definition to the table.
   */
  @FXML
  private void addSelectedStatisticDefinition() {
    // get the selected field and statistic from the combo boxes
    String selectedFieldName = fieldNameComboBox.getSelectionModel().getSelectedItem();
    String selectedStatisticType = statisticTypeComboBox.getSelectionModel().getSelectedItem();
    // check that a statistic definition with that field and statistic type is not already in the table
    if (statisticDefinitionsTableView.getItems().stream().filter(row -> row.getFieldName().equals(selectedFieldName) && row
        .getStatisticType().name().equals(selectedStatisticType)).collect(Collectors.toList()).isEmpty()) {
      // add the statistic definition to the table
      statisticDefinitionsTableView.getItems().add(new StatisticDefinition(selectedFieldName, StatisticType.valueOf(selectedStatisticType)));
    } else {
      new Alert(Alert.AlertType.WARNING, "The selected combination has already been chosen.").show();
    }
  }

  /**
   * Called when the "Remove" button is clicked. Removes a statistic definition from the table.
   */
  @FXML
  private void removeSelectedStatisticDefinition() {
    statisticDefinitionsTableView.getItems().removeAll(statisticDefinitionsTableView.getSelectionModel().getSelectedItems());
  }

  /**
   * Called when the ">>" button is clicked. Adds a Group By field to the Order By table.
   */
  @FXML
  private void addSelectedOrderByField() {
    // get the selected field in the Group By list
    GroupField selectedGroupField = groupFieldsListView.getSelectionModel().getSelectedItem();
    // check that the selected field is grouped by and not already in the Order By table
    if (!selectedGroupField.isGrouping()) {
      new Alert(Alert.AlertType.WARNING, "Only group fields can be ordered. You must check the selected group item's " +
          "checkbox before adding the field to the ordered list.").show();
    } else if (!orderByTableView.getItems().stream().filter(row -> row.getFieldName().equals(selectedGroupField
        .getFieldName())).collect(Collectors.toList()).isEmpty()) {
      new Alert(Alert.AlertType.WARNING, "The field has already been added.").show();
    } else {
      // add the field to the Order By table, defaulting to ascending sort order
      orderByTableView.getItems().add(new OrderByField(new QueryParameters.OrderBy(selectedGroupField.getFieldName(),
          QueryParameters.SortOrder.ASCENDING)));
    }
  }

  /**
   * Called when the "<<" button is clicked. Removes a Order by field from the table.
   */
  @FXML
  private void removeSelectedOrderByField() {
    OrderByField selectedOrderByField = orderByTableView.getSelectionModel().getSelectedItem();
    if (selectedOrderByField != null) {
      orderByTableView.getItems().remove(selectedOrderByField);
    }
  }

  /**
   * Called when the "Get Statistics" button is clicked. Executes the statistics query with all of the input parameters.
   */
  @FXML
  private void getStatistics() {
    // sets the root if this is the first result set, or clears the results from the previous query
    statisticRecordTreeView.setRoot(new TreeItem<>(""));

    // get the statistics definitions from the table, show an alert if there are none and return early
    List<StatisticDefinition> statisticDefinitions = statisticDefinitionsTableView.getItems();
    if (statisticDefinitions.isEmpty()) {
      new Alert(Alert.AlertType.WARNING, "Please define at least one statistic for the query").show();
      return;
    }

    // create statistics query parameters with the definitions
    StatisticsQueryParameters queryParameters = new StatisticsQueryParameters(statisticDefinitions);

    // add the selected fields from the Group By list into the parameters' group-by field names
    queryParameters.getGroupByFieldNames().addAll(groupFieldsListView.getItems().stream().filter(GroupField::isGrouping)
        .map(GroupField::getFieldName).collect(Collectors.toList()));

    // add the fields from the Order By table into the parameters' order-by fields
    queryParameters.getOrderByFields().addAll(orderByTableView.getItems().stream().map(OrderByField::getOrderBy).collect(Collectors.toList()));

    // execute the statistics query
    ListenableFuture<StatisticsQueryResult> statisticsQuery = featureTable.queryStatisticsAsync(queryParameters);
    statisticsQuery.addDoneListener(() -> {
      try {
        // get the query result
        StatisticsQueryResult result = statisticsQuery.get();

        // iterate through the result records
        for (Iterator<StatisticRecord> records = result.iterator(); records.hasNext();) {
          StatisticRecord record = records.next();

          // create a tree item representing the group list
          TreeItem<String> groupTreeItem = new TreeItem<>(String.join(", ", record.getGroup().values().stream().map
              (Object::toString).collect(Collectors.toList())));
          statisticRecordTreeView.getRoot().getChildren().add(groupTreeItem);

          // add child tree items for each record's statistics, showing the statistic name and value
          List<TreeItem<String>> statisticTreeItems = record.getStatistics().entrySet().stream().map(statistic ->
            new TreeItem<>(statistic.getKey() + " : " + statistic.getValue())
          ).collect(Collectors.toList());
          groupTreeItem.getChildren().addAll(statisticTreeItems);
        }
      } catch (Exception ex) {
        new Alert(Alert.AlertType.ERROR, ex.getCause().getMessage()).show();
      }
    });
  }

}
