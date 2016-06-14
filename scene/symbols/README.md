#Symbols#
Demonstrates how to create symbols with different 3D shapes. Includes tetrahedrons, cubes, spheres, diamonds, cylinders, and cones.

##How to use the sample##

![](ExtrudeGraphics.png)

##How it works##
To create a `SimpleMarkerSceneSymbol` with a 3D shape:

1. Create a `GraphicsOverlay`: `graphicsOverlay = new GraphicsOverlay()`.
2. Create a `SimpleMarkerSceneSymbol` with one of the factory methods, specifying its color and dimensions: `SimpleMarkerSceneSymbol redConeSymbol = SimpleMarkerSceneSymbol.createCone(0xFFFF0000, 200, 200)`. You can also use the symbol constructor: `new SimpleMarkerSceneSymbol(SimpleMarkerSceneSymbol.Style.CONE, 0xFFFF0000, 200, 200, 200, SceneSymbol.AnchorPosition.CENTER)`
3. Create a `Graphic` from the symbol: `graphic = new Graphic(location, redConeSymbol)`.
4. Add the graphic to the graphics overlay: `graphicsOverlay.getGraphics().add(graphic)`.
5. Add the graphics overlay to the `SceneView`: `sceneView.getGraphicsOverlays().add(graphicsOverlay)`.

##Features##
- ArcGISScene
- Graphic
- GraphicsOverlay
- SimpleMarkerSceneSymbol