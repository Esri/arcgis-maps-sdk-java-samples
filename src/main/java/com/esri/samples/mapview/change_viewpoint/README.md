<h1>Change Viewpoint</h1>

<p>Demonstrates different ways in which you can change the Viewpoint, visible area, of an ArcGISMap.</p>

<p><img src="ChangeViewpoint.png"/></p>

<h2>How to use the sample</h2>

<p>The <code>MapView</code> provides different methods you can use to set the viewpoint.
 - London button pans to London given a certain time length, <code>MapView.setViewpointWithDurationAsync()</code>.
 - Waterloo button centers at a point and set a distance from the ground using a scale, <code>MapView.setViewpointCenterAsync()</code>.
 - Westminster button set viewpoint given some type of geometry, <code>MapView.setViewpointGeometryAsync()</code>.</p>

<p>Below are some other ways to set a viewpoint.
 - setViewpoint
 - setViewpointAsync
 - setViewpointCenterAsync
 - setViewpointGeometryAsync
 - setViewpointRotationAsync
 - setViewpointScaleAsync</p>

<h2>How it works</h2>

<p>To change the <code>Viewpoint</code>:</p>

<ol>
 <li>Create an <code>ArcGISMap</code>. </li>
 <li>Set the map to the <code>MapView</code>, <code>MapView.setMap()</code>. </li>
 <li>Change the view point with <code>MapView.setViewPoint()</code> or any method mention above.</li>
</ol>

<h2>Relevant API</h2>

<ul>
 <li>ArcGISMap</li>
 <li>Basemap</li>
 <li>Point</li>
 <li>SpatialReference</li>
 <li>Viewpoint</li>
</ul>


