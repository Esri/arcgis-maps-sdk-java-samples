<h1>Dictionary Renderer with Feature Layer</h1>

<p>Demonstrates how to apply a dictionary renderer to a feature layer and display mil2525d graphics.
  The dictionary renderer creates these graphics using a mil2525d style file and the attributes attached to each
  feature within the geodatabase.</p>

<p><img src="FeatureLayerDictionaryRenderer.png" alt="" title="" /></p>

<h2>How it works</h2>

<p>To apply a <code>DictionaryRenderer</code> and display mil2525d graphics:</p>

<ol>
  <li>Create a <code>Geodatabase(geodatabasePath)</code>.
    <ul><li>geodatabasePath, local path to geodatabase</li></ul></li>
  <li>Load the geodatabase asynchronously, <code>Geodatabase.loadAsync()</code>.
    <ul><li>this will allows the application to continue working while the geodatabase loads in all feature tables</li></ul></li>
  <li>Create a <code>SymbolDicitonary</code>, <code>SymbolDictionary(specificationType)</code>.
    <ul><li>specificationType, this will be the mil2525d.stylx file</li>
      <li>load asynchronously, <code>DictionarySymbol.loadAsync()</code></li></ul></li>
  <li>Wait for geodatabase to completely load, <code>Geodatabase.addDoneLoadingListener()</code>.</li>
  <li>Cycle through each <code>GeodatabaseFeatureTable</code> from geodatabase, <code>Geodatabase.getGeodatabaseFeatureTables()</code>.</li>
  <li>Create a <code>FeatureLayer</code> from each table within the geodatabase, <code>FeatureLayer(GeodatabaseFeatureTable)</code>.
    <ul><li>load asynchronouly, <code>FeatureLayer.loadAsync()</code></li></ul></li>
  <li>Wait for each layer to load, <code>FeatureLayer.addDoneLoadingListener</code>.</li>
  <li>Check if layer is last layer to load and create <code>Envelope</code> from each layer.
    <ul><li>set this envelope to be the <code>Viewpoint</code> of the map view, <code>MapView.setViewpoint(new Viewpoint(Envelope))</code></li></ul></li>
  <li>Add feature layer to map, <code>Map.getOperationalLayers().add(FeatureLayer)</code>.</li>
  <li>Create <code>DictionaryRenderer(SymbolDictionary)</code> and attach to feature layer, <code>FeatureLayer.setRenderer(DictionaryRenderer)</code>.</li>
</ol>

<h2>Features</h2>

<ul>
  <li>ArcGISMap</li>
  <li>Basemap</li>
  <li>DictionaryRenderer</li>
  <li>Envelope</li>
  <li>FeatureLayer</li>
  <li>Geodatabase</li>
  <li>GeometryEngine</li>
  <li>MapView</li>
  <li>SymbolDictionary</li>
</ul>
