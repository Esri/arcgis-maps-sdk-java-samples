#Display a Map#
This sample demonstrates how to create `ArcGISMap` using a `Basemap`. A Basemap is beneath all other layers on a ArcGISMap and provides visual reference for all other layers.

![](DisplayMap.png)

##How it works##
To display a ArcGISMap:

- Create an ArcGISMap using a default basemap such us `Basemap#createImagery()`.  
- Set the map to the view via `MapView` via `MapView#setMap()`. 

##Features##
- ArcGISMap
- Basemap
- MapView