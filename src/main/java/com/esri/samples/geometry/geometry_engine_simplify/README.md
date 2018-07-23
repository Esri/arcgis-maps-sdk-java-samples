<h1>Geometry Engine Simplify</h1>

<p>Demonstrates how to use the GeometryEngine to perform the simplify geometry operation on a Polygon.</p>

<p><img src="GeometryEngineSimplify.png"/></p>

<h2>How to use the sample</h2>

<p>Click on the simplify button to apply the simplify geometry operation between the intersecting polygons. Click reset to restart the sample.</p>

<h2>How it works</h2>

<p>To perform the simplify geometry operation on a <code>Polygon</code>:</p>

<ol>
    <li>Create a <code>GraphicsOverlay</code> and add it to the <code>MapView</code>.</li>
    <li>Define the <code>PointCollection</code> of the <code>Geometry</code>.</li>
    <li>Add the polygons to the GraphicsOverlay.</li>
    <li>Determine the simplified geometry by using the <code>GeometryEngine.simplify(polygon.getGeometry()</code>.</li>
</ol>

<h2>Relevant API</h2>

<ul>
    <li>Geometry</li>
    <li>Graphic</li>
    <li>GraphicsOverlay</li>
    <li>MapView</li>
    <li>Point</li>
    <li>PointCollection</li>
    <li>SimpleLineSymbol</li>
    <li>SimpleFillSymbol</li>
</ul>