# Feature Layer Rendering Mode (Scene)

Render features statically or dynamically in 3D.

![](FeatureLayerRenderingModeScene.gif)

## How it works

To change `FeatureLayer.RenderingMode` using `LoadSettings`:


1.  Create a `ArcGISScene`.
2.  Set preferred rendering mode to scene, `sceneBottom.getLoadSettings().setPreferredPointFeatureRenderingMode(FeatureLayer.RenderingMode.DYNAMIC)`.
  
*   Can set preferred rendering mode for `Points`, `Polylines`, or `Polygons`.
*   `Multipoint` preferred rendering mode is the same as point.
3.  Set scene to `SceneView`, `sceneViewBottom.setArcGISScene(sceneBottom)`.
4.  Create a `ServiceFeatureTable` from a point service, `new ServiceFeatureTable("http://sampleserver6.arcgisonline.com/arcgis/rest/services/Energy/Geology/FeatureServer/0");`.
5.  Create `FeatureLayer` from table, `new FeatureLayer(poinServiceFeatureTable)`.
6.  Add layer to scene, `sceneBottom.getOperationalLayers().add(pointFeatureLayer.copy())`
  
*   Now the point layer will be rendered dynamically to scene view.


## Relevant API


*   ArcGISScene
*   Camera
*   FeatureLayer
*   FeatureLayer.RenderingMode
*   LoadSettings
*   Point
*   Polyline
*   Polygon
*   ServiceFeatureTable



