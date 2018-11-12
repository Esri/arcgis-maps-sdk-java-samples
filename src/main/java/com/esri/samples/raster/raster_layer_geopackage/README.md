<h1>Raster Layer GeoPackage</h1>

<p>Display raster data from a geopackage.</p>

<p><img src="RasterLayerGeoPackage.png"/></p>

<h2>How it works</h2>

<p>To add a<code>RasterLayer</code> as an operational layer from a <code>GeoPackage</code>:</p>
<ol>
  <li>Create and load a <code>GeoPackage</code>, specifying the path to the local .gpkg file.</li>
  <li>When it is done loading, get the <code>GeoPackageRaster</code>s inside with <code>geoPackage
  .getGeoPackageRasters()</code>.</li>
  <li>Construct a <code>RasterLayer</code> with the <code>GeoPackageRaster</code> in the list you want to use.</li>
  <li>Add the raster layer to the map as an operational layer <code>map.getOperationalLayers().add(rasterLayer)</code>.</li>
</ol>

<h2>Relevant API</h2>

<ul>
  <li>ArcGISMap</li>
  <li>Basemap</li>
  <li>GeoPackage</li>
  <li>GeoPackageRaster</li>
  <li>MapView</li>
  <li>RasterLayer</li>
</ul>
