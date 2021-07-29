# Perform valve isolation trace

Run a filtered trace to locate operable features that will isolate an area from the flow of network resources.

![Image of Perform valve isolation trace](PerformValveIsolationTrace.png)

## Use case

Determine the set of operable features required to stop a network's resource, effectively isolating an area of the network. For example, you can choose to return only accessible and operable valves: ones that are not paved over or rusted shut.

## How to use the sample

Click on one or more features on the map to add filter barriers (marked as red crosses) or create and set the configuration's filter barriers by selecting a utility category from the drop down menu. Toggle "Isolated Features" to update trace configuration. Click "Trace" to run a subnetwork-based isolation trace, and click "Reset" to clear filter barriers and trace results.

## How it works

1. Create an `ArcGISMap` and add it to a `MapView`.
2. Create and load a `ServiceGeodatabase` with a feature service URL and get tables with their layer IDs.
3. Create `FeatureLayer`s from the service geodatabase's tables, and add them to the operational layers of the map.
4. Create a `UtilityNetwork` with the same feature service URL and add it to the map's utility networks list, then load it.
5. Get a default `UtilityTraceConfiguration` from a given tier in a domain network. Set its filter with a new `UtilityTraceFilter`.
6. Create a `UtilityElement` to represent a default starting location from the `UtilityNetwork`, using a given asset type and global ID.
7. Create a `UtilityTraceParameters` with a `UtilityTraceType.ISOLATION` and the starting location.
8. Use `utilityNetwork.fetchFeaturesForElementsAsync()` to obtain the `Geometry` of this element, and create a `Graphic` with that geometry.
9. Add a `GraphicsOverlay` with the graphic that represents the starting location.
10. Populate the combo box for choosing the filter barrier category from `UtilityNetworkDefinition.getCategories()`.
11. When "Trace" is clicked,
    1. Create a new `UtilityCategoryComparison` with the selected category and `UtilityCategoryComparisonOperator.EXISTS`.
    2. Assign this condition to `utilityTraceFilter.setBarriers()` from the default configuration from step 5. Update this configuration's `utilityTraceConfiguration.isIncludeIsolatedFeatures()` property.
    3. Create a `UtilityTraceParameters` with `UtilityTraceType.ISOLATION` and default starting location from step 6.
    4. Set its utility trace configuration with this configuration and then, run a `utilityNetwork.traceAsync()`.
12. Add a listener for clicks on the map view. When the map view is clicked,
    1. Use `mapView.identifyLayersAsync()` to identify clicked features.
    2. Create a `UtilityElement` from the feature, and add it to the `utilityTraceParameters` list of filter barriers.
        * If the element is a junction with more than one terminal, display a terminal picker. Then set the junction's terminal property with the selected terminal.
        * If it is an edge, set its `fractionAlongEdge` property using `GeometryEngine.fractionAlong()` method.
    3. Create a `Graphic` for the filter barrier and add it to the map.
13. Get the list of `UtilityElement`s from the first trace result.
14. For every feature layer in the map, select all the features for which the layer's `FeatureTable.getTableName()` matches the `UtilityNetworkSource.getName()` of one of the utility elements.

## Relevant API

* GeometryEngine.fractionAlong
* ServiceGeodatabase
* UtilityCategory
* UtilityCategoryComparison
* UtilityCategoryComparisonOperator
* UtilityDomainNetwork
* UtilityElement
* UtilityElementTraceResult
* UtilityNetwork
* UtilityNetworkDefinition
* UtilityTerminal
* UtilityTier
* UtilityTraceFilter
* UtilityTraceParameters
* UtilityTraceResult
* UtilityTraceType

## About the data

The [Naperville gas](https://sampleserver7.arcgisonline.com/server/rest/services/UtilityNetwork/NapervilleGas/FeatureServer) network feature service, hosted on ArcGIS Online, contains a utility network used to run the isolation trace shown in this sample.

## Tags

category comparison, condition barriers, filter barriers, isolated features, network analysis, subnetwork trace, trace configuration, trace filter, utility network
