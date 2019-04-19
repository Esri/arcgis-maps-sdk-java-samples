# Show Labels on Layer

Add custom labels to a layer.

![](ShowLabelsOnLayer.png)

## How it works

To show labels on a feature layer:

1.  First, create a `FeatureLayer` with a `ServiceFeatureTable` using an
    online feature service.
2.  Create a `TextSymbol` to use for displaying the label text.
3.  Create a JSON string for the label definition.
4.  Set the “LabelExpressionInfo.expression” key to express what the
    text the label should display. You can use fields of the feature by
    using `$feature.field_name` in the expression.
5.  To use the text symbol, set the “symbol” key to the symbol’s JSON
    representation using `textSymbol.toJson()`.
6.  Create a label definition from the JSON using
    `LabelDefinition.fromJson(json)`.
7.  Add the definition to the feature layer with
    ` featureLayer.getLabelDefinitions().add(labelDefinition)  `.
8.  Lastly, enable labels on the layer using
    `featureLayer.setLabelsEnabled()`.
