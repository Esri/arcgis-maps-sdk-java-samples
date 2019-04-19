# Offline Geocode

Geocode with offline data.

![](OfflineGeocode.png)

## How to use the sample

For simplicity, the sample comes loaded with a set of addresses. You can
select any address to perform geocoding and show it’s location on map.
You can select the pin and move the mouse to perform reverse geocoding
in real-time. .

## How it works

To perform geocoding with offline resources:

1.  Create an `ArcGISMap`’s using a `Basemap` and add it to the map
    view.
    
      - basemap is created using a `TileCache`, which represent our
        offline resource
    
    <!-- end list -->

        </li>
        <li>Create a <code>LocatorTask</code> using a URI to the offline locator file and define the <code>ReverseGeocodeParameters</code>/<code>GeocodeParameters</code> for  the LocatorTask.</li>
        <li>To geocode an address, set the geocode parameters and use <code>LocatorTask.geocodeAsync(query, geocodeParameters)</code>.</li>
        <li>To reverse geocode a location, get the <code>Point</code> location on the map view and use <code>LocatorTask
        .reverseGeocodeAsync(point, reverseGeocodeParameters)</code>.</li>
        <li>Show the retrieved results by creating a <code>PictureMarkerSymbol</code> with attributes from the result and add that symbol to a <code>Graphic</code>  in the <code>GraphicsOverlay</code>.</li>
