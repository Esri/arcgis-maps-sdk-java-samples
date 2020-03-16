# Group layers

Group a collection of layers together and toggle their visibility as a group.

![Image of group layers](GroupLayers.png)

## Use case  

Group layers communicate to the user that layers are related and can be managed together.

In a land development project, you might group layers according to the phase of development.

## How to use the sample

The layers in the map will be displayed in a table of contents. Toggle the checkbox next to a layer's name to change its visibility. Turning a group layer's visibility off will override the visibility of its child layers.

## How it works

1. Create an empty `GroupLayer`.
2. Add a child layer to the group layer's layers collection.
3. To build the table of contents:
  - Check that the layer is a group layer.
  - Use `groupLayer.fetchLegendInfosAsync()` to get a `LegendInfo` with the layer's name to display.
  - If `showChildrenInLegend` is true, use `getSubLayerContents` to get a list of `LayerContent`s for each child with legend display information.
4. To toggle the visibility of the group, simply change the group layer's visibility property.

## Relevant API

* GroupLayer

## Additional information

The full extent of a group layer may change when child layers are added/removed. Group layers do not have a spatial reference, but the full extent will have the spatial reference of the first child layer.

Group layers can be saved to web scenes. In web maps, group layers will be ignored.

## Tags

group layer, layers
