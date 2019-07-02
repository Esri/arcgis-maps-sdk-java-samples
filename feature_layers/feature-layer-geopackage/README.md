# Feature Layer GeoPackage

Display features from a local GeoPackage.

![]("FeatureLayerGeoPackage.png)

## How it works

To create a `FeatureLayer` from a local `GeoPackage`:


  1. Create a `GeoPackage` passing the URI string into the constructor.
  2. Load the `GeoPackage` with `GeoPackage.loadAsync`
  3. When it's done loading, get the `GeoPackageFeatureTable`s inside with `geoPackage.getGeoPackageFeatureTables()`
  4. For each feature table, create a feature layer with `FeatureLayer(featureTable)`. Add each to 
  the map as an operational layer with `map.getOperationalLayers().add(featureLayer)`


## Relevant API


  * ArcGISMap
  * Basemap
  * FeatureLayer
  * GeoPackage
  * GeoPackageFeatureTable
  * MapView

