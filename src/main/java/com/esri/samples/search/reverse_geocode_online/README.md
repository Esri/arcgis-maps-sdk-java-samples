<h1>Reverse Geocode Online</h1>

<p>Demonstrates how to reverse geocode a location and find its nearest address.</p>

<p><img src="ReverseGeocodeOnline.png"/></p>

<h2>How to use the sample</h2>

<p>You can click on the ArcGISMap to perform online reverse geocoding and show the matching results in the ArcGISMap. </p>

<h2>How it works</h2>

<p>To perform online reverse geocode:</p>

<ol>
  <li>Create the <code>ArcGISMap</code>'s with <code>Basemap</code>.
    <ul><li>basemap is created using a <code>TileCache</code> to represent an offline resource </li></ul></li>
  <li>Create a <code>LocatorTask</code> using a URL.</li>
  <li>Set the <code>GeocodeParameters</code> for the LocatorTask and specify the geocodes' attributes.</li>
  <li>Get the matching results from the <code>GeocodeResult</code> using <code>LocatorTask.reverseGeocodeAsync()</code>.</li>
  <li>Lastly, to show the results using a <code>PictureMarkerSymbol</code> with attributes and add the symbol to a <code>Graphic</code> in the  <code>GraphicsOverlay</code>.</li>
</ol>

<h2>Relevant API</h2>

<ul>
  <li>ArcGISMap</li>
  <li>GeocodeParameters</li>
  <li>GraphicsOverlay</li>
  <li>LocatorTask</li>
  <li>MapView</li>
  <li>PictureMarkerSymbol</li>
  <li>ReverseGeocodeParameters </li>
  <li>TileCache</li>
</ul>



