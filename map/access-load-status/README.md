#Access Load Status#
This sample demonstrates how to tell what the `ArcGISMap`'s load status is. An ArcGISMap is considered loaded once has a valid `SpatialReference` and it has been set to the `MapView`.

##How to use the sample##
The sample provides an information area which displays what the `ArcGISMap`'s load status is. Click on the button to reload the ArcGISMap.

![](AccessLoadStatus.png)

##How it works##
To access to the ArcGISMap load status.

- Create an ArcGISMap.
- Use the `ArcGISMap#addLoadStatusChangedListener` method for whenever the `LoadSatus` changes.
- Set the ArcGISMap to the MapView.

##Features##
- MapView
- Graphic
- LoadStatus