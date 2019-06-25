<h1>Time Based Query</h1>

<p>Filter features within a time range.</p>

<p><img src="TimeBasedQuery.png"/></p>

<h2>How it works</h2>

<p>To query features by a time extent:</p>

<ol>
    <li>Create a <code>ServiceFeatureTable</code> from the URL of a feature service.</li>
    <li>Set the feature table's feature request mode to manual with <code>featureTable.setFeatureRequestMode(MANUAL_CACHE)</code>.</li>
    <li>After loading the service feature table, create <code>QueryParameters</code>.</li>
    <li>Create two <code>Calendar</code> objects with the beginning and ending timestamps and create a 
    <code>TimeExtent</code> with them.</li>
    <li>Set the time extent with <code>queryParameters.setTimeExtent(timeExtent)</code></li>
    <li>Populate the table with features in the time extent with <code>featureTable.popuateFromServiceAsync(queryParameters, true, outputFields)</code>.
        <ul>
            <li>The second argument is whether to clear the cache of features or not.</li>
            <li>The output fields is a list of fields of the features to return. Use a list of one string 
            <code>"*"</code> to get all of the fields.</li>
        </ul>
    </li>
    <li>Finally, create a feature layer from the feature table with <code>new FeatureLayer(featureTable)</code>, 
    and add it to the map with <code>map.getOperationalLayers().add(featureLayer)</code> to see the features in the 
    time extent.</li>
</ol>

<h2>Relevant API</h2>

<ul>
    <li>ArcGISMap</li>
    <li>Basemap</li>
    <li>FeatureLayer</li>
    <li>MapView</li>
    <li>QueryParameters</li>
    <li>ServiceFeatureTable</li>
    <li>TimeExtent</li>
</ul>