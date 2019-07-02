# Feature Layer Extrusion.

Extrude features based on their attributes.

<img src="FeatureLayerExtrusion.gif"/>

## How it works

To extrude features from a `FeatureLayer`:


  1. Create a `ServiceFeatureTable` from an URL.
  <li>Create a feature layer from service feature table.
  <ol>Make sure to set rendering mode to dynamic, `statesFeatureLayer.setRenderingMode(RenderingMode.DYNAMIC)`.</li>
  <li>Apply a `SimpleRenderer` to the feature layer.</li>
  <li>Set `ExtrusionMode` of render, `renderer.getSceneProperties().setExtrusionMode(SceneProperties.ExtrusionMode.BASE_HEIGHT)`.</li>
  <li>Set extrusion expression of renderer, `renderer.getSceneProperties().setExtrusionExpression("[POP2007]/ 10")`.</li>
</ol>

## Relevant API


  * FeatureLayer
  * SceneProperties
  * ServiceFeatureTable
  * FeatureLayer

