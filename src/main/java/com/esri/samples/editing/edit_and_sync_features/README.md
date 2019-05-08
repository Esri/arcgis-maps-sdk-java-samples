<h1>Edit and sync features</h1>

<p>Synchronize offline edits with a feature service.</p>

<p><img src="EditAndSyncFeatures.png/></p>

<h2>Use Case</h2>

<p>By generating a local geodatabase, a user can take an offline copy of a feature service, make changes to it while still offline, and later synchronize their edits to the online feature service. This is useful in cases where a network connection is not available (e.g. working in remote areas), but users still need to be able to make changes to features.</p>

<h2>How to use the sample</h2>

<ol>
  <li>Pan and zoom into the desired area, making sure the area you want to take offline is within the current extent of the view. </li>
  <li>Click on the Generate Geodatabase button to make an offline database of the area. Once the job completes successfully, the available features within this area will be displayed.</li>
  <li>A feature can be selected by tapping on it. The selected feature can be moved to a new location by tapping anywhere on the map. </li>
  <li>Once a successful edit has been made to a feature, the Sync Geodatabase button is enabled. Press this button to synchronize the edits made to the local geodatabase with the remote feature service.</li>
</ol>

<h2>How it works</h2>

<ol>
  <li>Create a <code>GeodatabaseSyncTask</code> from a URL.</li>
  <li>Use <code>createDefaultGenerateGeodatabaseParametersAsync(...)</code> to create <code>GenerateGeodatabaseParameters</code> from the <code>GeodatabaseSyncTask</code>, passing in an <code>Envelope</code> argument.</li>
  <li>Create a <code>GenerateGeodatabaseJob</code> from the <code>GeodatabaseSyncTask</code> using <code>generateGeodatabaseAsync(...)</code> passing in parameters and a path to the local geodatabase.</li>
  <li>Start the <code>GenerateGeodatabaseJob</code> and, on success, load the <code>Geodatabase</code>.</li>
  <li>On successful loading, call <code>getGeodatabaseFeatureTables()</code> on the <code>Geodatabase</code> and add it to the <code>ArcGISMap</code>'s operational layers.</li>
  <li>To sync changes between the local and web geodatabases:</li>
  <li>Define <code>SyncGeodatabaseParameters</code> including setting the SyncGeodatabaseParameters.SyncDirection`.</li>
  <li>Create a <code>SyncGeodatabaseJob</code> from <code>GeodatabaseSyncTask</code> using <code>.syncGeodatabaseAsync(...)</code> passing the <code>SyncGeodatabaseParameters</code> and <code>Geodatabase</code> as arguments.</li>
  <li>Start the <code>SyncGeodatabaseJob</code>.</li>
</ol>

<h2>Relevant API</h2>

<ul>
  <li>FeatureLayer</li>
  <li>FeatureTable</li>
  <li>GeodatabaseSyncTask</li>
  <li>GenerateGeodatabaseJob</li>
  <li>GenerateGeodatabaseParameters</li>
  <li>SyncGeodatabaseJob</li>
  <li>SyncGeodatabaseParameters</li>
  <li>SyncLayerOption</li>
</ul>

<h2>About the data</h2>

<p>The basemap for this sample is  a San Francisco offline tile package, provided by ESRI to support ArcGIS Runtime SDK Samples.
The <i>WildfireSync</i> feature service elements illustrate a collection schema for wildlfire information.</p>

<h2>Tags</h2>

<p>synchronize, GeodatabaseSyncTask, SyncGeodatabaseParameters, Geodatabase, GeodatabaseFeatureTable, GenerateGeodatabaseParameters, GenerateGeodatabaseJob</p>
