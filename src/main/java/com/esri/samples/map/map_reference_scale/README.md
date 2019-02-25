<h1>Map reference scale</h1>

<p>Set the map's reference scale and choose which feature layers should honor the reference scale.</p>

<p><img src="MapReferenceScale.png"/></p>

<h2>Use case</h2>

<p>Setting a reference scale on an <code>ArcGISMap</code> fixes the size of symbols and text to the desired height and width at that scale. As you zoom in and out, symbols and text will increase or decrease in size accordingly. When no reference scale is set, symbol and text sizes remain the same size relative to the <code>MapView</code>.</p>

<p>Map annotations are typically only relevant at certain scales. For instance, annotations to a map showing a construction site are only relevant at that construction site's scale. So, when the map is zoomed out that information shouldn't scale with the <code>MapView</code>, but should instead remain scaled with the <code>ArcGISMap</code>. </p>

<h2>How to use the sample</h2>

<ul>
<li>Use the drop down menu at the top left to choose a reference scale.</li>

<li>Click the "Set Map Scale to Reference Scale" button below the drop down menu to set the map scale to the reference scale.</li>

<li>Use the top right menu checkboxes to apply the map reference scale to the map's feature layers (which should scale according to the reference scale).</li>
</ul>

<h2>How it works</h2>

<ul>
<li>Set the map reference scale property on the <code>ArcGISMap</code> with <code>map.setReferenceScale(double)</code>.</li>

<li>Set the scale symbols property on each individual <code>FeatureLayer</code> within the map with <code>featureLayer.setScaleSymbols(boolean)</code>.</li>
</ul>

<h2>Relevant API</h2>

<ul>
<li>ArcGISMap</li>

<li>FeatureLayer</li>
</ul>

<h2>Additional Information</h2>

<p>The map reference scale should normally be set by the map's author and not exposed to the end user like it is in this sample. </p>

<h4>Tags</h4>

<p>Maps & Scenes, reference scale</p>