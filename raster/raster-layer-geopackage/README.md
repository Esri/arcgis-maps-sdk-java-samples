# Raster Layer GeoPackage

Display raster data from a geopackage.

<img src="RasterLayerGeoPackage.png"/>

## How it works

To add a`RasterLayer` as an operational layer from a `GeoPackage`:

  1. Create and load a `GeoPackage`, specifying the path to the local .gpkg file.
  2. When it is done loading, get the `GeoPackageRaster`s inside with `geoPackage
  .getGeoPackageRasters()`.
  3. Construct a `RasterLayer` with the `GeoPackageRaster` in the list you want to use.
  4. Add the raster layer to the map as an operational layer `map.getOperationalLayers().add(rasterLayer)`.


## Relevant API


  * ArcGISMap
  * Basemap
  * GeoPackage
  * GeoPackageRaster
  * MapView
  * RasterLayer

