# Read GeoPackage

Display rasters and features from local GeoPackages.

![](ReadGeoPackage.png)

## How to use the sample

The layers in the geoPackage, which have not been added to the map are
shown in the bottom list. Click an item to show it as a layer in the
map. Layers in the map are listed in the top list. Click layers from the
top list to remove them from the map.

## How it works

To read layers from a geoPackage and show them in a map:

1.  Create a `GeoPackage` with the path to the local geoPackage file.
2.  Load the `GeoPackage` with `GeoPackage.loadAsync`.
3.  Create raster layers for each of these with `new
    RasterLayer(geoPackageRaster)`.
