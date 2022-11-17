# Map rotation

Rotate a map.

![Image of map rotation](MapRotation.png)

## Use case

A user may wish to view the map in an orientation other than north-facing.

## How to use the sample

Press the A and D keys to rotate the map. The text will update to display the map's rotation value. If the map is not pointed north, the compass will display the current heading. Click the compass to set the map's heading to north.

## How it works

1. Create a `ArcGISMap` and set it to a `MapView`.
2. Use `mapView.mapRotationProperty().get()` to get the map's rotation angle.
3. Create a `new Compass(mapView)` to show the direction of north.

## Relevant API

* ArcGISMap
* MapView

## Tags

compass, rotate, rotation, toolkit
