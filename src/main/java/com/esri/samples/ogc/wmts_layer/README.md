<h1>WMTS Layer</h1>

<p>Shows how to display a WMTS layer from a WMTS service.</p>

<p><img src="WmtsLayer.png"/></p>

<h2>How it works</h2>

<p>To display a <code>WmtsLayer</code> from a <code>WmtsService</code>:</p>

<ol>
  <li>Create a <code>WmtsService</code> using the URL of the WMTS Service.</li>
  </li>After loading the WmtsService, get the list of <code>WmtsLayerInfo</code>s from the service info: 
  <code>service.getServiceInfo().getLayerInfos()</code></li>
  <li>For the layer you want to display, get the layer ID using <code>getLayerInfos().get(0).getId()</code></li>
  <li>Use the ID to create a WMTS Layer: <code>new WmtsLayer(serviceURL, layerID)</code><li>
  <li>Set it as the maps' basemap with <code>map.setBasemap(new Basemap(wmtsLayer))</code></li>
</ol>

<h2>Features</h2>

<ul>
  <li>ArcGISMap</li>
  <li>Basemap</li>
  <li>MapView</li>
  <li>WmtsLayer</li>
  <li>WmtsService</li>
</ul>
