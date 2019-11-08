# Find connected features in a utility network

Find all features connected to a set of starting points in a utility network.

![](ConnectedTrace.png)

## Use case

This is useful to visualize and validate the network topology of a utility network for quality assurance. 

## How to use the sample

To add a starting point, select 'Add starting location(s)' and tap on one or more features. To add a barrier, select 'Add barrier(s)' and tap on one or more features. Depending on the type of feature, you may be prompted to select a terminal or the distance from the tapped location will be computed. Click 'Trace' to highlight all features connected to the specified starting locations and not positioned beyond the barriers. Click 'Reset' to clear parameters and start over.

## How it works

1. Create a `MapView`.
2. Create and load a `Map` that contains `FeatureLayer`(s) that are part of a utility network.
3. Create and load a `UtilityNetwork` with the same feature service URL and map.
4. Add a `GraphicsOverlay` with symbology that distinguishes starting points from barriers.
5. Add a listener for clicks on the map view, and use `mapView.identifyLayersAsync()` to dentify clicked features.
6. Add a `Graphic` that represents its purpose (starting point or barrier) at the location of each identified feature.
7. Determine the type of the identified feature using `utilityNetwork.getDefinition().getNetworkSource()` passing its table name.
8. If a junction, display a terminal picker when more than one terminal is found and create a `UtilityElement` with the selected terminal or the single terminal if there is only one.
9. If an edge, create a utility element from the identified feature and compute its `FractionAlongLine` using `GeometryEngine.fractionAlong()`.
10. Run a `utilityNetwork.traceAsync()` with the specified parameters.
11. Group the utility elements by their network source name, which you can retrieve using `utilityElement.getNetworkSource().getName()`.
12. For every feature layer in this map with elements, select features by converting utility elements to `ArcGISFeature`(s) using `UtilityNetwork.fetchFeaturesForElementsAsync()`

## Relevant API

* UtilityNetworks.UtilityNetwork
* UtilityNetworks.UtilityTraceParameters
* UtilityNetworks.UtilityTraceResult
* UtilityNetworks.UtilityElementTraceResult
* UtilityNetworks.UtilityNetworkDefinition
* UtilityNetworks.UtilityNetworkSource
* UtilityNetworks.UtilityAssetType
* UtilityNetworks.UtilityTerminal
* UtilityNetworks.UtilityElement
* GeometryEngine.FractionAlong

## About the data

The sample uses a dark vector basemap. It includes a subset of feature layers from a feature service that contains the same utility network used to run the connected trace.

## Tags

connected trace, utility network, network analysis
