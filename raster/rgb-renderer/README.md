# Rgb Renderer

Apply an RGB renderer to a raster.

An RGB renderer is used to adjust the color bands of a multispectral image.

<img src="RgbRenderer.png"/>

## How to use the sample

Choose one of the stretch parameter types. The other options will adjust based on the chosen type. Add your 
inputs and press the Update button to update the renderer.

## How it works

To apply a `RgbRenderer` to a `RasterLayer`:

  1. Create a `Raster` from a multispectral raster file
  2. Create a `RasterLayer` from the raster
  3. Create a `Basemap` from the raster layer and set it to the map
  4. Create a `RgbRenderer`, specifying the stretch parameters and other properties
  5. Set the renderer on the raster layer with `rasterLayer.setRenderer(renderer)`


## Relevant API


  * ArcGISMap
  * Basemap
  * MapView
  * Raster
  * RasterLayer
  * RgbRenderer
  * StretchParameters

