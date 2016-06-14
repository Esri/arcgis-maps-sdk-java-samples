#Geometry Engine Sample#
This sample demonstrates how to use the `GeometryEngine` to perform geometry operations between intersecting polygons in a `GraphicsOverlay`.

##How to use the sample##
The sample provides a drop down on the top, where you can select a geometry operation. When you choose a geometry operation the application performs this operation between the intersecting polygons and applies the result to the geometries.

![](GeometryEngineSample.png)

##How it works##
To find the Union, difference and intersection between intersecting Polygons and buffer operation.

- Create a GraphicsOverlay and add it to the `MapView`.
- Define the `PointCollection` of each `Geometry`.
- Add the intersecting `Polygon`s to the GraphicsOverlay.
- Determine spatial relationships between polygons, e.g. union, difference, etc by using the appropriate operation `GeometryEngine#operation(polygon#geometry, polygon#geometry)`

##Features##
- MapView
- Graphic
- GraphicsOverlay
- Point
- PointCollection
- SimpleFillSymbol
- Geometry