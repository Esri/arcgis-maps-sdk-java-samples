# Spatial Operations

Find the union, difference, or intersection of two geometries.

![](SpatialOperations.png)

## How to use the sample

The sample provides a drop down on the top, where you can select a geometry operation. When you choose a geometry operation, the application performs this operation between the overlapping polygons and applies the result to the geometries.

## How it works

To find the union, difference, intersection, or symmetric difference between `Polygon`s:

1.  Create a `GraphicsOverlay` and add it to the `MapView`.
2.  Define a `PointCollection` of each `Geometry`.
3.  Add the overlapping polygons to the graphics overlay.
4.  Determine spatial relationships between polygons, e.g. union, difference, etc, by using the appropriate operation `GeometryEngine.operation(polygon.getGeometry(), polygon.getGeometry())`

## Relevant API

*   Geometry
*   Graphic
*   GraphicsOverlay
*   MapView
*   Point
*   PointCollection
*   SimpleLineSymbol
*   SimpleFillSymbol
