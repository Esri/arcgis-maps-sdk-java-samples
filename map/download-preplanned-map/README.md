# Download a preplanned map area

Take a map offline using a preplanned map area.

![Download Preplanned Map Sample](DownloadPreplannedMap.png)

## Use case

When a map is taken offline, a package containing basemap tiles, feature data, and other resources is created. In the preplanned workflow, the author of the web map has set up the offline packages ahead of time, which enables faster, more resource-efficient downloads compared to an on-demand workflow. Because the resources for the area are packaged once and can be downloaded many times by different users, this approach is more scalable for large organizations. To see the difference for yourself, compare this sample to the Generate an offline map sample.

## How to use the sample

**NOTE:** Downloading Tiles for offline use requires authentication with the web map's server. To use this sample, you will require an [ArcGIS Online](www.arcgis.com) account. 

Select a map area to take offline, then use the button to take it offline. Click 'Delete offline areas' to remove any downloaded map areas.

## How it works

1. Open the map from a Portal item and display it.
2. Create an `OfflineMapTask` from the Portal item.
3. Call `offlineMapTask.getPreplannedMapAreasAsync()` to find the preplanned areas, then load each one by calling `mapArea.loadAsync()`.
4. Display the areas in the UI.
5. When the user selects a map area, start the download.
    1. Create a `DownloadPreplannedOfflineMapParameters` using `OfflineMapTask.createDefaultDownloadPreplannedOfflineMapParametersAsync`.
    2. **Note:** setting the `.updateOption()` on the parameters allows fine-tuning the update behaviour of the offline map. In this case, the preplanned area is defined not to apply any future updates, `SomeEnum.NOUPDATES`.
    3. Create a `DownloadPreplannedOfflineMapJob` using `OfflineMapTask.downloadPreplannedOfflineMap`, passing in the parameters.
    4. Wait for the job to complete and get the result, `DownloadPreplannedOfflineMapJob.getResult()`.
    5. Display any errors to the user.
    6. Show the offline map in the `MapView`.

## Relevant API

* DownloadPreplannedOfflineMapJob
* DownloadPreplannedOfflineMapParameters
* DownloadPreplannedOfflineMapResult
* OfflineMapTask
* PreplannedMapArea

## About the data

The [Naperville stormwater network map](https://arcgisruntime.maps.arcgis.com/home/item.html?id=acc027394bc84c2fb04d1ed317aac674) is based on ArcGIS Solutions for Stormwater and provides a realistic depiction of a theoretical stormwater network.

## Additional information

See [Take a map offline - preplanned](https://developers.arcgis.com/net/latest/wpf/guide/take-map-offline-preplanned.htm) to learn about preplanned workflows, including how to define preplanned areas in ArcGIS Online.

## Tags

map area, offline, preplanned, pre-planned