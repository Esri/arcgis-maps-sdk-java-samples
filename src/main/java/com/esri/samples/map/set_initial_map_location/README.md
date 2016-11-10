<h1>Set Initial Map Location</h1>

<p>Creates an ArcGISMap with a standard ESRI National Geographic basemap. This basemap is centred on a latitude and longitude coordinate location, which is zoomed to a specific level of detail.</p>

<p><img src="SetInitialMapLocation.png" /></p>

<h2>How it works</h2>

<p>To set an initial location:</p>

<ol>
<li>Create an <code>ArcGISMap</code>, <code>ArcGISMap(Basemap, latitude, longitude, scale)</code>.
<ul><li><code>Basemap</code>, use basemap type to access a basemap for map, <code>Basemap.Type.NATIONAL_GEOGRAPHIC</code></li>
<li>latitude and longitude coordinate location</li>
<li>scale, level of detail displayed on <code>MapView</code></li></ul></li>
<li>Set the map to the map view, <code>MapView.setMap()</code>. </li>
</ol>

<h2>Features</h2>

<ul>
<li>ArcGISMap</li>
<li>Basemap</li>
<li>Basemap.Type</li>
<li>MapView</li>
</ul>