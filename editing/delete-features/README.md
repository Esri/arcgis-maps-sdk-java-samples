#Delete Features#
Demonstrates how to delete a Feature from a FeatureLayer using a feature service. 

##How to use the sample##
To delete a feature.
  - click on a feature on the Map
  - click on the delete button

![](DeleteFeatures.png)

##How it works##
To delete a `Feature` from a `ServiceFeatureTable`:

1. Create a ServiceFeatureTable from a URL.
2. Create a `FeatureLayer` from the ServiceFeatureTable.
3. Select features from the FeatureLayer via `FeatureLayer.selectFeatures()`.
4. Remove the selected features from the ServiceFeatureTable using `ServiceFeatureTable.deleteFeaturesAsync()`.
5. Update the table on the server using `ServiceFeatureTable.applyEditsAsync()`.

##Features##
- ArcGISMap
- Feature
- FeatureLayer
- MapView
- ServiceFeatureTable
