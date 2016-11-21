<h1>Extrude Graphics</h1>

<p>Demonstrates how to render graphics extruded in their Z direction.</p>

<p><img src="ExtrudeGraphics.png"/></p>

<h2>How to use the sample</h2>

<p>Zoom and pan the scene to find the extruded graphics. Note how they are extruded to the level set in their height
  property.</p>

<h2>How it works</h2>

<p>To extrude graphics according to a property:</p>

<ol>
  <li>Create a <code>GraphicsOverlay</code> and <code>SimpleRenderer</code>.</li>
  <li>Get the renderer's <code>SceneProperties</code> using <code>Renderer.getSceneProperties()</code>.</li>
  <li>Set the extrusion mode for the renderer with <code>SceneProperties.setExtrusionMode(ExtrusionMode)</code>.
    <ul><li>BASE_HEIGHT, graphic is extruded to various z-values</li></ul></li>
  <li>Specify the attribute name of the graphic that the extrusion mode will use, <code>SceneProperties
  .setExtrusionExpression("[HEIGHT]")</code>.</li>
  <li>Set the renderer on the graphics overlay, <code>GraphicsOverlay.setRenderer(Renderer)</code>.</li>
  <li>Create graphics with their attribute set, <code>Graphic.getAttributes().put("HEIGHT", Z Value)</code>.</li>
</ol>

<h2>Features</h2>

<ul>
  <li>ArcGISScene</li>
  <li>Graphic</li>
  <li>GraphicsOverlay</li>
  <li>Renderer</li>
  <li>Renderer.SceneProperties</li>
</ul>


