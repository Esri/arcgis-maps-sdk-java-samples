# Change Basemap

Change a map’s basemap.

A basemap is beneath all other layers on an ArcGISMap and is used to
provide visual reference to other layers.

![](ChangeBasemap.png)

## How to use the sample

Choose any basemap from the list view.

## How it works

To change the `ArcGISMap`’s `Basemap`:

1.  Create an ArcGIS map, `ArcGISMap(Basemap, latitude, longitude,
    scale)`.
2.  Basemap, use basemap type to access a basemap for map,
    `Basemap.Type.NATIONAL_GEOGRAPHIC`
3.  Set the ArcGIS map to the map view.
4.  Choose a new `Basemap.Type` and set it to the ArcGIS map to change
    it.
