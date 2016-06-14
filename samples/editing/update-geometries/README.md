#Update Geometries#
This sample demonstrates how to update the location of a Feature from a `ServiceFeatureTable`.

##How to use the sample##
Click on a feature on the map to select the feature. You can click on a new location in order to save the new geometry location.

![](UpdateGeometries1.png" alt="Update Geometries)
![](UpdateGeometries2.png" alt="Update Geometries)

##How it works##
To get features from a `ServiceFeatureTable` and change their geometry:

- Create a ServiceFeatureTable from a URL.
- Create a FeatureLayer from the ServiceFeatureTable.
- Select features from the FeatureLayer via `FeatureLayer#selectFeatures` method.
- Change the selected feature's location using `Feature#setGeometry(Geometry)` method.
- After a change, update the table on the server using `ServiceFeatureTable#applyEditsAsync` method.

##Features##
- ArcGISMap
- MapView
- Feature
- FeatureLayer
- ServiceFeatureTable