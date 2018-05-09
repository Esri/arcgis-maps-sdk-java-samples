<h1>Viewshed Geoprocessing</h1>

<p>Demonstrates how to calculate a viewshed for a given point using a geoprocessing service.</p>

<p><img src="ViewshedGeoprocessing.png"/></p>

<h2>How to use the sample</h2>

<p>After the geoprocessing task finishes loading (the spinner will stop), click anywhere on the map to generate a 
viewshed at that location. A viewshed will be calculated using the service's default distance of 15km.</p>

<h2>How it works</h2>

<p>To create a viewshed from a geoprocessing service:</p>

<ol>
    <li>Create a <code>GeoprocessingTask</code> with the URL set to the viewshed endpoint of a geoprocessing service
    .</li>
    <li>Create a <code>FeatureCollectionTable</code> and add a new <code>Feature</code> whose geometry is the 
    <code>Point</code> 
    where you want to create the viewshed.</li>
    <li>Make <code>GeoprocessingParameters</code> with an input for the viewshed operation <code>parameters.getInputs().put("Input_Observation_Point", new GeoprocessingFeatures(featureCollectionTable))</code>.</li>
    <li>Use the <code>GeoprocessingTask</code> to create a <code>GeoprocessingJob</code> with the parameters.</li>
    <li>Start the job and wait for it to complete and return a <code>GeoprocessingResult</code>.</li>
    <li>Get the resulting <code>GeoprocessingFeatures</code> using <code>geoprocessingResult.getOutputs().get("Viewshed_Result")</code>.</li>
    <li>Iterate through the viewshed features in <code>geoprocessingFeatures.getFeatures()</code> to use their 
    geometry or display the geometry in a graphic.</li>
</ol>

<h2>Features</h2>

<ul>
    <li>FeatureCollectionTable</li>
    <li>GeoprocessingJob</li>
    <li>GeoprocessingParameters</li>
    <li>GeoprocessingResult</li>
    <li>GeoprocessingTask</li>
</ul>
