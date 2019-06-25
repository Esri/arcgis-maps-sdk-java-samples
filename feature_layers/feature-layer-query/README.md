<h1>Feature Layer Query</h1>

<p>Find features matching a SQL query.</p>

<p><img src="FeatureLayerQuery.png"/></p>

<h2>How to use the sample</h2>

<p>Input the name of a U.S. state into search bar. When you hit search the application performs a query on the feature table and based on the result either highlights the state geometry or provides an error.</p>

<p>Note: The search is case sensitive.</p>

<h2>How it works</h2>

<p>To query a <code>Feature</code> from your <code>FeatureLayer</code>:</p>

<ol>
    <li>Create a <code>ServiceFeatureTable</code> from a URL.</li>
    <li>Create a feature layer from the service feature table.</li>
    <li>Create a <code>QueryParameters</code> object and specified the where clause on it with <code>QueryParameters.setWhereClause()</code> from the text entered by the user. </li>
    <li>Fire the query on the service feature table using <code>ServiceFeatureTable.queryFeaturesAsync(query)</code>.</li>
    <li>Once complete get the feature's from the <code>FeatureQueryResult</code>.</li>
</ol>

<h2>Relevant API</h2>

<ul>
    <li>ArcGISMap</li>
    <li>FeatureLayer</li>
    <li>FeatureQueryResult</li>
    <li>MapView</li>
    <li>QueryParameters</li>
    <li>ServiceFeatureTable</li>
</ul>
