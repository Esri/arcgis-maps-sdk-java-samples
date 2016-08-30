#Service Feature Table (Manual Cache)#
Demonstrates how to use a feature service in a manual-cache mode.

##How to use the sample##
Click on the Request Cache button to manually request Features. Returned label displays how many features were returned by the service.

Note: Maximum of Features returned is set to 1000.

![](ServiceFeatureTableManualCache.png)

##How it works##
How to set `FeatureRequestMode.MANUAL_CACHE` mode:

1. Create a `ServiceFeatureTable` from a URL.
2. Set request mode of table, `ServiceFeatureTable.setFeatureRequestMode(FeatureRequestMode.MANUAL_CACHE)`.

##Features
- ArcGISMap
- FeatureLayer
- MapView
- ServiceFeatureTable
- ServiceFeatureTable.FeatureRequestMode
