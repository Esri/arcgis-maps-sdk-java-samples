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

<p> The sample loads with the camera orbiting an aeroplane. The camera is preset with a restricted camera heading and pitch, and with a limited minimum and maximum camera distance set from the plane. The position of the plane on the screen is also set just below center.</p>

<p>The control panel contains three main sections. The first is for offsetting the camera from the plane. To animate to a point away from the plane, hit the "Animate camera away from plane" button and to return, hit the "Animate camera to plane cockpit". To jump the camera to the tail of plane, hit the "Jump camera to plane tail" button.</p>

<p> The camera heading can be adjusted with the mouse or keyboard when the sample loads. Uncheck the "Allow keyboard/mouse interaction?" checkbox to allow adjustment of the camera heading only in the app (using the slider). When the checkbox is checked, the user can resume interaction with the camera using the keyboard/mouse.</p>

<p> The heading of the plane can be adjusted using the slider. If the "Link camera heading to plane" is checked then the camera will follow the heading of the plane. If unchecked, the plane will rotate freely in the view whilst the camera remains stationary. </p>                             

<h2>How it works</h2>

<ol>

  <li>Instantiate an <code>OrbitGeoElementCameraController</code>, with <code>GeoElement</code> and camera distance as parameters.</li>
  <li>Use <code>sceneView.setCameraController(OrbitCameraController)</code> to set the camera to the scene view.</li> 
  <li>Set the heading, pitch and distance camera properties with:
  <ul>
  <li><code>orbitCameraController.setCameraHeadingOffset()</code></li> <li><code>orbitCameraController.setCameraPitchOffset()</code></li> 
  <li><code>orbitCameraController.setCameraDistance()</code></li>
  </ul></li>
  <li>Set the minimum and maximum angle of heading and pitch, and minimum and maximum distance for the camera with:
  <ul>
  <li><code>orbitCameraController.setMin</code> or <code>setMaxCameraHeadingOffset()</code>.</li>
  <li><code>orbitCameraController.setMin</code> or <code>setMaxCameraPitchOffset()</code>.</li>
  <li><code>orbitCameraController.setMin</code> or <code>setMaxCameraDistance()</code>.</li>
  </ul></li>
  <li>Set the distance from which the camera is offset from the plane with:
  <ul>
  <li><code>orbitCameraController.setTargetOffsetsAsync()</code></li>
  <li><code>orbitCameraController.setTargetOffsetX()</code></li>
  <li><code>orbitCameraController.setTargetOffsetY()</code></li>
  <li><code>orbitCameraController.setTargetOffsetZ()</code></li>
  </ul></li>
  <li>Set the vertical screen factor to set where the plane appears in the scene:
  <ul>
  <li><code>orbitCameraController.setTargetVerticalScreenFactor()</code></li>
  </ul></li>
  <li>Set if the camera will interact with zooming or panning using mouse or keyboard (default is true):
  <ul>
  <li><code>orbitCameraController.setCameraHeadingOffsetInteractive()</code></li>
  <li><code>orbitCameraController.setCameraPitchOffsetInteractive()</code></li>
  <li><code>orbitCameraController.setCameraDistanceInteractive()</code></li>
  </ul></li>
  <li>Set if the camera will follow the heading, pitch and roll of the plane (default is true):
  <ul>
  <li><code>orbitCameraController.setAutoHeadingEnabled()</code></li>
  <li><code>orbitCameraController.setAutoPitchEnabled()</code></li>
  <li><code>orbitCameraController.setAutoRollEnabled()</code></li>
  </ul></li>


  </ol>

<h2>Relevant API</h2>

<ul>

  <li>ArcGISScene</li>
  <li>Camera</li>
  <li>ModelSceneSymbol</li>
  <li>OrbitGeoElementCameraController</li>
  <li>SceneView</li>
  <li>Simple Renderer</li>
</ul>

<h2>Tags</h2>

OrbitGeoElementCameraController, Camera, SceneView, 3D


