# Scene Layer Selection

Select clicked features in a scene.

![](SceneLayerSelection.png">

## How to use the sample

Click on a building in the scene layer to select it. Unselect buildings by clicking away from the buildings.

## How it works

To select geoelements in a scene layer:


  1. Create an `ArcGISSceneLayer` passing in the URL to a scene layer service.
  2. Use `sceneView.setOnMouseClicked` to get the screen click location `Point2D`.
  3. Call `sceneView.identifyLayersAsync(sceneLayer, point2D, tolerance, false, 1)` to identify features 
  in the scene.
  4. From the resulting `IdentifyLayerResult`, get the list of identified `GeoElement`s with
   `result.getElements()`.
   5. Get the first element in the list, checking that it is a feature, and call `sceneLayer.selectFeature
   (feature)` to select it.


## Relevant API 


  * ArcGISSceneLayer
  * GeoElement
  * IdentifyLayerResult

