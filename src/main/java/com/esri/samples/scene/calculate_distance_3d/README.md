<h1>Calculate Distance 3D</h1>

<p>Demonstrates how to calculate the distance, in meters, between two Graphics in 3D space.</p>

<h2>How to use the sample</h2>

<p>Once the SceneView has loaded the Graphic's animation will begin. The distance between the two Graphics will be displayed at the top of the application and will be updated when the Graphic's animation starts. </p>

<p><img src="CalculateDistance3D.PNG" alt="" title="" /></p>

<h2>How it works</h2>

<p>To calculate the distance between two <code>Graphic</code>s in 3D space:</p>

<ol>
<li>Create a <code>GraphicsOverlay</code> and attach it to the <code>SceneView</code>.</li>
<li>Create the two graphics and add to graphics overlay.
<ul><li>supply each graphic with a <code>Point</code>, starting location, and <code>SimpleMarkerSymbol</code></li></ul></li>
<li>Convert each graphic's point to the Cartesian coordinate system</li>
<li>Create a JavaFX Point3D from the Cartesian x,y, and z value.</li>
<li>Then get the distance between each of the JavaFX Point3Ds, <code>Point3D.distance(Point3D)</code>.</li>
</ol>

<h2>Features</h2>

<ul>
<li>ArcGISScene</li>
<li>Graphic</li>
<li>GraphicsOverlay</li>
<li>SceneView</li>
</ul>