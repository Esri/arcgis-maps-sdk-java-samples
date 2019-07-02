# Display Layer View State

Determine if a layer is currently visible.

![](DisplayLayerViewState.png)

## How to use the sample

The view state of a layer changes while the layer is loading, like the start of the application. If you pan or zoom the map, the view state of some layers should also change.

The LayerViewStatus could be:

*   ACTIVE
*   ERROR
*   LOADING
*   NOT_VISIBLE
*   OUT_OF_SCALE
*   UNKNOWN

## How it works

To get a layer's view state:

1.  Create an `ArcGISMap`.
2.  Set the map to the `MapView`, `MapView.setMap()`.
3.  Add the `MapView.addLayerViewStateChangedListener()` property and listen when the `Layer.getLayerViewStatus()` changes.

## Relevant API

*   ArcGISMap
*   ArcGISMapImageLayer
*   Layer
*   LayerViewStatus
*   LayerViewStateChangedEvent
*   MapView
*   Viewpoint
