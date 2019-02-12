<h1>Orbit the camera around an object</h1>

<p>Fix the camera to point at and rotate around a target object.</p>

<p><img src="OrbitTheCameraAroundAnObject.png"/></p>

<h2>Use case</h2>

<p>The orbit geoelement camera controller provides control over the following camera behaviors:</p>

<ul>
<li>automatically track the target</li>


<li>stay near the target by setting a minimum and maximum distance offset</li>

<li>restrict where you can rotate around the target</li>

<li>automatically rotate the camera when the target's heading and pitch changes</li>

<li>disable user interactions for rotating the camera</li>

<li>animate camera movement over a specified duration</li>

<li>control the vertical positioning of the target on the screen</li>

<li>set a target offset (e.g.to orbit around the tail of the plane) instead of defaulting to orbiting the center of the object</li>
</ul>

<h2>How to use the sample</h2>

<p> The sample loads with the camera orbiting an aeroplane model. The camera is preset with a restricted camera heading and pitch, and a limited minimum and maximum camera distance set from the plane. The position of the plane on the screen is also set just below center.</p>


<p> Use the "Camera Heading" slider to adjust the camera heading. Select the "Allow camera distance interaction" checkbox to allow zooming in and out with the mouse/keyboard: when the checkbox is deselected the user will be unable to adjust with the camera distance.</p> 

<p> Use the "Plane Pitch" slider to adjust the plane's pitch. When not in Cockpit view, the plane's pitch will change independently to that of the camera pitch.</p>

<p>Use the "Cockpit view" button to offset and fix the camera into the cockpit of the aeroplane. Use the "Plane pitch" slider to control the pitch of aeroplane: the camera will follow the pitch of the plane in this mode.    In this view adjusting the camera distance is disabled. Hit the "Center view" button to exit cockpit view mode and fix the camera controller on the center of the plane.</p>                           

<h2>How it works</h2>

<ol>
  <li>Instantiate an <code>OrbitGeoElementCameraController</code>, with <code>GeoElement</code> and camera distance as parameters.</li>
  <li>Use <code>sceneView.setCameraController(OrbitCameraController)</code> to set the camera to the scene view.</li> 
  <li>Set the heading, pitch and distance camera properties with:
  <ul>
  <li><code>orbitCameraController.setCameraHeadingOffset(double)</code></li> <li><code>orbitCameraController.setCameraPitchOffset(double)</code></li> 
  <li><code>orbitCameraController.setCameraDistance(double)</code></li>
  </ul></li>
  <li>Set the minimum and maximum angle of heading and pitch, and minimum and maximum distance for the camera with:
  <ul>
  <li><code>orbitCameraController.setMin</code> or <code>setMaxCameraHeadingOffset(double)</code>.</li>
  <li><code>orbitCameraController.setMin</code> or <code>setMaxCameraPitchOffset(double)</code>.</li>
  <li><code>orbitCameraController.setMin</code> or <code>setMaxCameraDistance(double)</code>.</li>
  </ul></li>
  <li>Set the distance from which the camera is offset from the plane with:
  <ul>
  <li><code>orbitCameraController.setTargetOffsetsAsync(x, y, z, duration)</code></li>
  <li><code>orbitCameraController.setTargetOffsetX(double)</code></li>
  <li><code>orbitCameraController.setTargetOffsetY(double)</code></li>
  <li><code>orbitCameraController.setTargetOffsetZ(double)</code></li>
  </ul></li>
  <li>Set the vertical screen factor to determine where the plane appears in the scene:
  <ul>
  <li><code>orbitCameraController.setTargetVerticalScreenFactor(float)</code></li>
  </ul></li>

  <li>Animate the camera to the cockpit using <code>orbitCameraController.setTargetOffsetsAsync(x, y, z, duration)</code>
</li>

  <li>Set if the camera distance will adjust when zooming or panning using mouse or keyboard (default is true):
  <ul>
  <li><code>orbitCameraController.setCameraDistanceInteractive(boolean)</code></li>
  </ul></li>
  <li>Set if the camera will follow the pitch of the plane (default is true):
  <ul>
  <li><code>orbitCameraController.setAutoPitchEnabled(boolean)</code></li>
  </ul></li>
  </ol>

<h2>Relevant API</h2>

<ul>
  <li>OrbitGeoElementCameraController</li>
</ul>

<h2>Tags</h2>

OrbitGeoElementCameraController, Camera, SceneView, 3D


