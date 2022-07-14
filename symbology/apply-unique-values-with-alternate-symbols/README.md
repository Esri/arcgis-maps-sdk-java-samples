# Apply unique values with alternate symbols

Apply a unique value with alternate symbols at different scales.

![ApplyUniqueValuesWithAlternateSymbols](ApplyUniqueValuesWithAlternateSymbols.png)

## Use case

When a layer is symbolized with unique value symbology, you can specify the visible scale range for each unique value. This is an effective strategy to limit the amount of detailed data at smaller scales without having to make multiple versions of the layer, each with a unique definition query.

Once scale ranges are applied to unique values, you can further refine the appearance of features within those scale ranges by establishing alternate symbols to different parts of the symbol class scale range.

## How to use the sample

Zoom in and out of the map to see alternate symbols at each scale. The symbology changes according to the following scale ranges: 1:0-1:5000, 1:5000-1:10000, 1:10000-1:20000. To go back to the initial viewpoint, click "Reset Viewpoint".

## How it works

1. Create a `FeatureLayer` using the service url and add it to the map's list of operational layers.
2. Create two alternate symbols:

   a. Create a symbol using `SimpleMarkerSymbol`.
   b. Convert the simple marker symbol to a `MultilayerPointSymbol` using `SimpleMarkerSymbol.toMultilayerSymbol()`.
   c. Set the valid scale range through reference properties on the multilayer point symbols by calling `multilayerPointSymbol.referenceProperties = SymbolReferenceProperties(double minScale, double maxScale)`.

3. Create a new `UniqueValue`, and add it to a new `UniqueValueRenderer` with `uniqueValueRenderer.getUniqueValues.add(uniqueValue)`
4. Set the default symbol on the unique value renderer to a multilayer point symbol with `uniqueValueRenderer.setDefaultSymbol(multilayerPointSymbol)`.
5. Set the field name on the unique value renderer with `uniqueValueRenderer.getFieldNames().add(("req_type))`.
6. Apply this unique value renderer to the feature layer with `featureLayer.setRenderer(uniqueValueRenderer)`.

## Relevant API

* MultilayerPointSymbol
* SimpleMarkerSymbol
* SymbolReferenceProperties
* UniqueValue
* 
* UniqueValueRenderer

## About the data

The [San Francisco 311 incidents layer](https://sampleserver6.arcgisonline.com/arcgis/rest/services/SF311/FeatureServer/0) in this sample displays point features related to crime incidents such as graffiti and tree damage that have been reported by city residents.

## Tags

alternate symbols, multilayer symbol, scale based rendering, simple marker symbol, symbol reference properties, symbology, unique value, unique value renderer
