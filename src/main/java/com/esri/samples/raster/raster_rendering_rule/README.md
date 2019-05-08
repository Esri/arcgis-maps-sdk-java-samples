<h1>Raster Rendering Rule</h1>

<p>Display a raster on a map and apply different rendering rules to that raster.</p>

<p><img src="RasterRenderingRule.png"/></p>

<h2>Use case</h1>

<p>Image Service Rasters may provide several rendering rules options. When it is necessary to switch between rendering rules in order to display the desired data, the interface for the selection and application of the required rule can be used.</p>

<h2>How to use the sample</h2>

<p>Run the sample and use the drop-down menu at the top to select a rendering rule.</p>

<h2>How it works</h2>

<ol>
  <li> Create an <code>ImageServiceRaster</code> and add it to a <code>RasterLayer</code> and add the <code>RasterLayer</code> to the map as an operational layer. </li>
  <li> Connect to the <code>loadStatusChanged</code> signal for the image service raster.</li>
  <li> Once the image service raster is loaded, the <code>RenderingRuleInfos</code> are fetched and saved to a <code>List</code>: <code>imageServiceRaster.getServiceInfo().getRenderingRuleInfos()</code></li>
  <li> Iterate over each item in the list of rendering rule infos to get the rendering rule name, and use these to populate a <code>ComboBox</code> for rule selection.</li>
  <li> When an item from the ComboBox is selected, the <code>RenderingRuleInfo</code> for the selected index is fetched from the service info. A <code>RenderingRule</code> object is created using the rendering rule info and applied to a newly created <code>ImageServiceRaster</code>. The image service raster is then added to the <code>RasterLayer</code>. </li>
</ol>

<h2>Relevant API</h2>

<ul>
  <li>ImageServiceRaster</li>
  <li>RasterLayer</li>
  <li>RenderingRule</li>
</ul>

<h2>About the data</h2>

<p>This raster image service contains 9 LAS files covering North Carolina’s, City of Charlotte downtown area. The lidar data was collected in 2007. Four Raster Rules are available for selection: None, RFTAspectColor, RFTHillshade, and RFTShadedReliefElevationColorRamp</p>

<h2>Tags</h2>

<p>ImageServiceRaster, Raster, RasterLayer, RenderingRule, Visualization</p>
