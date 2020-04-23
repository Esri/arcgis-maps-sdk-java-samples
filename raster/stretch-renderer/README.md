# Stretch renderer

Use a stretch renderer to enhance the visual contrast of raster data for analysis.

![Image of stretch renderer](StretchRenderer.png)

## Use case

An appropriate stretch renderer can enhance the contrast of raster imagery, allowing the user to control how their data is displayed for efficient imagery analysis.

## How to use the sample

Choose one of the stretch parameter types from the drop down menu:

* Std Deviation - a linear stretch defined by the standard deviation of the pixel values
* Min Max - a linear stretch based on minimum and maximum pixel values
* Percent Clip - a linear stretch between the defined percent clip minimum and percent clip maximum pixel values

Then configure the parameters and click 'Update'.

## How it works

1. Create a `Raster` from a raster file.
2. Create a `RasterLayer` from the raster.
3. Create a `Basemap` from the raster layer with `Basemap(rasterLayer)` and set it to the map with `ArcGISMap(basemap)`.
4. Create a `StretchRenderer`, specifying the stretch parameters and other properties.
5. Set the stretch renderer on the raster layer with `rasterLayer.setRasterRenderer(stretchRenderer)`.

## Relevant API

* MinMaxStretchParameters
* PercentClipStretchParameters
* Raster
* RasterLayer
* StandardDeviationStretchParameters
* StretchParameters
* StretchRenderer

## About the data

The raster used in this sample shows an area in the south of the Shasta-Trinity National Forest, California.

## Additional information

See [Stretch function](http://desktop.arcgis.com/en/arcmap/latest/manage-data/raster-and-images/stretch-function.htm) in the *ArcMap* documentation for more information about the types of stretches that can be performed.

## Tags

analysis, deviation, histogram, imagery, interpretation, min-max, percent clip, pixel, raster, stretch, symbology, visualization
