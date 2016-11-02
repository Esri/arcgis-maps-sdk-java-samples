<h1>Find Place</h1>

<p>Demonstrates how find places of interest (POIs) by geocoding near a location or within an specific area.</p>

<h2>How to use the sample</h2>

<p>Choose from the dropdown or input your own place and location to search near. Click the search button to find matching places. A redo search button will appear if you pan the map after a search.</p>

<p><img src="FindPlace.png"/></p>

<h2>How it works</h2>

<p>To find locations matching a query and a search area:</p>

<ol>
    <li>Create an <code>ArcGISMap</code>'s with <code>Basemap</code>.
        <ul><li>basemap is created using a <code>TileCache</code> to represent an offline resource. </li></ul></li>
    <li>Add the map to the <code>MapView</code>, <code>MapView.setMap()</code>. </li>
    <li>Create a <code>LocatorTask</code> using a URL and set the <code>GeocodeParameters</code>.</li>
    <li>To reverse geocode near a location, pass the location's position into <code>GeocodeParameters.setSearchArea(Geometry)</code> to set the search area.</li>
    <li>Limit results to the view's visible area using the <code>MapView.getVisibleArea()</code> method.</li>
    <li>Show the matching retrieved results from the <code>LocatorTask.geocodeAsync(String, GeocodeParameters)</code> via <code>PictureMarkerSymbol</code>s with a <code>Graphic</code> in a <code>GraphicsOverlay</code>.</li>
</ol>

<h2>Features</h2>

<ul>
    <li>ArcGISMap</li>
    <li>GeocodeParameters</li>
    <li>GeocodeResult</li>
    <li>Graphic</li>
    <li>GraphicsOverlay</li>
    <li>LocatorTask </li>
    <li>MapView</li>
    <li>PictureMarkerSymbol</li>
    <li>ReverseGeocodeParameters</li>
    <li>TileCache</li>
</ul>



