# Update attributes

Update feature attributes in an online feature service.

![Image of update attributes feature service](UpdateAttributes.gif)

## Use case

Online feature services can be updated with new data. This is useful for updating existing data in real time while working in the field.

## How to use the sample

To change the feature's damage property, click on the feature to select it, and update the damage type using the drop down.

## How it works

1. Create a `ServiceFeatureTable` object from a URL.
2. Create a `FeatureLayer` object from the `ServiceFeatureTable`.
3. Select features from the `FeatureLayer`.
4. To update the feature's attribute, first load it, then use `.getAttributes().put(keyValuePair)` to modify the desired attribute.
5. Update the feature table with `.updateFeatureAsync(feature)`.
6. After a change, apply the changes on the service feature table using `.applyEditsAsync()`.

## Relevant API

* ArcGISFeature
* FeatureLayer
* ServiceFeatureTable

## Tags

amend, attribute, details, edit, editing, information, update, value
