<h1>Feature Layer Rendering Mode (Map)</h1>

<p>This sample demonstrates how to use load settings to set preferred rendering mode for feature layers, specifically static or dynamic rendering modes..</p>

<p><img src="FeatureLayerRenderingModeMap.gif"/></p>

<h2>How it works</h2>

<p>To change <code>FeatureLayer.RenderingMode</code> using <code>LoadSettings</code>:</p>

<ol>
    <li>Create a <code>ArcGISMap</code>.</li>
    <li>Set preferred rendering mode to map, <code>mapBottom.getLoadSettings().setPreferredPointFeatureRenderingMode(FeatureLayer.RenderingMode.DYNAMIC)</code>.
      <ol>
        <li>Can set preferred rendering mode for <code>Points</code>, <code>Polylines</code>, or <code>Polygons</code>.</li>
        <li><code>Multipoint</code> preferred rendering mode is the same as point.</li>
      </ol>
    </li>
    <li>Set map to <code>MapView</code>, <code>mapViewBottom.setMap(mapBottom)</code>.</li>
    <li>Create a <code>ServiceFeatureTable</code> from a point service, <code>new ServiceFeatureTable("http://sampleserver6.arcgisonline.com/arcgis/rest/services/Energy/Geology/FeatureServer/0");</code>.</li>
    <li>Create <code>FeatureLayer</code> from table, <code>new FeatureLayer(poinServiceFeatureTable)</code>.</li>
    <li>Add layer to map, <code>mapBottom.getOperationalLayers().add(pointFeatureLayer.copy())</code>
      <ol>
        <li>Now the point layer will be rendered dynamically to scene view.</li>
      </ol>
    </li>
</ol>

<h2>Features</h2>

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


