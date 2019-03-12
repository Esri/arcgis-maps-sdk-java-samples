<h1>Tiled layer</h1>

<p>Display tiles from an ArcGIS tile service.</p>

<h2>Use case</h2>

<p>An `ArcGISTiledLayer` consumes raster tiles provided by an ArcGIS service or a tile package (.tpk & .tpkx). Dividing a raster into tiles allows the map to provide relevant tiles and level of detail to the user when panning and zooming, allowing for rapid map visualization and navigation. The service in this sample pre-generates images based on a tiling scheme.</p>

<p><img src="TiledLayer.png"/></p>

<h2>How it works</h2>

<ol>
    <li>Create an <code>ArcGISTiledLayer</code> object from a URL.</li>
    <li>Pass the tiled layer in to a new <code>Basemap</code>.</li>
    <li>Set the basemap to an <code>ArcGISMap</code>.</li>
</ol>

<h2>Relevant API</h2>

<ul>
    <li>ArcGISMap</li>
    <li>ArcGISTiledLayer</li>
    <li>MapView</li>
</ul>

<h2>Additional information</h2>

<p>An <code>ArcGISTiledLayer</code> can also be added to the ArcGISMap as a layer, but it's best practice to use as a basemap since its purpose is to provide geographical context. Tiled Layer supports both .tpk and .tpkx file formats.</p>

<h2>Tags</h2>

<p>layers, tile, ArcGISTiledLayer</p>