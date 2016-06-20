#Feature Layer Feature Service#
Demonstrates how to create a FeatureLayer from a ServiceFeatureTable and add it to an ArcGISMap.

![](FeatureLayerFeatureService.png)

##How it works##
To add `Feature`s from your `FeatureLayer` to an `ArcGISMap`.

1. Create a `ServiceFeatureTable` from a URL.
2. Create a feature layer from the service feature table.
3. Set the feature layer to your ArcGISMap using `ArcGISMap.getOperationalLayers().add(FeatureLayer)`.

##Features##
- ArcGISMap
- FeatureLayer
- MapView
- ServiceFeatureTable
