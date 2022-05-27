# Local Server generate elevation profile

Create an elevation profile from local raster data and a polyline, using a local geoprocessing package `.gpkx` created using the interpolate shape geoprocessing tool in ArcGIS Pro.

![Image of local server generate elevation profile](LocalServerGenerateElevationProfile.png)

## Use case

Local server applications are helpful for executing offline geoprocessing tasks in your ArcGIS Runtime apps via an offline (local) server. This sample uses a geoprocessing package created with the Interpolate Shape tool, which creates a 3D feature by interpolating z-values on to a surface.

## How to use the sample

The sample loads with a raster. Click the "Draw Polyline" button and sketch a polyline where the elevation profile is to be drawn. Right-click to save the sketch and draw the polyline, then click "Generate Elevation Profile" to interpolate the sketched polyline onto the raster surface in 3D. Once ready, the view will auto zoom side on to the newly drawn elevation profile. Click "Clear Results" to reset the sample.

## How it works

1. Create and run a local server with `LocalServer.INSTANCE`.
2. Start the server asynchronously with `Server.startAsync()`.
3. Wait for server to be in the  `LocalServerStatus.STARTED` state.
    * Callbacks attached to `Server.addStatusChangedListener()` will invoke whenever the status of the local server has changed.
4. Start a `LocalGeoprocessingService` and run a `GeoprocessingTask`.
    1. Instantiate `LocalGeoprocessingService(Url, ServiceType)` to create a local geoprocessing service.
    2. Invoke `LocalGeoprocessingService.start()` to start the service asynchronously.
    3. Instantiate `GeoprocessingTask(LocalGeoprocessingService.url() + "/CreateElevationProfileModel")` to create a geoprocessing task that uses the elevation profile tool.
5. Create an instance of `GeoprocessingParameters` and get its list of inputs with `gpParameters.getInputs()`. Add  `GeoprocessingFeatures` with a `FeatureCollectionTable` pointing to a polyline geometry, and `GeoprocessingString` with a path to raster data on disk to the list of inputs.
6. Create and start a `GeoprocessingJob` using the previous parameters.
    1. Create a geoprocessing job with `GeoprocessingTask.createJob(GeoprocessingParameters)`.
    2. Start the job with `GeoprocessingJob.start()`.
7. Add generated elevation profile as a `FeatureLayer` to the scene.
    1. Get url from local geoprocessing service using `LocalGeoprocessingService.getUrl()`.
    2. Get server job id of geoprocessing job using `GeoprocessingJob.getServerJobId()`.
    3. Replace `GPServer` from url with `MapServer/jobs/jobId`, to get generate elevation profile data.
    4. Create a `ServiceGeodatabase` from the new url, get its first `FeatureTable` and create a `FeatureLayer` from it. Set the surface placement and a renderer to the feature layer, and add it to the scene's list of operational layers.

## Relevant API

* GeoprocessingFeatures
* GeoprocessingJob
* GeoprocessingParameter
* GeoprocessingParameters
* GeoprocessingTask
* LocalGeoprocessingService
* LocalGeoprocessingService.ServiceType
* LocalServer
* LocalServerStatus

## Additional information

Local Server can be downloaded for Windows and Linux platforms from your [ArcGIS Developers dashboard](https://developers.arcgis.com/java/local-server/install-local-server/). Local Server is not supported on macOS.

The [Package Result](https://pro.arcgis.com/en/pro-app/latest/tool-reference/data-management/package-result.htm) tool (in ArcGIS Pro) is used to author ArcGIS Runtime compatible [geoprocessing](https://pro.arcgis.com/en/pro-app/latest/help/analysis/geoprocessing/basics/what-is-geoprocessing-.htm) packages (.gpkx files). For more information on running powerful offline geoprocessing tasks to provide advanced spatial analysis to your applications, see [ArcGIS Runtime Local Server SDK](https://developers.arcgis.com/java/local-server/).

## Tags

elevation profile, geoprocessing, interpolate shape, local, offline, parameters, processing, service
