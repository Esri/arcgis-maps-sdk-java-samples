#Display Scene#
Demonstrates how to display a scene with an elevation source. An elevation source allows objects to be viewed in 3D, like this picture of f Mt. Everest.

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
