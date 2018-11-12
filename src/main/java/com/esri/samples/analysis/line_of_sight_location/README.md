<h1>Line of Sight Location</h1>

<p>Perform line of sight analysis in real-time.</p>

<h2>How to use the sample</h2>

<p>Click to turn on the mouse move event listener. Then move the mouse where you want the target location to be. 
Click again to lock the target location.</p>

<p><img src="LineOfSightLocation.gif"/></p>

<h2>How it works</h2>

<p>To create a line of sight and update it with the mouse:</p>

<ol>
  <li>Create an <code>AnalysisOverlay</code> and add it to the scene view.</li>
  <li>Create a <code>LocationLineOfSight</code> with initial observer and target locations and add it to the analysis
   overlay.</li>
  <li>Make an <code>EventHandler&lt;MouseEvent&gt;</code> to capture mouse movement. Turn the screen point into a scene 
  point with <code>sceneView.screenToLocationAsync(screenPoint)</code>.</li>
  <li>Update the target location with <code>lineOfSight.setTargetLocation(scenePoint)</code>.</li>
</ol>

<h2>Relevant API</h2>

<ul>
  <li>3D</li>
  <li>AnalysisOverlay</li>
  <li>ArcGISTiledElevationSource</li>
  <li>ArcGISScene</li>
  <li>Camera</li>
  <li>LocationLineOfSight</li>
  <li>SceneView</li>
</ul>
