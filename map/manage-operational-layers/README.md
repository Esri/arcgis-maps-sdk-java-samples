#Manage Operational Layers#
Demonstrates how to add, remove and reorder operational layers in an ArcGISMap.

##How to use the sample##
The map in the sample application comes with three ArcGISMapImageLayers already added. In the left control panel are two lists to hold layers. 

First list has the layers that are currently part of the map. Right click on the layer to remove it or left click on the layer to move it to the top.

Second list has layers that have been removed from the first list. Click on layer to add it back to the first list.

![](ManageOperationalLayers.png)

##How it works##
To manage the operational layers:

1. Create an `ArcGISMap`.  
2. Get the ArcGISMap `LayerList` using `ArcGISMap.getOperationalLayers()`.
3. Add/Remove layers from the ArcGISMap by add/removing them from the LayerList.
4. The last `Layer` added to the list will be the Layer that is on top.

##Features##
- ArcGISMap
- ArcGISMapImageLayer
- Basemap
- LayerList
- MapView
