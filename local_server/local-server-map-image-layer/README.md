# Local Server Map Image Layer

Create a local map imagery service and show its tiles in a map.

**   Note: **   Local Server is not supported on MacOS

![](LocalServerMapImageLayer.png)

## How to use the sample

A Local Server and Local Map Service will automatically be started and once running a Map Image Layer will be created and added to the map.

## How it works

To create a `ArcGISMapImageLayer` from a `LocalMapService`:


1.  Create and run a local server.
*   `LocalServer.INSTANCE` creates a local server
*   `Server.startAsync()` starts the server asynchronously
2.  Wait for server to be in the  `LocalServerStatus.STARTED` state.
*   `Server.addStatusChangedListener()` fires whenever the status of the local server has changed.
3.  Create and run a local map service.
*   `new LocalMapService(Url)`, creates a local map service with the given url path to mpk file
*   `LocalMapService.startAsync()`, starts the service asynchronously
*   service will be added to the local server automatically
4.  Wait for map service to be in the  `LocalServerStatus.STARTED` state.
*   `LocalMapService.addStatusChangedListener()` fires whenever the status of the local service has changed.
5.  Create a map image layer from local map service.
*   create a `new ArcGISMapImageLayer(Url)` from local map service url, `LocalMapService.getUrl()`
*   load the layer asynchronously, `ArcGISMapImageLayer.loadAsync()`
6.  Add map image layer to map, `Map.getOperationalLayers().add(ArcGISMapImageLayer)`.


## Relevant API

*   ArcGISMapImageLayer
*   LocalMapService
*   LocalServer
*   LocalServerStatus
*   StatusChangedEvent


