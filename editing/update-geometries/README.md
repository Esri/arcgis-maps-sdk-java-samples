# Update geometries (feature service)

Update a feature's location in an online feature service.

![Image of update geometries feature service](UpdateGeometries.gif)

## Use case

Sometimes users may want to edit features in an online feature service by moving them.

## How to use the sample

Click a feature to select it. Click again to set the updated location for that feature. An alert will be shown confirming success or failure.

## How it works

1. Create and load a `ServiceGeodatabase` with a feature service URL.
2. Get the `ServiceFeatureTable` from the service geodatabase.
3. Create a `FeatureLayer` from the service feature table.
4. Select and load a feature from the `FeatureLayer`.
5. Change the selected feature's location using `Feature.setGeometry(geometry)`.
6. Apply edits to the `ServiceGeodatabase` by calling `applyEditsAsync`, which will update the feature's geometry on the online service.

## Relevant API

* Feature
* FeatureLayer
* ServiceFeatureTable
* ServiceGeodatabase

## Tags

editing, feature layer, feature table, moving, service, updating