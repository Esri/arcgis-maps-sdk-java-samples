<h1>Hillshade Renderer</h1>

<p>Demonstrates how to use a hillshade renderer on a raster layer. Hillshade renderers can adjust a grayscale raster (usually of 
terrain) according to a sun angle.</p>

<p><img src="HillshadeRenderer.png"/></p>

<h2>How to use the sample</h2>

<p>Choose and adjust the settings to update the hillshade renderer on the raster layer.</p>

<h2>How it works</h2>

<p>To apply a <code>HillshadeRenderer</code> to a <code>RasterLayer</code>:</p>
<ol>
  <li>Create a <code>Raster</code> from a grayscale raster file.</li>
  <li>Create a raster layer from the raster.</li>
  <li>Create a <code>Basemap</code> from the raster layer and set it to the map.</li>
  <li>Create a hillshade renderer, specifying the slope type and other properties.</li>
  <li>Set the renderer on the raster layer with <code>rasterLayer.setRenderer(renderer)</code>.</li>
</ol>

<h2>Features</h2>

<ul>
  <li>ArcGISMap</li>
  <li>Basemap</li>
  <li>HillshadeRenderer</li>
  <li>MapView</li>
  <li>Raster</li>
  <li>RasterLayer</li>
</ul>
