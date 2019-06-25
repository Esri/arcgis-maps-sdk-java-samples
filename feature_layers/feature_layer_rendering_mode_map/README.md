<h1>Feature Layer Rendering Mode (Map)</h1>

<p>Render features statically or dynamically.</p>

<p><img src="FeatureLayerRenderingModeMap.gif"/></p>

<h2>How it works</h2>

<p>To change <code>FeatureLayer.RenderingMode</code> using <code>LoadSettings</code>:</p>

<ol>
    <li>Create a <code>ArcGISMap</code>.</li>
    <li>Set preferred rendering mode to map, <code>mapBottom.getLoadSettings().setPreferredPointFeatureRenderingMode(FeatureLayer.RenderingMode.DYNAMIC)</code>.
      <ul>
        <li>Can set preferred rendering mode for <code>Points</code>, <code>Polylines</code>, or <code>Polygons</code>.</li>
        <li><code>Multipoint</code> preferred rendering mode is the same as point.</li>
      </ul>
    </li>
    <li>Set map to <code>MapView</code>, <code>mapViewBottom.setMap(mapBottom)</code>.</li>
    <li>Create a <code>ServiceFeatureTable</code> from a point service, <code>new ServiceFeatureTable("http://sampleserver6.arcgisonline.com/arcgis/rest/services/Energy/Geology/FeatureServer/0");</code>.</li>
    <li>Create <code>FeatureLayer</code> from table, <code>new FeatureLayer(poinServiceFeatureTable)</code>.</li>
    <li>Add layer to map, <code>mapBottom.getOperationalLayers().add(pointFeatureLayer.copy())</code>
      <ul>
        <li>Now the point layer will be rendered dynamically to map view.</li>
      </ul>
    </li>
</ol>

<h2>Relevant API</h2>

<ul>
    <li>ArcGISMap</li>
    <li>FeatureLayer</li>
    <li>FeatureLayer.RenderingMode</li>
    <li>LoadSettings</li>
    <li>Point</li>
    <li>Polyline</li>
    <li>Polygon</li>
    <li>ServiceFeatureTable</li>
    <li>Viewpoint</li>
</ul>


