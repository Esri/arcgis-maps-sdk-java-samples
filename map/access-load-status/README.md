#Access Load Status#
Demonstrates how to tell what the ArcGISMap's load status is. An ArcGISMap is considered loaded once it has a valid SpatialReference and it has been set to the MapView.

##How to use the sample##
The sample provides an information area which displays what the ArcGISMap's load status is. Click on the button to reload the ArcGISMap.

![](AccessLoadStatus.png)

##How it works##
To access the `ArcGISMap`'s `LoadStatus`:

1. Create an ArcGIS map.
2. Use `ArcGISMap.addLoadStatusChangedListener()` and `ArcGISMap.getNewLoadStatus()` to display the load status of the map.
3. Set the ArcGIS map to the `MapView` to began loading.

##Tags
- ArcGISMap 
- Basemap
- MapView
- LoadStatus
