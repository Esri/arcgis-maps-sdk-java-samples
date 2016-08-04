#Geometry Engine Sample#
Demonstrates how to use the GeometryEngine to perform geometry operations between intersecting polygons in a GraphicsOverlay.

##How to use the sample##
The sample provides a drop down on the top, where you can select a geometry operation. When you choose a geometry operation the application performs this operation between the intersecting polygons and applies the result to the geometries.

![](GeometryEngineSample.png)

##How it works##
To find the union, difference, or intersection between intersecting `Polygon`s:

1. Create a `GraphicsOverlay` and add it to the `MapView`.
2. Define a `PointCollection` of each `Geometry`.
3. Add the intersecting polygons to the graphics overlay.
4. Determine spatial relationships between polygons, e.g. union, difference, etc, by using the appropriate operation `GeometryEngine.operation(polygon.getGeometry(), polygon.getGeometry())`

##Tags
- Geometry
- Graphic
- GraphicsOverlay
- MapView
- Point
- PointCollection
- SimpleLineSymbol
- SimpleFillSymbol
