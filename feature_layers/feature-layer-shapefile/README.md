# Feature Layer Shapefile

Display features from a local shapefile.
  
![](FeatureLayerShapefile.png)

## How it works

To show a shapefile as a feature layer:


1.  Create a `ShapefileFeatureTable` passing in the URI of a shapefile.
2.  Create a `FeatureLayer` using the `ShapefileFeatureTable`.
3.  Add the layer to the map with `map.getOperationalLayers().add(featureLayer)`.


## Relevant API


*   FeatureLayer
*   ShapefileFeatureTable


