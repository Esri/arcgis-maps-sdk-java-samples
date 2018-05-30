<h1>Query Map Image Sublayer</h1>

<p>This sample demonstrates how to execute an attribute and spatial query on the sublayers of an ArcGIS map image 
layer.</p>    

<p>Sublayers of an <code>ArcGISMapImageLayer</code> may expose a <code>ServiceFeatureTable</code> through a 
<code>Table</code> property. This allows you to perform the same queries available when working with a table from a 
<code>FeatureLayer</code>: attribute query, spatial query, statistics query, query for related features, and so on.

<p><img src="QueryMapImageSublayer.png"></p>

<h2>How to use the sample:</h2>

<p>Specify a minimum population in the spinner and click the query button to query the sublayers. After a short time,
 the results for each sublayer will appear as graphics.</p>

<h2>How it works</h2>

<p>To query map image sublayers:</p>

<ol>
    <li>Create an <code>ArcGISMapImageLayer</code> using the URL of it's image service.</li>
    <li>After loading the layer, get its sublayers you want to query with <code>(ArcGISMapImageSublayer) layer
    .getSubLayers().get(index)</code>.</li>
    <li>Load the sublayer, and then get its <code>ServiceFeatureTable</code> with <code>sublayer.getTable()</code>.</li>
    <li>Create <code>QueryParameters</code>. You can use the where clause to query against a table attribute or set 
    the parameters geometry to limit the results to an area of the map.</li>
    <li>Call <code>sublayerTable.queryFeaturesAsync(queryParameters)</code> to get a <code>FeatureQueryResult</code> 
    with features matching the query.</li>
    <li>Iterate through the result features to use them however you wish.</li>
</ol>

<h2>Features</h2>

<ul>
    <li>ArcGISMapImageLayer</li>
    <li>ArcGISMapImageSublayer</li>
    <li>QueryParameters</li>
    <li>ServiceFeatureTable</li>
</ul>
