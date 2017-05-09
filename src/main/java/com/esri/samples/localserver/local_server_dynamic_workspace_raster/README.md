<h1>Local Server Feature Layer</h1>

<p>Demonstrates how to dynamically add a local raster file to map using the Local Server. </p>

<p><b>Note:</b> Local Server is not supported on MacOS</p>

<img src="LocalServerDynamicWorkspaceRaster.png"/>

<h2>How to use the sample</h2>

<p>A Local Server and Local Feature Service will automatically be started and once running a Feature Layer will be created and added to the map. </p>

<h2>How it works</h2>

<p>To create a <code>RasterWorkspace</code> and add it to a <code>LocalMapService</code>:</p>

<ol>
<li>Create and run a local server.
<ul><li><code>LocalServer.INSTANCE</code> creates a local server</li></ul>
<li><code>Server.startAsync()</code> starts the server asynchronously</li></ul></li>
<li>Create a <code>LocalMapService</code> instance using an empty .MPK file (the sample uses one that is created for you). Don't start it yet.</li>
<li>Create the <code>RasterWorkspace</code> and <code>RasterSublayerSource</code> instances.</li>
<li>Add the <code>RasterWorkspace</code> the list of dynamic workspaces of the <code>LocalMapService</code>.</li>
<li>Start the <code>LocalMapService</code>
<ul><li><code>localMapService.startAsync()</code></li></ul>
<ul><li>Wait for server to be in the  <code>LocalServerStatus.STARTED</code> state.</li></ul>
<ul><li><code>localMapService.addStatusChangedListener()</code> fires whenever the status of the local server has changed.</li></ul></li>
<li>Create a <code>ArcGISMapImageLayer</code> using the url from the <code>localMapService</code></li>
<li>Load the <code>ArcGISMapImageLayer</code>. After it is done loading, Add the ArcGISMapImageSublayer to it's list of sublayers. The ArcGISMapImageSublayer points to the raster file on disk.
<li>Finally, add the <code>ArcGISMapImageLayer</code> to map's list of operational layers. The raster layer appears in the map.</li>
</ol>

<h2>Features</h2>
<ul>
<li>ArcGISMapImageLayer</li>
<li>ArcGISMapImageSublayer</li>
<li>RasterWorkspace</li>
<li>RasterSublayerSource</li>
<li>LocalServer</li>
<li>LocalServerStatus</li>
<li>StatusChangedEvent</li>
</ul>
