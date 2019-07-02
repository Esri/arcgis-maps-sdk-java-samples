# Change Feature Layer Renderer

Change how a feature layer looks with a renderer.

![](ChangeFeatureLayerRenderer.gif)

## How to use the sample

Use the buttons in the control panel to change the renderer.

## How it works

To change the `FeatureLayer`'s `Renderer`:

* Create a `ServiceFeatureTable` from a URL.
* Create a feature layer from the service feature table.
* Create a new renderer (in this case, a `SimpleRenderer`).
* Change the feature layer's renderer using `FeatureLayer.setRenderer(SimpleRenderer)`.

## Relevant API

* ArcGISMap
* FeatureLayer
* MapView
* Renderer
* ServiceFeatureTable
* SimpleRenderer
