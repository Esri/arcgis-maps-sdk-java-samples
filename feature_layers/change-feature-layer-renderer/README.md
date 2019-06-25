<h1>Change Feature Layer Renderer</h1>

<p>Change how a feature layer looks with a renderer.</p>

<p><img src="ChangeFeatureLayerRenderer.gif"/></p>

<h2>How to use the sample</h2>

<p>Use the buttons in the control panel to change the renderer.</p>

<h2>How it works</h2>

<p>To change the <code>FeatureLayer</code>'s <code>Renderer</code>:</p>

<ul>
    <li>Create a <code>ServiceFeatureTable</code> from a URL.</li>
    <li>Create a feature layer from the service feature table.</li>
    <li>Create a new renderer (in this case, a <code>SimpleRenderer</code>).</li>
    <li>Change the feature layer's renderer using <code>FeatureLayer.setRenderer(SimpleRenderer)</code>.</li>
</ul>

<h2>Relevant API</h2>

<ul>
    <li>ArcGISMap</li>
    <li>FeatureLayer</li>
    <li>MapView</li>
    <li>Renderer</li>
    <li>ServiceFeatureTable</li>
    <li>SimpleRenderer</li>
</ul>

