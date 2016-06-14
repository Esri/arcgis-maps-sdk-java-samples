#Display Scene#
Demonstrates how to display an scene with elevation data.

![](DisplayScene.png)

##How it works##
To create an `ArcGISScene` with elevation data:

1. Create an `ArcGISScene` and set the basemap with `scene.setBasemap()`.
2. Create a `SceneView` and set the scene to the view with `sceneView.setScene(scene)`.
3. Create a `Surface` and add a `ArcGISTiledElevationSource` to `surface.getElevationSources()`.
4. Set the surface as the scene's base surface: `scene.setBaseSurface(surface)`.

##Features##
- ArcGISScene
- ArcGISTiledElevationSource
- SceneView
- Surface
