#Add Features#
Demonstrates how to add new Features to a ServiceFeatureTable and apply those Features to it's server. A FeatureLayer created using this SeviceFeatutreTable that is applied to the ArcGISMap will display any new Features automatically.

##How to use the sample##
Click on a location in the MapView to add a Feature at that location.

![](AddFeatures.png)

##How it works##
To add a feature to a `ServiceFeatureTable` and update it's server with that `Feature`:

1. Create a service feature table from a URL, `new ServiceFeatureTable("URL")`.
2. Create a `FeatureLayer` from the service feature table, `new FeatureLayer(ServiceFeatureTable)`.
3. Create a feature with attributes and a location using service feature table, `ServiceFeatureTable.createFeature(attributes, location)`.
4. Apply the addition to the service feature table, `ServiceFeatureTable.addFeatureAsync(Feature)`.
5. Update the new feature to the server, `ServiceFeatureTable.applyEditsAsync()`.

##Features##
- ArcGISMap
- Feature
- FeatureEditResult
- FeatureLayer
- MapView
- ServiceFeatureTable
