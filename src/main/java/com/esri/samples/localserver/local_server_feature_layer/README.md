<h1>Local Server Feature Layer</h1>

<p>Start a local feature service and display its features in a map.</p>

<p><b>Note:</b> Local Server is not supported on MacOS</p>

<img src="LocalServerFeatureLayer.png"/>

<h2>How to use the sample</h2>

<p>A Local Server and Local Feature Service will automatically be started and once running a Feature Layer will be created and added to the map. </p>

<h2>How it works</h2>

<p>To create a <code>FeatureLayer</code> from a <code>LocalFeatureService</code>:</p>

<ol>
<li>Create and run a local server.
<ul><li><code>LocalServer.INSTANCE</code> creates a local server</li>
<li><code>Server.startAsync()</code> starts the server asynchronously</li></ul></li>
<li>Wait for server to be in the  <code>LocalServerStatus.STARTED</code> state.
<ul><li><code>Server.addStatusChangedListener()</code> fires whenever the status of the local server has changed.</li></ul></li>
<li>Create and run a local feature service.
<ul><li><code>new LocalFeatureService(Url)</code>, creates a local feature service with the given url path to mpk file</li>
<li><code>LocalFeatureService.startAsync()</code>, starts the service asynchronously</li>
<li>service will be added to the local server automatically</li></ul></li>
<li>Wait for feature service to be in the  <code>LocalServerStatus.STARTED</code> state.
<ul><li><code>LocalFeatureService.addStatusChangedListener()</code> fires whenever the status of the local service has changed.</li></ul></li>
<li>Create a feature layer from local feature service.
<ul><li>create a <code>ServiceFeatureTable(Url)</code> from local feature service url, <code>LocalFeatureService.getUrl()</code></li>
<li>load the table asynchronously, <code>ServiceFeatureTable.loadAsync()</code></li>
<li>create feature layer from service feature table, <code>new FeatureLayer(ServiceFeatureTable)</code></li>
<li>load the layer asynchronously, <code>FeatureLayer.loadAsync()</code></li></ul></li>
<li>Add feature layer to map, <code>Map.getOperationalLayers().add(FeatureLayer)</code>.</li>
</ol>

<h2>Relevant API</h2>
<ul>
<li>FeatureLayer</li>
<li>LocalFeatureService</li>
<li>LocalServer</li>
<li>LocalServerStatus</li>
<li>StatusChangedEvent</li>
</ul>

