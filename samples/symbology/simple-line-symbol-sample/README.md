##Simple Line Symbol##
This sample demonstrates how to change a `SimpleLineSymbol` colour and style properties.

##How to use the sample##
For simplicity, the sample starts with a `Polyline` symbol with a default colour, width and style. Use the sample dropdowns to change the `SimpleLineSymbol` colour, width and style properties.

![](SimpleLineSymbol.png)

##How it works##
 To show picture marker symbols in your app:

- Create the `ArcGISMap`'s basemap
- Create the GraphicsOverlay and add it to the `MapView` using `MapView#getGraphicsOverlays` method.
- Add the map to the view via `MapView` via `MapView#setMap()`. 
- Create a `Polyline` using `PointCollection` to indicate the `Graphic`'s geometry. 
- Create a `SimpleLineSymbol` to indicate your line style and colour.
- Lately, create a `Graphic` via `Graphic(geometry, symbol)` method and add it to the `GraphicsOverlay`.
 
##Features##
- ArcGISMap
- MapView
- Graphic
- Polyline
- PointCollection
- GraphicsOverlay
- SimpleLineSymbol
 