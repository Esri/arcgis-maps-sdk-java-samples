# Unique Value Renderer

Symbolize features based on their unique attribute value.

Demonstrates how to use an UniqueValueRenderer to display Features from
a FeatureLayer using different symbols. An unique value renderer sets an
attribute to look for any matches within it’s unique values. If there is
a match then the symbol assign to that unique value is use to display
that feature with the same value.

![](UniqueValueRenderer.png)

In this sample the unique value renderer is looking for the
“STATE\_ABBR” attribute within each feature of the feature layer.
For example we will have a unique value renderer that has a unique value
set to the abbreviation for California.

    SimpleFillSymbol californiaFillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, RED,
    new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, RED, 2));
    
    List<Object> californiaValue = new ArrayList<>();
    californiaValue.add("CA");
    uniqueValueRenderer.getUniqueValues().add(new UniqueValue(State of California", "California",
    californiaFillSymbol, californiaValue));

When this unique value renderer comes across the feature whos key
“STATE\_ABBR” is value “CA” it will assign the symbol from the unique
value above to this feature.

## How to use the sample

Sample starts with a predefined UniqueValues for some US states which
are set to the UniqueValueRenderer and applied to the FeatureLayer.

## How it works

To display different `Symbol` for different `Graphic`s:

1.  Create a `ArcGISMap`’s with `Basemap`.
2.  Create a `FeatureLayer` and add it to the map,
    `ArcGISMap.getOperationalLayers().add()`.
3.  Add the map to the view, `MapView.setMap()`.
4.  Create a `UniqueValueRenderer`.
5.  specify default feature attribute to look for,
    `UniqueValueRenderer.getFieldNames().add()`
