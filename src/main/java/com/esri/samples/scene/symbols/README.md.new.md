# Symbols

Create graphics with simple 3D shapes.

Includes tetrahedrons, cubes, spheres, diamonds, cylinders, and cones.

![](Symbols3D.png)

## How it works

To create a `SimpleMarkerSceneSymbol` with a 3D shape:

1.  Create a `GraphicsOverlay`.
2.  Create a `SimpleMarkerSceneSymbol(Style, color, width, height,
    depth, AnchorPosition)`.
3.  You can also use `SimpleMarkerSceneSymbol.createCone(color,
    diameter, height)`
4.  Create a graphic using the symbol, `Graphic(Geometry, Symbol)`.
5.  Add the graphic to the graphics overlay,
    `GraphicsOverlay.getGraphics().add(Graphic)`.
6.  Add the graphics overlay to the `SceneView`,
    `SceneView.getGraphicsOverlays().add(GraphicsOverlay)`.
