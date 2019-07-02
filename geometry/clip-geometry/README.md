# Clip Geometry

Clip a geometry to an envelope.

![](ClipGeometry.gif)

## How to use the sample

Click the "Clip" button to clip the blue graphic with the red envelopes.

## How it works

To clip a `Geometry` with an `Envelope`:


  1. Use the static method `GeometryEngine.clip(geometry, envelope)`.
  2. Keep in mind that the resulting `Geometry` may be null if the envelope does not intersect the 
  geometry you are clipping.`


## Relevant API


*   ArcGISMap
*   Basemap
*   Envelope
*   Geometry
*   GeometryEngine
*   Graphic
*   GraphicsOverlay
*   MapView
*   Point
*   PointCollection
*   Polygon
*   Polyline
*   SimpleFillSymbol
*   SimpleLineSymbol
*   SpatialReferences

