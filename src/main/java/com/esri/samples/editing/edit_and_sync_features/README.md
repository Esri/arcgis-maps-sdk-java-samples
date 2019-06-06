<h1>Edit and sync features</h1>

<p>Synchronize offline edits with a feature service.</p>

<p><img src="EditAndSyncFeatures.png"/></p>

<h2>Use case</h2>

<p>By generating a local geodatabase, a user can take an offline copy of a feature service, make changes to it while still offline, and later synchronize their edits to the online feature service. For example, an infrastructure survey worker could use this functionality to save an up-to-date geodatabase to their device, perform their survey work in a remote area without internet connection, and later sync their results to an online geodatabase when regaining internet access.</p>

<h2>How to use the sample</h2>

<ol>
  <li>Pan and zoom to the area you would like to download point features for, ensuring that all desired features are within the red rectangle.</li>
  <li>Click on the Generate Geodatabase button to make an offline database of the area. Once the job completes successfully, the available features within this area will be displayed.</li>
  <li>A feature can be selected by tapping on it. The selected feature can be moved to a new location by tapping anywhere on the map.</li>
  <li>Once a successful edit has been made to a feature, the Sync Geodatabase button is enabled. Press this button to synchronize the edits made to the local geodatabase with the remote feature service.</li>
</ol>

<h2>How it works</h2>

<ol>
  <li>Create a <code>GeodatabaseSyncTask</code> from a URL.</li>
  <li>Use <code>createDefaultGenerateGeodatabaseParametersAsync()</code> on the geodatabase sync task to create <code>GenerateGeodatabaseParameters</code>, passing in an <code>Envelope</code> extent as the parameter.</li>
  <li>Create a <code>GenerateGeodatabaseJob</code> from the <code>GeodatabaseSyncTask</code> using <code>generateGeodatabaseAsync(...)</code> passing in parameters and a path to the local geodatabase.</li>
  <li>Start the <code>GenerateGeodatabaseJob</code> and, on success, load the <code>Geodatabase</code>.</li>
  <li>On successful loading, call <code>getGeodatabaseFeatureTables()</code> on the <code>Geodatabase</code> and add it to the <code>ArcGISMap</code>'s operational layers.</li>
  <li>To sync changes between the local and web geodatabases:
    <ul>
      <li>Create a <code>SyncGeodatabaseParameters</code> object, and set it's sync direction with <code>syncGeodatabaseParameters.SyncDirection()</code>.</li>
      <li>Create a <code>SyncGeodatabaseJob</code> from <code>GeodatabaseSyncTask</code> using <code>.syncGeodatabaseAsync(...)</code> passing the <code>SyncGeodatabaseParameters</code> and <code>Geodatabase</code> as arguments.</li>
      <li>Start the <code>SyncGeodatabaseJob</code> to synchronize the edits.</li>
    </ul>
  </li>
</ol>

<h2>Relevant API</h2>

<ul>
  <li>FeatureLayer</li>
  <li>FeatureTable</li>
  <li>GenerateGeodatabaseJob</li>
  <li>GenerateGeodatabaseParameters</li>
  <li>GeodatabaseSyncTask</li>
  <li>SyncGeodatabaseJob</li>
  <li>SyncGeodatabaseParameters</li>
  <li>SyncLayerOption</li>
</ul>

<h2>About the data</h2>

<p>The basemap for this sample is a San Francisco offline tile package, provided by ESRI to support ArcGIS Runtime SDK Samples. The <i>WildfireSync</i> feature service elements illustrate a collection schema for wildfire information.</p>

<h2>Tags</h2>

<p>feature service, geodatabase, offline, synchronize</p>
