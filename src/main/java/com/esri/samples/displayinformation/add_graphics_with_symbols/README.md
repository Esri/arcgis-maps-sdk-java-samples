<h1>Add Graphics with Symbols</h1>

<p>Demonstrates how to display a Graphic using a SimpleMarkerSymbol, SimpleLineSymbol, SimpleFillSymbol, and a TextSymbol.</p>

<p><img src="AddGraphicsWithSymbols.png"/></p>

<h2>How it works</h2>

<p>To display a <code>Graphic</code> using a symbol:</p>

<ol>
  <li>Create a <code>GraphicsOverlay</code> and add it to the <code>MapView</code>, <code>MapView.getGraphicsOverlay.add()</code>.</li>
  <li>To create a graphic using a <code>SimpleMarkerSymbol</code>.
    <ul><li>create a <code>Point</code> where the graphic will be located</li>
      <li>create a simple marker symbol that will display at that point</li>
      <li>assign point and symbol to graphic, <code>Graphic(point, symbol)</code></li></ul></li>
  <li>To create a graphic using a <code>SimpleLineSymbol</code>.
    <ul><li>create a <code>PointCollection</code> that will hold all the points that make up the line</li>
      <li>create a <code>Polyline</code> using the point collection, <code>Polyline(PointCollection)</code></li>
      <li>create a simple line symbol that will display over those collected points</li>
      <li>assign polyline and symbol to graphic, <code>Graphic(polyline, symbol)</code></li></ul></li>
  <li>To create a graphic using a <code>SimepleFillSymbol</code>.
    <ul><li>create a point collection that will hold all the points that make up the line</li>
      <li>create a <code>Polygon</code> using the point collection, <code>Polygon(PointCollection)</code></li>
      <li>create a simple line symbol that will display as an outline for points collected</li>
      <li>create a simeple fill symbol, using line symbol from above, that will fill the region in between the points collected with a single color </li>
      <li>assign polygon and symbol to graphic, <code>Graphic(polygon, symbol)</code></li></ul></li>
  <li>To create a graphic using a <code>TextSymbol</code>.
    <ul><li>create a point where the graphic will be located</li>
      <li>create a text symbol, that will display at that point</li>
      <li>assign point and symbol to graphic, <code>Graphic(point, symbol)</code></li></ul></li>
  <li>Add graphic to graphics overlay to display it to the map view.</li>
</ol>

<h2>Relevant API</h2>

<ul>
  <li>Graphic</li>
  <li>GraphicsOverlay</li>
  <li>MapView</li>
  <li>Point</li>
  <li>PointCollection</li>
  <li>Polygon</li>
  <li>Polyline</li>
  <li>SimepleFillSymbol</li>
  <li>SimpleLineSymbol</li>
  <li>SimpleMarkerSymbol</li>
  <li>TextSymbol</li>
</ul>


