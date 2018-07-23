<h1>Style WMS Layer</h1>

<p>Shows how to change the style of a WMS layer.</p>

<p><img src="StyleWmsLayer.png"/></p>

<h2>How to use the sample</h2>

<p>Once the layer loads, the toggle button will be enabled. Click it to toggle between the first and second styles 
of the WMS layer.</p>

<h2>How it works</h2>

<p>To style a WMS Layer:</p>

<ol>
  <li>Create a <code>WmsLayer</code> specifying the URL of the service and the layer names you want <code>new 
  WmsLayer(url, names)</code>.</li>
  <li>When the layer is done loading, get it's list of style strings using <code>wmsLayer.getSublayers().get(0).getSublayerInfo().getStyles()</code>.</li>
  <li>Set one of the styles using <code>wmsLayer.getSublayers().get(0).setCurrentStyle(styleString)</code>.</li>
</ol>

<h2>Relevant API</h2>

<ul>
  <li>WmsLayer</li>
</ul>
