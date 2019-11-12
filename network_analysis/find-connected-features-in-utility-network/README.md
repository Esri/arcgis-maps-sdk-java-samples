# Find connected features in a utility network

Find all features connected to a set of starting points in a utility network.

![](ConnectedTrace.png)

## Use case

This is useful to visualize and validate the network topology of a utility network for quality assurance. 

## How to use the sample

To add a starting point, select 'Add starting location(s)' and click on one or more features. To add a barrier, select 'Add barrier(s)' and click on one or more features. Depending on the type of feature, you may be prompted to select a terminal or the distance from the clicked location will be computed. Click 'Trace' to highlight all features connected to the specified starting locations and not positioned beyond the barriers. Click 'Reset' to clear parameters and start over.

## How it works

1. Create a `Map` and add it to a `MapView`.
2. Using the URL to a utility network's feature service, create `FeatureLayer`s that contain the utility network's features, and add them to the operational layers of the map.
3. Create and load a `UtilityNetwork` with the same feature service URL and map.
4. Add a `GraphicsOverlay` with symbology that distinguishes starting points from barriers.
5. Add a listener for clicks on the map view, and use `mapView.identifyLayersAsync()` to dentify clicked features.
6. Add a `Graphic` that represents its purpose (starting point or barrier) at the location of each identified feature.
7. Determine the type of the identified feature using `utilityNetwork.getDefinition().getNetworkSource()` passing its table name.
8. If a junction, display a terminal picker when more than one `UtilityTerminal` is found and create a `UtilityElement` with the selected terminal or the single terminal if there is only one.
9. If an edge, create a utility element from the identified feature and set its `FractionAlongEdge` using `GeometryEngine.fractionAlong()`.
10. Create `UtilityTraceParameters` for the trace, specifying the trace type, `UtilityTraceType.CONNECTED`, adding the starting location, and adding barriers (if applicable).
11. Run a `utilityNetwork.traceAsync()` with the specified parameters, and get the `UtilityTraceResult`.
12. From the utility trace result, get the `UtilityElementTraceResult`, and group the utility elements within by their network source name, which you can retrieve using `utilityElement.getNetworkSource().getName()`.
13. For every feature layer in this map with elements, select features by converting utility elements to `ArcGISFeature`(s) using `UtilityNetwork.fetchFeaturesForElementsAsync()`

## Relevant API

* UtilityNetwork
* UtilityTraceParameters
* UtilityTraceResult
* UtilityElementTraceResult
* UtilityNetworkSource
* UtilityAssetType
* UtilityTerminal
* UtilityElement
* GeometryEngine

## About the data

The [feature service](https://sampleserver7.arcgisonline.com/arcgis/rest/services/UtilityNetwork/NapervilleElectric/FeatureServer) in this sample represents an electric network in Naperville, Illinois, which contains a utility network used to run the subnetwork-based trace.

## Tags

connected trace, utility network, network analysis
