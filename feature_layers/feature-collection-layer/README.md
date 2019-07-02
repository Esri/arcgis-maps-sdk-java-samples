# Feature Collection Layer

Combine feature tables with different geometries into a single layer.

![]("FeatureCollectionLayer.png)

## How it works

To display a `FeatureCollection` as a `FeatureCollectionLayer` on an `ArcGISMap` using different `FeatureCollectionTable`s:


  1. Create a feature collection layer using a new feature collection, `new FeatureCollectionLayer(featureCollection)`
  2. The layer is then added to the map, `ArcGISMap.getOperationalLayers().add(featureCollectionLayer)`.
  3. A feature collection table is then created for the `GeometryType`s `Point` `Polyline` `Polygon`, `new FeatureCollectionTable(fields, geometryType, spatialRefernce)`
  
    * `Field`s is a list of the feature's attributes, which this one defines its name.
  4. A `SimpleRenderer` is then assigned to each table which will render any `Feature`s from that table using the `Symbol` that was set.
  5. The table is then added to the feature collection, `FeatureCollection.getTables().add(featureCollectionTable)`.
  6. To create a feature from the feature collection table use the createFeature method passing an attribute and geometry for that feature, `FeatureCollectionTable.createFeature(attributes, geometry)`.
  7. Add new feature to the table, `FeatureCollectionTable.addFeatureAsync(feature)`.


## Relevant API


  * FeatureCollection
  * FeatureCollectionLayer
  * FeatureCollectionTable
  * Feature
  * Field
  * SimpleFillSymbol
  * SimpleLineSymbol
  * SimpleMarkerSymbol
  * SimpleRenderer

