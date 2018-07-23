<h1>Vector Tiled Layer URL</h1>

<p>Demonstrates how to display an ArcGIS vector tiled layer from an online vector tile service.</p>

<p><img src="VectorTiledLayerURL.png"/></p>

<h2>How it works</h2>

<p>To add an <code>ArcGISTiledLayer</code> as a <code>Basemap</code> to an <code>ArcGISMap</code>:</p>

<ol>
    <li>Create an ArcGISVectorTiledLayer from a URL with <code>new ArcGISVectorTiledLayer(url)</code>.</li>
    <li>Create a basemap passing in the vector tiled layer.</li>
    <li>Set the basemap to the map with <code>map.setBasemap(basemap)</code>.</li>
</ol>

<h2>Relevant API</h2>

<ul>
    <li>ArcGISMap</li>
    <li>ArcGISVectorTiledLayer</li>
    <li>Basemap</li>
    <li>MapView</li>
</ul>
