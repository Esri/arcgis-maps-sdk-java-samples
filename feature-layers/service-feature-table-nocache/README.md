#Service Feature Table (No Cache)#
Demonstrates how to use a feature service in an on-interaction-no-cache mode.

On-interaction-no-cache mode will always fetch Features from the server and doesn't cache any Features on the client's side. This meaning that Features will be fetched whenever the ArcGISMap pans, zooms, selects, or queries.

![](ServiceFeatureLayerNoCache.png)

##How it works##
By setting the `FeatureRequestMode` to ON_INTERACTION_NO_CACHE on the `FeatureTable`.

##Features##
- ArcGISMap
- FeatureLayer
- MapView
- ServiceFeatureTable
- ServiceFeatureTable.FeatureRequestMode
