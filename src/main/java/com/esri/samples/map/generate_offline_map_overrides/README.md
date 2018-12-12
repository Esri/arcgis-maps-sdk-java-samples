<h1>Generate Offline Map (Overrides)</h1>

<p>Take a web map offline with additional options for each layer.</p>

<p>For applications where you just need to take all layers offline, use the standard workflow (using only 
<code>GenerateOfflineMapParameters</code>). For a simple example of how you take a map offline, see the "Generate Offline Map" 
sample. For more fine-grained control over the data you are taking offline, use overrides to adjust the settings for
 each layer. Some example use cases for the overrides approach could be when you need to:</p>

<ul>
    <li>adjust the extent for one or more layers to be different to the rest of the map.</li>
    <li>reduce the amount of data (such as tiles) downloaded for one or more layers in the map.</li>
    <li>filter features to be taken offline.</li>
    <li>take features with no geometry offline.</li>
</ul>

<p><img src="GenerateOfflineMapOverrides.png"/></p>

<h2>How to use the sample</h2>

<p>Sign in with a free developer account when prompted for credentials (taking web maps offline requires an account).</p>

<p>Use the min/max scale spinners to adjust the level IDs to be taken offline for the Streets basemap.</p>

<p>Use the extent buffer distance spinner to set the buffer radius for the streets basemap. </p>

<p>Check the checkboxes for the feature operational layers you want to include in the offline map.</p>

<p>Use the min hydrant flow rate spinner to only download features with a flow rate higher than this value.</p>

<p>Select the "Water Pipes" checkbox if you want to crop the water pipe features to the extent of the map.</p>

<p>Click the "Generate offline map" button to start the download. A progress bar will display. Click the "Cancel" button
 if you want to stop the download. When the download is complete, the view will display the offline map. Pan around 
 to see that it is cropped to the download area's extent.</p>

<h1>How it works</h1>
<ol>
    <li>Load a web map from a <code>PortalItem</code>. Authenticate with the portal if required.</li>
    <li>Create an <code>OfflineMapTask</code> with the map.</li>
    <li>Generate default task parameters using the extent area you want to download with <code>offlineMapTask.createDefaultGenerateOfflineMapParametersAsync(extent)</code>.</li>
    <li>Generate additional "override" parameters using the default parameters with <code>offlineMapTask.createGenerateOfflineMapParameterOverridesAsync(parameters)</code>.</li>
    <li>For the basemap:
        <ul>
            <li>Get the parameters <code>OfflineMapParametersKey</code> for the basemap layer.</li>
            <li>Get the <code>ExportTileCacheParameters</code> for the basemap layer with <code>overrides.getExportTileCacheParameters().get(basemapParamKey)</code>.</li>
            <li>Set the level IDs you want to download with <code>exportTileCacheParameters.getLevelIDs().add(levelID)</code>.</li>         
            <li>To buffer the extent, use <code>exportTileCacheParameters.setAreaOfInterest(bufferedGeometry)</code> where bufferedGeometry
            can be calculated with the <code>GeometryEngine</code>.</li>
        </ul>
    </li>
    <li>To remove operational layers from the download:
        <ul>
            <li>Create a <code>OfflineParametersKey</code> with the operational layer.</li>
            <li>Get the generate geodatabase layer options using the key with <code>List&lt;GenerateLayerOption&gt; layerOptions = overrides.getGenerateGeodatabaseParameters().get(key).getLayerOptions();</code></li>
            <li>Loop through each <code>GenerateLayerOption</code> in the the list, and remove it if the layer 
            option's ID matches the layer's ID.</li>
        </ul>
    </li>
    <li>To filter the features downloaded in an operational layer:
        <ul>
            <li>Get the layer options for the operational layer using the directions in step 6.</li>
            <li>Loop through the layer options. If the option layerID matches the layer's ID,  set the filter clause with
             <code>layerOption.setWhereClause(sqlQueryString)</code> and set the query option with <code>layerOption.setQueryOption(GenerateLayerOption.QueryOption.USE_FILTER)</code>.
            </li>
        </ul>
    </li>
    <li>To not crop a layer's features to the extent of the offline map (default is true):
        <ul>
            <li>Set <code>layerOption.setUseGeometry(false)</code>.</li>
        </ul>
    </li>
    <li>Create a <code>GenerateOfflineMapJob</code> with <code>offlineMapTask.generateOfflineMap(parameters, downloadPath, overrides)</code>. Start the job with <code>job.start()</code>.
    </li>
    <li>When the job is done, get a reference to the offline map with <code>job.getResult.getOfflineMap()</code>.</li>
</ol>

<h2>Relevant API</h2>
<ul>
    <li>ExportTileCacheParameters</li>
    <li>GenerateGeodatabaseParameters</li>
    <li>GenerateLayerOption</li>
    <li>GenerateOfflineMapJob</li>
    <li>GenerateOfflineMapParameterOverrides</li>
    <li>GenerateOfflineMapParameters</li>
    <li>GenerateOfflineMapResult</li>
    <li>OfflineMapParametersKey</li>
    <li>OfflineMapTask</li>
</ul>

<h2>Tags</h2>
<p>Offline</p>