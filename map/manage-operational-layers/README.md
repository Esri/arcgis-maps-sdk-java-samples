# Manage Operational Layers

Add, remove and reorder operational layers in a map.

![](ManageOperationalLayers.png" />

## How to use the sample

The map in the sample application comes with three ArcGISMapImageLayers already added. In the left control panel are two lists to hold layers.

First list has the layers that are currently part of the map. Right click on the layer to remove it or left click on the layer to move it to the top.

Second list has layers that have been removed from the first list. Click on layer to add it back to the first list.

## How it works

To manage the operational layers:


1. Create an `ArcGISMap`.
2. Get the ArcGIS map `LayerList` using `ArcGISMap.getOperationalLayers()`.
3. Add/Remove layers from the ArcGIS map by add/removing them from the layer list.
4. The last `Layer` added to the list will be the Layer that is on top.


## Relevant API


*   ArcGISMap
*   ArcGISMapImageLayer
*   Basemap
*   LayerList
*   MapView
