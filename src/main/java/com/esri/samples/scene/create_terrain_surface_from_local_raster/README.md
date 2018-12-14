<h1>Create Terrain Surface from a Local Raster</h1>

<p>Set the terrain surface with elevation described by a raster file.</p>

<p><img src="CreateTerrainSurfaceFromLocalRaster.png"/></p>

<p>The terrain surface is what the basemap, operational layers, and graphics are draped on. Supported raster formats include:</p>
<ul>
    <li>ASRP/USRP</li>
    <li>CIB1, 5, 10</li>
    <li>DTED0, 1, 2</li>
    <li>GeoTIFF</li>
    <li>HFA</li>
    <li>HRE</li>
    <li>IMG</li>
    <li>JPEG</li>
    <li>JPEG 2000</li>
    <li>NITF</li>
    <li>PNG</li>
    <li>RPF</li>
    <li>SRTM1, 2</li>
</ul>

<h2>How it works</h2>
<ol>
    <li>Create an <code>ArcGISScene</code> and add it to a <code>SceneView</code>.</li>
    <li>Create a <code>RasterElevationSource</code> with a list of raster file paths.</li>
    <li>Add this source to the scene's base surface: <code>scene.getBaseSurface().getElevationSources().add(rasterElevationSource)</code>.</li>
</ol>

<h2>Relevant API</h2>
<ul>
    <li>RasterElevationSource</li>
    <li>Surface</li>
</ul>

<h2 id="tags">Tags</h2>
<p>3D, Raster, Elevation, Surface</p>