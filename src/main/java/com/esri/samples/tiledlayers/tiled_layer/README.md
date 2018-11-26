<h1>Tiled Layer</h1>

<p>Display tiles from an ArcGIS tile service.</p>

<p>This service pre-generates images based on a tiling scheme which allows for rapid ArcGISMap visualization and navigation.
    An ArcGISTiledLayer can also be added to the ArcGISMap as a layer but it's best practice to use as a basemap since its purpose is to provide geographical context.</p>

<p><img src="TiledLayer.png"/></p>

<h2>How it works</h2>

<p>To add an <code>ArcGISTiledLayer</code> as a <code>Basemap</code> to an <code>ArcGISMap</code>:</p>

<ol>
    <li>Create an ArcGISTiledLayer from a URL.</li>
    <li>Create a Basemap passing in the tiled layer from above.</li>
    <li>Set basemap to ArcGIS map, <code>ArcGISMap.setBasemap()</code>.</li>
    <li>Set map to mapview, <code>MapView.setMap()</code>.</li>
</ol>

<h2>Relevant API</h2>

<ul>
    <li>ArcGISMap</li>
    <li>ArcGISTiledLayer</li>
    <li>Basemap</li>
    <li>MapView</li>
</ul>