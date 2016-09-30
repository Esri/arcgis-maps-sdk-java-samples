#Calculate Distance 3D
Demonstrates how to calculate the distance, in meters, between two Graphics in 3D space.

##How to use the sample
Once the SceneView has loaded the Graphic's animation will begin. The distance between the two Graphics will be displayed at the top of the application and will be updated when the Graphic's animation starts. 

![](CalculateDistance3D.PNG)


##How it works
To calculate the distance between two `Graphic`s in 3D space:

1. Create a `GraphicsOverlay` and attach it to the `SceneView`.
1. Create the two graphics and add to graphics overlay.
  - supply each graphic with a `Point`, starting location, and `SimpleMarkerSymbol`
1. Convert each graphic's point to the Cartesian coordinate system
1. Create a JavaFX Point3D from the Cartesian x,y, and z value.
1. Then get the distance between each of the JavaFX Point3Ds, `Point3D.distance(Point3D)`.

##Features
- ArcGISScene
- Graphic
- GraphicsOverlay
- SceneView
