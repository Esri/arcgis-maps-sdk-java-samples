##Simple Renderer##
Demonstrates how to create a SimpleRenderer and add it to a GraphicsOverlay. Renderers are used to display graphics that don't already have a symbol set. A renderer will not override a symbol that is manually set to a graphic.

##How to use the sample##
Starts with a predefined SimpleRenderer that sets a cross SimpleMarkerSymbol as a default symbol for graphics.

![](SimpleRendererSample.png)

##How it works##
 How to set a default symbol using a `SimpleRenderer`:

1. Create a `ArcGISMap`'s with `Basemap`.
2. Create a `GraphicsOverlay` and add it to the `MapView`, `MapView.getGraphicsOverlays().add()`.
3. Add the map to the view, `MapView.setMap()`.  
4. Create a simple renderer using a `SimpleMarkerSymbol`, `SimpleRenderer(Symbol)`. 
5. Lately, set the renderer to graphics overlay using `GraphicsOverlay.setRenderer(Renderer)`.
 
##Tags
- ArcGISMap
- Graphic
- GraphicsOverlay
- MapView
- Point
- SimpleMarkerSymbol
- SimpleMarkerSymbol.Style
- SimpleRenderer
