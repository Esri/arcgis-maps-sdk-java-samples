# Local Server services

Demonstrates how to start and stop the Local Server and start and stop a local map, feature, and geoprocessing service running on the Local Server.

![Image of local server services](LocalServerServices.png)

## Use case

For executing offline geoprocessing tasks in your ArcGIS Runtime apps via an offline (local) server.

## How to use the sample

Choose an option from the dropdown control to filter packages by service type. Then click the Open button to choose a package. Finally, click the Start button to start the service. The service's status will be displayed in the center log.

To stop a service, select it from the Running Services list and click the Stop Service button. To go to the service's URL in your default web browser, select it and click the Go to URL button.

## How it works

To start a `LocalServer` and attach a `LocalService`:

1. Create and run a local server with `LocalServer.INSTANCE`.
2. Start the server asynchronously with `Server.startAsync()`.
3. Wait for server to be in the  `LocalServerStatus.STARTED` state.
    * Callbacks attached to `Server.addStatusChangedListener()` will invoke whenever the status of the local server has changed.
4. Create and run a local service. Here is an example of running a `LocalMapService`:
    1. Instantiate `LocalMapService(Url)` to create a local map service with the given URL path to map package (`mpk` or `mpkx` file).
    2. Start the job with `LocalMapService.startAsync()`.
        * The service is added to the `LocalServer` automatically.

To stop a `LocalServer` and any attached `LocalServices`:

1. If required, you can retrieve a list of all running services with `LocalServer.INSTANCE.getServices()`.
2. Stop the services asynchronously using `LocalService.stopAsync()`.
3. Use `LocalServer.INSTANCE.stopAsync()` to stop the server asynchronously. You can use `.addDoneListener()` on this process to perform additional actions after the server is successfully stopped, such as removing temporary files.

## Relevant API

* LocalFeatureService
* LocalGeoprocessingService
* LocalMapService
* LocalServer
* LocalServerStatus
* LocalService

## Additional information

Local Server can be downloaded for Windows and Linux platforms from your [ArcGIS Developers dashboard](https://developers.arcgis.com/java/local-server/install-and-set-up/). Local Server is not supported on macOS.

Specific versions of ArcGIS Runtime Local Server are compatible with the version of ArcGIS Pro you use to create geoprocessing and map packages. For example, the ArcGIS Runtime API for Java v100.11.0 is configured for Local Server v100.10.0 which provides compatibility for packages created with ArcGIS Pro 2.7.x. For more information see the [ArcGIS Developers guide](https://developers.arcgis.com/java/reference/system-requirements/#local-server-version-compatibility-with-arcgis-desktop-and-arcgis-pro).

To configure the ArcGIS Runtime API for Java v100.11.0 to work with Local Server 100.9.0:

* Development machine:
    * Locate the Local Server installation directory and rename the folder from `LocalServer100.9` to `LocalServer100.10`.
    * Update the environment variable from `RUNTIME_LOCAL_SERVER_100_9` to `RUNTIME_LOCAL_SERVER_100_10`.
* Deployment machine(s): Rename the deployment folder included with your application (or referenced by the LocalServerEnvironment.InstallPath property) from `LocalServer100.9` to `LocalServer100.10`.

## Tags

feature, geoprocessing, local services, map, server, service
