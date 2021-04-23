# Reverse geocode online

Use an online service to find the address for a point on the map.

![Image of reverse geocode](ReverseGeocodeOnline.png)

## Use case

You might use a geocoder to find a customer's delivery address based on the location returned by their device's GPS.

## How to use the sample

Click on the map to see the nearest address displayed in a callout.

## How it works

1. Create a `LocatorTask` object using a URL to a geocoder service.
2. Set the `GeocodeParameters` for the `LocatorTask` and specify the geocoder's attributes.
3. Get the matching results from the `GeocodeResult` using  `LocatorTask.reverseGeocodeAsync`.
4. Show the results using a `PictureMarkerSymbol` and add the symbol to a `Graphic` in the `GraphicsOverlay`.

## Relevant API

* GeocodeParameters
* LocatorTask
* ReverseGeocodeParameters

## About the data

This sample uses the [World Geocoding Service](https://www.arcgis.com/home/item.html?id=305f2e55e67f4389bef269669fc2e284).

## Tags

address, geocode, locate, reverse geocode, search
