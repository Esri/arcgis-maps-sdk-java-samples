<h1>Control the Camera</h1>

<p>Use the available camera types to navigate within the scene view.</p>

<p><img src="ControlTheCamera.png"/></p>

<h2>Use case</h2>

The available scene view camera controllers allow for either free panning or orbiting an object. Free panning is useful for exploring the scene unhindered, however, there may be times when orbiting the camera is useful. For example, orbiting the camera round an object (e.g. a cable car at the top of a mountain range, a point on the scene, or a moving object) would allow not only for 3D viewing of that object from all angles, but also an accurate representation of the scene surrounding that e.g. terrain, aerial photography and any associated data. Orbit camera "pins" the camera to that object's location, ensuring the object of interest is never lost when navigating the map.

<h2>How to use the sample</h2>

<p>Use the sliders to control the camera orbiting the model aeroplane. Choose from heading, pitch, and distance. The sliders will update automatically when navigating the scene view. Exit camera orbit mode by hitting the "Free camera mode" button to navigate freely around the scene view (i.e. camera won't be fixed to the plane). Hit the "Fix camera to plane button" to fix the camera back onto the plane.</p>                                   

<h2>How it works</h2>

<ol>
  <li>Instantiate an <code>ArcGISScene</code> and set the <code>Basemap</code> with <code>ArcGISScene.setBasemap()</code>.</li>
  <li>Instantiate a <code>SceneView</code> and set the scene to it <code>sceneView.setScene(scene).</code></li>
  <li>Instantiate an <code>OrbitGeoElementCameraController</code>, with <code>GeoElement</code> and camera distance parameters. This camera controller will orbit the given <code>GeoElement</code>.</li>
  <li>Use <code>sceneView.setCameraController(OrbitCameraController)</code> to set the camera to the scene view.</li> 
  <li>Set the camera properties with a range of setters, including <code>orbitCameraController.setCameraHeadingOffset()</code>, <code>orbitCameraController.setCameraPitchOffset</code> and
  <code>orbitCameraController.setCameraDistance()</code>.</li>
  <li>Instantiate a <code>GlobeCameraController</code> and set it to the scene view to navigate freely across the <code>Scene</code></li>
</ol>

<h2>Relevant API</h2>

<ul>
  <li>3D</li>
  <li>ArcGISScene</li>
  <li>Camera</li>
  <li>GlobeCameraController</li>
  <li>ModelSceneSymbol</li>
  <li>OrbitGeoElementCameraController</li>
  <li>SceneView</li>
</ul>

<h2>Tags</h2>

OrbitGeoElementCameraController, GlobeCameraController, Camera, SceneView, 3D


