#Set Initial Map Location#
Creates an ArcGISMap with a standard ESRI National Geographic basemap. This basemap is centred on a latitude and longitude coordinate location, which is zoomed to a specific level of detail.

![](SetInitialMapLocation.png)

##How it works##
To set an initial location:

1. Create an `ArcGISMap`, `ArcGISMap(Basemap, latitude, longitude, scale)`.
  - `Basemap`, use basemap type to access a basemap for map, `Basemap.Type.NATIONAL_GEOGRAPHIC`
  - latitude and longitude coordinate location
  - scale, level of detail displayed on `MapView`
2. Set the map to the map view, `MapView.setMap()`. 

##Features##
- ArcGISMap
- Basemap
- MapView
