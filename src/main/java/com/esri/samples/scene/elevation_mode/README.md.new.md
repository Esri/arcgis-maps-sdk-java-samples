# Elevation Mode

Position graphics relative to terrain.

![](ElevationMode.png)

## How it works

To position `Graphic`s using `SurfacePlacement` (DRAPED, RELATIVE,
ABSOLUTE):

1.  Create a `GraphicsOverlay`.
2.  Set the surface placement mode
    `GraphicsOverlay.getSceneProperties().setSurfacePlacement(SurfacePlacement)`.
3.  Draped, Z value of graphic has no affect and graphic is attached to
    surface
4.  Add graphics to the graphics overlay,
    `GraphicsOverlay.getGraphics.add(Graphic)`.
5.  Add the graphics overlay to the `SceneView`,
    `SceneView.getGraphicsOverlays().add(GraphicsOverlay)`.
