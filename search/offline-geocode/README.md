# Offline geocode

Geocode addresses to locations and reverse geocode locations to addresses offline.

![Image of offline geocode](OfflineGeocode.png)

## Use case

You can use an address locator file to geocode addresses and locations. For example, you could provide offline geocoding capabilities to field workers repairing critical infrastructure in a disaster when network availability is limited.

## How to use the sample

Select an address from the drop-down list to geocode the address and view the result on the map. Click the location you want to reverse geocode. Click on the pin to highlight it and then move the mouse to perform reverse geocoding in real-time.

## How it works

1. Use the path of a .loc file to create a `LocatorTask` object.
2. Set up `GeocodeParameters` and call `GeocodeAsync` to get geocode results.

## Relevant API

* GeocodeParameters
* GeocodeResult
* LocatorTask
* ReverseGeocodeParameters

## Additional information

This sample uses the [San Diego Streets Tile Package](https://www.arcgis.com/home/item.html?id=22c3083d4fa74e3e9b25adfc9f8c0496) and the [San Diego Offline Locator](https://arcgis.com/home/item.html?id=3424d442ebe54f3cbf34462382d3aebe).

## Tags

geocode, geocoder, locator, offline, package, query, search
