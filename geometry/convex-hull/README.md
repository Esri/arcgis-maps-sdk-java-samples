<h1>Convex Hull</h1>

<p>Calculate the convex hull for a set of points.</p>

<p>The convex hull is the polygon with shortest perimeter that encloses a set of points. As a visual analogy, consider a set of points as nails in a board. The convex hull of the points would be like a rubber band stretched around the outermost nails.</p>

<p>A convex hull can be useful in collision detection. When checking if two complex geometries touch and their convex hulls have been precomputed, it is efficient to first check if their convex hulls intersect before computing their proximity point-by-point.</p>

<p><img src="ConvexHull.png"/></p>

<h2>How to use the sample</h2>

<p>Tap on the map to add points. Click the "Create Convex Hull" button to show the convex hull. Click the "Reset" button to start over.</p>

<h2>How it works</h2>

<ol>
<li>Create an input geometry such as a <code>Multipoint</code>.</li>
<li>Call <code>GeometryEngine.convexHull(inputGeometry)</code>. The returned <code>Geometry</code> will either be a <code>Point</code>, <code>Polyline</code>, or <code>Polygon</code> based on the number of input points.</li>
</ol>

<h2>Relevant API</h2>

<ul>
<li>GeometryEngine</li>
</ul>