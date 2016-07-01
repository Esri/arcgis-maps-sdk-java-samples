#Map Spatial Reference#
Demonstrates how you can set the spatial reference on a ArcGISMap and all the operational layers will project accordingly.

##How to use the sample##
ArcGISMapImageLayer is added to map with default spatial reference of GCS_WGS_1984 (WKID: 4326). By setting the ArcGISMap to a spatial reference of world bonne (WKID: 54024), the ArcGISMapImageLayer gets re-projected to map's spatial reference.

![](MapSpatialReference.png)

##How it works##
To set a `SpatialReference` and project that to all operational layers of `ArcGISMap`:

1. Create an ArcGIS map passing in a spatial reference, `ArcGISMap(SpatialReference.create(54024))`.  
2. Create an `ArcGISMapImageLayer` as a `Basemap`.
3. Set basemap to ArcGIS map.
4. Set ArcGIS map to the `MapView`.
  - the ArcGIS map image layer will now use the spatial reference set to the map and not it's default spatial reference

##Features##
- ArcGISMap
- ArcGISMapImageLayer
- Basemap
- MapView
- SpatialReference
