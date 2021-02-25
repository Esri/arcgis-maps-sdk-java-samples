# Display 3D labels in scene

Display custom labels in a 3D scene.

![Display 3D Labels in Scene](display-3d-labels-in-scene.png)

## Use case

Labeling features is useful to visually display information or attributes on a scene. For example, city officials or maintenance crews may want to show installation dates of features of a gas network.

## How to use the sample

Pan and zoom to explore the scene. Notice the labels showing installation dates of features in the 3D gas network.

## How it works

1. Create an `ArcGISScene` using a URL.
2. Apply the scene to an `SceneView` and load it.
3. After loading is complete, obtain the `FeatureLayer` from the scene's `operationalLayers`.
4. Set the feature layer's `labelsEnabled` property to `true`.
5. Create an `TextSymbol` to use for displaying the label text.
6. Create a JSON string for the label definition.
    * Set the "labelExpressionInfo.expression" key to define what text the label should display. You can use fields of the feature by using `$feature.NAME` in the expression.
    * To use a text symbol, set the "symbol" key in the JSON representation using `TextSymbol.toJSON()`.
5. Create a label definition from the JSON using `AGSJSONSerializable.fromJSON(_:)`. 
6. Add the definition to the feature layer's `labelDefinitions` array.

## Relevant API

* ArcGISScene
* FeatureLayer
* LabelDefinition
* SceneView
* TextSymbol

## About the data

This sample shows a [New York City infrastructure](https://www.arcgis.com/home/item.html?id=850dfee7d30f4d9da0ebca34a533c169) scene hosted on ArcGIS Online.

## Tags

3D, attribute, buildings, label, model, scene, symbol, text, URL, visualization