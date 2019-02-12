<h1>View point cloud data offline</h1>

<p>Display local 3D point cloud data.</p>

<p><img src="ViewPointCloudDataOffline.png"/></p>

<h2>Use case</h2>

<p>Point clouds are often used to visualize massive sets of sensor data such as lidar. The point locations indicate where the sensor data was measured spatially, and the color or size of the points indicate the measured/derived value of the sensor reading. In the case of lidar, the color of the visualized point could be the color of the reflected light, so that the point cloud forms a true color 3D image of the area.</p>

<p>.</p>

<h2>How it works</h2>

<ol>
<li>Create a <code>PointCloudLayer</code> with the path to a local .slpk file containing a point cloud layer.</li>

<li>Add the layer to a scene's operational layers collection.</li>
</ol>

<h2>Relevant API</h2>

<ul>
<li>PointCloudLayer</li>
</ul>

<h2>About the data</h2>

<p>This point cloud data comes from Balboa Park in San Diego, California. Created and provided by USGS.</p>

<p>The points are colored by elevation on a spectrum from red (higher elevation) to blue (lower elevation).</p>

<h2>Additional information</h2>

<p>Point clouds can be loaded offline from scene layer packages (.slpk) or online via a scene service.</p>

<h2>Tags</h2>

<p>3D, point cloud, lidar</p>