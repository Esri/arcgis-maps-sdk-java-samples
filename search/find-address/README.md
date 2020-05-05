# Find address

Find the location for an address.

![Image of find address](FindAddress.gif)

## Use case

A user can input a raw address into your app's search bar and zoom to the address location.

## How to use the sample

Select an entry from the drop-down menu at the top left of the screen to zoom to the address, marked with a pin. 

## How it works

1. Create a `LocatorTask` using the URL to a locator service.
2. Set the `GeocodeParameters` for the locator task and specify the geocode's attributes.
3. Get the matching results from the `GeocodeResult` using `locatorTask.geocodeAsync(addressString, geocodeParameters)`.
4. Create a `Graphic` with the geocode result's location and store the geocode result's attributes in the graphic's attributes.
5. Show the graphic in a `GraphicsOverlay`.

## Relevant API

* GeocodeParameters
* GeocodeResult
* LocatorTask

## Tags

address, geocode, locator, search
