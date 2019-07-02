# Geodesic Operations

Calculate the geodesic path and distance between two points.

![]("GeodesicOperations.png)

## How to use the sample

Click on the map to select a path destination. The geodesic path between the two points will update.

## How it works

To create a geodesic path between two points:


  1. Create a `Polyline` using two points.
  2. Pass this polyline to: `GeometryEngine.densifyGeodetic(polyline, segmentLength, unitOfMeasurement, GeodeticCurveType
  .GEODESIC)`. This will create a new polyline with segments of length `segmentLength` and 
  `LinearUnit` set to `unitOfMeasurement`. The curve will be geodesic.
  3. You can set this geometry to a `Graphic` to display the curve in a `GraphicsOverlay`.
  4. To get the distance, use `GeometryEngine.lengthGeodetic(pathGeometry, unitOfMeasurement, GeodeticCurveType.GEODESIC)` 


## Relevant API


  * ArcGISMap
  * GeodeticCurveType
  * Geometry
  * GeometryEngine
  * Graphic
  * GraphicsOverlay
  * LinearUnit
  * LinearUnitId
  * MapView
  * Point
  * PointCollection
  * Polyline
  * SimpleLineSymbol
  * SimpleMarkerSymbol

