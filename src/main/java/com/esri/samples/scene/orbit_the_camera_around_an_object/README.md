<h1>Orbit the camera around an object</h1>

<p>Use the available controls to navigate around an object within the scene view.</p>

<p><img src="OrbitTheCameraAroundAnObject.png"/></p>

<h2>Use case</h2>

As a user I want to fixate the camera on a target, such as a model aeroplane, and learn how to customise the camera to show exactly the scene I want by restricting orientation, distance from target and how the camera mimics the objects orientation, so that I can provide my customers with the most optimum experience for the data I want to show.

<h2>How to use the sample</h2>

<p> The sample contains two main control panels: one for the aeroplane and one for the camera.</p> 

<p>For the plane controls (top left control panel):</p>

<ul>
<li>The heading, pitch and roll of the aeroplane can be controlled: adjusting the sliders will update the plane's orientation accordingly.</li>
<li>If the relevant "link camera to plane" checkbox is checked, the camera will follow the graphic as its orientation is adjusted. If unchecked it will not, and the camera will remain fixed whilst the aeroplane heading, pitch or roll changes.</li>
</ul>

<p>For the camera controls (lower control panel):</p>

<ul>
<li>The camera's heading, pitch and distance can be changed via a slider, and the minimum and maximum for each set using a spinner (remember to hit enter when changing a value). By setting these the camera will be restricted to moving within the defined limits.</li>
<li>To pan the camera smoothly away from the plane, use the buttons in the 'Gradually travel relative to plane' section, or to jump immediately to a particular distance away from the plane, use the X Y or Z offset spinners below.</li>
<li>The plane's position relative to the screen can be set in the far right control panel, with 0.5 being the default option (the plane will be centred on the screen). 1 will move the aeroplane to the top of the screen and 0 to the bottom.</li>
<li>Finally there are options which will limit the camera navigation to using the control panel only i.e. mouse or keyboard interactions will not adjust the heading, pitch or distance zoom.</li>
</ul>                               

<h2>How it works</h2>

<ol>

  <li>Instantiate an <code>OrbitGeoElementCameraController</code>, with <code>GeoElement</code> and camera distance parameters. This camera controller will orbit the given <code>GeoElement</code>.</li>
  <li>Use <code>sceneView.setCameraController(OrbitCameraController)</code> to set the camera to the scene view.</li> 
  <li>Set the heading, pitch and distance camera properties with:
  <ul>
<li><code>orbitCameraController.setCameraHeadingOffset()</code></li> <li><code>orbitCameraController.setCameraPitchOffset</code></li> 
 <li><code>orbitCameraController.setCameraDistance()</code></li>
</ul>
<li>Set the minimum and maximum angle of heading and pitch, and minimum and maximum distance for the camera with:</li>
<ul>
<li><code>orbitCameraController.setMin/MaxCameraHeadingOffset(value)</code></li>
<li><code>orbitCameraController.setMin/MaxCameraPitchOffset(value)</code></li>
<li><code>orbitCameraController.setMin/MaxCameraDistance(value)</code></li>
</ul>

</ol>

<h2>Relevant API</h2>

<ul>

  <li>ArcGISScene</li>
  <li>Camera</li>
  <li>ModelSceneSymbol</li>
  <li>OrbitGeoElementCameraController</li>
  <li>SceneView</li>
</ul>

<h2>Tags</h2>

OrbitGeoElementCameraController, Camera, SceneView, 3D


