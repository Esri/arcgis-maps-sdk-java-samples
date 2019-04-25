<h1>Display WFS Layer</h1>

<p>Display a layer from a WFS service, requesting only features for the current extent.</p>

<p><img src="DisplayWFSLayer.png"/></p>

<h2>Use case</h2>

<p>WFS is an open standard with functionality similar to ArcGIS feature
services. Runtime support for WFS allows you to interoperate with open
systems, which are often used in inter-agency efforts, like those for
disaster relief.</p>

<h2>How to use the sample</h2>

<p>Pan and zoom to see features within the current map extent.</p>

<h2>How it works</h2>

<ol>
<li>Create a <code>WfsFeatureTable</code> with a URL. </li>

<li>Create a <code>FeatureLayer</code> from the feature table and add it to the map.</li>

<li>Add a <code>NavigationChangedListener</code> to the map view and listen for a
<code>NavigationChangedEvent</code>. Check if it <code>!isNavigating()</code> to detect
when the user has stopped navigating the map.</li>

<li>When the user is finished navigating, use
<code>populateFromServiceAsync(...)</code> to load the table with data for the
current visible extent.</li>
</ol>

<h2>Relevant API</h2>

<ul>
<li>FeatureLayer</li>

<li>NavigationChangedEvent</li>

<li>QueryParameters</li>

<li>WfsFeatureTable</li>
</ul>

<h2>About the data</h2>

<p>This service shows building footprints for downtown Seattle. For
additional information, see the underlying service on
<a href="https://arcgisruntime.maps.arcgis.com/home/item.html?id=1b81d35c5b0942678140efc29bc25391">ArcGIS Online</a>.</p>

<h2>Tags</h2>

<p>OGC, WFS, layers,  feature, web, service, browse, catalog, interaction cache</p>