<h1>Service Feature Table (No Cache)</h1>

<p>Demonstrates how to use a feature service in an on-interaction-no-cache mode.</p>

<p>On-interaction-no-cache mode will always fetch Features from the server and doesn't cache any Features on the client's side. This meaning that Features will be fetched whenever the ArcGISMap pans, zooms, selects, or queries.</p>

<p><img src="ServiceFeatureTableNoCache.png"/></p>

<h2>How it works</h2>

<p>How to set <code>FeatureRequestMode.ON_INTERACTION_NO_CAHCE</code> mode:</p>

<ol>
    <li>Create a <code>ServiceFeatureTable</code> from a URL.</li>
    <li>Set request mode of table, <code>ServiceFeatureTable.setFeatureRequestMode(FeatureRequestMode.ON_INTERACTION_NO_CAHCE)</code>.</li>
</ol>

<h2>Relevant API</h2>

<ul>
    <li>ArcGISMap</li>
    <li>FeatureLayer</li>
    <li>MapView</li>
    <li>ServiceFeatureTable</li>
    <li>ServiceFeatureTable.FeatureRequestMode</li>
</ul>

