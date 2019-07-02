# Dictionary Renderer with Feature Layer

Convert features into graphics to show them with mil2525d symbols.

The dictionary renderer creates these graphics using a mil2525d style file and the attributes attached to each 
feature within the geodatabase.

![](FeatureLayerDictionaryRenderer.png)

## How it works

To apply a `DictionaryRenderer` and display mil2525d graphics:


  1. Create a `Geodatabase(geodatabasePath)`.
*   geodatabasePath, local path to geodatabase
  2. Load the geodatabase asynchronously, `Geodatabase.loadAsync()`.
*   this will allows the application to continue working while the geodatabase loads in all feature tables
  3. Create a `SymbolDicitonary`, `SymbolDictionary(specificationType)`.
*   specificationType, this will be the mil2525d.stylx file
*   load asynchronously, `DictionarySymbol.loadAsync()`
  4. Wait for geodatabase to completely load, `Geodatabase.addDoneLoadingListener()`.
  5. Cycle through each `GeodatabaseFeatureTable` from geodatabase, `Geodatabase.getGeodatabaseFeatureTables()`.
  6. Create a `FeatureLayer` from each table within the geodatabase, `FeatureLayer(GeodatabaseFeatureTable)`.
*   load asynchronouly, `FeatureLayer.loadAsync()`
  7. Wait for each layer to load, `FeatureLayer.addDoneLoadingListener`.
  8. Check if layer is last layer to load and create `Envelope` from each layer.
*   set this envelope to be the `Viewpoint` of the map view, `MapView.setViewpoint(new Viewpoint(Envelope))`
  9. Add feature layer to map, `Map.getOperationalLayers().add(FeatureLayer)`.
  10. Create `DictionaryRenderer(SymbolDictionary)` and attach to feature layer, `FeatureLayer.setRenderer(DictionaryRenderer)`.


## Relevant API


*   ArcGISMap
*   Basemap
*   DictionaryRenderer
*   Envelope
*   FeatureLayer
*   Geodatabase
*   GeometryEngine
*   MapView
*   SymbolDictionary

