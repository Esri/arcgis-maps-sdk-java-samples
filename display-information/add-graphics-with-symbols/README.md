#Add Graphics with Symbols#
This sample demonstrates how to add points, polylines, and polygons as graphics. It also demostrates how to set a symbol to a renderer to the graphics and add to a `GraphicsOverlay`. The sample also adds `TextSymbol` to represent text as symbols on the GraphicsOverlay.

![](AddGraphicsWithSymbols.png)

##How it works##
To add a `Graphic` that displays a symbol:

1. Create a `GraphicsOverlay` and add it to the `MapView`, `MapView.getGraphicsOverlay.add()`.
2. To create a `Graphic` using a `Point`.
  - create a point where the graphic will be located
  - create a `SimpleMarkerSymbol` that will display this symbol at that point
  - assign point and symbol to graphic, `Graphic(point, symbol)`
3. To create a graphic using a `Polyline`:
  - create a `PointCollection` that will hold all the points that make up the line
  - create a `Polyline` using the point collection, `Polyline(PointCollection)`
  - create a `SimpleLineSymbol` that will display a symbol over those collected points
  - assign polyline and symbol to graphic, `Graphic(polyline, symbol)`
4. To create a graphic using a `Polygon`.
  - create a point collection that will hold all the points that make up the line
  - create a `Polygon` using the point collection, `Polygon(PointCollection)`
  - create a `SimpleLineSymbol` that will display a symbol that outlines those points collected
  - create a `SimepleFillSymbol` using line symbol from above, that will fill the region in between the points collected with a single color 
  - assignn polygon and symbol to graphic, `Graphic(polygon, symbol)`
5. Add graphic to graphics overlay to display it to the map view.

##Features##
- Graphic
- GraphicsOverlay
- MapView
- Point
- PointCollection
- Polygon
- Polyline
- SimepleFillSymbol
- SimpleLineSymbol
- SimpleMarkerSymbol
