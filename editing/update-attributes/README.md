# Update attributes

Update feature attributes in an online feature service.

![Image of update attributes feature service](UpdateAttributes.gif)

## Use case

Online feature services can be updated with new data. This is useful for updating existing data in real time while working in the field.

## How to use the sample

To change the feature's damage property, click on the feature to select it, and update the damage type using the drop down.

## How it works

1. Create and load a `ServiceGeodatabase` with a feature service URL.
2. Get the `ServiceFeatureTable` from the service geodatabase.
3. Create a `FeatureLayer` from the service feature table.
4. Select features from the `FeatureLayer`.
5. To update the feature's attribute, first load it, then use `.getAttributes().put(keyValuePair)` to modify the desired attribute.
6. Update the feature table with `.updateFeatureAsync(feature)`.
7. Apply edits to the `ServiceGeodatabase` by calling `applyEditsAsync`, which will update the feature on the online service.

## Relevant API

* ArcGISFeature
* FeatureLayer
* ServiceFeatureTable
* ServiceGeodatabase

## Tags

amend, attribute, details, edit, editing, information, update, value
