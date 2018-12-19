<h1>Feature Layer Collection Query</h1>

<p>Create a feature collection layer which shows the result of a SQL query from a service feature table.</p>

<p><img src="FeatureLayerCollectionQuery.png"/></p>

<h2>How to use the sample</h2>


<p>Simply run the sample. The result of the SQL query from the service feature table (in this case wildfire response point data) feature collection is then displayed on a map with a feature collection layer.</p>

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
