<h1>Tile Cache</h1>

<p>Create a basemap from a local tile cache.</p>

<p><img src="TileCache.png"/></p>

<h2>How it works</h2>

<p>To create a <code>Basemap</code> from a local tile package:</p>
<ol>
  <li>Create a <code>TileCache</code>, specifying the path to the local tile package</li>
  <li>Create a <code>ArcGISTiledLayerLayer</code> with the tile cache</li>
  <li>Create a <code>Basemap</code> with the tiled layer</li>
  <li>Create a <code>ArcGISMap</li> with the basemap and set it to a <code>MapView</code></li>
</ol>

<h2>Relevant API</h2>

<ul>
  <li>ArcGISMap</li>
  <li>ArcGISTiledLayer</li>
  <li>Basemap</li>
  <li>MapView</li>
  <li>TileCache</li>
</ul>
