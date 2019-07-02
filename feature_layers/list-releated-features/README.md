# List Related Features

Find features related to the selected feature.

![](ListRelatedFeatures.png)

## How to use the sample

After the layer is loaded, click on a feature to select it. An accordion view will display related features 
grouped by feature table.

## How it works

To query related `Feature`s:


1.  Get a `Feature` from a loaded `FeatureLayer`.
2.  Get the feature's `FeatureTable` and call `featureTable.queryRelatedFeaturesAsync(feature)`.
3.  You will get a list of `RelatedFeatureQueryResult`s.
  
*   You can get the name of the table containing the related features with `relatedFeatureQueryResult
  .getRelatedTable().getTableName()`.
  <li>The `RelatedFeatureQueryResult` implements `Iterable&lt;Feature&gt;`. You can iterate over 
  the result to get the features:
  ``for (Feature feature: relatedFeatureQueryResult) {
  //do something with the related feature...
  }``


## Relevant API


*   ArcGISFeature
*   ArcGISFeatureTable
*   ArcGISMap
*   Feature
*   FeatureLayer
*   FeatureQueryResult
*   FeatureTable
*   MapView
*   RelatedFeatureQueryResult

