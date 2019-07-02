# Display Drawing Status

Determine if a layer is done drawing.

![](DisplayDrawingStatus.png)

## How to use the sample

The progress bar in the top left displays the drawing status of the map view.

## How it works

To use the `MapView`'s `DrawStatus`:


  1. Create an `ArcGISMap`.
  2. Set the map to the view `MapView`, `MapView.setMap()`.
  3. Add `MapView.addDrawStatusChangedListener()` block and listen when the `MapView.DrawStatus` changes.


## Relevant API


  * ArcGISMap
  * Basemap
  * DrawStatus
  * DrawStatusChangedEvent
  * Envelope
  * FeatureLayer
  * MapView
  * Point

