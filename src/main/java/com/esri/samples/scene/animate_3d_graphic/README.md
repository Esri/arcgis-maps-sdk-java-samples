<h1>Animate 3d Graphic</h1>

<p>Demonstrates how to animate a graphic's position and rotation and follow using an OrbitGeoElementCameraController. 
Also shows how to combine a SceneView and MapView in an MVC application with property binding.</p>

<p><img src="Animate3dGraphic.png"/></p>

<h2>How to use the sample</h2>

<p>Animation Controls (Top Left Corner):
  - Select a mission -- selects a location with a route for plane to fly
  - Mission progress -- shows how far along the route the plane is. Slide to change keyframe in animation
  - Play -- toggles playing and stopping the animation
  - Follow -- toggles camera following plane</p>

<p>Speed Slider (Top Right Corner): controls speed of animation</p>

<p>2D Map Controls (Bottom Left Corner):
  - Plus and Minus -- controls distance of 2D view from ground level</p>
  
<p>Moving the Camera: Simply use regular zoom and pan interactions with the mouse. When in follow mode, the 
<code>OrbitGeoElementCameraController</code> being used will keep the camera locked to the plane.</p>

<h2>How it works</h2>

<p>To animate a <code>Graphic</code> by updating it's <code>Geometry</code>, heading, pitch, and roll:</p>

<ol>
  <li>Create a <code>GraphicsOverlay</code> and attach it to the <code>SceneView</code>.</li>
  <li>Create a <code>ModelSceneSymbol</code> with <code>AnchorPosition.CENTER</code>.</li>
  <li>Create a <code>Graphic(Geometry, Symbol)</code>.
    <ul><li>set geometry to a point where graphic will be located in scene view</li>
      <li>set symbol to the one we made above</li></ul></li>
  <li>Add Attributes to graphic.
    <ul><li>Get attributes from graphic, <code>Graphic.getAttributes()</code>.</li>
      <li>Add heading, pitch, and roll attribute, <code>attributes.put("[HEADING]", heading)</code>;</li></ul></li>
  <li>Create a <code>SimpleRenderer</code> to access and set it's expression properties.
    <ul><li>access properties with <code>Renderer.getSceneProperties()</code></li>
      <li>set heading, pitch, and roll expressions, <code>SceneProperties.setHeadingExpression("[HEADING]")</code>.</li></ul></li>
  <li>Add graphic to the graphics overlay.</li>
  <li>Set renderer to graphics overlay, <code>GraphicsOverlay.setRenderer(Renderer)</code></li>
  <li>Update graphic's location, <code>Graphic.setGeometry(Point)</code>.</li>
  <li>Update symbol's heading, pitch, and roll, <code>attributes.replace("[HEADING]", heading)</code>.</li>
</ol>

<h2>Features</h2>

<ul>
  <li>3D</li>
  <li>ArcGISMap</li>
  <li>ArcGISScene</li>
  <li>Camera</li>
  <li>Graphic</li>
  <li>GraphicsOverlay</li>
  <li>LayerSceneProperties.SurfacePlacement</li>
  <li>MapView</li>
  <li>ModelSceneSymbol</li>
  <li>OrbitGeoElementCameraController</p>
  <li>Point</li>
  <li>Polyline</li>
  <li>Renderer</li>
  <li>Renderer.SceneProperties</li>
  <li>SceneView</li>
  <li>Viewpoint</li>
</ul>


