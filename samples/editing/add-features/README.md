#Add Features#
This sample demonstrates how to add `Feature`s to the `FeatureLayer` using a feature service. 

##How to use the sample##
Click on a location in the `MapView` to add a feature at that location.

![](AddFeatures.png)

##How it works##
To get features from a `ServiceFeatureTable` and update it with new features:

* Create a ServiceFeatureTable from a URL.
* Create a FeatureLayer from the ServiceFeatureTable.
* Create a Feature with attributes and a location using ServiceFeatureTable.
* Apply the addition to the ServiceFeatureTable by using 
`ServiceFeatureTable.addFeatureAsync()`.
* Update the data on the server using
`ServiceFeatureTable.applyEditsAsync()`.

##Features##
- ArcGISMap
- MapView
- Feature
- FeatureLayer
- ServiceFeatureTable