# Change Basemap

Change a map's basemap.

A basemap is beneath all other layers on an ArcGISMap and is used to provide visual reference to other layers.

![](ChangeBasemap.png)

## How to use the sample

Choose any basemap from the list view.

## How it works

To change the `ArcGISMap`'s `Basemap`:

1.  Create an ArcGIS map, `ArcGISMap(Basemap, latitude, longitude, scale)`.
*   Basemap, use basemap type to access a basemap for map, `Basemap.Type.NATIONAL_GEOGRAPHIC`
*   latitude and longitude coordinate location
*   scale, level of detail displayed on `MapView`
2.  Set the ArcGIS map to the map view.
3.  Choose a new `Basemap.Type` and set it to the ArcGIS map to change it.

## Relevant API

*   ArcGISMap
*   Basemap
*   Basemap.Type
*   MapView
