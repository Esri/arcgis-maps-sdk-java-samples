# Statistical Query Group And Sort

Group and sort feature statistics by different fields.

![](StatisticalQueryGroupAndSort.png">

## How to use the sample

The sample will start with some default options selected. You can immediately click the "Get Statistics" button to
 see the results for these options.

To change the statistic definitions, you can add statistic definitions to the top-left table using the combo boxes 
and "Add button". Select a table row and click "Remove" to remove the statistic definition.

To change the group-by fields, check the box by the field you want to group by in the bottom-left list view.

To change the order-by fields, select a group by field (it must be checked) and click the ">>" button to add it to
 the Order By table. To remove a field from the Order by table, select it and click the "<<" button. To change the 
 sort order of the order-by field, click on a cell in the Sort Order column to edit it using a ComboBox.
 
 ## How it works
 
 To query statistics from a feature table:
 
 
  1. Create and load a `ServiceFeatureTable`.
  2. Get the feature tables field names list with `featureTable.getFields()`.
  3. Create `StatisticDefinition`s specifying the field to compute statistics on and the 
  `StatisticType` to compute.
  4. Create `StatisticsQueryParameters` passing in the list of statistic definitions.
  5. To have the results grouped by fields, add the field names to the query parameters' 
  `groupByFieldNames` collection.
  6. To have the results ordered by fields, create `OrderBy`s, specifying the field name and 
  `SortOrder`. Pass these `OrderBy`s to the parameters' `orderByFields` 
  collection.
  7. To execute the query, call `featureTable.queryStatisticsAsync(queryParameters)`
  8. Get the `StatisticQueryResult`. From this, you can get an iterator of 
  `StatisticRecord`s to loop through and display.
 
 
 ## Relevant API
 
 
*   Field
*   QueryParameters
*   ServiceFeatureTable
*   StatisticDefinition
*   StatisticRecord
*   StatisticType
*   StatisticsQueryParameters
*   StatisticsQueryResult
 
