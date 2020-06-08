# Open map (URL)

Display a web map.

![Image of open map URL](OpenMapURL.png)

## Use case

For displaying web maps stored on ArcGISOnline.

## How to use the sample

A web map can be selected from the drop-down list. On selection the web map displays in the map view.

## How it works

1. Create a `Portal`.
2. Create a `PortalItem` using the Portal and the web map ID: `new PortalItem(portal, ID)`.
3. Create a `ArcGISMap` using the portal item.
4. Set the map to the `MapView`.

## Relevant API

* ArcGISMap
* MapView
* Portal
* PortalItem

## About the data

The web maps accessed by this sample show [USA soil survey results](https://www.arcgis.com/home/webmap/viewer.html?webmap=0edea1c7bbb84ba5842d20483af11679) and [2014 USA Tapestry Segmentation Density](https://www.arcgis.com/home/webmap/viewer.html?webmap=01f052c8995e4b9e889d73c3e210ebe3).

## Tags

portal item, web map
