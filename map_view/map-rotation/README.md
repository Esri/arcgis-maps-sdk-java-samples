# Map Rotation

Rotate a map.

![](MapRotation.png)

## How to use the sample

Use the slider to rotate the map. If the map is not pointed north, the compass will display the current heading.
Click the compass to set the map's heading to north.

## How it works

To rotate a map:

1. Create an `ArcGISMap`.
2. Add the map to a `MapView`.
3. Use `MapView.setViewpointRotationAsync` to indicate the rotation angle.

## Relevant API

* ArcGISMap
* Compass
* Envelope
* MapView
* Point
