# Scene Layer

Display a scene layer with 3D buildings.
  
![](SceneLayer.png)

## How it works

To add an `ArcGISSceneLayer` to a scene:


  1. Create an `ArcGISScene` and set its `Basemap` with `ArcGISScene.setBasemap()`.
  2. Create a `SceneView` and set the scene to the view, `SceneView.setScene(scene)`.
  3. Create an `ArcGISSceneLayer`:  `sceneLayer = new ArcGISSceneLayer(SCENE_LAYER_SERVICE_URL)`
  4. Add the scene layer to the scene: `Scene.getOperationalLayers().add(sceneLayer)`;


## Relevant API


  * 3D
  * ArcGISScene
  * ArcGISSceneLayer
  * ArcGISTiledElevationSource
  * Camera
  * SceneView
  * Surface

