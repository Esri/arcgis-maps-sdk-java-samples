<h1>Elevation Mode</h1>

<p>Demonstrates how to position graphics using different elevation modes.</p>

<p><img src="ElevationMode.png"/></p>

<h2>How it works</h2>

<p>To position <code>Graphic</code>s using <code>SurfacePlacement</code> (DRAPED, RELATIVE, ABSOLUTE):</p>

<ol>
  <li>Create a <code>GraphicsOverlay</code>.</li>
  <li>Set the surface placement mode <code>GraphicsOverlay.getSceneProperties().setSurfacePlacement(SurfacePlacement)</code>.
    <ul><li>Draped, Z value of graphic has no affect and graphic is attached to surface</li>
      <li>Absolute, position graphic using only it's Z value</li>
      <li>Relative, position graphic using it's Z value plus the elevation of the surface</li></ul></li>
  <li>Add graphics to the graphics overlay, <code>GraphicsOverlay.getGraphics.add(Graphic)</code>.</li>
  <li>Add the graphics overlay to the <code>SceneView</code>, <code>SceneView.getGraphicsOverlays().add(GraphicsOverlay)</code>.</li>
</ol>

<h2>Features</h2>

<ul>
  <li>ArcGISScene</li>
  <li>Camera</li>
  <li>Graphic</li>
  <li>GraphicsOverlay</li>
  <li>LayerSceneProperties.SurfacePlacement</li>
  <li>SceneProperties</li>
  <li>SceneView</li>
  <li>Surface</li>
</ul>


