#ArcGISMap Initial Extent#
This sample demonstrates how to display an `ArcGISMap` at a specific `Viewpoint`.

##How to use the sample##
A viewpoint is constructed from an `Envelope` defined by minimum (x,y) and maximum (x,y) `Point`'s values. The map's initialViewpoint is set to this viewpoint before the map is loaded by the ArcGISMap. Upon loading the map zooms to this initial area.

![](ArcGISMapInitialExtent.png)

##How it works##
To set an initial viewpoint:

- Create an ArcGISMap.  
- Create a Viewpoint using an Envelope. the ArcGISMap `LayerList` using the `ArcGISMap#getOperationalLayers` method.
- Set the starting location of the ArcGISMap using the `ArcGISMap#setInitialViewpoint` method.
- The last set the ArcGISMap to the `MapView`.

##Features##
- ArcGISMap
- MapView
- Viewpoint
- Envelope
- Point