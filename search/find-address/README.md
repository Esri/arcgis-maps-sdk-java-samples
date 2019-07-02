# Find Address

Find the location for an address.

![](FindAddress.gif)

## How to use the sample

For simplicity, the sample comes loaded with a set of addresses. You can select an address to perform online geocoding and show the matching results on the ArcGISMap.

## How it works

To get a geocode from a query and display its location on the `ArcGISMap`:

1.  Create a `LocatorTask` using a URL.
2.  Set the `GeocodeParameters` for the locator task and specify the geocode's attributes.
3.  Get the matching results from the `GeocodeResult` using `LocatorTask.geocodeAsync(query, geocodeParameters)`.
4.  Show each result with a `Graphics` in a `GraphicsOverlay`, storing the attributes in the graphic's attributes so they can later be shown in a callout.

## Relevant API

*   ArcGISMap
*   GeocodeParameters
*   GeocodeResult
*   GraphicsOverlay
*   LocatorTask
*   MapView
