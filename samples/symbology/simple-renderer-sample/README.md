##Simple Renderer##
This sample demonstrates  how to create a `SimpleRenderer` and add it to a `GraphicsOverlay`. Renderers are used to display any `Graphic`s that don't already have a symbol set. A Renderer will not override a symbol that is manually set to a Graphic.

##How to use the sample##
For simplicity, the sample starts with a predefined `SimpleRenderer`.

![](SimpleRenderer.png)

##How it works##
 To show picture marker symbols in your app:

- Create the `ArcGISMap`'s basemap
- Create the GraphicsOverlay and add it to the `MapView` using `MapView#getGraphicsOverlays` method.
- Add the map to the view via `MapView` via `MapView#setMap()`.  
- Create a `SimpleRenderer` using a `SimpleMarkerSymbol` via `SimpleRenderer#symbol` constructor. 
- Lately, set the renderer into the GraphicsOverlays using the `graphicsOverlay#setRenderer(renderer)` method.
 
##Features##
- ArcGISMap
- MapView
- Graphic
- GraphicsOverlay
- SimpleMarkerSymbol
- SimpleRenderer