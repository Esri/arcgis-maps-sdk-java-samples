# Set max extent

Limit the view of a map to a particular area.

![Image of set max extent](SetMaxExtent.png)

## Use case

When showing map information relevant to only a certain area, you may wish to constrain the user's
ability to pan or zoom away.

## How to use the sample

The application loads with a map whose maximum extent has been set to the borders of Colorado. Note
that you won't be able to pan far from the Colorado border or zoom out beyond the minimum scale set
by the max extent. Click the "Enable Max Extent" checkbox to disable the max extent to freely pan/zoom around the map.

## How it works

1. Create an `ArcGISMap`.
2. Create a new envelope `Envelope(Point(x, y), Point(x, y))` with the required max extent.
3. Wait for the map to load, then set the maximum extent of the map with `map.setMaxExtent(envelope)`.
4. Create a new `MapView` and set the map to it.
5. Set `map.setMaxExtent(null)` to disable the maximum extent of the map.

## Relevant API

* ArcGISMap
* Envelope

## Tags

extent, limit panning, map, mapview, max extent, zoom