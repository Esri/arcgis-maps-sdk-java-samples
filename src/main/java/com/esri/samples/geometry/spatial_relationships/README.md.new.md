# Spatial Relationships

Determine spatial relationships between two geometries.

![](SpatialRelationships.png)

## How to use the sample

Click on one of the three graphics to select it. The tree view will list
the relationships the selected graphic has to the other graphic
geometries.

## How it works

To check the relationship between geometries.

1.  Get the geometry from two different graphics. In this example the
    geometry of the selected graphic is compared to the geometry of each
    graphic not selected.
2.  Use the methods in `GeometryEngine` to check the relationship
    between the geometries, e.g.  `contains`, `disjoint`, `intersects`,
    etc. If the method returns `true`, the relationship exists.
