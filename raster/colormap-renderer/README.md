# Colormap renderer

Apply a colormap renderer to a raster.

![Image of colormap renderer](ColormapRenderer.png)

## Use case

A colormap renderer transforms pixel values in a raster to display raster data based on specific colors, aiding in visual analysis of the data. For example, a forestry commission may want to quickly visualize areas above and below the tree-line line occurring at a known elevation on a raster containing elevation values. They could overlay a transparent colormap set to color those areas below the tree-line elevation green, and those above white.

## How to use the sample

Pan and zoom to explore the effect of the colormap applied to the raster.

## How it works

1. Create a `Raster` from a raster file.
2. Create a `RasterLayer` from the raster.
3. Create a `List<Integer>` representing colors. Colors at the beginning of the list replace the darkest values in the raster and colors at the end of the list replaced the brightest values of the raster.
4. Create a `ColormapRenderer` with the color list: `ColormapRenderer(colors)`, and apply it to the raster layer with `rasterLayer.setRasterRenderer(colormapRenderer)`.

## Relevant API

* ColormapRenderer
* Raster
* RasterLayer

## About the data

The raster used in this sample shows an area in the south of the Shasta-Trinity National Forest, California.

## Tags

colormap, data, raster, renderer, visualization
