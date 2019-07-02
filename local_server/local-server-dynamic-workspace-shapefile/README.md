# Local Server Dynamic Workspace Shapefile

Dynamically add a local shapefile to a map using Local Server.

**   Note: **   Local Server is not supported on MacOS

![](LocalServerDynamicWorkspaceShapefile.png)

## How to use the sample

A Local Server will automatically start once application is running. Select a shapefile using the `Choose Shapefile` button which will automatically start a Local Map Service where a Dynamic Shapefile Workspace will be set and displayed to the Map View.

## How it works

To create a `ShapefileWorkspace` and add it to a `LocalMapService`:

1.  Create and run a local server.
    *   `LocalServer.INSTANCE` creates a local server
    *   `Server.startAsync()` starts the server asynchronously
2.  Create a LocalMapService instance using an empty .MPK file (the sample uses one that is created for you). Don't start it yet.
3.  Create a ShapefileWorkspace  with id 'shp_wkspc' and absolute path to location of were shapefile is being stored.
4.  Create a `TableSublayerSource`  using  `shapefileWorkspace.getId()` and name of  shapefile with extension, example `mjrroads.shp`.
5.  Add ShapefileWorkspace to `LocalMapService.setDynamicWorkspaces()`.
6.  Start the LocalMapService
    *   `localMapService.startAsync()`
    *   `localMapService.addStatusChangedListener()` fires whenever the status of the local server has changed.
7.  Wait for server to be in the  `LocalServerStatus.STARTED` state.
8.  Create a `ArcGISMapImageLayer` using `localMapService.getUrl()`
9.  Add the `ArcGISMapImageSublayer` to it's list of sublayers. The ArcGISMapImageSublayer points to the shapefile file on disk.
    *   Once ArcGISMapImageLayer is done loading,  set ArcGISMapImageSublayer to `ArcGISMapImageLayer.getSublayers()`. The ArcGISMapImageSublayer points to the shapefile file on disk.

## Relevant API

*   ArcGISMapImageLayer
*   ArcGISMapImageSublayer
*   DynamicWorkspace
*   LocalServer
*   ShapefileWorkspace
*   TableSublayerSource
