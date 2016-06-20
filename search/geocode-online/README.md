#Geocode Online#
This sample demonstrates how to geocode an address query and display its location on the `ArcGISMap`.

##How to use the sample##
For simplicity, the sample comes loaded with a set of address. You can select an address to perform online geocoding and show the matching results in the `ArcGISMap`. 

![](GeocodeOnline.png)

##How it works##
To get a geocode from a query and display its location on the ArcGISMap:

- Create the `ArcGISMap`'s basemap, in this case we used an offline `TileCache`. 
- Create a `LocatorTask` using a URL.
- Set the `GeocodeParameters` for the LocatorTask and specify the geocodes' attributes.
- Get the matching results from the `GeocodeResult` using the `LocatorTask#geocodeAsync(query, geocodeParameters)` method.
- Lastly, to show the results using a `PictureMarkerSymbol` with attributes and add the symbol to a `GraphicsOverlay`.

##Features##
- ArcGISMap
- MapView
- GraphicsOverlay
- GeocodeParameters
- ReverseGeocodeParameters
- LocatorTask 
- GeocodeResult