<h1>Identify KML Features</h1>

<p>Identify clicked features in a KML Layer.</p>

<p><img src="IdentifyKMLFeatures.png"/></p>

<h2>How to use the sample</h2>

<p>When the KML layer is done loaded, click on one of the features. A callout should display next to the feature with HTML describing it.</p>

<h2>How it works</h2>

<p>To identify KML features:</p>

<ol>
  <li>Add a <code>KmlLayer</code> to the map as an operational layer.</li>
  <li>Add a clicked listener to the map view with <code>mapView.setOnMouseClicked</code> to get the clicked screen point.</li>
  <li>Identify features at the screen location with <code>mapView.identifyLayerAsync(kmlLayer, screenPoint, tolerance, false)</code>.</li>
  <li>Use the returned future to get the <code>IdentifyLayerResult</code>.</li>
  <li>Loop through <code>result.getElements()</code>, looking for <code>GeoElements</code> which are instances of <code>KmlPlacemark</code>.</li>
</ol>

<h2>Relevant API</h2>

<ul>
  <li>IdentifyLayerResult</li>
  <li>KmlDataset</li>
  <li>KmlLayer</li>
  <li>KmlPlacemark</li>
</ul>
