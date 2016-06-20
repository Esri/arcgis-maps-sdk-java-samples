#Map Image Layer Sublayer Visibility#
Demonstrates how to add multiple layers to your ArcGISMap using a BasemapType and an ArcGISMapImageLayer which has multiple sub-layers.

##How to use the sample##
A list of ArcGISSubLayers is display in a drop down box in the top right. Each sublayer in the list has a check-box, which can be used to toggle the visibility of that particular sublayer.

![](ArcGISImageLayerSublayerVisibility.png)

##How it works##
To add an `ArcGISMapImageLayer` to your `ArcGISMap` using its URL:

- Create an ArcGISMap image layer from its URL.
- Add it to `ArcGISMap.getOperationalLayers().add()`.
- Display the ArcGISMap by adding it to the `MapView`.
- Gain access to the sub-layers from the `ArcGISMapImageLayer.getSubLayers()` method which returns a `SubLayerList`. The sub layer list is a modifiable list of `ArcGISSubLayers`. 
- Determine if the layer is visible or not by turning on / off the ArcGISSubLayers visibility in the sub layer list.

##Features##
- ArcGISMap
- ArcGISMapImageLayer
- Basemap
- MapView
- SubLayerList
