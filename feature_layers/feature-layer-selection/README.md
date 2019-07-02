# Feature Layer Selection

Select clicked features.

![]("FeatureLayerSelection.png)

## How to use the sample

Click on a feature from the map to select it.

## How it works

To select `Feature`s from your `FeatureLayer`:


  1. Create a `ServiceFeatureTable` from a URL.
  2. Create a feature layer from the service feature table.
  3. Identify `MapView` on the location the user has clicked `MapView.identifyLayerAsync
  (featureLayer, clickLocation, tolerance, returnPopupsOnly, maxResults)
  `
  4. Select all features that were identified with `FeatureLayer.selectFeatures(features)`.


## Relevant API


  * ArcGISMap
  * Feature
  * FeatureLayer
  * MapView
  * ServiceFeatureTable

