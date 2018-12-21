<h1>Feature Collection Layer Query</h1>

<p>Create a feature collection layer which shows the result of a SQL query from a service feature table.</p>

<p><img src="FeatureCollectionLayerQuery.png"/></p>

<h2>How it works</h2>

<ol>
    <li>Create a <code>ServiceFeatureTable</code> object from a URL, and store it as a <code>FeatureTable</code> object.</li>
	<li>Create a <code>QueryParameters</code> object and specify the where clause with <code>QueryParameters.setWhereClause()</code>.</li>
	<li>Query the features stored in the feature table with <code>FeatureTable.queryFeaturesAsync(query).</code></li>
	<li>Once complete, instantiate a <code>FeatureCollectionTable</code> with the results of the query from the <code>FeatureQueryResult</code> object.</li>
	<li>Create a <code>FeatureCollection</code> object, and add the feature collection table to it.</li>
	<li>Create a <code>FeatureCollectionLayer</code> object from the feature collection.</li>
	<li>Display the layer by adding it to the map's operational layers with <code>mapView.getMap().getOperationalLayers().add(featureCollectionLayer).</code></li>
</ol>

<h2>Relevant API</h2>

<ul>
	<li>FeatureCollection</li>
	<li>FeatureCollectionLayer</li>
	<li>FeatureCollectionTable</li>
    <li>FeatureLayer</li>
	<li>FeatureTable</li>
    <li>FeatureQueryResult</li>
    <li>QueryParameters</li>
    <li>ServiceFeatureTable</li>
</ul>

<h2>Tags</h2>
<p>FeatureQueryResult, FeatureCollection, FeatureCollectionLayer, FeatureCollectionTable, query</p>
