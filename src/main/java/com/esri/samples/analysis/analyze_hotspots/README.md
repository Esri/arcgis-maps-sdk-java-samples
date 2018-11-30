<h1>Analyze Hotspots</h1>

<p>Perform hotspot analysis using a geoprocessing service.</p>

<p>In this case, frequency of 911 calls in an area are analyzed.</p>

<p><img src="AnalyzeHotspots.png"/></p>

<h2>How to use the sample</h2>

<p>Select a start and end date using the datepickers between 1/1/1998 and 5/31/1998 respectively. Click the "Analyze 
hotspots" button to start the geoprocessing job.</p>

<h2>How it works</h2>

<p>To analyze hotspots using a geoprocessing service:</p>

<ol>
    <li>Create a <code>GeoprocessingTask</code> with the URL set to the endpoint of the geoprocessing service.</li>
    <li>Create a query string with the date range as an input of <code>GeoprocessingParameters</code>.</li>
    <li>Use the <code>GeoprocessingTask</code> to create a <code>GeoprocessingJob</code> with the parameters.</li>
    <li>Start the job and wait for it to complete and return a <code>GeoprocessingResult</code>.</li>
    <li>Get the resulting <code>ArcGISMapImageLayer</code> using <code>geoprocessingResult.getMapImageLayer()</code>.</li>
    <li>Add the layer to the map's operational layers.</li>
</ol>

<h2>Relevant API</h2>

<ul>
    <li>ArcGISMapImageLayer</li>
    <li>GeoprocessingJob</li>
    <li>GeoprocessingParameters</li>
    <li>GeoprocessingResult</li>
    <li>GeoprocessingString</li>
    <li>GeoprocessingTask</li>
</ul>
