<h1>Hillshade Renderer</h1>

<p>How to use a hillshade renderer on a raster layer. Hillshade renderers can adjust a grayscale raster (usually of 
terrain) according to a sun angle.</p>

<p><img src="HillshadeRenderer.png"/></p>

<h2>How to use the sample</h2>

<p>Choose and adjust the settings to update the hillshade renderer on the raster layer.</p>

<h2>How it works</h2>

<p>To apply a `HillshadeRenderer` to a `RasterLayer`:</p>
<ol>
  <li>Create a `Raster` from a grayscale raster file</li>
  <li>Create a `RasterLayer` from the raster</li>
  <li>Create a `Basemap` from the raster layer and set it to the map</li>
  <li>Create a `HillshadeRenderer`, specifying the slope type and other properties</li>
  <li>Set the renderer on the raster layer with `rasterLayer.setRenderer(renderer)`.</li>
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