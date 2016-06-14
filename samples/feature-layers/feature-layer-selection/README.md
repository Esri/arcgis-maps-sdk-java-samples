#Feature Layer Selection#
This sample shows how to select `Feature`s in a `FeatureLayer`.

##How to use the sample##
click on a feature on the map to select it.

![](FeatureLayerSelection.png)

##How it works##
To select Features in your FeatureLayer

- Create a `ServiceFeatureTable` from a URL.
- Create a FeatureLayer from the ServiceFeatureTable.
- An `Envelope` is created every time the user clicks on the `MapView`.
- Set the `QueryParameters` based on the Envelope using the `query#setGeometry(geometry)` method. 
- Fire the query on the FeatureTable using `featureTable#queryFeaturesAsync(query)` method.
- Once it gets complete get the selected Feature's from the `FeatureQueryResult`.

##Features##
- ArcGISMap
- MapView
- Envelope
- FeatureLayer
- ServiceFeatureTable
- QueryParameters
- FeatureQueryResult