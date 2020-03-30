# Open mobile map package

Display a map from a mobile map package.

![Image of open mobile map package](OpenMobileMapPackage.png)

## Use case

An .mmpk file is an archive containing the data (specifically, basemaps and features) used to display an offline map.

## How to use the sample

When the sample opens, it will automatically display the map in the mobile map package. Pan and zoom to observe the data from the mobile map package.

## How it works

1. Create a `MobileMapPackage` specifying the path to the .mmpk file.
2. Load the mobile map package with `mmpk.loadAsync()`.
3. After it successfully loads, get the map from the mmpk and add it to the map view: `mapView.setMap(mmpk.getMaps().get(0))`.

## Relevant API

* MapView
* MobileMapPackage

## About the data

This sample shows points of interest within a [Yellowstone Mobile Map Package](https://arcgisruntime.maps.arcgis.com/home/item.html?id=e1f3a7254cb845b09450f54937c16061) hosted on ArcGIS Online.

## Additional information

Mobile map packages can also include address locators and geometric networks. For an example of this, see the "Mobile Map Search and Route Sample".

## Tags

mmpk, mobile map package, offline
