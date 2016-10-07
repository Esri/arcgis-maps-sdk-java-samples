<h1>Feature Layer Selection</h1>

<p>Demonstrates how to select Features from a FeatureLayer.</p>

<h2>How to use the sample</h2>

<p>Click on a feature from the map to select it.</p>

<p><img src="FeatureLayerSelection.png" alt="" title="" /></p>

<h2>How it works</h2>

<p>To select <code>Feature</code>s from your <code>FeatureLayer</code>:</p>

<ol>
  <li>Create a <code>ServiceFeatureTable</code> from a URL.</li>
  <li>Create a feature layer from the service feature table.</li>
  <li>Identify <code>MapView</code> on the location the user has clicked.
    <ul><li>call <code>MapView.identifyLayerAsync(FeatureLayer, Point2D, tolerance, max results)</code></li>
      <li>feature layer, from above</li>
      <li>Point2D, location where the user clicked</li>
      <li>tolerance, area around Point2D to locate features</li>
      <li>max results, maximum number of features to return</li></ul></li>
  <li>Select all features that were identified, <code>FeatureLayer.selectFeatures()</code>.</li>
</ol>

<h2>Features</h2>

<ul>
  <li>ArcGISMap</li>
  <li>Feature</li>
  <li>FeatureLayer</li>
  <li>MapView</li>
  <li>ServiceFeatureTable</li>
</ul>
