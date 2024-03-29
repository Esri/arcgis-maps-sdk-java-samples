# Display draw status

Get the draw status of your map view or scene view to know when all layers in the map or scene have finished drawing.

![Image of display drawing status](DisplayDrawingStatus.png)

## Use case

You may want to display a loading indicator while layers are loading, which displays on `DrawStatus.IN_PROGRESS`.

## How to use the sample

Pan and zoom around the map. Observe how the progress indicator is displayed and then hidden, indicating when drawing has completed.

## How it works

1. Create a `MapView` and a progress indicator.
2. Bind the visibility of the progress indicator to the `drawStatusProperty`.

## Relevant API

* ArcGISMap
* DrawStatus
* MapView

## Tags

draw, loading, map, render
