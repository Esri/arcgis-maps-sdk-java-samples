# Create Geometries

Create simple geometry types.

![](CreateGeometries.png)

## How it works
To create different geometries and show them as graphics:

  1. Use the constructors for the various simple `Geometry` types including `Point`, 
  `Polyline`, `Multipoint`, `Polygon`, and `Envelope`. Geometries made 
  of multiple points usually take a `PointCollection` as an argument.
  2. To display the geometry, create a `Graphic` passing in the geometry, and a `Symbol` 
  appropriate for the geometry type.
  3. Add the `Graphic` to a `GraphicsOverlay` and add the overlay to the 
  `MapView`.


## Relevant API  

* Envelope
* Graphic
* Multipoint
* Point
* PointCollection
* Polygon
* Polyline
