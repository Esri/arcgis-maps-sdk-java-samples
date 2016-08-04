#Service Feature Table (Cache)#
Demonstrates how to use a feature service in in an on-interaction-cache mode. This is the default mode for a Service Feature Table.

![](ServiceFeatureLayerCache.png)

##How it works##
How to set `FeatureRequestMode.ON_INTERACTION_CACHE` mode:

1. Create a `ServiceFeatureTable` from a URL.
2. Set request mode of table, `ServiceFeatureTable.setFeatureRequestMode(FeatureRequestMode.ON_INTERACTION_CACHE)`.

##Tags
- ArcGISMap
- FeatureLayer
- MapView
- ServiceFeatureTable
- ServiceFeatureTable.FeatureRequestMode
