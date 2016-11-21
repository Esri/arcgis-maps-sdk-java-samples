<h1>Geocode Online</h1>

<p>Demonstrates how to geocode an address query and display its location on the ArcGISMap.</p>

<p><img src="GeocodeOnline.png"/></p>

<h2>How to use the sample</h2>

<p>For simplicity, the sample comes loaded with a set of addresses. You can select an address to perform online geocoding and show the matching results on the ArcGISMap. </p>

<h2>How it works</h2>

<p>To get a geocode from a query and display its location on the <code>ArcGISMap</code>:</p>

<ol>
  <li>Create the ArcGIS map's with <code>Basemap</code>.
    <ul><li>basemap is created using a <code>TileCache</code> to represent an offline resource </li></ul></li>
  <li>Create a <code>LocatorTask</code> using a URL.</li>
  <li>Set the <code>GeocodeParameters</code> for the locator task and specify the geocodes' attributes.</li>
  <li>Get the matching results from the <code>GeocodeResult</code> using <code>LocatorTask.geocodeAsync(query, geocodeParameters)</code>.</li>
  <li>Lastly, to show the results using a <code>PictureMarkerSymbol</code> with attributes and add the symbol to a <code>Graphic</code> in the  <code>GraphicsOverlay</code>.</li>
</ol>

<h2>Features</h2>

<ul>
  <li>ArcGISMap</li>
  <li>GeocodeParameters</li>
  <li>GeocodeResult</li>
  <li>GraphicsOverlay</li>
  <li>LocatorTask</li>
  <li>MapView</li>
  <li>ReverseGeocodeParameters</li>
  <li>TileCache</li>
</ul>


