<h1>Service Area Task</h1>

<p>Demonstrates how find services areas around a point using the ServiceAreasTask. A service area shows locations that can be reached from a facility based off a certain impedance. One kind of impedence could be time.</p>

<p><img src="ServiceAreasTask.gif"/></p>

<h2>How to use sample</h2>
<p>In order to find any services areas at least one ServiceAreaFaciltiy needs to be added.</p>
<p>To add a facility, click the facility button then click anywhere on the MapView.</p>
<p>To add a barrier, click the barrier button and click multiple location on MapView to draw a barrier.</p>
<ul><p>Hit the barrier button again to finish drawing barrier.</p>
<p>Hitting any other button will also stop the barrier from drawing.</p></ul>
<p>To show service areas around facilities that were added, click show service areas button.</p>
<p>Reset button, clears all graphics and reset service area task</p>

<h2>How it works</h2>

<p>To display service areas around a certain location:</p>

<ol>
<li>Create a <code>ServiceAreasTask</code> from an online service.</li>
<li>Get ServiceAreasParameters from task, <code>task.createDefaultParametersAsync()</code></li>
<li>Set spatial reference to paremeters, this will allow geometry that is return to be displayed on map view, <code>serviceAreaParameters.setOutputSpatialReference(spatialReference)</code></li>
<li>Set return polygons to parameters, this will return any serivec areas that need to be displayed, <code>serviceAreaParameters.setReturnPolygons(true)</code></li>
<li>Add a <code>ServiceAreaFacilty</code> to parameters, <code>serviceAreaParameters.setFacilities(serviceAreaFacility)</code></li>
<li>Get <code>ServiceAreasResult</code> by solving the service areas task using parameters, <code>task.solveServiceAreaAsync(serviceAreaParameters).get()</code></li>
<li>Get any <code>ServiceAreaPolygon</code>s areas that were returned, <code>serviceAreaResult.getResultPolygons(facilityIndex)</code></li>
<ul>facilityIndex is the faciltiy from the map view that you want to get the services areas of</ul>
<li>Display services areas to map view, <code>graphicsOverlay.getGraphics().add(new Graphic(serviceAreaPolygon.getGeometry(), fillSymbol))</code></li>
</ol>

<h2>Features</h2>

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

