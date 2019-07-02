# Display Scene

Display a 3.  scene with terrain and imagery.

![](DisplayScene.png)

## How it works

To create an `ArcGISScene` with elevation data:

1.  Create an ArcGIS scene and set the `Basemap` with `ArcGISScene.setBasemap()`.
2.  Create a `SceneView` and set the scene to the view, `SceneView.setScene(scene)`.
3.  Create a `Surface` and add a `ArcGISTiledElevationSource`, `Surface.getElevationSources().add()`.
4.  Set the surface as the scene's base surface: `ArcGIScene.setBaseSurface(surface)`.

## Relevant API

*   ArcGISScene
*   ArcGISTiledElevationSource
*   Camera
*   SceneView
*   Surface
