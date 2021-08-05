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

The web maps accessed by this sample show [Geology for United States](https://runtime.maps.arcgis.com/apps/mapviewer/index.html?webmap=92ad152b9da94dee89b9e387dfe21acd#), [Terrestrial Ecosystems of the World](https://runtime.maps.arcgis.com/home/item.html?id=5be0bc3ee36c4e058f7b3cebc21c74e6) and [US Wind Turbine Database](https://runtime.maps.arcgis.com/home/item.html?id=70dc2991bbb442b8bb4b50f005bfbb4e).

## Tags

portal item, web map
