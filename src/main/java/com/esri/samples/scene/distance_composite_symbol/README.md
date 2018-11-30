<h1>Distance Composite Symbol</h1>

<p>Change a graphic's symbol based on camera proximity.</p>

<p>Distance composite scene symbols can render different symbols depending on the distance between the camera and the
 graphic.</p>

<p><img src="DistanceCompositeSymbol.gif"/></p>

<h2>How to use the sample</h2>

<p>The symbol of graphic will change while zooming in or out.</p>

<h2>How it works</h2>

<p>To create and display a <code>DistanceCompositeSceneSymbol</code>:</p>

<ol>
  <li>Create a <code>GraphicsOverlay</code> and add it to the <code>SceneView</code>.</li>
  <li>Create symbols for each <code>Range</code> the composite symbol.</li>
  <li>Create a distance composite scene symbol`.</li>
  <li>Add a range for each symbol to <code>DistanceCompositeSceneSymbol.getRangeCollection().add(new Range(symbol, min distance, max distance))</code>.
    <ul><li>symbol, symbol to be used within the min/max range that is given</li>
      <li>min/max distance, the minimum and maximum distance that the symbol will be display from the <code>Camera</code></li></ul></li>
  <li>Create a <code>Graphic</code> with the symbol: <code>Graphic(Point, DistanceCompositeSceneSymbol)</code></li>
  <li>Add the graphic to the graphics overlay.</li>
</ol>

<h2>Relevant API</h2>

<ul>
  <li>ArcGISScene</li>
  <li>ArcGISTiledElevationSource</li>
  <li>Camera</li>
  <li>DistanceCompositeSceneSymbol</li>
  <li>DistanceCompositeSceneSymbol.Range</li>
  <li>Graphic</li>
  <li>GraphicsOverlay</li>
  <li>ModelSceneSymbol</li>
  <li>Range</li>
  <li>RangeCollection</li>
  <li>SceneView</li>
  <li>SimpleMarkerSceneSymbol</li>
</ul>


