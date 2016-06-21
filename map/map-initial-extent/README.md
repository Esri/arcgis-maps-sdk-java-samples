#Map Initial Extent#
Demonstrates how to display an ArcGISMap at a specific view point.

##How to use the sample##
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
