<h1>Generate Geodatabase</h1>

<p>How to generate a geodatabase from a feature service.</p>

<p><img src="GenerateGeodatabase.png"/></p>

<h2>How to use the sample</h2>

<p>Zoom to any extent. Then click the generate button to generate a geodatabase of features from a feature service
filtered to the current extent. A graphic will display showing the extent used. The progress bar in the top right
will show the generate job's progress. Once the geodatabase has been generated, a dialog will display and the layers in
the geodatabase will be added to the map.</p>

<h2>How it works</h2>

<p>To generate a <code>Geodatabase</code> from a feature service:</p>

<ol>
  <li>Create a <code>GeodatabaseSyncTask</code> with the URL of the feature service and load it.</li>
  <li>Create </code>GenerateGeodatabaseParameters</code> specifying things like the extent and whether to include
  attachments.</li>
  <li>Create a <code>GenerateGeodatabaseJob</code> with <code>GenerateGeodatabaseJob job = syncTask
  .generateGeodatabaseAsync(parameters, filePath)</code>. Start the job with <code>job.start()</code>.</li>
  <li>When the job is done, <code>job.getResult()</code> will return the geodatabase. Inside the geodatabase are
  feature tables that can be used to add feature layers to the map.</li>
  </li>Lastly, it is good practice to call <code>syncTask.unregisterGeodatabaseAsync(geodatabase)</code> when
  you're not planning on syncing changes to the service.</li>
</ol>

<h2>Features</h2>

<ul>
  <li>ArcGISMap</li>
  <li>FeatureLayer</li>
  <li>Geodatabase</li>
  <li>GenerateGeodatabaseJob</li>
  <li>GenerateGeodatabaseParameters</li>
  <li>MapView</li>
  <li>ServiceFeatureTable</li>
</ul>
