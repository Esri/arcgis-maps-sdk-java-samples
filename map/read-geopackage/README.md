# Read GeoPackage

Display rasters and features from local GeoPackages.

![]("ReadGeoPackage.png)

## How to use the sample

The layers in the geoPackage, which have not been added to the map are shown in the bottom list. Click an item to 
show it as a layer in the map. Layers in the map are listed in the top list. Click layers from the top list to 
remove them from the map.

## How it works

To read layers from a geoPackage and show them in a map:


  1. Create a `GeoPackage` with the path to the local geoPackage file.
  2. Load the `GeoPackage` with `GeoPackage.loadAsync`.
  3. Create raster layers for each of these with `new RasterLayer(geoPackageRaster)`.
  4. Add each layer to the map as an operational layer with `map.getOperationalLayers().add(layer)`.
  5. When it's done loading, get the `GeoPackageFeatureTable`s inside with `geoPackage
  .getGeoPackageFeatureTables()`.
  6. For each feature table, create a feature layer with `new FeatureLayer(featureTable)`.
  7. You can also get the `GeoPackageRaster`s inside using `GeoPackage.getGeoPackageRasters()`.


## Relevant API


* ArcGISMap
* Basemap
* FeatureLayer
* GeoPackage
* Layer
* MapView
* RasterLayer

