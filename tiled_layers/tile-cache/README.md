# Tile cache

Load an offline copy of a tiled map service as a basemap.

![Image of tile cache](TileCache.png)

## Use case

Constructing an `ArcGISTiledLayer` from a local tile package (.tpk & .tpkx), allows you to use that basemap tiled service when the application is offline. Dividing a raster into tiles allows the map to provide relevant tiles and level of detail to the user when panning and zooming. For example, when working in an environment that has no connectivity, this could provide access to a map for navigating your surroundings.

## How to use the sample	

Launch the sample  to view the "San Francisco offline tile package" as the basemap. 

## How it works

1. Create a `TileCache`, specifying the path to the local tile package.
2. Create an `ArcGISTiledLayer` with the tile cache.
3. Create a `Basemap` with the tiled layer.
4. Create an `ArcGISMap` with the basemap and set it to a MapView.

## Relevant API

* ArcGISMap
* ArcGISTiledLayer
* Basemap
* TileCache

## About the data

The map opens to a view of the city of San Francisco, CA. In a disconnected environment, this basemap tile service would be fully accessible to zoom and pan as if you were connected to a online service.

## Additional information

`ArcGISTiledLayer` and `TileCache` supports both .tpk and .tpkx file formats.

## Tags

cache, layers, offline, tile
