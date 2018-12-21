<h1>Create Terrain from a Local Tile Package</h1>

<p>Set the terrain surface with elevation described by a local tile package.</p>

<p>The terrain surface is what the basemap, operational layers, and graphics are draped on. The tile package must be a LERC (limited error raster compression) encoded TPK. Details on creating these are in the <a href="https://pro.arcgis.com/en/pro-app/help/sharing/overview/tile-package.htm">ArcGIS Pro documentation</a>.</p>

<p><img src="CreateSurfaceTerrainFromLocalTilePackage.png"/></p>

<h2>How it works</h2>
<ol>
    <li>Create an <code>ArcGISScene</code> and add it to a <code>SceneView</code>.</li>
    <li>Create an <code>ArcGISTiledElevationSource</code> with the path to the local tile package.</li>
    <li>Add this source to the scene's base surface: <code>scene.getBaseSurface().getElevationSources().add(tiledElevationSource)</code>.</li>
</ol>

<h2>Relevant API</h2>
<ul>
    <li>ArcGISTiledElevationSource</li>
    <li>Surface</li>
</ul>

<h2 id="tags">Tags</h2>
<p>3D, Tile Cache, Elevation, Surface</p>