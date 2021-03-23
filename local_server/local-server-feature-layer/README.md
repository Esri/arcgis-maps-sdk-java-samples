# Local Server feature layer

Start a local feature service and display its features in a map.

![Image of local server feature layer](LocalServerFeatureLayer.png)

## Use case

For executing offline geoprocessing tasks in your ArcGIS Runtime apps via an offline (local) server.

## How to use the sample

A Local Server and Local Feature Service will automatically be started. Once started then a `FeatureLayer` will be created and added to the map.

## How it works

1. Create and run a local server with `LocalServer.INSTANCE`.
2. Start the server asynchronously with `Server.startAsync()`.
3. Wait for server to be in the  `LocalServerStatus.STARTED` state.
    * Callbacks attached to `Server.addStatusChangedListener()` will invoke whenever the status of the local server has changed.
4. Create and run a local feature service.
    1. Instantiate `LocalFeatureService(Url)` to create a local feature service with the given url path to mpk file.
    2. Start the service asynchronously with `LocalFeatureService.startAsync()`.
        * The service will be added to the local server automatically.
5. Wait for state of the feature service to be `LocalServerStatus.STARTED`.
    * Callbacks attached to `LocalFeatureService.addStatusChangedListener()` will invoke whenever the status of the local service has changed.
6. Create a feature layer from local feature service.
    1. Create a `ServiceFeatureTable(Url)` from local feature service url provided by `LocalFeatureService.getUrl()`.
    2. Load the table asynchronously using `ServiceFeatureTable.loadAsync()`.
    3. Create feature layer from service feature table using `new FeatureLayer(ServiceFeatureTable)`.
    4. Load the layer asynchronously using `FeatureLayer.loadAsync()`.
7. Add feature layer to map using `ArcGISMap.getOperationalLayers().add(FeatureLayer)`.

## Relevant API

* FeatureLayer
* LocalFeatureService
* LocalServer
* LocalServerStatus
* StatusChangedEvent

## Additional information

Local Server can be downloaded for Windows and Linux platforms from your [ArcGIS Developers dashboard](https://developers.arcgis.com/java/local-server/install-local-server/). Local Server is not supported on macOS.

Note: You may wish to reference earlier releases of Local Server. For example, the ArcGIS Runtime API for Java v100.10.0 is configured for Local Server v100.10.0. If you wish to access Local Server 100.9.0, you will have to locate the Local Server install directory and rename it from `LocalServer100.9` to `LocalServer100.10`, and also update the environment variable from `RUNTIME_LOCAL_SERVER_100_9` to `RUNTIME_LOCAL_SERVER_100_10`.

## Tags

feature service, local, offline, server, service
