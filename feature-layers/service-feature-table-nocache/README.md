#Service Feature Table (No Cache)#
This sample shows how to use a Feature service in on interaction no cache mode.

On-interaction-no-cache mode will always fetch `Feature`s from the server and doesn't cache any Features on the client's side. This meaning that Features will be fetched whenever the `ArcGISMap` pans, zooms, selects, or queries.

![](ServiceFeatureLayerManualNoCache.png)

##How it works##
By setting the `FeatureRequestMode` to ON_INTERACTION_NO_CACHE on the `FeatureTable`.

##Features##
- ArcGISMap
- MapView
- FeatureLayer
- ServiceFeatureTable
- ServiceFeatureTable.FeatureRequestMode
