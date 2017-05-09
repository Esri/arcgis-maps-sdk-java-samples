<h1>Local Server Dynamic Workspace Raster</h1>

<p>Demonstrates how to dynamically add a local raster file to a map using the Local Server.</p>

<p><b>Note:</b> Local Server is not supported on MacOS</p>

<img src="LocalServerDynamicWorkspaceRaster.png"/>

<h2>How to use the sample</h2>

<p>After the local server is done loading, click the Choose Raster button to select a raster to work with.</p>

<h2>How it works</h2>

<p>To create a <code>RasterWorkspace</code> and add it to a <code>LocalMapService</code>:</p>

<ol>
<li><code>LocalServer.INSTANCE.startAsync()</code> starts the server asynchronously.</li></ul></li>
<li>Create a <code>LocalMapService</code> instance using an empty .MPK file.</li>
<li>Create the <code>RasterWorkspace</code> and <code>RasterSublayerSource</code> instances.</li>
<li>Add the <code>RasterWorkspace</code> to the list of dynamic workspaces of the <code>LocalMapService</code>.</li>
<li>Start the <code>LocalMapService</code>
<ul><li><code>localMapService.startAsync()</code></li></ul>
<ul><li>Wait for server to be in the  <code>LocalServerStatus.STARTED</code> state.</li></ul>
<ul><li><code>localMapService.addStatusChangedListener()</code> fires whenever the status of the local server has changed.</li></ul></li>
<li>Create a <code>ArcGISMapImageLayer</code> using the url from the <code>localMapService</code>.</li>
<li>Load the <code>ArcGISMapImageLayer</code>. After it is done loading, Add the <code>ArcGISMapImageSublayer</code> to 
it's list of sublayers. The ArcGISMapImageSublayer points to the raster file on disk.
<li>Finally, add the <code>ArcGISMapImageLayer</code> to the map's list of operational layers to show it on the map
.</li>
</ol>

<h2>Features</h2>
<ul>
<li>ArcGISMapImageLayer</li>
<li>ArcGISMapImageSublayer</li>
<li>LocalServer</li>
<li>LocalServerStatus</li>
<li>RasterSublayerSource</li>
<li>RasterWorkspace</li>
</ul>
