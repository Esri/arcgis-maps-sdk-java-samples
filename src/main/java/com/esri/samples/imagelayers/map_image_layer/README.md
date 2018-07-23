<h1>Map Image Layer</h1>

<p>Demonstrates how to display an ArcGISMapImageLayer on a ArcGISMap. Typically this type of content is known as operational data, an example would be business data that changes frequently, such as displaying a fleet of vehicles as they make deliveries.</p>

<p><img src="MapImageLayer.png"/></p>

<h2>How it works</h2>

<p>To add an <code>ArcGISMapImageLayer</code> to your <code>ArcGISMap</code> using its URL:</p>

<ol>
    <li>Create an ArcGIS map image layer from its URL.</li>
    <li>Add it to <code>ArcGISMap.getOperationalLayers().add()</code>.</li>
    <li>Display the ArcGIS map by adding it to the <code>MapView</code>.</li>
</ol>

<h2>Relevant API</h2>

<ul>
    <li>ArcGISMapImageLayer</li>
    <li>ArcGISMap</li>
    <li>MapView</li>
</ul>

