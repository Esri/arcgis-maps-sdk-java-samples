# Symbolize Shapefile

Override the default rendering of a shapefile.

![](SymbolizeShapefile.png)

## How to use the sample

Press the toggle button to switch between red and yellow symbols and the
default renderer.

## How it works

To change the renderer of a shapefile feature layer:

1.  Create a `ShapefileFeatureTable` passing in the URL of a shapefile.
2.  Create a `FeatureLayer` using the `ShapefileFeatureTable`.
3.  Create a `SimpleLineSymbol` and `SimpleFillSymbol` (uses the line
    symbol).
4.  Make a `SimpleRenderer` with the `SimpleFillSymbol`.
5.  To apply the renderer, use `featureLayer.setRenderer(renderer)`.
6.  To go back to the default renderer, use
    `featureLayer.resetRenderer()`.
