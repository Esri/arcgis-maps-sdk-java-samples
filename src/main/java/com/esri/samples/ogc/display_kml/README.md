<h1>Display KML</h1>

<p>Display a KML layer from a URL, portal item, or local KML file.</p>

<p><img src="DisplayKML.png"/></p>

<h2>How it works</h2>

<p>To display a <code>KMLLayer</code>:</p>

<ol>
  <li>To create a KML layer from a URL, create a <code>KMLDataset</code> using the URL to the KML file. Then pass the dataset to the <code>KmlLayer</code> constructor.</li>
  <li>To create a KML layer from a portal item, construct a <code>PortalItem</code> with a portal and the KML portal item. Pass the portal item to the <code>KmlLayer</code> constructor.</li>
  <li>To create a KML layer from a local file, create a <code>KMLDataset</code> using the absolute file path to the local KML file. Then pass the dataset to the <code>KmlLayer</code> constructor.</li>
  <li>Add the layer as an operational layer to the map with <code>map.getOperationalLayers().add(kmlLayer)</code>.</li>
</ol>

<h2>Relevant API</h2>

<ul>
  <li>KmlDataset</li>
  <li>KmlLayer</li>
  <li>Portal</li>
  <li>PortalItem</li>
</ul>
