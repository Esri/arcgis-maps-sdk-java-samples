<h1>Service Area Task</h1>

<p>Demonstrates how to find services areas around a point using the ServiceAreaTask. A service area shows locations that can be reached from a facility based off a certain impedance [such as travel time]. Barriers can also be added which can effect the impedance by not letting traffic through or adding the time is takes to pass that barrier.</p>

<p><img src="ServiceAreaTask.gif"/></p>

<h2>How to use sample</h2>
<li>In order to find any service areas at least one ServiceAreaFaciltiy needs to be added.</li>
<li>To add a facility, click the facility button, then click anywhere on the MapView.</li>
<li>To add a barrier, click the barrier button, and click multiple locations on MapView.</li>
<ul><li>Hit the barrier button again to finish drawing barrier.</li>
<li>Hitting any other button will also stop the barrier from drawing.</li></ul>
<li>To show service areas around facilities that were added, click show service areas button.</li>
<li>Reset button, clears all graphics and reset ServiceAreaTask.</li>

<h2>How it works</h2>

<p>To display service areas around a certain location:</p>

<ol>
<li>Create a <code>ServiceAreaTask</code> from an online service.</li>
<li>Get ServiceAreaParameters from task, <code>task.createDefaultParametersAsync()</code></li>
<li>Setting return polygons to true will return all services areas, <code>serviceAreaParameters.setReturnPolygons(true)</code></li>
<li>Add a <code>ServiceAreaFacilty</code> to parameters, <code>serviceAreaParameters.setFacilities(serviceAreaFacility)</code></li>
<li>Get <code>ServiceAreaResult</code> by solving the service area task using parameters, <code>task.solveServiceAreaAsync(serviceAreaParameters).get()</code></li>
<li>Get any <code>ServiceAreaPolygon</code>s that were returned, <code>serviceAreaResult.getResultPolygons(facilityIndex)</code></li>
<ul><li>facilityIndex is the faciltiy from the mapview that you want to get the services areas of</li></ul>
<li>Display services areas to MapView, <code>graphicsOverlay.getGraphics().add(new Graphic(serviceAreaPolygon.getGeometry(), fillSymbol))</code></li>
</ol>

<h2>Relevant API</h2>
<ul>
    <li>ArcGISMap</li>
    <li>GraphicsOverlay</li>
    <li>MapView</li>
    <li>PolylineBarrier</li>
    <li>ServiceAreaFacility</li>
    <li>ServiceAreaParameters</li>
    <li>ServiceAreaPolygon</li>
    <li>ServiceAreaResult</li>
    <li>ServiceAreaTask</li>
    <li>PolylineBuilder</li>
</ul>

