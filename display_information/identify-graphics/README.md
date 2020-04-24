# Identify graphics

Identify a graphic to get further information about the object.

![Image of identify graphics](IdentifyGraphics.png)

## Use case

A user may wish to select a graphic on a map to view relevant information about it.

## How to use the sample

Select a graphic to identify it. You will see an alert message displayed.

## How it works

1. Use the `.setOnMouseClicked()` method to listen to clicks on a `MapView`. 
2. When the map view is clicked, use the event to create a `Point` from the location clicked on the map.
3. Identify the graphic on the map view with `MapView.identifyGraphicsOverlayAsync(graphicsOverlay, pointClicked, tolerance, max results)`.

## Relevant API

* Graphic
* GraphicsOverlay
* MapView
* Point

## Tags

graphics, identify