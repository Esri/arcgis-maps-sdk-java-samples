# Feature Layer Query

Find features matching a SQL query.

![](FeatureLayerQuery.png)

## How to use the sample

Input the name of a U.S. state into search bar. When you hit search the application performs a query on the feature table and based on the result either highlights the state geometry or provides an error.

Note: The search is case sensitive.

## How it works

To query a `Feature` from your `FeatureLayer`:

1.  Create a `ServiceFeatureTable` from a URL.
2.  Create a feature layer from the service feature table.
3.  Create a `QueryParameters` object and specified the where clause on it with `QueryParameters.setWhereClause()` from the text entered by the user.
4.  Fire the query on the service feature table using `ServiceFeatureTable.queryFeaturesAsync(query)`.
5.  Once complete get the feature's from the `FeatureQueryResult`.

## Relevant API

*   ArcGISMap
*   FeatureLayer
*   FeatureQueryResult
*   MapView
*   QueryParameters
*   ServiceFeatureTable

