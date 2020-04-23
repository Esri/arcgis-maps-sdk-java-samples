# Map rotation

Rotate a map.

![Image of map rotation](MapRotation.png)

## Use case

A user may wish to view the map in an orientation other than north-facing.

## How to use the sample

Use the slider to rotate the map. If the map is not pointed north, the compass will display the current heading. Click the compass to set the map's heading to north.

## How it works

1. Create a `ArcGISMap` and set it to a `MapView`.
2. Use `setViewpointRotationAsync(...)` to set the rotation angle of the map.

## Relevant API

* ArcGISMap
* MapView

## Tags

compass, rotate, rotation, viewpoint
