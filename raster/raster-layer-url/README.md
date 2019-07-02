# Raster Layer URL

Show raster data from an online raster image service.

![](RasterLayerURL.png)

## How it works

To add a `RasterLayer` as an operational layer from an `ImageServiceRaster`:

1. Create an `ImageServiceRaster` using the service's URL
2. Create a `RasterLayer` from the raster
3. Add it as an operational layer with `map.getOperationalLayers().add(rasterLayer)`

## Relevant API

* ArcGISMap
* Basemap
* ImageServiceRaster
* MapView
* RasterLayer
