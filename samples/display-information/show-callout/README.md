#Display Callout
This sample demonstrates how to show coordinates for a clicked location on an
`ArcGISMap` in a `Callout`.

##How to use the sample
Click anywhere on the map to show a callout with the clicked location's 
coordinates.

![](DisplayCallout.png)

##How it works
To show a callout with the clicked location's coordinates:

* Use `MapView.setOnMouseClicked()` to create a click event handler.
* Create a new `Point2D` object from the events `getX()` and `getY()` 
ordinates.
* Get the points location on the map using `MapView.screenToLocation(point)`.
* Get the `MapView`'s callout using `MapView.getCallout()` and format the map
 point's `Point.getX()` and `Point.getY()` to put in the callout with 
 `Callout.setDetail()`.
* Show the callout with `Callout.showCalloutAt(point)` and dismiss on the 
next click with `Callout.dismiss()`.
 
##Features
 - ArcGISMap
 - MapView
 - Callout