#Service Feature Table (No Cache)#
Demonstrates how to use a feature service in an on-interaction-no-cache mode.

On-interaction-no-cache mode will always fetch Features from the server and doesn't cache any Features on the client's side. This meaning that Features will be fetched whenever the ArcGISMap pans, zooms, selects, or queries.

![](ServiceFeatureTableNoCache.png)

##How it works##
How to set `FeatureRequestMode.ON_INTERACTION_NO_CAHCE` mode:

1. Create a `ServiceFeatureTable` from a URL.
2. Set request mode of table, `ServiceFeatureTable.setFeatureRequestMode(FeatureRequestMode.ON_INTERACTION_NO_CAHCE)`.

##Tags
- ArcGISMap
- FeatureLayer
- MapView
- ServiceFeatureTable
- ServiceFeatureTable.FeatureRequestMode
