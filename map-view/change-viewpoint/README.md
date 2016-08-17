#Change Viewpoint#
Demonstrates different ways in which you can change the Viewpoint, visible area, of an ArcGISMap.


##How to use the sample##
The `MapView` provides different methods you can use to set the viewpoint. 
 - London button pans to London given a certain time length, `MapView.setViewpointWithDurationAsync()`.
 - Waterloo button centers at a point and set a distance from the ground using a scale, `MapView.setViewpointCenterAsync()`.
 - Westminster button set viewpoint given some type of geometry, `MapView.setViewpointGeometryAsync()`.
 
Below are some other ways to set a viewpoint.
- setViewpoint
- setViewpointAsync
- setViewpointCenterAsync
- setViewpointGeometryAsync
- setViewpointRotationAsync
- setViewpointScaleAsync


![](ChangeViewpoint.png)

##How it works##
To change the `Viewpoint`:

1. Create an `ArcGISMap`. 
2. Set the map to the `MapView`, `MapView.setMap()`. 
3. Change the view point with `MapView.setViewPoint()` or any method mention above.

##Tags
- ArcGISMap
- Basemap
- Point
- SpatialReference
- Viewpoint
