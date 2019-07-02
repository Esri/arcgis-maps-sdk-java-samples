# Map Spatial Reference

Specify a map's spatial reference.

Operational layers will automatically project to this spatial reference if possible.

![](MapSpatialReference.png)

## How to use the sample

ArcGISMapImageLayer is added to map with default spatial reference of GCS*WG*1984. (WKID: 4326). By setting the ArcGISMap to a spatial reference of world bonne (WKID: 54024), the ArcGISMapImageLayer gets re-projected to map's spatial reference.

## How it works

To set a `SpatialReference` and project that to all operational layers of `ArcGISMap`:

1.  Create an ArcGIS map passing in a spatial reference, `ArcGISMap(SpatialReference.create(54024))`.
2.  Create an `ArcGISMapImageLayer` as a `Basemap`.
3.  Set basemap to ArcGIS map.
4.  Set ArcGIS map to the `MapView`.
*   the ArcGIS map image layer will now use the spatial reference set to the map and not it's default spatial reference

## Relevant API

*   ArcGISMap
*   ArcGISMapImageLayer
*   Basemap
*   MapView
*   SpatialReference
