<h1>Choose camera controller</h1>

<p>Control the behavior of the camera in a scene.</p>

<p><img src="ChooseCameraController.png"/></p>

<h2>Use case</h2>

<p>The globe camera controller (the default camera controller in all new scenes) allows a user to explore the scene freely by zooming in/out and panning around the globe. The orbit camera controllers fix the camera to look at a target location or geoelement. A primary use case is for following moving objects like cars and planes.</p>

<h2>How to use the sample</h2>

<p> The application loads with the default globe camera controller. To rotate and fix the scene around the plane, exit globe mode by choosing the "Orbit camera around plane" option (i.e. camera will now be fixed to the plane). Choose the "Orbit camera around crater" option to rotate and centre the scene around the location of the Upheaval Dome crater structure, or choose the "Free pan round the globe" option to return to default free navigation.</p>                                   

<h2>How it works</h2>

<ol>
  <li>Create an instance of a class extending <code>CameraController</code>: <code>GlobeCameraController</code>, <code>OrbitLocationCameraController</code>, <code>OrbitGeoElementCameraController</code></li>
  <li>Set the scene view's camera controller with <code>sceneView.setCameraController(cameraController)</code>.</li>
</ol>

<h2>Relevant API</h2>

<ul>
  <li>ArcGISScene</li>
  <li>Camera</li>
  <li>GlobeCameraController</li>
  <li>OrbitGeoElementCameraController</li>
  <li>OrbitLocationCameraController</li>
  <li>SceneView</li>
</ul>

<h2>Tags</h2>

camera controller, Camera, SceneView, 3D


