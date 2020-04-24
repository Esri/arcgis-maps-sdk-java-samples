# Change viewpoint

Set the map view to a new viewpoint.

![Image of change viewpoint](ChangeViewpoint.png)

## Use case

Programmatically navigate to a specified location in the map or scene. Use this to focus on a particular point or area of interest.

## How to use the sample

The map view has several methods for setting its current viewpoint. Click one of the buttons to see the viewpoint changed to that location using the method noted in parentheses.

## How it works

1. Create a new `ArcGISMap` and set it to the `MapView`.
2. Change the map's `Viewpoint` using one of the available methods:
  * Use `mapView.setViewpointAsync()` to pan to a viewpoint over the specified length of time.
  * Use `MapView.setViewpointCenterAsync()` to center the viewpoint on a `Point` and set a distance from the ground using a scale.
  * Use `MapView.setViewpointGeometryAsync()` to set the viewpoint to a given `Geometry`.

## Relevant API

* ArcGISMap
* Geometry
* MapView
* Point
* Viewpoint

## Additional information

Below are some other ways to set a viewpoint on the `MapView`:

* setViewpoint
* setViewpointAsync
* setViewpointCenterAsync
* setViewpointGeometryAsync
* setViewpointRotationAsync
* setViewpointScaleAsync

## Tags

animate, extent, pan, rotate, scale, view, zoom
