<h1>Service Area Task</h1>

<p>Demonstrates how find services areas around a point using the ServiceAreasTask. </p>

<p><img src="ServiceAreasTask.gif"/></p>

<h2>How to use sample</h2>
<p>In order to find any services areas at least one ServiceAreaFaciltiy needs to be added.</p>
<p>To add a facility, click the facility button then click anywhere on the MapView.</p>
<p>To add a barrier, click the barrier button and click multiple location on MapView to draw a barrier.</p>
<ul><p>Hit the barrier button again to finish drawing barrier.</p>
<p>Hitting any other button will also stop the barrier from drawing.</p></ul>
<p>To show service areas around facilities that were added, click show service areas button.</p>
<p>Reset button, clears all graphics and reset service area task</p>

<h2>How it works</h2>

<p>To display a <code>ArcGISMap</code>:</p>

<ol>
    <li>Create an ArcGIS map using a default <code>Basemap</code> such use <code>Basemap.createImagery()</code>.  </li>
    <li>Set the map to the map view, <code>MapView.setMap()</code>. </li>
</ol>

<h2>Features</h2>

<ul>
    <li>ArcGISMap</li>
    <li>GraphicsOverlay</li>
    <li>MapView</li>
    <li>PolylineBarrier</li>
    <li>ServiceAreaFacility</li>
    <li>ServiceAreaParameters</li>
    <li>ServiceAreaPolygon</li>
    <li>ServiceAreaResult</li>
    <li>ServiceAreaTask</li>
    <li>PolylineBuilder</li>
</ul>

