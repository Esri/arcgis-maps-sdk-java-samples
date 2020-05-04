# Identify raster cell

Get the cell value of a local raster at the clicked location and display the result in a callout.

![Image of identify raster cell](IdentifyRasterCell.png)

## Use case

You may want to identify a raster layer to get its exact cell value in the case the approximate value conveyed by its symbology is not sufficient. The information available for the raster cell depends on the type of raster layer being identified. For example, a 3-band satellite or aerial image might provide 8-bit RGB values, whereas a digital elevation model (DEM) would provide floating point z values. By identifying a raster cell of a DEM, you can retrieve the precise elevation of a location. 

## How to use the sample

Click an area of the raster to identify it. See the raw raster cell information displayed in a callout.

## How it works

1. Add a listener to the `MapView` to capture mouse clicks.
2. On click:
  * Dismiss the `Callout`, if one is showing.
  * Call `identifyLayerAsync(...)` passing in the layer, screen point, tolerance, whether to return popups only, and maximum number of results.
  * Await the result of the identify and then get the `GeoElement` from the layer result.
  * Create a callout at the calculated map point and populate the callout content with text from the `RasterCell` attributes. 
  * Show the callout.

## Relevant API

* MapView
* IdentifyLayerResult
* RasterCell
* RasterLayer

## About the data

[TODO: Appropriate data that is approved for use in the sample should be used. The initial provided screen shot should be replaced when actual approved sample data is used.]

## Tags

band, cell, cell value, continuous, discrete, identify, pixel, pixel value, raster
