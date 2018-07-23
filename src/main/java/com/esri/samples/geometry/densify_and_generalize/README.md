<h1>Densify and Generalize</h1>

<p>Demonstrates how to densify or generalize a polyline geometry. In this example, points representing a ships 
location are shown at irregular intervals. One can densify the polyline connecting these lines to interpolate points 
along the line at regular intervals. Generalizing the polyline can also simplify the geometry while preserving its 
general shape.</p>

<p><img src="DensifyAndGeneralize.gif"/></p>

<h2>How to use the sample</h2>

<p>Use the sliders to control the parameters of the densify and generalize methods. You can deselect the checkboxes 
for either method to remove its effect from the result polyline. You can also hide the result to only see the 
original by deselecting the "Show result" checkbox.</p>

<h2>How it works</h2>

<p>To densify and generalize a polyline:</p>

<ol>
    <li>Use the static method <code>GeometryEngine.densify(polyline, maxSegmentLength)</code> to densify the polyline
    . The resulting polyline will add points along the line so that there are no points greater than <code>maxSegmentLength</code> from the next point.</li>
    <li>Use the static method <code>GeometryEngine.generalize(polyline, maxDeviation, true)</code> to generalize the 
    polyline. The resulting polyline will have points or shifted from the line to simplify the shape. None of these points can 
    deviate farther from the original line than <code>maxDeviation</code>. The last parameter, 
    <code>removeDegenerateParts</code> , will clean up extraneous parts if the geometry is multi-part (it will have 
    no effect in this sample.</li>
    <li>Note that <code>maxSegmentLength</code> and <code>maxDeviation</code> are in the units of geometry's 
    coordinate system. This could be in degrees in some coordinate systems. In this example, a cartesian coordinate 
    system is used and at a small enough scale that geodesic distances are not required.</li>
</ol>

<h2>Relevant API</h2>

<ul>
   <li>ArcGISMap</li>
   <li>Basemap</li>
   <li>GeometryEngine</li>
   <li>Graphic</li>
   <li>GraphicsOverlay</li>
   <li>MapView</li>
   <li>Multipoint</li>
   <li>Point</li>
   <li>PointCollection</li>
   <li>Polyline</li>
   <li>SimpleLineSymbol</li>
   <li>SimpleMarkerSymbol</li>
   <li>SpatialReference</li>
</ul>
