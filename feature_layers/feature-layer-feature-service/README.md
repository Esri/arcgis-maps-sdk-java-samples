# Feature Layer Feature Service

Show features from an online feature service.

![](FeatureLayerFeatureService.png)

## How it works

To add `Feature`s from your `FeatureLayer` to an `ArcGISMap`.

1. Create a `ServiceFeatureTable` from a URL.
2. Create a feature layer from the service feature table.
3. Set the feature layer to your ArcGISMap using `ArcGISMap.getOperationalLayers().add(FeatureLayer)`.

## Relevant API

* ArcGISMap
* FeatureLayer
* MapView
* ServiceFeatureTable
