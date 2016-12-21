<h1>Feature Layer (Geodatabase)</h1>

<p>Demonstrates how to consume an Esri Geodatabase by using a FeatureLayer and a GeodatabaseFeatureTable.</p>

<p><img src="FeatureLayerGeodatabase.PNG"/></p>

<h2>How it works</h2>

<p>To create a <code>Geodatabase</code> and display it as a <code>FeatureLayer</code>:</p>

<ol>
    <li>Create a geodatabase using the provided local resource, <code>new Geodatabase(geodatabaseResourceUrl)</code>.</li>
    <li>Wait for geodatabase to load, <code>Geodatabase.addDoneLoadingListener(runnable)</code></li>
    <li>Get the `Trailheads` <code>GeodatabaseFeatureTable</code> from the geodatabase, <code>Geodatabase.getGeodatabaseFeatureTable(tableName)</code></li>
    <li>Create feature layer using the table from above, <code>new FeatureLayer(geodatabaseFeatureTable)</code></li>
    <li>Add feature layer to <code>ArcGISMap</code>, <code>ArcGISMap.getOperationalLayers().add(featureLayer)</code></li>
</ol>

<h2>Features</h2>

<ul>
  <li>Basemap</li>
  <li>FeatureLayer</li>
  <li>Geodatabase</li>
  <li>GeodatabaseFeatureTable</li>
  <li>Map</li>
  <li>MapView</li>
</ul>

##Offline Data
Link | Local Location
---------|-------|
|[Los Angeles Trailheads](https://www.arcgis.com/home/item.html?id=2b0f9e17105847809dfeb04e3cad69e0)| `<userhome>`/arcgis-runtime-samples-java/samples-data/los_angeles/LA_Trails.geodatabase |
