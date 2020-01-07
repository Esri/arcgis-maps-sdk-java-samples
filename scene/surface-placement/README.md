# Surface placement

Position graphics relative to terrain.

![](SurfacePlacement.png)

## Use case

Depending on the use case, data might be displayed at a consistent, absolute height (e.g. flight data recorded relative to sea level), at a relative height to the terrain (e.g. transmission lines positioned relative to the ground), or draped directly onto the terrain (e.g. location markers, area boundaries).

## How to use the sample

The application loads a scene showing three points that use the individual surface placement rules (Absolute, Relative, and either Draped Billboarded or Draped Flat). Use the control to toggle the draped mode, then explore the scene by zooming in/out and by panning around to observe the effects of the surface placement rules.

## How it works

1. Create a `GraphicsOverlay`.
2. Set the surface placement mode `GraphicsOverlay.getSceneProperties().setSurfacePlacement(SurfacePlacement)`.
    `ABSOLUTE`, position graphic using only its Z value.
    `RELATIVE`, position graphic using its Z value plus the elevation of the surface.
    `DRAPED_BILLBOARDED`, position graphic upright on the surface and always facing the camera, not using its Z value.
    `DRAPED_FLAT`, position graphic flat on the surface, not using its Z value.
3. Add graphics to the graphics overlay, `GraphicsOverlay.getGraphics.add(Graphic)`.
4. Add the graphics overlay to the `SceneView`, `SceneView.getGraphicsOverlays().add(GraphicsOverlay)`.

## Relevant API

* Graphic
* GraphicsOverlay
* LayerSceneProperties.SurfacePlacement
* SceneProperties
* Surface

## About the data

The scene launches with a view of northern Snowdonia National Park. Three points are shown hovering with positions defined by each of the different surface placement modes.

## Tags

3D, absolute, altitude, draped, elevation, floating, relative, scene, sea level, surface placement
