# Stretch Renderer

Apply a stretch renderer to a raster.

A stretch renderer can be used to stretch tightly grouped values across the available value spectrum, creating more contrast between objects. This is useful for identifying objects in dark images.

![](StretchRenderer.png)

## How to use the sample

Choose one of the stretch parameter types. The other options will adjust based on the chosen type.

## How it works

To apply a `StretchRenderer` to a `RasterLayer`:

1. Create a `Raster` from a raster file
2. Create a `RasterLayer` from the raster
3. Create a `Basemap` from the raster layer and set it to the map
4. Create a `StretchRenderer`, specifying the stretch parameters and other properties
5. Set the renderer on the raster layer with `rasterLayer.setRenderer(renderer)`

## Relevant API

* ArcGISMap
* Basemap
* MapView
* Raster
* RasterLayer
* StretchParameters
* StretchRenderer
