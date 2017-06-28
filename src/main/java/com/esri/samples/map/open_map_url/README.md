<h1>Open an Existing Map</h1>

<p>Demonstrates how to open an existing web map.</p>

<p><img src="OpenMapURL.png"/></p>

<h2>How to use the sample</h2>

<p>A ArcGISMap web map can be selected from the drop-down list. On selection the web map opens up in the MapView.</p>

<h2>How it works</h2>

<p>To open an existing web map:</p>

<ol>
    <li>Create a <code>Portal</code> from the ArcGIS url <code>http://www.arcgis.com/</code>.</li>
    <li>Create a <code>PortalItem</code> using the Portal and the web map id, <code>WebMapEntry.getId()</code>.</li>
    <li>Create a <code>ArcGISMap</code> using the portal item.</li>
    <li>Set map to the <code>MapView</code>.</li>
</ol>

<h2>Features</h2>

<ul>
    <li>ArcGISMap</li>
    <li>MapView</li>
    <li>Portal</li>
    <li>PortalItem</li>
    <li>WebMapEntry</li>
</ul>


