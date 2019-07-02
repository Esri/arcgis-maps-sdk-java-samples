# Local Server Geoprocessing

Create contour lines from local raster data and Local Server.
 
This is accomplished using a local geoprocessing package (.gpk) and the contour geoprocessing tool.

**   Note: **   Local Server is not supported on MacOS

![](LocalServerGeoprocessing.png)

## How to use the sample

Contour Line Controls (Top Left):
*   Interval-- Specifies the spacing between contour lines.
*   Generate Contours --  Adds contour lines to map using interval.
*   Clear Results --  Removes contour lines from map.
  
## How it works

To start a `GeoprocessingTask` that generates contour lines from raster data:


1. Add raster data to map using as an `ArcGISTiledLayer`.
2. Create and run a `LocalServer`.
  
*   `LocalServer.INSTANCE` creates a local server
*   `Server.startAsync()` starts the server asynchronously
3. Wait for server to be in the `LocalServerStatus.STARTED` state.
  
*   `Server.addStatusChangedListener()` fires whenever the running status of the local server has changed.
4. Start a `LocalGeoprocessingService` and run a `GeoprocessingTask`.
  
*   `new LocalGeoprocessingService(Url, ServiceType)`, creates a local geoprocessing service
*   `LocalGeoprocessingService.startAsync()` starts the geoprocessing service asynchronously
*   `new GeoprocessingTask(LocalGeoprocessingService.getUrl() + "/Contour")`, creates a geoprocessing task that uses the contour lines tool
5. Create `GeoprocessingParameters` and add a `GeoprocessingDouble` as a parameter using set interval.
  
*   `new GeoprocessingParameters(ExecutionType)`, creates geoprocess parameters
*   `GeoprocessingParameters.getInputs().put("Interval", new GeoprocessingDouble(double))`, creates a parameter with name `Interval` with the interval set as its value.
6. Create and start a `GeoprocessingJob` using the parameters from above.
  
*   `GeoprocessingTask.createJob(GeoprocessingParameters)`, creates a geoprocessing job
*   `GeoprocessingJob.start()`, starts job
7. Add contour lines as an `ArcGISMapImageLayer` to map.
  
*   get url from local geoprocessing service, `LocalGeoprocessingService.getUrl()`
*   get server job id of geoprocessing job, `GeoprocessingJob.getServerJobId()`
*   replace `GPServer` from url with `MapServer/jobs/jobId`, to get generate contour lines data
*   create a map image layer from that new url and add that layer to the map


## Relevant API


*   GeoprocessingDouble
*   GeoprocessingJob
*   GeoprocessingParameter
*   GeoprocessingParameters
*   GeoprocessingTask
*   LocalGeoprocessingService
*   LocalGeoprocessingService.ServiceType
*   LocalServer
*   LocalServerStatus

