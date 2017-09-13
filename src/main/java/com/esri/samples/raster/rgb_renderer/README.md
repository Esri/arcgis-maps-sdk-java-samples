<h1>Rgb Renderer</h1>

<p>How to use a RGB renderer on a raster layer. An RGB renderer is used to adjust the color bands of a multispectral 
image.</p>

<p><img src="RgbRenderer.png"/></p>

<h2>How to use the sample</h2>

<p>Choose one of the stretch parameter types. The other options will adjust based on the chosen type. Add your 
inputs and press the Update button to update the renderer.</p>

<h2>How it works</h2>

<p>To apply a <code>RgbRenderer</code> to a <code>RasterLayer</code>:</p>
<ol>
  <li>Create a <code>Raster</code> from a multispectral raster file</li>
  <li>Create a <code>RasterLayer</code> from the raster</li>
  <li>Create a <code>Basemap</code> from the raster layer and set it to the map</li>
  <li>Create a <code>RgbRenderer</code>, specifying the stretch parameters and other properties</li>
  <li>Set the renderer on the raster layer with <code>rasterLayer.setRenderer(renderer)</code></li>
</ol>

<h2>Features</h2>

<ul>
  <li>ArcGISMap</li>
  <li>Basemap</li>
  <li>MapView</li>
  <li>Raster</li>
  <li>RasterLayer</li>
  <li>RgbRenderer</li>
  <li>StretchParameters</li>
</ul>
