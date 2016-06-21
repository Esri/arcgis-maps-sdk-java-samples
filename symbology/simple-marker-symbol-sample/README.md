##Simple Marker Symbol##
Demonstrates how to add a SimpleMarkerSymbol to your ArcGISMap.

##How to use the sample##
For simplicity, the sample starts with a predefined SimpleMarkerSymbol set as a red circle.

![](SimpleMarkerSymbol.png)

##How it works##
 To display a `SimpleMarkerSymbol`:

1. Create a `ArcGISMap`'s with `Basemap`.
2. Create a `GraphicsOverlay` and add it to the `MapView`,`MapView.getGraphicsOverlays().add()`.
3. Add the map to the view, `MapView.setMap()`.  
4. Create a `SimpleMarkerSymbol(SimpleMarkerSymbol.Style, color, size)`.
  -  style, how the symbol will be displayed (circle, square, etc.)
  -  color, color that the symbol will be displayed
  -  size, size of the symbol
5. Lately, create a `Graphic(Geometry, Symbol)` method and add it to the graphics overlay.
 
##Features##
- ArcGISMap
- Graphic
- GraphicsOverlay
- MapView
- Point
- SimpleMarkerSymbol
- SimpleMarkerSymbol.Style
