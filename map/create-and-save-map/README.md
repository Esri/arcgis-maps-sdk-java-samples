# Create and save map

Create and save a map as an ArcGIS `PortalItem` (i.e. web map). 

![Image of create and save map](CreateAndSaveMap.png)

## Use case

Maps can be created programmatically in code and then serialized and saved as an ArcGIS `web map`. A `web map` can be shared with others and opened in various applications and APIs throughout the platform, such as ArcGIS Pro, ArcGIS Online, the JavaScript API, Collector, and Explorer.

## How to use the sample

This sample requires you to setup your own app on arcgis.com. See https://github.com/Esri/arcgis-runtime-samples-java/blob/master/OAuth.md for instructions.

Fill in your portal and registered app credentials in the starting dialog to authenticate. Then, choose the basemap and layers for your new map. To save the map, choose a title, tags and description (optional), and a folder on your portal (you will need to create one in your portal's My Content). Click the Save button to save the map to the chosen folder.

## How it works

1. A `Map` is created with a `Basemap` and a few operational layers.
2. A `Portal` object is created and loaded. This will issue an authentication challenge, prompting the user to provide credentials.
3. Once the user is authenticated, `Map.saveAsAsync()` is called and a new `Map` is saved with the specified title, tags, and folder.

## Relevant API

* ArcGISMap
* Portal

## Tags

ArcGIS Online, ArcGIS Pro, portal, publish, share, web map
