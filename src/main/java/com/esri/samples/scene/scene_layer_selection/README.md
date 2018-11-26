<h1>Scene Layer Selection</h1>

<p>Select clicked features in a scene.</p>

<p><img src="SceneLayerSelection.png"></p>

<h2>How to use the sample</h2>

<p>Click on a building in the scene layer to select it. Unselect buildings by clicking away from the buildings.</p>

<h2>How it works</h2>

<p>To select geoelements in a scene layer:</p>

<ol>
    <li>Create an <code>ArcGISSceneLayer</code> passing in the URL to a scene layer service.</li>
    <li>Use <code>sceneView.setOnMouseClicked</code> to get the screen click location <code>Point2D</code>.</li>
    <li>Call <code>sceneView.identifyLayersAsync(sceneLayer, point2D, tolerance, false, 1)</code> to identify features 
    in the scene.</li>
    <li>From the resulting <code>IdentifyLayerResult</code>, get the list of identified <code>GeoElement</code>s with
     <code>result.getElements()</code>.</li>
     <li>Get the first element in the list, checking that it is a feature, and call <code>sceneLayer.selectFeature
     (feature)</code> to select it.</li>
</ol>

<h2>Relevant API</h2> 

<ul>
    <li>ArcGISSceneLayer</li>
    <li>GeoElement</li>
    <li>IdentifyLayerResult</li>
</ul>
