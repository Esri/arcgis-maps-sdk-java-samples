<h1>Symbols</h1>

<p>Create graphics with simple 3D shapes.</p>

<p>Includes tetrahedrons, cubes, spheres, diamonds, cylinders, and cones.</p>

<p><img src="Symbols3D.png"/></p>

<h2>How it works</h2>

<p>To create a <code>SimpleMarkerSceneSymbol</code> with a 3D shape:</p>

<ol>
  <li>Create a <code>GraphicsOverlay</code>.</li>
  <li>Create a <code>SimpleMarkerSceneSymbol(Style, color, width, height, depth, AnchorPosition)</code>.
    <ul><li>You can also use <code>SimpleMarkerSceneSymbol.createCone(color, diameter, height)</code></li>
      <li>color, hex code color of symbol (Red = 0xFFFF0000)</li>
      <li>(width, height, depth), size of the symbol</li>
      <li>AnchorPosition, where to postion symbol on <code>Graphic</code></li></ul></li>
  <li>Create a graphic using the symbol, <code>Graphic(Geometry, Symbol)</code>.</li>
  <li>Add the graphic to the graphics overlay, <code>GraphicsOverlay.getGraphics().add(Graphic)</code>.</li>
  <li>Add the graphics overlay to the <code>SceneView</code>, <code>SceneView.getGraphicsOverlays().add(GraphicsOverlay)</code>.</li>
</ol>

<h2>Relevant API</h2>

<ul>
  <li>ArcGISScene</li>
  <li>Graphic</li>
  <li>GraphicsOverlay</li>
  <li>SimpleMarkerSceneSymbol</li>
  <li>SimpleMarkerSceneSymbol.STYLE</li>
  <li>SceneSymbol.AnchorPosition</li>
</ul>
