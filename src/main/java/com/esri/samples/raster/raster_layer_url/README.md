<h1>Raster Layer URL</h1>

<p>Show raster data from an online raster image service.</p>

<p><img src="RasterLayerURL.png"/></p>

<h2>How it works</h2>

<p>To add a <code>RasterLayer</code> as an operational layer from an <code>ImageServiceRaster</code>:</p>
<ol>
  <li>Create an <code>ImageServiceRaster</code> using the service's URL</li>
  <li>Create a <code>RasterLayer</code> from the raster</li>
  <li>Add it as an operational layer with <code>map.getOperationalLayers().add(rasterLayer)</code></li>
</ol>

<h2>Relevant API</h2>

<ul>
  <li>ArcGISMap</li>
  <li>Basemap</li>
  <li>ImageServiceRaster</li>
  <li>MapView</li>
  <li>RasterLayer</li>
</ul>
