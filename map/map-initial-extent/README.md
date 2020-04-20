# Map initial extent

Display the map at an initial viewpoint representing a bounding geometry.

![](MapInitialExtent.png)

## Use case

Setting the initial viewpoint is useful when a user wishes to first load the map at a particular area of interest. 

## How to use the sample

As application is loading, initial view point is set and map view opens at the given location.

## How it works

1. Create an `ArcGISMap`.
2. Create a `Viewpoint` using an `Envelope`.
3. Set the starting location of the map with `setInitialViewpoint(Viewpoint)`.
4. Set the map to a `MapView`.
 
## Relevant API

* ArcGISMap
* Envelope
* MapView
* Point
* Viewpoint


## Tags

InitialViewpoint, extent, zoom, Envelope
