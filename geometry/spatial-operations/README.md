# Spatial Operations

Find the union, difference, or intersection of two geometries.

![Image of spatial operations](SpatialOperations.png)

## Use case

Determining the spatial relationships between points, lines or polygons is a fundamental concept in GIS. For example, a data analyst may need to find the intersection between areas with a high concentration of medical emergency situations and the service area of a hospital.

## How to use the sample

The sample provides a drop down on the top, where you can select a geometry operation. When you choose a geometry operation, the application performs this operation between the overlapping polygons and applies the result to the geometries.

## How it works

1. Get the `Geometry` of the features you would like to perform a spatial operation on.
2. Use the various static methods of `GeometryEngine` to determine the spatial relationships between the geometries. For example, use `GeometryEngine.intersection(polygon1.getGeometry(), polygon2.getGeometry())` to find the intersection between two polygons.

## Relevant API

* Geometry
* Graphic
* GraphicsOverlay
* MapView
* Point
* PointCollection
* SimpleLineSymbol
* SimpleFillSymbol

## Tags

combine, difference, edit, intersect, intersection, modify, symmetric difference, union
