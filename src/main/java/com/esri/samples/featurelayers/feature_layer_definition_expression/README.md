<h1>Feature Layer Definition Expression</h1>

<p>Demonstrates how to set the limit of Features to display on the ArcGISMap.</p>

<h2>How to use the sample</h2>

<p>Use the buttons in the control panel to apply or reset definition expression.</p>

<p><img src="FeatureLayerDefinitionExpression.png" alt="" title="" /></p>

<h2>How it works</h2>

<p>To limit the <code>Feature</code>s in your <code>FeatureLayer</code>:</p>

<ol>
    <li>Create a <code>ServiceFeatureTable</code> from a URL.</li>
    <li>Create a feature layer from the service feature table.</li>
    <li>Set the limit of the features on your feature layer using <code>FeatureLayer.setDefinitionExpression(Expression)</code>.</li>
</ol>

<h2>Features</h2>

<ul>
    <li>ArcGISMap</li>
    <li>FeatureLayer</li>
    <li>MapView</li>
    <li>ServiceFeatureTable</li>
</ul>
