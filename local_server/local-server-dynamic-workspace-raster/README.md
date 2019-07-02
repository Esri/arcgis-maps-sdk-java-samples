# Local Server Dynamic Workspace Raster

Dynamically add a local raster file to a map using Local Server.

**Note:**   Local Server is not supported on MacOS

![](LocalServerDynamicWorkspaceRaster.png)

## How to use the sample

A Local Server and Local Feature Service will automatically be started and once running a Feature Layer will be created and added to the map.

## How it works

To create a `RasterWorkspace` and add it to a `LocalMapService`:

1.  Create and run a local server.
    *   `LocalServer.INSTANCE` creates a local server
2.  `Server.startAsync()` starts the server asynchronously
3.  Create a LocalMapService instance using an empty .MPK file (the sample uses one that is created for you). Don't start it yet.
4.  Create the RasterWorkspace and `RasterSublayerSource` instances.
5.  Add the RasterWorkspace the list of dynamic workspaces of the LocalMapService.
6.  Start the LocalMapService
    *   `localMapService.startAsync()`
    *   Wait for server to be in the  `LocalServerStatus.STARTED` state.
    *   `localMapService.addStatusChangedListener()` fires whenever the status of the local server has changed.
7.  Create a `ArcGISMapImageLayer` using the url from the LocalMapService
8.  Add the `ArcGISMapImageSublayer` to it's list of sublayers. The ArcGISMapImageSublayer points to the raster file on disk.
9.  Finally, add the ArcGISMapImageLayer to map's list of operational layers. The raster layer appears in the map.

## Relevant API

*   ArcGISMapImageLayer
*   ArcGISMapImageSublayer
*   DynamicWorkspace
*   LocalServer
*   RasterWorkspace
*   RasterSublayerSource
*   StatusChangedEvent
