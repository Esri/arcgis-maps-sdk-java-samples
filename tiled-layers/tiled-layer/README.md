#Tiled Layer
Demonstrates how to display an ArcGISTiledLayer on an ArcGISMap as a Basemap.
<p>
This service pre-generates images based on a tiling scheme which allows for rapid ArcGISMap visualization and navigation.
An ArcGISTiledLayer can also be added to the ArcGISMap as a layer but it's best practice to use as a Basemap since its purpose is to provide geographical context.
![](Tiled Layer.png)

##How it works
To add an `ArcGISTiledLayer` as a `Basemap` to an `ArcGISMap`.

1. Create an ArcGISTiledLayer from a URL.
2. Create a Basemap passing in the tiled layer from above.
3. Set basemap to ArcGISMap, `ArcGISMap.setBasemap()`.
4. Set map to mapview, `MapView.setMap()`.

##Features
- ArcGISMap
- ArcGISTiledLayer
- Basemap
- MapView
