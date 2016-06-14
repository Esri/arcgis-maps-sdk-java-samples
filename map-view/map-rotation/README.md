#Map Rotation#
This sample demonstrates how to rotate an `ArcGISMap`. To set rotation of a map, use the `MapView`method `setViewpointRotationAsync` which takes an angle in degrees and rotates the MapView to that angle.

##How to use the sample##
There are a bunch of ways you can change the rotation of the ArcGISMap. In this sample you use a slider to rotate.

![](MapRotation.png)

##How it works##
To rotate an ArcGISMap:

- Create an ArcGISMap 
- Add the map to the view via `MapView` via `MapView#setMap()`. 
- Use the `MapView#setViewpointRotationAsync` method to indicate the rotation angle.

##Features##
- ArcGISMap
- MapView