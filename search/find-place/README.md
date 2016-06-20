#Find Place#
This sample demonstrates how find places of interest (POIs) by geocoding near a location or within an specific area.

##How to use the sample##
Choose from the dropdowns or input your own place and location to search near. Click the search button to find matching places. A redo search button will appear if you pan the map after a search.

![](FindPlace.png)

##How it works##
 To find locations matching a query and a search area:

- Create the `ArcGISMap`'s basemap, in this case we used an offline `TileCache`. 
- Add the map to the view via `MapView` via `MapView#setMap()`. 
- Create a `LocatorTask` using a URL and set the `GeocodeParameters`.
- To reverse geocode near a location, pass the location's position into `GeocodeParameters#setSearchArea(Geometry)` to set the search area.
- Limit results to the view's visible area using the `MapView#getVisibleArea()` method.
- Show the matching retrieved results from the `LocatorTask#geocodeAsync(String, GeocodeParameters)` via `PictureMarkerSymbol`s in a `GraphicsOverlay`.

##Features##
- ArcGISMap
- MapView
- GraphicsOverlay
- GeocodeParameters
- ReverseGeocodeParameters
- LocatorTask 
- GeocodeResult