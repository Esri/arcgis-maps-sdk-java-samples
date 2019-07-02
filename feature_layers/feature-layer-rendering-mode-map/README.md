# Feature Layer Rendering Mode (Map)

Render features statically or dynamically.

![]("FeatureLayerRenderingModeMap.gif)

## How it works

To change `FeatureLayer.RenderingMode` using `LoadSettings`:


  1. Create a `ArcGISMap`.
  2. Set preferred rendering mode to map, `mapBottom.getLoadSettings().setPreferredPointFeatureRenderingMode(FeatureLayer.RenderingMode.DYNAMIC)`.
  
  * Can set preferred rendering mode for `Points`, `Polylines`, or `Polygons`.
  * `Multipoint` preferred rendering mode is the same as point.
  3. Set map to `MapView`, `mapViewBottom.setMap(mapBottom)`.
  4. Create a `ServiceFeatureTable` from a point service, `new ServiceFeatureTable("http://sampleserver6.arcgisonline.com/arcgis/rest/services/Energy/Geology/FeatureServer/0");`.
  5. Create `FeatureLayer` from table, `new FeatureLayer(poinServiceFeatureTable)`.
  6. Add layer to map, `mapBottom.getOperationalLayers().add(pointFeatureLayer.copy())`
  
  * Now the point layer will be rendered dynamically to map view.


## Relevant API


  * ArcGISMap
  * FeatureLayer
  * FeatureLayer.RenderingMode
  * LoadSettings
  * Point
  * Polyline
  * Polygon
  * ServiceFeatureTable
  * Viewpoint



