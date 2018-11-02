<h1>Scale Bar</h1>

<p>Adds a scale bar to visually gauge distances on a map.</p>

<p><img src="ScaleBar.png"/></p>

<h2>How to use the sample</h2>

<p>Zoom in or out of the map. The scale bar will automatically display the appropriate scale based on zoom level. Units can be in metric and/or imperial.</p>

<h2>How it works</h2>

<p>To add a scale bar to a map view:</p>

<ol>
<li>Create a <code>ArcGISMap</code> and display it in a <code>MapView</code>.</li>

<li>Create a <code>ScaleBar</code> passing in the map view.</li>

<li>Set the scale bar's skin style with <code>scaleBar.setSkinStyle(skinStyle)</code>.</li>

<li>Set the unit system with <code>scaleBar.setUnitSystem(unitSystem)</code>.</li>

<li>Add the scale bar to the UI overlaying the map view.</li>
</ol>

<h2>Relevant API</h2>

<ul>
<li><code>ArcGISMap</code></li>

<li><code>MapView</code></li>

<li><code>Scalebar</code></li>

<li><code>UnitSystem</code></li>
</ul>

<h2>Tags</h2>

<p>Map, Scale Bar, Toolkit </p>

<h2>Additional Information</h2>

<p>The scale will be accurate for the center of the map, and in general more accurate at larger scales (zoomed in). This means at smaller scales (zoomed out), the reading may be inaccurate at the extremes of the visible extent.</p>