# Feature layer shapefile

Open a shapefile stored on the device and display it as a feature layer with default symbology.

![Image of feature layer shapefile](FeatureLayerShapefile.png)

## Use case

Shapefiles store location, shape and attributes of geospatial vector data. Shapefiles can be loaded directly into ArcGIS Runtime.

## How to use the sample

Pan and zoom around the map. View the data loaded from the shapefile.

## How it works

1. Create a `ShapefileFeatureTable` passing in the URL of a shapefile.
2. Create a `FeatureLayer` using the shapefile feature table.
3. Add the layer to the map's operation layers.

## Relevant API

* FeatureLayer
* ShapefileFeatureTable

## Tags

Layers, shapefile, shp, vector
