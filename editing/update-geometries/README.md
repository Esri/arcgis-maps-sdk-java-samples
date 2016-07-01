#Update Geometries#
Demonstrates how to update the location of a Feature from a ServiceFeatureTable.

##How to use the sample##
To update a feature's location.
 - click on a feature from the map and then click on another location to move it

![](Update-Geometries.png)

##How it works##
To get a `Feature` from a `ServiceFeatureTable` and change it's geometry:

- Create a service feature table from a URL.
- Create a `FeatureLayey` from the service feature table.
- Select features from the feature layer, `FeatureLayer.selectFeatures`.
- Change the selected feature's location using `Feature.setGeometry(Geometry)`.
- After a change, update the table on the server using `ServiceFeatureTable.applyEditsAsync`.

##Features##
- ArcGISMap
- Feature
- FeatureLayer
- MapView
- ServiceFeatureTable
