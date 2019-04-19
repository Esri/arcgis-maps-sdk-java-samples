# Feature Layer Extrusion.

Extrude features based on their attributes.

![](FeatureLayerExtrusion.gif)

## How it works

To extrude features from a `FeatureLayer`:

1.  Create a `ServiceFeatureTable` from an URL.
2.  Create a feature layer from service feature table.
3.  Apply a `SimpleRenderer` to the feature layer.
4.  Set `ExtrusionMode` of render,
    `renderer.getSceneProperties().setExtrusionMode(SceneProperties.ExtrusionMode.BASE_HEIGHT)`.
5.  Set extrusion expression of renderer,
    `renderer.getSceneProperties().setExtrusionExpression(“[POP2007]/ 10”)`.
