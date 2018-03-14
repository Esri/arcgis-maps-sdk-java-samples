<h1>Spatial Relationships</h1>

<p>Shows how to use the GeometryEngine to determine spatial relationships between two geometries.</p>

<p><img src="SpatialRelationships.png"/></p>

<h2>How to use the sample</h2>

<p>Click on one of the three graphics to select it. The tree view will list the relationships the selected graphic 
has to the other graphic geometries.</p>

<h2>How it works</h2>

<p>To check the relationship between geometries.</p>

<ol>
    <li>Get the geometry from two different graphics. In this example the geometry of the selected graphic is 
    compared to the geometry of each graphic not selected.</li>
    <li>Use the methods in <code>GeometryEngine</code> to check the relationship between the geometries, e.g. 
    <code>contains</code>, <code>disjoint</code>, <code>intersects</code>, etc. If the method returns 
    <code>true</code>, the relationship exists.</li>
</ol>

<h2>Features</h2>
<ul>
    <li>ArcGISMap</li>
    <li>Basemap</li>
    <li>Geometry</li>
    <li>GeometryEngine</li>
    <li>GeometryType</li>
    <li>Graphic</li>
    <li>GraphicsOverlay</li>
    <li>MapView</li>
    <li>Point</li>
    <li>PointCollection</li>
    <li>Polygon</li>
    <li>Polyline</li>
    <li>SimpleFillSymbol</li>
    <li>SimpleLineSymbol</li>
    <li>SimpleMarkerSymbol</li>
</ul>
