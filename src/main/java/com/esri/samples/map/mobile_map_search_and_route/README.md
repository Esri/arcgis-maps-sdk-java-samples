<h1>Mobile Map Search And Route</h1>

<p>Use locators and networks saved in an offline map.</p>

<p><img src="MobileMapSearchAndRoute.png"/></p>

<h2>How to use the sample</h2>

<p>Click the "Open mobile map package" button to bring up a file choosing dialog. Browse to and select a .mmpk file.
When chosen, the maps inside the mobile map package will be displayed in a list view. If the mobile map package has a locator task,
the list items will have a pin icon. If the map contains transportation networks, it will have a navigation icon.
Click on a map list item to open it. Click on the map to reverse geocode the clicked locations's address if a locator task is available.
If transportation networks are available, a route will be calculated between geocode locations.</p>

<h2>How it works</h2>

<p>To search and route from a mobile map package:</p>

<ol>
    <li>Create a <code>MobileMapPackage</code> passing in the path to the local mmpk file.</li>
    <li>Load the mobile map package and get its maps with <code>mobileMapPackage.getMaps()</code>.</li>
    <li>A <code>LocatorTask</code> can be retrieved from the mobile map package with <code>mobileMapPackage.getLocatorTask()</code> if it has one.</li>
    <li>To see if a map has transportation networks saved with it call <code>map.getTransportationNetworks()</code>. Each <code>TransportationNetworkDataset</code> can be used to construct a <code>RouteTask</code>.</li>
</ol>

<h2>Relevant API</h2>

<ul>
    <li>GeocodeResult</li>
    <li>MobileMapPackage</li>
    <li>ReverseGeocodeParameters</li>
    <li>Route</li>
    <li>RouteParameters</li>
    <li>RouteResult</li>
    <li>RouteTask</li>
    <li>TransportationNetworkDataset</li>
</ul>

<h2>Tags</h2>
<p>Offline, Routing, Search</p>
