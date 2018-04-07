<h1>Web Tiled Layer</h1>

<p>Demonstrates how to display map tiles from a non-ArcGIS service as a layer in a map.</p>

<p>WebTiledLayer provides a simple way to integrate non-ArcGIS Services as a layer in a map. In this case, map tiles
from Stamen are added to the map. The template URI is specified by setting the subDomains, level, col, and row
attributes. Additionally, copyright information is added to the layer so that the layer can be properly attributed.</p>

<p><img src="WebTiledLayer.png"/></p>

<h2>How it works</h2>

<p>To create and display a <code>WebTiledLayer</code> with custom attribution:</p>

<ol>
    <li>Create a <code>WebTiledLayer</code> specifying the list of subdomains and a template URI.</li>
    <li>After loading the layer, use <code>webTiledLayer.setAttribution(attributionText)</code> to set custom
    attribution.</li>
</ol>

<h2>Features</h2>

<ul>
    <li>ArcGISMap</li>
    <li>Basemap</li>
    <li>MapView</li>
    <li>WebTiledLayer</li>
</ul>
