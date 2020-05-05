# Extrude graphics

Extrude graphics based on an attribute value.

![Image of extruded graphics](ExtrudeGraphics.png)

## Use case

Graphics representing features can be vertically extruded to represent properties of the data that might otherwise go unseen. Extrusion can add visual prominence to data beyond what may be offered by varying the color, size, or shape of symbol alone. For example, graphics representing wind turbines in a wind farm application can be extruded by a real-world "height" attribute so that they can be visualized in a landscape. Likewise, census data can be extruded by a thematic "population" attribute to visually convey population levels across a country.

## How to use the sample

Run the sample. Note that the graphics are extruded to the level set in their height property.

## How it works

1. Create a `GraphicsOverlay` and `SimpleRenderer`.
2. Get the renderer's `SceneProperties` using `simpleRenderer.getSceneProperties()`.
3. Set the extrusion mode for the renderer with `SceneProperties.setExtrusionMode(ExtrusionMode)`.
4. Specify the attribute name of the graphic that the extrusion mode will use, `SceneProperties.setExtrusionExpression("[HEIGHT]")`.
5. Set the renderer on the graphics overlay, `GraphicsOverlay.setRenderer(Renderer)`.
6. Create graphics with their attribute set, `Graphic.getAttributes().put("HEIGHT", Z Value)`.

## Relevant API

* Renderer
* SceneProperties.ExtrusionMode
* SceneProperties
* SimpleRenderer

## About the data

Data is hard coded in this sample as a demonstration of how to create and set an attribute to a graphic. To extrude graphics based on pre-existing attributes (e.g. from a feature layer) see the FeatureLayerExtrusion sample.

## Tags

3D, extrude, extrusion, height, scene, visualization
