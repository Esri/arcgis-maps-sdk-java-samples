<h1>Raster Function</h1>

<p>Apply a raster function to a raster.</p>

<p>Raster functions are operations performed on a raster to apply on-the-fly processing. In this sample, a hillshade 
raster function is applied to an online raster image service.</p>

<p><img src="RasterFunction.png"/></p>

<h2>How it works</h2>

<p>To create a <code>RasterLayer</code> using a <code>RasterFunction</code> and add it to the map:</p>
<ol>
  <li>Create an initial raster such as an <code>ImageServiceRaster</code></li>
  <li>Create a <code>RasterFunction</code> from a json string source</li>
  <li>Get the raster function's arguments with <code>rasterFunction.getArguments()</code></li>
  <li>Set the initial raster and raster name in the arguments: <code>arguments.setRaster(arguments.getRasterNames().get(0), imageServiceRaster)</code></li>
  <li>Create a new <code>Raster</code> from the function</li>
  <li>Create a <code>RasterLayer</code> with the new raster</li>
  <li>Add it as an operational layer with <code>map.getOperationalLayers().add(rasterLayer)</code></li>
</ol>

<h2>Relevant API</h2>

<ul>
  <li>ArcGISMap</li>
  <li>Basemap</li>
  <li>ImageServiceRaster</li>
  <li>MapView</li>
  <li>RasterFunction</li>
  <li>RasterFunctionArguments</li>
  <li>RasterLayer</li>
</ul>
