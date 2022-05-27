# Delete features

Delete features from an online feature service.

![Image of delete features feature service](DeleteFeatures.gif)

## Use case

Sometimes users may want to delete features from an online feature service.

## How to use the sample

Click on a feature on the Map, then click the 'delete' button to delete.

## How it works

1. Create and load a `ServiceGeodatabase` with a feature service URL.
2. Get the `ServiceFeatureTable` from the service geodatabase.
3. Create a `FeatureLayer` from the service feature table.
4. Identify the selected feature by using `identifyLayerAsync`.
5. Remove the selected features from the `ServiceFeatureTable` using `deleteFeaturesAsync`.
6. Update the data on the server using `applyEditsAsync` on the `ServiceGeodatabase`, which will remove the feature from the online service.

## Relevant API

* Feature
* FeatureLayer
* ServiceFeatureTable
* ServiceGeodatabase

## Tags

deletion, feature, online, service, table