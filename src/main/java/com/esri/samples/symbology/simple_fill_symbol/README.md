<h2>Simple Fill Symbol</h2>

<p>Demonstrates how to change a SimpleFillSymbol color, outline (LineSymbol), and style properties.</p>

<p><img src="SimpleFillSymbol.png"/></p>

<h2>How to use the sample</h2>

<p>Change Fill color:
  - change the color that makes up the area of the polygon</p>

<p>Change Outline Color:
  - adds a border color to the polygon</p>

<p>Change Fill Style:
  - changes the pattern that makes up the area of the polygon</p>

<h2>How it works</h2>

<p>To create a <code>SimpleFillSymbol</code>:</p>

<ol>
  <li>Create the <code>ArcGISMap</code>'s with <code>Basemap</code>.</li>
  <li>Create the <code>GraphicsOverlay</code> and add it to the <code>MapView</code>, <code>MapView.getGraphicsOverlays().add()</code>.</li>
  <li>Add the map to the <code>MapView</code>, <code>MapView.setMap()</code>. </li>
  <li>Create a <code>Polygon</code> using a <code>PointCollection</code> to indicate the boundaries of the <code>Graphic</code>. </li>
  <li>Create a <code>SimpleFillSymbol(SimpleMarkerSymbol.Style, color, outline)</code>.
    <ul><li>style, pattern that makes up the area of the geometry </li>
      <li>color, color the symbol will be displayed</li>
      <li>outline, <code>SimpleLineSymbol</code> that make up the border of the symbol</li></ul></li>
  <li>Lately, create a <code>Graphic(Geometry, Symbol)</code> and add it to the graphics overlay.</li>
</ol>

<h2>Relevant API</h2>

<ul>
  <li>ArcGISMap</li>
  <li>Graphic</li>
  <li>GraphicsOverlay</li>
  <li>MapView</li>
  <li>Polygon</li>
  <li>PointCollection</li>
  <li>SimpleFillSymbol</li>
  <li>SimpleFillSymbol.Style</li>
  <li>SimpleLineSymbol</li>
</ul>


