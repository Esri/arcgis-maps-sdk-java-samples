<h1>Identify Layers</h1>

<p>Get clicked features from multiple layers.</p>

<p><img src="IdentifyLayers.png"/></p>

<h2>How to use the sample</h2>

<p>Tap on map to get features at that location. The number of features identified from each layer will be shown in an alert.</p>

<h2>How it works</h2>

<p>To identify features from layers in a map:</p>

<ol>
<li>Get a <code>Point2D</code> with the screen location where the user clicked.</li>
<li>Call <code>mapView.identifyLayersAsync(screenLocation, tolerance, returnPopupsOnly, maximumResults)</code>.</li>
<li>Get the list of <code>IdentifyLayerResult</code>s from the result. You can get the identified <code>GeoElement</code>s in the layer with <code>identifyLayerResult.getElements()</code>.</li>
<li>To find identified GeoElements in sublayers, use <code>identifyLayerResult.getSublayerResults()</code> and repeat the last step.</li>
</ol>

<h2>Relevant API</h2>

<ul>
<li>ArcGISMapImageLayer</li>
<li>FeatureLayer</li>
<li>FeatureTable</li>
<li>IdentifyLayerResult</li>
<li>MapView</li>
</ul>
