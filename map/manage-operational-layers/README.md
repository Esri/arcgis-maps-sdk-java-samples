#Manage Operational Layers#
This sample demonstrates how to add, remove and reorder operational layers in an `ArcGISMap`.

##How to use the sample##
The map in the sample application comes with three `ArcGISMapImageLayer`s already added. In the left control panel is shown two lists of the layers. The first list has the layers that are currently part of the map and the second sections has the removed layers. In first section, you can right click on the layer to remove a layer or you click on the layer to reorder a layer. In the second list, you can simplify click on a removed layer to put it back. The layer gets added onto the top.

![](ManageOperationalLayers.png)

##How it works##
To manage the operational layers:

- Create an ArcGISMap.  
- Get the ArcGISMap `LayerList` using the `ArcGISMap#getOperationalLayers` method.
- Add/Remove layers from the ArcGISMap by add/removing them from the LayerList.
- The last `Layer` added to the list will be the Layer that is on top.

##Features##
- ArcGISMap
- MapView
- LayerList
- ArcGISMapImageLayer