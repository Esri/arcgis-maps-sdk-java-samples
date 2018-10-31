<h1 id="scalebar">Scale Bar</h1>

<p>This sample demonstrates how to use the scale bar from the <a href ="https://github.com/Esri/arcgis-runtime-toolkit-java/blob/master/src/main/java/com/esri/arcgisruntime/toolkit/Scalebar.javascale"> ArcGIS Runtime Toolkit for Java</a> and display it on a map. The scale bar provides an accurate tool for users to visually gauge distances on a <code>MapView</code>.</p>

<p><img src="ScaleBar.png" alt="Image" /></p>

<h2 id="howtousethesample">How to use the sample</h2>

<p>Zoom in or out of the map. The scale bar will automatically display the appopriate scale based on zoom level. Units can be in metric and/or imperial.</p>

<h2 id="howitworks">How it works</h2>

<p>To add a <code>Scalebar</code> with the <code>Toolkit</code> component:</p>

<ol>
<li>Import <code>Scalebar</code> class via the ArcGIS Runtime Toolkit library.</li>

<li>Create a new <code>Map</code> and <code>MapView</code> to display a map, and pass these into a new <code>Scalebar</code> object.</li>

<li>User can choose from one of 5 scale bar styles through <code>Scalebar.SkinStyle</code> (graduated line, line, bar, alternating bar and dual unit line). </li>

<li>Scale bar units are set to Metric (metres and kilometres) by default: user can set units for Imperial (miles, feet) with <code>UnitSystem.IMPERIAL</code>.</li>

<p><img src="ScaleBarStyles.png" alt="Image" width= 60% /></p>

<li>The position of the scale bar on the <code>MapView</code> can be customised.</li>

<li>The background of the scale bar is made a transparent white in this sample for ease of viewing. </li>
</ol>

<h2 id="relevantapi">Relevant API</h2>

<ul>
<li><code>Map</code></li>

<li><code>UnitSystem</code></li>

<li><code>Scalebar</code></li>
</ul>

<h2 id="tags">Tags</h2>

<p>Map, Toolkit, Scale Bar. </p>

<h2 id="additionalinformation">Additional Information</h2>

<p><b>Note</b>: the scale will be accurate for the center of the map, and in general more accurate at larger scales (zoomed in). This means at smaller scales (zoomed out), the reading may be inaccurate at the extremes of the visible extent.</p>