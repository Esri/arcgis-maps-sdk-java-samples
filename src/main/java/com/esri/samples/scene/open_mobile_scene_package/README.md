<h1>Open mobile scene package</h1>

<p>Open and display a scene from an offline Mobile Scene Package (.mspk).</p>

<p><img src="OpenMobileScenePackage.png"/></p>

<h2>Use Case</h2>

<p>A .mspk file is an archive containing the data (specifically, basemaps and features) used to display an offline 3D scene.</p>

<h2>How it works</h2>

<ol>
<li>Use the static method <code>MobileScenePackage.isDirectReadSupportedAsync(mspkData)</code> to check whether the package can be read in the archived form (.mspk) or whether it needs to be unpacked.</li>

<li>If direct read is supported, use <code>isDirectReadSupported.get()</code> and instantiate a <code>MobileScenePackage</code> with the path to the .mspk file.</li>

<li>If the mobile scene package requires unpacking, use <code>MobileScenePackage.unpackAsync(mspkPath, pathToUnpackTo)</code> and instantiate a <code>MobileScenePackage</code> with the path to the unpacked .mspk file.</li>

<li>Call <code>mobileScenePackage.loadAsync</code> to load the mobile scene package. When finished, get the <code>ArcGISScene</code> objects inside with <code>mobileScenePackage.getScenes()</code>.</li>

<li>Set the first scene in the object collection on the scene view with <code>sceneView.setArcGISScene(scene)</code>.</li>
</ol>

<h2>Relevant API</h2>

<ul>
<li>MobileScenePackage</li>
</ul>

<h2>Additional information</h2>

<p>Before loading the <code>MobileScenePackage</code>, it is important to first check if direct read is supported. The mobile scene package could contain certain data types that would require the data to be unpacked. For example, scenes containing raster data will need to be unpacked.</p>

<h2>Tags</h2>

<p>Offline, Scene, MobileScenePackage</p>