<h1>Local Server Services</h1>

<p>Demonstrates how to start and stop a Local Server and start and stop a Local Map, Feature, and Geoprocessing Service to that server.</p>

<h2>How to use the sample</h2>

<p>Local Server Controls (Top Left):
  <li>- Start Local Server -- Starts a Local Server if one is not already running.
  - Stop Local Server --  Stops a Local Server if one is running. </p></li>

<p>Local Services Controls (Top Right):
  - Combo Box -- Allows for the selection of a Local Map, Feature, or Geoprocessing Service. 
  - Start Service -- Starts the Service that is selected in the combo box.
  - Stop Service --  Stops the Service that is selected in the <code>List of Running Services</code>.</p>

<p>Text Area (Middle):
  - Displays the running status of the Local Server and any services that are added to that server. </p>

<p>List of Running Services (Bottom):
  - Displays any services that are currently running on the server and allows for one of the services to be selected. 
  - Go to URL -- Opens browser to the service that is selected in the <code>List of Running Services</code>. </p>

<p><img src="LocalServerServices.png" alt="" title="" /></p>

<h2>How it works</h2>

<p>To start a <code>LocalServer</code> and start a <code>LocalService</code> to it:</p>

<ol>
<li>Create and run a local server.
<ul><li><code>LocalServer.INSTANCE</code> creates a local server</li>
<li><code>Server.startAsync()</code> starts the server asynchronously</li></ul></li>
<li>Wait for server to be in the  <code>LocalServerStatus.STARTED</code> state.
<ul><li><code>Server.addStatusChangedKistener()</code> fires whenever the running status of the local server has changed.</li></ul></li>
<li>Create and run a local service, example of running a <code>LocalMapService</code>.
<ul><li><code>new LocalMapService(Url)</code>, creates a local map servie with the given url path to mpk file</li>
<li><code>Service.startAsync()</code>, starts the service asynchronously</li>
<li>service will be added to the local server automatically </li></ul></li>
</ol>

<p>To stop a <code>LocalServer</code> and stop any <code>LocalService</code>s that are added to it:</p>

<ol>
<li>Get any services that are currently running on the local server, <code>Server.getServices()</code>.</li>
<li>Loop through all services and stop any that are currently started.
<ul><li>check service is started, <code>Service.getStatus()</code> equals <code>LocalServerStatus.STARTED</code></li>
<li><code>Service.stopAsync()</code>, stops the service asynchronously</li></ul></li>
<li>Wait for all services to be in the <code>LocalServerStatus.STOPPED</code> state.
<ul><li><code>Service.addStatusChangedKistener()</code> fires whenever the running status of the local service has changed.</li></ul></li>
<li>Stop the local server, <code>Server.stopAsync()</code>.</li>
</ol>

<h2>Tags</h2>

<ul>
<li>LocalFeatureService</li>
<li>LocalGeoprocessingService</li>
<li>LocalMapService</li>
<li>LocalServer</li>
<li>StatusChangedEvent</li>
<li>LocalServerStatus</li>
<li>LocalService</li>
</ul>
