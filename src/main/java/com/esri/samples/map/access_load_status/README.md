<h1>Access Load Status</h1>

<p>Demonstrates how to tell what the ArcGISMap's load status is. An ArcGISMap is considered loaded once it has a valid SpatialReference and it has been set to the MapView.</p>

<p><img src="AccessLoadStatus.png"/></p>

<h2>How to use the sample</h2>

<p>The sample provides an information area which displays what the ArcGISMap's load status is. Click on the button to reload the ArcGISMap.</p>

<h2>How it works</h2>

<p>To access the <code>ArcGISMap</code>'s <code>LoadStatus</code>:</p>

<ol>
    <li>Create an ArcGIS map.</li>
    <li>Use <code>ArcGISMap.addLoadStatusChangedListener()</code> and <code>ArcGISMap.getNewLoadStatus()</code> to display the load status of the map.</li>
    <li>Set the ArcGIS map to the <code>MapView</code> to began loading.</li>
</ol>

<h2>Features</h2>

<ul>
    <li>ArcGISMap </li>
    <li>Basemap</li>
    <li>MapView</li>
    <li>LoadStatus</li>
</ul>