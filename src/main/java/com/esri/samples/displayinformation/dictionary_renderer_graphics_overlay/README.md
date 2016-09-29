#Dictionary Renderer with Graphics Overlay#
Demonstrates how to apply a dictionary renderer to a graphics overlay and display mil2525d graphics. 
The dictionary renderer creates these graphics using a local mil2525d style file and a XML file with key, 
value attributes for each graphic.

![](DictionaryRendererGraphicsOverlay.png)

##How it works##
To apply a `DictionaryRenderer` and display mil2525d graphics:

1.  Create a `SymbolDicitonary`, `SymbolDictionary(specificationType, dictionaryPath)`.
  - specificationType, this will be the mil2525d.stylx local file
  - dictionaryPath,  path to the mil2525d.stylx local file
2. Load the dictionary asynchronouly, `DictionarySymbol.loadAsync()`.
  - this will allows the application to continue working while the dictionary loads all symbol primitives found within the mil2525d specification
3. Create a `DictionaryRenderer`, `DictionaryRenderer(SymbolDictionary)`.
  - apply it to the `GraphicsOverlay.setRenderer(DictionaryRenderer)`
4. Parse through local XML file creating a mapping of key,value pairs for each block of attributes.
  - use the name of the attribute as key and text within that attribute as the value
5. Create a graphic for each mapping of attributes.
  - _wkid key, holds the geometry's spatial reference
  - _control_points, creates the shape of the geometry
  - other attributes explain to dictionary symbol how to display graphic
  - add graphic to `GraphicsOverlay.getGraphics().add(Graphic)`

##Features
- ArcGISMap
- Basemap
- DictionaryRenderer
- Graphic
- GraphicsOverlay
- LayerViewStatus
- MapView
- Point
- PointCollection
- Polygon
- Polyline
- SymbolDictionary

