<h1>Service Feature Table (Cache)</h1>

<p>Cache features on the client when the user interacts.</p>

<p>On-interaction-cache mode will fetch features within the current extent when needed (performing a pan or zoom) from 
the server and caches those features within a table on the client's side. Any queries performed on features within 
that table will be performed locally, otherwise they will be requested from the server.</p>

<p><img src="ServiceFeatureTableCache.png"/></p>

<h2>How it works</h2>

<p>How to set <code>FeatureRequestMode.ON_INTERACTION_CACHE</code> mode:</p>

<ol>
    <li>Create a <code>ServiceFeatureTable</code> from a URL.</li>
    <li>Set request mode of table, <code>ServiceFeatureTable.setFeatureRequestMode(FeatureRequestMode.ON_INTERACTION_CACHE)</code>.</li>
</ol>

<h2>Relevant API</h2>

<ul>
    <li>ArcGISMap</li>
    <li>FeatureLayer</li>
    <li>MapView</li>
    <li>ServiceFeatureTable</li>
    <li>ServiceFeatureTable.FeatureRequestMode</li>
</ul>
