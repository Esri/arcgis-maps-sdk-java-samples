<h1>Add a point scene layer</h1>

<p>View a point scene layer from a scene service.</p>

<p><img src="AddAPointSceneLayer.png"/></p>

<h2>Use case</h2>

<p>Point scene layers can efficiently display large amounts of point features. While point cloud layers can only display simple symbols, point scene layers can display any type of billboard symbol or even 3D models, as long as the location of the symbol can be described by a point. Points are cached and automatically thinned when zoomed out to improve performance.</p>

<h2>How to use the sample</h2>

<p>Pan around the scene and zoom in. Notice how many thousands of additional features appear at each successive zoom scale.</p>

<h2>How it works</h2>

<ol>
<li>Create a scene.</li>

<li>Create an <code>ArcGISSceneLayer</code> with the URL to a point scene layer service.</li>

<li>Add the layer to the scene's operational layers collection.</li>
</ol>

<h2>Relevant API</h2>

<ul>
<li>ArcGISSceneLayer</li>
</ul>

<h2>About the data</h2>

<p>This dataset contains more than 40,000 points representing world airports. Points are retrieved on demand by the scene layer as the user navigates the scene.</p>

<h2>Additional information</h2>

<p>Point scene layers can also be retrieved from scene layer packages (.slpk) and mobile scene packages (.mspk).</p>

<h2>Tags</h2>

<p>3D, point scene layer, layers</p>