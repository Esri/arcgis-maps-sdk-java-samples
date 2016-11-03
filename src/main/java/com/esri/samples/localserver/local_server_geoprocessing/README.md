<h1>Local Server Geoprocessing</h1>

<p>Demonstrates how to .</p>

<p><img src="LocalServerGeoprocessing.gif"/></p>

<h2>How to use the sample</h2>

<p>Contour Line Controls (Top Left): </p>
  <ul><li> Start Local Server -- Starts a Local Server if one is not already running.</li>
  <li> Stop Local Server --  Stops a Local Server if one is running. </li></ul>
  
<h2>How it works</h2>

<p>To start a <code></code>:</p>

<ol>
<li>Create and run a local server.
  <ul><li><code>LocalServer.INSTANCE</code> creates a local server</li>
      <li><code>Server.startAsync()</code> starts the server asynchronously</li></ul></li>
<li>Wait for server to be in the  <code>LocalServerStatus.STARTED</code> state.
  <ul><li><code>Server.addStatusChangedListener()</code> fires whenever the running status of the local server has changed.</li></ul></li>
<li>Create and run a local service, example of running a <code>LocalMapService</code>.
  <ul><li><code>new LocalMapService(Url)</code>, creates a local map service with the given url path to mpk file</li>
      <li><code>Service.startAsync()</code>, starts the service asynchronously</li>
      <li>service will be added to the local server automatically </li></ul></li>
</ol>

<h2>Features</h2>

<ul>
<li>LocalFeatureService</li>
</ul>
