# Add features

Add features to a feature layer.

![Image of adding features](AddFeatures.gif)

## Use case

An end-user performing a survey may want to add features to the map during the course of their work.

## How to use the sample

Click on a location on the map to add a feature at that location.

## How it works

1. Create and load a `ServiceGeodatabase` with a feature service URL.
2. Get the `ServiceFeatureTable` from the service geodatabase.
3. Create a `FeatureLayer` from the service feature table.
4. Create a `Feature` with attributes and a location using the `ServiceFeatureTable`.
5. Add the `Feature` to the `ServiceFeatureTable`.
6. Apply edits to the `ServiceGeodatabase` by calling `applyEditsAsync`, which will upload the new feature to the online service.

## Relevant API

* Feature
* FeatureEditResult
* FeatureLayer
* ServiceFeatureTable
* ServiceGeodatabase

## Tags

edit, feature, online service