<h1>Identify Graphics</h1>

<p>Demonstrates how to create a Graphic and add it to a GraphicOverlay where it can be identified from the MapView.</p>

<h2>How to use the sample</h2>

<p>When you click on a graphic on the map, you should see an alert.</p>

<p><img src="IdentifyGraphics.png"/></p>

<h2>How it works</h2>

<p>To identify a <code>Graphic</code> from the <code>MapView</code>.</p>

<ol>
    <li>Create a <code>GraphicsOverlay</code> and add it to the MapView.</li>
    <li>Add Graphic along with a <code>SimpleFillSymbol</code>. </li>
    <li>Add the graphic to the graphics overlay. </li>
    <li>Identify the graphics on the specified location, <code>MapView.identifyGraphicsOverlayAsync(graphicsOverlay, point, tolerance, max results)</code> method.</li>
</ol>

<h2>Features</h2>

<ul>
    <li>Graphic</li>
    <li>GraphicsOverlay</li>
    <li>MapView</li>
    <li>PointCollection</li>
    <li>Polygon</li>
    <li>SimpleFillSymbol</li>
</ul>
