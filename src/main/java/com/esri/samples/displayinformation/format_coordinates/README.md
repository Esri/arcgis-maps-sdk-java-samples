<h1>Format Coordinates</h1>

<p>Demonstrates how to convert a map location Point in WGS84 to a String in a number of different 
coordinate notations (and vice versa) including decimal degrees; degrees, minutes, seconds; Universal Transverse 
Mercator (UTM), and United States National Grid (USNG).</p>
 
 <p><img src="FormatCoordinates.png"/></p>

<h2>How to use the sample</h2>

<p>Click on the map to see a callout with the clicked location's coordinate formatted in 4 different ways. You can 
also put a coordinate string in any of these formats in the text field. Hit Enter and the coordinate string will be 
converted to a map location which the callout will move to.</p>

<h2>How it works</h2>

<p>To convert between a <code>Point</code> and different coordinate formats using the 
<code>CoordinateFormatter</code>:</p>
<ol>
    <li>Get or create a map <code>Point</code> with a spatial reference.</li>
    <li>Use one of the static "to" methods on <code>CoordinateFormatter</code> such as 
    <code>CoordinateFormatter.toLatitudeLongitude(point, CoordinateFormatter.LatitudeLongitudeFormat.DECIMAL_DEGREES,
     4)</code> to get the formatted string</code>.</li>
    <li>To go from a formatted string to a <code>Point</code>, use one of the "from" static methods like 
    <code>CoordinateFormatter.fromUtm(coordinateString, map.getSpatialReference(), CoordinateFormatter.UtmConversionMode
    .LATITUDE_BAND_INDICATORS)</code>.</li>
</ol>

<h2>Relevant API</h2>

<ul>
    <li>ArcGISMap</li>
    <li>ArcGISTiledLayer</li>
    <li>Basemap</li>
    <li>Callout</li>
    <li>CoordinateFormatter</li>
    <li>MapView</li>
</ul>
