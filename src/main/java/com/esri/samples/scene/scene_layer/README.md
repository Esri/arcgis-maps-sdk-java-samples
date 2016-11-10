<h1>Scene Layer</h1>

<p>Demonstrates how to add a scene layer to a scene.
    <img src="SceneLayer.png"/></p>

<h2>How it works</h2>

<p>To add an <code>ArcGISSceneLayer</code> to a scene:  </p>

<ol>
    <li>Create an <code>ArcGISScene</code> and set its <code>Basemap</code> with <code>ArcGISScene.setBasemap()</code>.</li>
    <li>Create a <code>SceneView</code> and set the scene to the view, <code>SceneView.setScene(scene)</code>.</li>
    <li>Create an <code>ArcGISSceneLayer</code>:  <code>sceneLayer = new ArcGISSceneLayer(SCENE_LAYER_SERVICE_URL)</code></li>
    <li>Add the scene layer to the scene: <code>Scene.getOperationalLayers().add(sceneLayer)</code>;</li>
</ol>

<h2>Features</h2>

<ul>
    <li>3D</li>
    <li>ArcGISScene</li>
    <li>ArcGISSceneLayer</li>
    <li>ArcGISTiledElevationSource</li>
    <li>Camera</li>
    <li>SceneView</li>
    <li>Surface</li>
</ul>
