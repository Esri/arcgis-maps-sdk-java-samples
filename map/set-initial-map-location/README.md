# Set Initial Map Location

Display a map centered at a latitude and longitude plus zoom level.

![]("SetInitialMapLocation.png)

## How it works

To set an initial location:


1. Create an `ArcGISMap`, `ArcGISMap(Basemap, latitude, longitude, scale)`.
* `Basemap`, use basemap type to access a basemap for map, `Basemap.Type.NATIONAL_GEOGRAPHIC`
* latitude and longitude coordinate location
* scale, level of detail displayed on `MapView`
2. Set the map to the map view, `MapView.setMap()`.


## Relevant API


* ArcGISMap
* Basemap
* Basemap.Type
* MapView
