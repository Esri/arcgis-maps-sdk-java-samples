# Map Image Layer Tables

Find features in a spatial table related to features in a non-spatial table.

The non-spatial tables contained by a map service may contain additional information about sublayer features. Such information can be accessed by traversing table relationships defined in the service.

<img src="MapImageLayerTables.png"></a>

## How to use the sample

Once the map image layer loads, a list view will be populated with comment data from non-spatial features. Click on 
one of the comments to query related spatial features and display the first result on the map.

## How it works

To query a map image layer's tables and find related features:


  1. Create an `ArcGISMapImageLayer` with the URL of a map image service.
  2. Load the layer and get one of it's tables with `imageLayer.getTables().get(index)`.
  3. To query the table, create `QueryParameters`. You can use `queryParameters.setWhereClause
  (sqlQuery)` to filter the features returned. Use `table.queryFeaturesAsync(parameters)` to get a
   `FeatureQueryResult`.
   4. The `FeatureQueryResult` is an iterable, so simply loop through it to get each result 
   `Feature`.
   5. To query for related features, get the table's relationship info with `table.getLayerInfo()
   .getRelationshipInfos()`. This returns a list of `RelationshipInfo`s. Choose which one to 
   base your query on.
   6. Now create `RelatedQueryParameters` passing in the `RelationshipInfo`. To query 
   related features use `table.queryRelatedFeaturesAsync(feature, relatedQueryParameters)`.
   7. This returns a list of `RelatedFeatureQueryResult`s, each containing a set of related 
   features`.


## Relevant API


* ArcGISFeature
* ArcGISMapImageLayer
* Feature
* FeatureQueryResult
* QueryParameters
* RelatedFeatureQueryResult
* RelatedQueryParameters
* RelationshipInfo
* ServiceFeatureTable


## Additional information

You can use `ArcGISMapImageLayer.LoadTablesAndLayersAsync()` to recursively load all sublayers and tables 
associated with a map image layer.