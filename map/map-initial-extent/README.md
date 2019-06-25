<h1>Map Initial Extent</h1>

<p>Display the map at an initial viewpoint.</p>

<p><img src="MapInitialExtent.png"/></p>

<h2>How to use the sample</h2>

<p>As application is loading, initial view point is set and map view zooms to that location.</p>

<h2>How it works</h2>

<p>To set an initial <code>Viewpoint</code>:</p>

<ol>
    <li>Create an <code>ArcGISMap</code>.  </li>
    <li>Create a view point using an <code>Envelope</code>, <code>Viewpoint(Envelope)</code>.</li>
    <li>Set the starting location of the ArcGIS map, <code>ArcGISMap.setInitialViewpoint(Viewpoint)</code>.</li>
    <li>Set the ArcGIS map to the <code>MapView</code>.</li>
</ol>

<h2>Relevant API</h2>

<ul>
    <li>ArcGISMap</li>
    <li>Envelope</li>
    <li>MapView</li>
    <li>Point</li>
    <li>Viewpoint</li>
</ul>


