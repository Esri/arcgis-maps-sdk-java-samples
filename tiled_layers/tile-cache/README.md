<h1>Tile cache</h1>

<p>Create a basemap from a local tile cache.</p>

<p><img src="TileCache.png"/></p>

<h2>Use case</h2>

<p>An <code>ArcGISTiledLayer</code> consumes raster tiles provided by an ArcGIS service or a tile package (.tpk & .tpkx). Dividing a raster into tiles allows the map to provide relevant tiles and level of detail to the user when panning and zooming.</p>

<h2>How it works</h2>

<ol>
  <li>Create a <code>TileCache</code>, specifying the path to the local tile package.</li>
  <li>Create a <code>ArcGISTiledLayer</code> with the tile cache.</li>
  <li>Create a <code>Basemap</code> with the tiled layer.</li>
  <li>Create a <code>ArcGISMap</code> with the basemap and set it to a <code>MapView</code>.</li>
</ol>

<h2>Relevant API</h2>

<ul>
  <li>ArcGISMap</li>
  <li>ArcGISTiledLayer</li>
  <li>TileCache</li>
</ul>

<h2>Additional information</h2>

<p><code>ArcGISTiledLayer</code> supports both .tpk and .tpkx file formats.</p>

<h2>Tags</h2>

<p>layers, tile, TileCache, ArcGISTiledLayer</p>
