#Identify Graphics#
Demonstrates how to create a Graphic and add it to a GraphicOverlay where it can be identified from the MapView.

##How to use the sample##
When you click on a graphic on the map, you should see an alert.

![](IdentifyGraphics.png)

##How it works##
To identify a `Graphic` from the `MapView`.

1. Create a `GraphicsOverlay` and add it to the MapView.
2. Add Graphic along with a `SimpleFillSymbol`. 
3. Add the graphic to the graphics overlay. 
4. Identify the graphics on the specified location using the `MapView.identifyGraphicsOverlayAsync(graphicsOverlay, point, tolerance, max results)` method.

##Features##
- Graphic
- GraphicsOverlay
- MapView
- PointCollection
- Polygon
- SimpleFillSymbol
