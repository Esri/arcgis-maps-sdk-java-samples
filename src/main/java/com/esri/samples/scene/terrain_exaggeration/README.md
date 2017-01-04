<h1>Terrain Exaggeration</h1>

<p>Demonstrates how to add vertical exaggeration to a scene's surface.</p>

<p><img src="TerrainExaggeration.gif"/></p>

<h2>How to use the sample</h2>

<p>Selecting an exaggeration amount from the slider will apply that to the scene's surface.</p>

<h2>How it works</h2>

<p>To exaggerate a <code>Scene</code>'s <code>Surface</code>:</p>

<ol>
  <li>Create an elevated surface and add it to the scene, <code>surface.getElevationSources().add("elevationURL")</code></li>
  <li>Add surface to the scene, <code> scene.setBaseSurface(Surface)</code></li>
  <li>Set exaggeration amount of the surface, <code>surface.setElevationExaggeration(exaggeration)</code></li>
</ol>

<h2>Features</h2>

<ul>
  <li>ArcGISScene</li>
  <li>Surface</li>
  <li>ArcGISTiledElevationSource</li>
</ul>
