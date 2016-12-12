<h1>Terrain Exaggeration</h1>

<p>Demonstrates how to add vertical exaggeration to a scene's surface.</p>

<p><img src="TerrainExaggeration.PNG"/></p>

<h2>How to use the sample</h2>

<p>Selecting an exaggeration amount from the slider will apply that ammonut to the scene's surface.</p>

<h2>How it works</h2>

<p>To set exaggeration to a <code>Scene</code>'s <code>Surface</code>:</p>

<ol>
  <li>Create an elevated surface and add it to the scene, <code>Surface.getElevationSources().add("Elevation URL")</code></li>
  <li>Add surface to the scene, <code> scene.setBaseSurface(Surface)</code></li>
  <li>Set exaggeration amount of the surface, <code>Surface.setElevationExaggeration(Exaggeration Amount)</code></li>
</ol>

<h2>Features</h2>

<ul>
  <li>ArcGISScene</li>
  <li>ArcGISSurface</li>
  <li>ArcGISTiledElevationSource</li>
</ul>
