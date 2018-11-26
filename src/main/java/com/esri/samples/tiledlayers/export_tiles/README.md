<h1>Export Tiles</h1>

<p>Export tiles from an online tile service.</p>

<p><img src="ExportTiles.png"/></p>

<h2>How it works</h2>

<p>To export tiles from an <code>ArcGISTiledLayer</code>:</p>
<ol>
  <li>Create an <code>ExportTileCacheTask</code>, passing in the URI of the tiled layer</li>
  <li>Create default <code>ExportTileCacheParameters</code> with <code>task.createDefaultExportTileCacheParametersAsync
  (extent, minScale, maxScale)</code></li>
  <li>Call <code>task.exportTileCacheAsync(defaultParams, downloadFile)</code> to create the 
  <code>ExportTileCacheJob</code></li>
  <li>Call <code>job.start()</code> to start the job</li>
  <li>When the job is done, use </code>job.getResult()</code> to get the resulting <code>TileCache</code></li>
</ol>

<h2>Relevant API</h2>

<ul>
  <li>ArcGISMap</li>
  <li>ArcGISTiledLayer</li>
  <li>Basemap</li>
  <li>ExportTileCacheJob</li>
  <li>ExportTileCacheParamters</li>
  <li>ExportTileCacheTask</li>
  <li>MapView</li>
  <li>TileCache</li>
</ul>
