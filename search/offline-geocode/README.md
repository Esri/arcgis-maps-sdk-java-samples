# Offline geocode

Geocode addresses to locations and reverse geocode locations to addresses offline.

![Image of offline geocode](OfflineGeocode.png)

## Use case

You can use an address locator file to geocode addresses and locations. For example, you could provide offline geocoding capabilities to field workers repairing critical infrastructure in a disaster when network availability is limited.

## How to use the sample

Select an address from the drop-down list to geocode the address and view the result on the map. Click the location you want to reverse geocode. Click on the pin to highlight it and then click and and drag on the map to get real-time geocoding.

## How it works

1. Use the path of a .loc file to create a `LocatorTask` object. 
2. Set up `GeocodeParameters` and call `GeocodeAsync` to get geocode results.

## Relevant API

* GeocodeParameters
* GeocodeResult
* LocatorTask
* ReverseGeocodeParameters

## Tags

geocode, geocoder, locator, offline, package, query, search
