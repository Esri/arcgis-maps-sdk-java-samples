#Display Callout
Demonstrates how to show coordinates for a clicked location on an ArcGISMap in a Callout.

##How to use the sample
Click anywhere on the map to show a callout with the clicked location's coordinates.

![](ShowCallout.png)

##How it works
To show a `Callout` with the clicked location's coordinates:

1. Use `MapView.setOnMouseClicked()` to create a click event handler.
2. Create a new Point2D object from the events getX() and getY() coordinates.
3. Get the `Point`s location on the map, `MapView.screenToLocation(Point2D)`.
4. Get the `MapView`'s callout, `MapView.getCallout()`.
5. Use `Callout.setDetail()` to display map's point `Point.getX()` and `Point.getY()` to screen.
5. Show the callout, `Callout.showCalloutAt(point)`, and dismiss on the next click with `Callout.dismiss()`.
 
##Features
 - ArcGISMap
 - Callout
 - MapView
 - Point