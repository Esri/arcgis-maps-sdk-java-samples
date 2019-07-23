# Download a preplanned map area

Take a map offline using a preplanned map area.

![Download Preplanned Map Sample](DownloadPreplannedMap.png)

## Use case

When a map is taken offline, a package containing basemap tiles, feature data, and other resources is created. In the preplanned workflow, the author of the web map has set up the offline packages ahead of time, which enables faster, more resource-efficient downloads compared to an on-demand workflow. Because the resources for the area are packaged once and can be downloaded many times by different users, this approach is more scalable for large organizations. To see the difference for yourself, compare this sample to the Generate an offline map sample.

## How to use the sample

**NOTE:** Downloading Tiles for offline use requires authentication with the web map's server. To use this sample, you will require an [ArcGIS Online](www.arcgis.com) account.

Select a map area to take offline, then use the button to take it offline. Click 'Delete offline areas' to remove any downloaded map areas.

## How it works

1. Open the `Map` from a `PortalItem` and display it.
2. Create an `OfflineMapTask` from the portal item.
3. Get the `PreplannedMapArea`s from the task, and then load them.
4. To download a selected map area, Create the default `DownloadPreplannedOfflineMapParameters` from the task.
5. Set the update mode of the preplanned map area with `PreplannedUpdateMode.NO_UPDATES`, so as not to apply any future updates (see further information section for other options).
6. Use the parameters and a download path to create a `DownloadPreplannedOfflineMapJob` from the task.
7. Start the job, and once it completed get the `DownloadPreplannedOfflineMapResult`.
8. Get the `Map` from the result and display it in the `MapView`.

## Relevant API

* DownloadPreplannedOfflineMapJob
* DownloadPreplannedOfflineMapParameters
* DownloadPreplannedOfflineMapResult
* OfflineMapTask
* PreplannedMapArea

## About the data

The [Naperville stormwater network map](https://arcgisruntime.maps.arcgis.com/home/item.html?id=acc027394bc84c2fb04d1ed317aac674) is based on ArcGIS Solutions for Stormwater and provides a realistic depiction of a theoretical stormwater network.

## Additional information

`PreplannedUpdateMode` can be used to set the way the preplanned map area receives updates in several ways:

* `NO_UPDATES` - No updates will be performed.
* `SYNC_WITH_FEATURE_SERVICES` - Changes, including local edits, will be synced directly with the underlying feature services.
* `DOWNLOAD_SCHEDULED_UPDATES` - Scheduled, read-only updates will be downloaded from the online map area and applied to the local mobile geodatabases.

See [Take a map offline - preplanned](https://developers.arcgis.com/java/latest/guide/take-map-offline-preplanned.htm) to learn about preplanned workflows, including how to define preplanned areas in ArcGIS Online.

## Tags

map area, offline, preplanned, pre-planned
