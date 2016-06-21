#Map Initial Extent#
Demonstrates how to display an ArcGISMap at a specific view point.

##How to use the sample##
A viewpoint is constructed from an `Envelope` defined by minimum (x,y) and maximum (x,y) `Point`'s values. The map's initialViewpoint is set to this viewpoint before the map is loaded by the ArcGISMap. 
As application is loading, initial view point is set and map view zooms to that location.

![](MapInitialExtent.png)

##How it works##
To set an initial `Viewpoint`:

1. Create an `ArcGISMap`.  
2. Create a view point using an `Envelope`, `Viewpoint(Envelope)`.
3. Set the starting location of the ArcGISMap, `ArcGISMap.setInitialViewpoint(Viewpoint)`.
4. Set the ArcGISMap to the `MapView`.

##Features##
- ArcGISMap
- Envelope
- MapView
- Point
- Viewpoint
