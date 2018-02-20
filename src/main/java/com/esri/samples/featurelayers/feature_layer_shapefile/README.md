<h1>Feature Layer Shapefile</h1>

<p>Demonstrates how display a shapefile as a feature layer.</p>
  
<p><img src="FeatureLayerShapefile.png"/></p>

<h2>How it works</h2>

<p>To show a shapefile as a feature layer:</p>

<ol>
  <li>Create a <code>ShapefileFeatureTable</code> passing in the URI of a shapefile.</li>
  <li>Create a <code>FeatureLayer</code> using the <code>ShapefileFeatureTable</code>.</li>
  <li>Add the layer to the map with <code>map.getOperationalLayers().add(featureLayer)</code>.</li>
</ol>

<h2>Features</h2>

<ul>
  <li>FeatureLayer</li>
  <li>ShapefileFeatureTable</li>
</ul>

