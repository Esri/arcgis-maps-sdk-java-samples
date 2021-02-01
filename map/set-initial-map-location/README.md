# Set initial map location

Display a basemap centered at an initial location and scale.

![Image of set initial map location](SetInitialMapLocation.png)

## Use case

You can set a map's initial location when you want to highlight a particular feature or area to establish the context.

## How to use the sample

When the map loads, note the specific location and scale of the initial map view.

## How it works

1. Create an `ArcGISMap`, specifying a basemap style.
2. Display the map in a map view.
3. Set a `Viewpoint` on the map view, specifying the latitude, longitude and scale.

## Relevant API

* ArcGISMap
* BasemapStyle
* MapView
* Viewpoint

## About the data

The map opens with a basemap centred over East Scotland.

## Tags

basemap, center, envelope, extent, initial, lat, latitude, level of detail, location, LOD, long, longitude, scale, zoom level
