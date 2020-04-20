# Mobile map (search and route)

Display maps and use locators to enable search and routing offline using a Mobile Map Package.

![](MobileMapSearchAndRoute.png)

## Use case

Mobile map packages make it easy to transmit and store the necessary components for an offline map experience including: transportation networks (for routing/navigation), locators (address search, forward and reverse geocoding), and maps. 

A field worker might download a mobile map package to support their operations while working offline, for example to navigate remote oil field roads.

## How to use the sample

Click the "Open mobile map package" button to bring up a file choosing dialog. Browse to and select a .mmpk file. When chosen, the maps inside the mobile map package will be displayed in a list view.
 Click on a map in the list to open it. 

If the mobile map package has a locator task, the list items will have a pin icon. Click on the map to reverse geocode the clicked locations's address if a locator task is available. 

If the map contains transportation networks, it will have a navigation icon. Click on a map list item to open it. If transportation networks are available, a route will be calculated between geocode locations.

## How it works

1. Create a `MobileMapPackage` using `MobileMapPackage.OpenAsync(path)`.
2. Get a list of maps inside the package using the `Maps` property.
3. If the package has a locator, access it using the `LocatorTask` property.
4. To see if a map contains transportation networks, check each map's `TransportationNetworks` property.

## Relevant API

* GeocodeResult
* MobileMapPackage
* ReverseGeocodeParameters
* Route
* RouteParameters
* RouteResult
* RouteTask
* TransportationNetworkDataset

## Tags

disconnected, field mobility, geocode, network, network analysis, offline, routing, search, transportation
