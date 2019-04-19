# List KML Contents

Show KML nodes in their nested hierarchy.

![](ListKMLContents.png)

## How to use the sample

When the scene and KML layer loads, the KML node tree will be shown in
the tree view. Click on a node to zoom to its extent (if it has one).

## How it works

To list the nodes in a KML file:

1.  Create a `KmlDataset` pointing to the KML file.
2.  Start with a list of the rood nodes with
    `kmlDataset.getRootNodes()`.
3.  For each node, check if it is a `KmlContainer` or `KmlNetworkLink`.
    These types can have child nodes. If it is one of these, cast to the
    appropriate type and call `getChildNodes()`.
4.  Recursively search these child nodes for more nodes.

## About the data

This is an example KML file meant to demonstrate how Runtime supports
several common features.
