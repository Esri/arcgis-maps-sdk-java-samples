<h1>Viewshed Camera</h1>

<p>Create a viewshed using the current camera viewpoint.</p>

<p>A viewshed shows the visible and obstructed areas from an observer's vantage point.</p>

<h2>How to use the sample</h2>

<p>The sample will start with a viewshed created from the initial camera location, so only the visible (green) 
portion of the viewshed will be visible. Move around the scene to see the obstructed (red) portion. Click the Update 
from Camera button to update the viewshed to the current camera position.</p>

<p><img src="ViewshedCamera.gif"/></p>

<h2>How it works</h2>

<p>To create and update a viewshed from a camera:</p>

<ol>
  <li>Get a <code>Camera</code> either by creating it, or by getting the current camera from the scene with 
  <code>sceneView.getCurrentViewpointCamera()</code>.</li>
  <li>Create a <code>LocationViewshed</code> passing in the <code>Camera</code> plus a min/max distance.</li>
  <li>To update the viewshed with a new camera, use <code>viewshed.updateFromCamera(camera)</code></li>
</ol>

<h2>Relevant API</h2>

<ul>
  <li>3D</li>
  <li>AnalysisOverlay</li>
  <li>ArcGISTiledElevationSource</li>
  <li>ArcGISScene</li>
  <li>ArcGISSceneLayer</li>
  <li>Camera</li>
  <li>LocationViewshed</li>
  <li>SceneView</li>
</ul>
