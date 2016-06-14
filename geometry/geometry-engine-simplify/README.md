#Geometry Engine Simplify#
This sample demonstrates how to use the `GeometryEngine` to perform the simplify geometry operation on a `Polygon`.

##How to use the sample##
Click on the simplify button to apply the simplify geometry operation between the intersecting polygons. Click reset to restart the sample.

![](GeometryEngineSimplify.png)

##How it works##
To perform the simplify geometry operation on a `Polygon`.

- Create a GraphicsOverlay and add it to the `MapView`.
- Define the `PointCollection` of the `Geometry`.
- Add the `Polygon`s to the GraphicsOverlay.
- Determine the simplified geometry by using the `GeometryEngine.simplify(polygon#getGeometry()` method.

##Features##
- MapView
- Graphic
- GraphicsOverlay
- Point
- PointCollection
- SimpleFillSymbol
- Geometry