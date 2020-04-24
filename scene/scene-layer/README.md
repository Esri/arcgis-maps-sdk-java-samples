# Scene layer

Add a scene layer to a scene.

![Image of scene layer](SceneLayer.png)

## Use case

Each scene layer added to a scene can assist in performing helpful visual analysis. For example, if presenting the results of a shadow analysis of a major metropolitan downtown area in 3D, adding a scene layer of 3D buildings to the scene that could be toggled on/off would help to better contextualize the source of the shadows.

## How to use the sample

When launched, this sample displays a scene service with an `ArcGISSceneLayer`. Pan and zoom to explore the scene.

## How it works

1. Create an `ArcGISScene` and set its `Basemap` with `ArcGISScene.setBasemap()`.
2. Create a `SceneView` and set the scene to the view with `sceneView.setScene(scene)`.
3. Create an `ArcGISSceneLayer` using a data source URI: `new ArcGISSceneLayer(Uri)`.
4. Add the new scene layer to the scene as an operational layer with `ArcGISScene.getOperationalLayers().add(sceneLayer)`.

## About the data

The scene shows a [buildings layer in Brest, France](https://tiles.arcgis.com/tiles/P3ePLMYs2RVChkJx/arcgis/rest/services/Buildings_Brest/SceneServer/layers/0) hosted on ArcGIS Online.

## Relevant API

* ArcGISScene
* ArcGISSceneLayer
* SceneView

## Tags

3D, layer, scene
