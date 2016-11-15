<h2>Simple Marker Symbol</h2>

<p>Demonstrates how to add a SimpleMarkerSymbol to your ArcGISMap.</p>

<p><img src="SimpleMarkerSymbol.png"/></p>

<h2>How to use the sample</h2>

<p>For simplicity, the sample starts with a predefined SimpleMarkerSymbol set as a red circle.</p>

<h2>How it works</h2>

<p>To display a <code>SimpleMarkerSymbol</code>:</p>

<ol>
    <li>Create a <code>ArcGISMap</code>'s with <code>Basemap</code>.</li>
    <li>Create a <code>GraphicsOverlay</code> and add it to the <code>MapView</code>,<code>MapView.getGraphicsOverlays().add()</code>.</li>
    <li>Add the map to the view, <code>MapView.setMap()</code>.  </li>
    <li>Create a <code>SimpleMarkerSymbol(SimpleMarkerSymbol.Style, color, size)</code>.
        <ul><li>style, how the symbol will be displayed (circle, square, etc.)</li>
            <li>color, color that the symbol will be displayed</li>
            <li>size, size of the symbol</li></ul></li>
    <li>Lately, create a <code>Graphic(Geometry, Symbol)</code> method and add it to the graphics overlay.</li>
</ol>

<h2>Features</h2>

<ul>
    <li>ArcGISMap</li>
    <li>Graphic</li>
    <li>GraphicsOverlay</li>
    <li>MapView</li>
    <li>Point</li>
    <li>SimpleMarkerSymbol</li>
    <li>SimpleMarkerSymbol.Style</li>
</ul>



