<h1>Read a GeoPackage</h1>

<p>Add rasters and feature tables from a GeoPackage to a map.</p>

<img src="ReadGeoPackage.jpg"/>

<h2>Use case</h2>

<p>The OGC GeoPackage specification defines an open standard for sharing raster and vector data. You may want to use GeoPackage files to support file-based sharing of geographic data.</p>

<h2>How to use the sample</h2>

<p>When the sample loads, the feature tables and rasters from the GeoPackage will be shown on the map.</p>

<h2>How it works</h2>

<ol>
  <li>Open the GeoPackage using <code>GeoPackage.openAsync(path)</code>.</li>
  <li>Iterate through available rasters, exposed by <code>geopackage.geoPackageRasters</code>.
  <ul>
    <li>For each raster, create a raster layer using <code>new Rasterlayer(geopackageRaster)</code>, then add it to the map.</li>
  </ul>
  </li>
  <li>Iterate through available feature tables, exposed by <code>geopackage.geoPackageFeatureTables</code>.
  <ul>
    <li>For each feature table, create a feature layer using <code>new FeatureLayer(geopackageFeatureTable)</code>, then add it to the map.</li>
  </ul>
  </li>
</ol>

<h2>Relevant API</h2>

<ul>
  <li>GeoPackage</li>
  <li>GeoPackageRaster</li>
  <li>GeoPackage.GeoPackageRasters</li>
  <li>GeoPackageFeatureTable</li>
  <li>GeoPackage.GeoPackageFeatureTables</li>
</ul>

<h2>Offline data</h2>

<p>Find this item on <a href="https://arcgisruntime.maps.arcgis.com/home/item.html?id=68ec42517cdd439e81b036210483e8e7">ArcGIS Online</a>.</p>

<h2>About the data</h2>

<p>This sample features a GeoPackage with datasets that cover Aurora, Colorado: Public art (points), Bike trails (lines), Subdivisions (polygons), Airport noise (raster), and liquour license density (raster).</p>

<h2>Additional information</h2>

<p>GeoPackage uses a single SQLite file (.gpkg) that conforms to the OGC GeoPackage Standard. You can create a GeoPackage file (.gpkg) from your own data using the create a SQLite Database tool in ArcGIS Pro.</p>

<h2 id="tags">Tags</h2>

<p>container, layers, maps, OGC, package, rasters, tables</p>