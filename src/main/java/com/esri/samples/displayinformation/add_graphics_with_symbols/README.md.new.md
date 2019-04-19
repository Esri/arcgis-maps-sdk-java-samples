# Add Graphics with Symbols

Draw simple graphics with marker, line, polygon, and text symbols.

![](AddGraphicsWithSymbols.png)

## How it works

To display a `Graphic` using a symbol:

1.  Create a `GraphicsOverlay` and add it to the `MapView`,
    `MapView.getGraphicsOverlay.add()`.
2.  To create a graphic using a `SimpleMarkerSymbol`.
3.  create a `Point` where the graphic will be located
4.  To create a graphic using a `SimpleLineSymbol`.
5.  create a `PointCollection` that will hold all the points that make
    up the line
6.  To create a graphic using a `SimepleFillSymbol`.
7.  create a point collection that will hold all the points that make up
    the line
8.  To create a graphic using a `TextSymbol`.
9.  create a point where the graphic will be located
10. Add graphic to graphics overlay to display it to the map view.
