# Terrain Exaggeration

Vertically exaggerate terrain.

![](TerrainExaggeration.gif)

## How to use the sample

Selecting an exaggeration amount from the slider will apply that to the scene's surface.

## How it works

To exaggerate a `Scene`'s `Surface`:


  1. Create an elevated surface and add it to the scene, `Surface.getElevationSources().add("elevationURL")`
  2. Add surface to the scene, ` scene.setBaseSurface(Surface)`
  3. Set exaggeration amount of the surface, `Surface.setElevationExaggeration(exaggeration)`


## Relevant API


*   ArcGISScene
*   Surface
*   ArcGISTiledElevationSource

