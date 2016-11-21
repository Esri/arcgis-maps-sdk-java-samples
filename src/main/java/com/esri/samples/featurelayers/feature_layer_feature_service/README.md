<h1>Feature Layer Feature Service</h1>

<p>Demonstrates how to create a FeatureLayer from a ServiceFeatureTable and add it to an ArcGISMap.</p>

<p><img src="FeatureLayerFeatureService.png"/></p>

<h2>How it works</h2>

<p>To add <code>Feature</code>s from your <code>FeatureLayer</code> to an <code>ArcGISMap</code>.</p>

<ol>
    <li>Create a <code>ServiceFeatureTable</code> from a URL.</li>
    <li>Create a feature layer from the service feature table.</li>
    <li>Set the feature layer to your ArcGISMap using <code>ArcGISMap.getOperationalLayers().add(FeatureLayer)</code>.</li>
</ol>

<h2>Features</h2>

<ul>
    <li>ArcGISMap</li>
    <li>FeatureLayer</li>
    <li>MapView</li>
    <li>ServiceFeatureTable</li>
</ul>
