# Feature layer (geodatabase)

Display features from a local geodatabase.

![Image of feature layer geodatabase](FeatureLayerGeodatabase.png)

## Use case

Accessing data from a local geodatabase is useful when working in an environment that has an inconsistent internet connection or that does not have an internet connection at all. For example, a department of transportation field worker might source map data from a local geodatabase when conducting signage inspections in rural areas with poor network coverage.

## How to use the sample

Pan and zoom around the map. View the data loaded from the geodatabase.

## How it works

1. Create a geodatabase using the provided local resource, `new Geodatabase(geodatabaseResourceUrl)`.
2. Wait for geodatabase to load, `Geodatabase.addDoneLoadingListener(runnable)`.
3. Get the 'Trailheads' `GeodatabaseFeatureTable` from the geodatabase, `Geodatabase.getGeodatabaseFeatureTable(tableName)`.
4. Create feature layer using the table from above, `new FeatureLayer(geodatabaseFeatureTable)`.
5. Add feature layer to `ArcGISMap` with `ArcGISMap.getOperationalLayers().add(featureLayer)`.

## Relevant API

* FeatureLayer
* Geodatabase
* GeodatabaseFeatureTable

## About the data

The sample shows trailheads in the greater Los Angeles area displayed on top of a vector tile basemap.

## Additional information

Learn more about geodatabases and how to utilize them on the [ArcGIS Pro documentation](https://pro.arcgis.com/en/pro-app/latest/help/data/geodatabases/overview/what-is-a-geodatabase-.htm) page. 

Note: You could also use the 'Services Pattern' and access the `Geodatabase` class via a Feature Service served up via ArcGIS Online or ArcGIS Enterprise. Instead of using the `Geodatabase` class to access the .geodatabase file on disk, you would use `GeodatabaseSyncTask` to point to a Uri instead.

## Tags

geodatabase, mobile, offline
