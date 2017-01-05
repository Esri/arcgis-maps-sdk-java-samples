<h1>Colormap Renderer</h1>

<p>Demonstrates how to use a colormap renderer on raster layer. Colormap renderers can be used to replace values on a raster layer 
with a color based on the original value brightness.</p>

<p><img src="ColormapRenderer.png"/></p>

<h2>How it works</h2>

<p>To apply a <code>ColormapRenderer</code> to a <code>RasterLayer</code>:</p>
<ol>
  <li>Create a <code>Raster</code> from a raster file.</li>
  <li>Create a raster layer from the raster.</li>
  <li>Create a <code>List&lt;Integer&gt;</code> representing colors. Colors at the beginning of the list replace the darkest values in 
  the raster and colors at the end of the list replaced the brightest values of the raster.</li>
  <li>Create a colormap renderer with the color list and apply it to the raster layer with <code>rasterLayer.setRasterRenderer(colormapRenderer)</code>.</li>
</ol>

<h2>Features</h2>

<ul>
  <li>ArcGISMap</li>
  <li>Basemap</li>
  <li>ColormapRenderer</li>
  <li>MapView</li>
  <li>Raster</li>
  <li>RasterLayer</li>
</ul>
