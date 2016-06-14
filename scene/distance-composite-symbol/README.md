#Distance Composite Symbol#
Demonstrates how to create a graphic using a distance composite scene symbol. Distance composite scene symbols can 
render different symbols depending on the distance between the camera and the graphic.

##How to use the sample##
Zoom out, away from the plane model. At a certain distance away, the plane will become a cone. And farther away, it 
will render as a dot.

![](DistanceCompositeSymbol.png)

##How it works##
To create and display a `DistanceCompositeSceneSymbol`:
1. Create a `GraphicsOverlay` and add it to the `SceneView`.
2. Create symbols for each way the composite symbol can be rendered.
3. Create a `DistanceCompositeSceneSymbol`.
4. Add a `Range` for each symbol to `compositeSymbol.getRangeCollection()`, specifying the distance range it will be 
used for: `compositeSymbol.getRangeCollection().add(new Range(farAwaySymbol, 0, 1000));`
5. Create a graphic with the symbol: `Graphic compositeGraphic = new Graphic(position, compositeSymbol);`
6. Add the graphic to the graphics overlay.

##Features##
- ArcGISScene
- DistanceCompositeSceneSymbol
- DistanceCompositeSceneSymbol.Range
- Graphic
- GraphicsOverlay
- SceneView