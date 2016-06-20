##Unique Value Renderer##
This sample demonstrates how to use an `UniqueValueRenderer` to style Features from a `FeatureLayer` using different Symbols. A UniqueValueRenderer displays `UniqueValues` on a `ArcGISMap`. A UniqueValue stores a label, description, symbol, and attribute values to which the symbol is applied.
 
##How to use the sample##
For simplicity, the sample starts with a predefined `UniqueValue`s for some US states which are set to the `UniqueValueRenderer` and applied to the FeatureLayer.

![](UniqueValueRendererSample.png)

##How it works##
 To show picture marker symbols in your app:

- Create the `ArcGISMap`'s basemap
- Create the FeatureLayer and add it to the map using `ArcGISMap#getOperationalLayers` method.
- Add the map to the view via `MapView` via `MapView#setMap()`.  
- Create a set of `UniqueValues` and add them to the renderer using the `UniqueValueRenderer#getUniqueValues` method. 
- Lately, when the FeatureLayer is loaded, the UniqueValueRenderer will display those `Feature`s using the values set in the list of UniqueValues.
 
##Features##
- ArcGISMap
- MapView
- FeatureLayer
- UniqueValues
- UniqueValueRenderer
