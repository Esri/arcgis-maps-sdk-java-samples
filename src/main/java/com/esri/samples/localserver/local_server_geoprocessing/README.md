<h1>Local Server Geoprocessing</h1>

<p>Demonstrates how to create contour lines from local raster data using a local geoprocessing package (.gpk) and the contour geoprocessing tool.</p>

<p><img src="LocalServerGeoprocessing.gif"/></p>

<h2>How to use the sample</h2>

<p>Contour Line Controls (Top Left): </p>
  <ul><li> Interval-- Specifies the spacing between contour lines.</li>
      <li> Generate Contours --  Adds contour lines to map using interval. </li>
      <li> Clear Results --  Removes contour lines from map. </li></ul>
  
<h2>How it works</h2>

<p>To start a <code>Local Geoprocessing Service</code> and generate contour lines from raster data:</p>

<code></code>

<ol>
<li> Add raster data to map using as an <code>ArcGISTiledLayer</code>.</li>
<li> Create and run a <code>LocalServer</code>.
  <ul><li><code>LocalServer.INSTANCE</code> creates a local server</li>
      <li><code>Server.startAsync()</code> starts the server asynchronously</li></ul></li>
<li>Wait for server to be in the  <code>LocalServerStatus.STARTED</code> state.
  <ul><li><code>Server.addStatusChangedListener()</code> fires whenever the running status of the local server has changed.</li></ul></li>
<li> Start a <code>LocalGeoprocessingService</code> and run a <code>GeoprocessTask</code>.
  <ul>
    <li><code>new LocalGeoprocessingService(Url, ServiceType)</code>, create local geoprocessing service</li>
    <li><code>LocalGeoprocessingService.startAsync()</code> starts the geoprocessing service asynchronously</li>
    <li><code>new GeoprocessingTask(LocalGeoprocessingService.getUrl() + "/Contour")</code>, creates a geoprocessing task that uses the contour lines tool</li>
  </ul>
</li>
<li> Create <code>GeoprocessingParameters</code> and add a <code>GeoprocessingDouble</code> as a parameter using set interval.
  <ul>
    <li><code>new GeoprocessingParameters(ExecutionType)</code>, creates geoprocess parameters</li>
    <li><code>GeoprocessingParameters.getInputs().put("Interval", new GeoprocessingDouble(double))</code>, creates a parameter with name `Interval` with the interval set as its value.</li>
  </ul>
</li>
<li> Ceate and start a <code>GeoprocessingJob</code> using the parameters from above.
  <ul>
    <li><code>GeoprocessingTask.createJob(GeoprocessingParameters)</code>, creates a geoprocessing job</li>
    <li><code>GeoprocessingJob.start()</code>, starts job</li>
  </ul>
</li>

<li> Add contour lines as an <code>ArcGISMapImageLayer</code> to map.
  <ul>
    <li>get url from local geoprocessing service, <code>LocalGeoprocessingService.getUrl()</code></li>
    <li>get server job id of geoprocessing job, <code>GeoprocessingJob.getServerJobId()</code></li>
    <li>replace `GPServer` from url with `MapServer/jobs/jobId`, to get generate contour line data</li>
    <li>create a map image layer from that new url and add that layer to the map</li>
  </ul>
</li>

</ol>

<h2>Features</h2>

<ul>
<li>GeoprocessingDouble</li>
<li>GeoprocessingJob</li>
<li>GeoprocessingParameter</li>
<li>GeoprocessingParameters</li>
<li>GeoprocessingTask</li>
<li>LocalGeoprocessingService</li>
<li>LocalGeoprocessingService.ServiceType</li>
<li>LocalServer</li>
<li>LocalServerStatus</li>
</ul>
