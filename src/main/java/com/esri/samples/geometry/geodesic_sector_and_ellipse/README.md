<h1>Project</h1>

<p>This sample demonstrates how to create and display geodesic sectors and ellipses.</p>

<p>Geodesic sectors and ellipses can be used in a wide range of analyses ranging from antenna coverage to projectile landing zones.</p>

<p><img src="GeodesicSectorAndEllipse.png"/></p>

<h2>How to use the sample</h2>

<p>The geodesic sector and ellipse will display with default parameters at the start. Click anywhere on the map to change the center of the geometries. Adjust any of the controls to see how they affect the sector and ellipse on the fly.</p>

<h2 id="howitworks">How it works</h2>

<p>To create a geodesic sector and ellipse:</p>

<ol>
<li>Create <code>GeodesicSectorParameters</code> and <code>GeodesicEllipseParameters</code> using one of the constructors with default values or using each setter individually.</li>

<li>Set the <code>center</code>, <code>axisDirection</code>, <code>semiAxis1Length</code>, and the <code>semiAxis2Length</code> properties to change the general ellipse position, shape, and orientation.</li>

<li>Set the <code>sectorAngle</code> and <code>startDirection</code> angles to change the sector's shape and orientation.</li>

<li>Set the <code>maxPointCount</code> and <code>maxSegmentLength</code> properties to control the complexity of the geometries and the approximation of the ellipse curve.</li>

<li>Specify the <code>geometryType</code> to either <code>POLYGON</code>, <code>POLYLINE</code>, or <code>MULTIPOINT</code> to change the result geometry type.</li>

<li>Pass the parameters to the related static methods: <code>GeometryEngine.ellipseGeodesic(geodesicEllipseParameters)</code> and <code>GeometryEngine.sectorGeodesic(geodesicSectorParameters)</code>. The returned value will be a <code>Geometry</code> of the type specified by the <code>geometryType</code> parameter.</li>
</ol>

<h2>Features</h2>

<ul>
<li>GeodesicSectorParameters</li>

<li>GeodesicEllipseParameters</li>

<li>GeometryEngine</li>

<li>GeometryType</li>
</ul>

<h2 id="additionalinformation">Additional information</h2>

<p>To create a circle instead of an ellipse, simply set <code>semiAxis2Length</code> to 0.0 and <code>semiAxis1Length</code> to the desired radius of the circle. This eliminates the need to update both parameters to the same value.</p>

<h2>Tags</h2>

<ul>

<p>GeometryEngine</p>

</ul>