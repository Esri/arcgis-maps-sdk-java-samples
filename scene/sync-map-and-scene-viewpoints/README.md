# Sync Map and Scene Viewpoints

Synchronize the viewpoints between a `MapView` and a `SceneView`.

The two `GeoViews` share a common `ViewPoint`. When navigating in one view, the other view is immediately updated to display the same `ViewPoint`.

![](SyncMapAndSceneViewpoints.png" />

## How to use the sample

Interact with the map by panning, zooming or rotating the map or scene view. The other view will update automatically to match your navigation. Note that the resulting maps may not look identical due to the fact the `MapView` is 2.  and the `SceneView` is 3D: but the centers and scales of each view will be kept the same.

## How it works

`MapView` and `SceneView` inherit from the `GeoView` parent class. When the `GeoView`'s viewpoint has changed, a listener can be added and allow the viewpoint of the other `GeoView` to be set and synchronized.

1.  Create a `MapView` and a `SceneView`.
2.  Add a viewpoint changed listener to each `GeoView` with `geoView.addViewpointChangedListener(viewpointChangedEvent)`.
3.  Check if the `GeoView` is being navigated with `geoView.isNavigating()`.
4.  Get the current viewpoint of the active `GeoView` with `geoView.getcurrentViewpoint(Viewpoint.Type.CENTER_AND_SCALE)`.
5.  Set the viewpoint of the other view to the active view's viewpoint with `geoView.setViewpoint(Viewpoint)`.

## Relevant API

*   GeoView

*   Viewpoint

*   ViewpointChangedEvent

## Tags

<p>2D, 3D, view synchronisation, Viewpoint, Scene, Map

