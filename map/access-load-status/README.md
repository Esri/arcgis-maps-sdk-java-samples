#Access Load Status#
Demonstrates how to tell what the ArcGISMap's load status is. An ArcGISMap is considered loaded once it has a valid SpatialReference and it has been set to the MapView.

##How to use the sample##
The sample provides an information area which displays what the ArcGISMap's load status is. Click on the button to reload the ArcGISMap.

![](AccessLoadStatus.png)

##How it works##
To access to the ArcGISMap load status.

1. Create an ArcGISMap.
2. Use `ArcGISMap.addLoadStatusChangedListener()` and `ArcGISMap.getNewLoadStatus()`t o display the `LoadStatus` of the map.
3. Set the ArcGISMap to the MapView to began loading map.

##Features##
- ArcGISMap 
- Basemap
- MapView
- LoadStatus
