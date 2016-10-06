<h1>Service Feature Table (Manual Cache)</h1>

<p>Demonstrates how to use a feature service in a manual-cache mode.</p>

<h2>How to use the sample</h2>

<p>Click on the Request Cache button to manually request Features. Returned label displays how many features were returned by the service.</p>

<p>Note: Maximum of Features returned is set to 1000.</p>

<p><img src="ServiceFeatureTableManualCache.png" alt="" title="" /></p>

<h2>How it works</h2>

<p>How to set <code>FeatureRequestMode.MANUAL_CACHE</code> mode:</p>

<ol>
    <li>Create a <code>ServiceFeatureTable</code> from a URL.</li>
    <li>Set request mode of table, <code>ServiceFeatureTable.setFeatureRequestMode(FeatureRequestMode.MANUAL_CACHE)</code>.</li>
</ol>

<h2>Features</h2>

<ul>
    <li>ArcGISMap</li>
    <li>FeatureLayer</li>
    <li>MapView</li>
    <li>ServiceFeatureTable</li>
    <li>ServiceFeatureTable.FeatureRequestMode</li>
</ul>
