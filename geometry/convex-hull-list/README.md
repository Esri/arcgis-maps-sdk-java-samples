# Convex Hull List

Generate convex hull polygon(s) from multiple input geometries.

The convex hull generates a minimum bounding geometry of input graphics. As a visual analogy, consider a set of points as nails in a board. The convex hull of the points would be like a rubber band stretched around the outermost nails.

![](ConvexHullList.png)

## How to use the sample

Click the 'Create Convex Hull' button to create convex hull(s) from the polygon graphics. If the 'Union' checkbox is checked, the resulting output will be one polygon being the convex hull for the two input polygons. If the 'Union' checkbox is un-checked, the resulting output will have two convex hull polygons - one for each of the two input polygons. Click the 'Clear' button to start over.

## How it works

1.  Create an `ArcGISMap` and display it in a `MapView`.
2.  Create two input polygon graphics and add them to a `GraphicsOverlay`.
3.  Call `GeometryEngine.convexHull(inputGeometries, boolean)`, loop through the returned geometry (or geometries) and add them to a new `GraphicsOverlay`, set above the one containing the two input polygons.

## Relevant API

*   GeometryEngine.ConvexHull
*   GraphicsOverlay
*   PointCollection

## Tags

Analysis, ConvexHull, GeometryEngine
