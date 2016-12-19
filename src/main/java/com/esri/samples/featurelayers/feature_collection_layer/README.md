<h1>Display Scene</h1>

<p>Demonstrates how to display a scene with an elevation source. An elevation source allows objects to be viewed in 3D, like this picture of Mt. Everest.</p>

<p><img src="DisplayScene.png"/></p>

<h2>How it works</h2>

<p>To create an <code>ArcGISScene</code> with elevation data:</p>

<ol>
    <li>Create an ArcGIS scene and set the <code>Basemap</code> with <code>ArcGISScene.setBasemap()</code>.</li>
    <li>Create a <code>SceneView</code> and set the scene to the view, <code>SceneView.setScene(scene)</code>.</li>
    <li>Create a <code>Surface</code> and add a <code>ArcGISTiledElevationSource</code>, <code>Surface.getElevationSources().add()</code>.</li>
    <li>Set the surface as the scene's base surface: <code>ArcGIScene.setBaseSurface(surface)</code>.</li>
</ol>

<h2>Features</h2>

<ul>
    <li>ArcGISScene</li>
    <li>ArcGISTiledElevationSource</li>
    <li>Camera</li>
    <li>SceneView</li>
    <li>Surface</li>
</ul>