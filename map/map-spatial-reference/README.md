#ArcGISMap Spatial Reference#
This sample demonstrates how you can set the `SpatialReference` on a `ArcGISMap` and all the operational layers would project accordingly.

##How to use the sample##
The `ArcGISMap` is initialized with the spatial reference as world bonne (WKID: 54024) using the SpatialReference initializer. Hence the `ArcGISMapImageLayer` is added, with default spatial reference as GCS_WGS_1984 (WKID: 4326), gets re-projected to map's spatial reference.

![](ArcGISSpatialReference.png)

##How it works##
To project operational layers with different spatial reference to the ArcGISMap:

- Create an ArcGISMap using the `SpatialReference#create(54024)` method.  
- Create an ArcGISMapImageLayer with default spatial reference and set it as a `Basemap`.
- The last set the Basemap into the ArcGISMap and the ArcGISMap to the `MapView`.

##Features##
- ArcGISMap
- MapView
- ArcGISMapImageLayer
- Basemap
- SpatialReference