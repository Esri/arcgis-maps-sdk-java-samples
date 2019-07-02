# Take Screen Shot

Export an area of the map as an image.

![]("TakeScreenShot.png)

## How to use this Sample

Click the Take Screenshot button to export the image. In the file chooser that opens, set a file name and location to
 save the image.

## How it works

To export the visible area of the `ArcGISMap` as an image file:


  1. Export the image with `mapView.exportImageAsync()`.
  2. Get the `Image` once export is done loading.


## Relevant API


  * ArcGISMap
  * MapView


