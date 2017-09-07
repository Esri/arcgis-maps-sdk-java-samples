<h1>Open Mobile Map Package</h1>

<p>Shows how to open and display a map from a mobile map package.</p>

<p><img src="OpenMobileMapPackage.png"/></p>

<h2>How it works</h2>

<p>To display a map from a <code>MobileMapPackage</code>:</p>

<ol>
    <li>Create a <code>MobileMapPackage</code> specifying the path to the .mmpk file.</li>
    <li>Load the mobile map package with <code>mmpk.loadAsync()</code>.</li>
    <li>After it successfully loads, get the map from the mmpk and add it to the map view: <code>mapView.setMap(mmpk
    .getMaps().get(0))</code>.</li>
</ol>

<h2>Features</h2>

<ul>
    <li>MapView</li>
    <li>MobileMapPackage</li>
</ul>

