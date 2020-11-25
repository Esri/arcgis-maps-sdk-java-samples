# Change basemap

Change a map's basemap. A basemap is beneath all layers on an `ArcGISMap` and is used to provide visual reference for the operational layers.

![Image of change basemap](ChangeBasemap.png)

## Use case

Basemaps should be selected contextually, for example, in maritime applications, it would be more appropriate to use a basemap of the world's oceans as opposed to a basemap of the world's streets.

## How to use the sample

Select a basemap style from the list to set it to the map.

## How it works

1. Create an `ArcGISMap` object.
2. Set the map to the `MapView` object.
3. Choose a new basemap style with `BasemapStyle` and set it on the map.

## Relevant API
* ArcGISMap
* BasemapStyle
* MapView

## Tags

basemap, map
