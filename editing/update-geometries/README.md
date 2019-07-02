# Update Geometries

Update a feature's location in an online feature service.

<img src="UpdateGeometries.gif"/>

## How to use the sample

To update a feature's location.
 - click on a feature from the map and then click on another location to move it

## How it works

To get a `Feature` from a `ServiceFeatureTable` and change it's geometry:


 1. Create a service feature table from a URL.
 2. Create a `FeatureLayer` from the service feature table.
 3. Select features from the feature layer, `FeatureLayer.selectFeatures`.
 4. Change the selected feature's location using `Feature.setGeometry(Geometry)`.
 5. After a change, update the table on the server using `ServiceFeatureTable.applyEditsAsync()`.


## Relevant API


 * ArcGISMap
 * Feature
 * FeatureLayer
 * MapView
 * ServiceFeatureTable

