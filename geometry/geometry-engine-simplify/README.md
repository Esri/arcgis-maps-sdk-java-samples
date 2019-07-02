# Geometry Engine Simplify

Simplify a polygon that has multiple parts.

![]("GeometryEngineSimplify.png)

## How to use the sample

Click on the simplify button to apply the simplify geometry operation between the intersecting polygons. Click reset to restart the sample.

## How it works

To perform the simplify geometry operation on a `Polygon`:


  1. Create a `GraphicsOverlay` and add it to the `MapView`.
  2. Define the `PointCollection` of the `Geometry`.
  3. Add the polygons to the GraphicsOverlay.
  4. Determine the simplified geometry by using the `GeometryEngine.simplify(polygon.getGeometry())`.


## Relevant API


  * Geometry
  * Graphic
  * GraphicsOverlay
  * MapView
  * Point
  * PointCollection
  * SimpleLineSymbol
  * SimpleFillSymbol
