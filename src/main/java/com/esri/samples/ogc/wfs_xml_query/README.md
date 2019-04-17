<h1>﻿Load WFS with XML query</h1>

<p>Load a WFS feature table using an XML query.</p>

<p><img src="WfsXmlQuery.png"/></p>

<h2>Use case</h2>

<p>ArcGIS Runtime <code>QueryParameters</code> objects can't represent all possible queries that can be made against a WFS feature service. For example, Runtime query parameters don't support wildcard searches. However, queries can be provided as raw XML strings, allowing access to query functionality not available with <code>QueryParameters</code>.</p>

<h2>How it works</h2>

<ol>
<li>Create a <code>WfsFeatureTable</code> with a URL.</li>

<li>Set the feature table's axis order to <code>NO_SWAP</code>, and the feature request mode to <code>MANUAL_CACHE</code>. </li>

<li>Create a <code>FeatureLayer</code> from the feature table and add it to the map's operational layers.</li>

<li>Call <code>populateFromServiceAsync</code> on the feature table to populate it with only those features returned by the XML query.</li>
</ol>

<h2>Relevant API</h2>

<ul>
<li>FeatureLayer</li>

<li>WfsFeatureTable</li>

</ul>

<h2>About the data</h2>

<p>This service shows trees in downtown Seattle and the surrounding area. An XML-encoded <code>GetFeature</code> request is used to limit results to only trees of the genus <em>Tilia</em>.</p>

<p>For additional information, see the underlying service on <a href="https://arcgisruntime.maps.arcgis.com/home/item.html?id=1b81d35c5b0942678140efc29bc25391">ArcGIS Online</a>.</p>

<h2>Tags</h2>

<p>OGC, WFS, feature, web, service, XML, query</p>