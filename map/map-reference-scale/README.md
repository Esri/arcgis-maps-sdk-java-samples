# Map reference scale

Set the map's reference scale and choose which feature layers should honor the reference scale.

![]("MapReferenceScale.png)

## Use case

Setting a reference scale on an `ArcGISMap` fixes the size of symbols and text to the desired height and width at that scale. As you zoom in and out, symbols and text will increase or decrease in size accordingly. When no reference scale is set, symbol and text sizes remain the same size relative to the `MapView`.

Map annotations are typically only relevant at certain scales. For instance, annotations to a map showing a construction site are only relevant at that construction site's scale. So, when the map is zoomed out that information shouldn't scale with the `MapView`, but should instead remain scaled with the `ArcGISMap`.

## How to use the sample


* Use the drop down menu at the top left to set the map's reference scale.

* Click the "Set Map Scale to Reference Scale" button to set the map view scale to that of the map reference scale.

* Use the top right menu checkboxes to apply the map reference scale to the map's feature layers (which should scale according to the reference scale).


## How it works


* Set the map reference scale property on the `ArcGISMap` with `map.setReferenceScale(double)`.

* Set the scale symbols property on each individual `FeatureLayer` within the map with `featureLayer.setScaleSymbols(boolean)`.


## Relevant API


* ArcGISMap

* FeatureLayer


## Additional Information

The map reference scale should normally be set by the map's author and not exposed to the end user like it is in this sample.

## ## Tags

Maps & Scenes, reference scale