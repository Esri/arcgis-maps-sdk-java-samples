<h1>Raster Rendering Rule</h1>

<p>Display a raster on a map and apply different rendering rules to that raster.</p>

<p><img src="RasterRenderingRule.png"/></p>

<h2>Use case</h1>

<p>A single raster image can be rendered in a number of different ways, including representation of slope aspect, hillshade and shaded relief. Applying these different renderers to the same raster allows for a powerful visual analysis of the data. For example, a geologist could interrogate the raster image using all available renderers to map subtle geological features on a landscape not apparent in a regular aerial photograph.</p>

<h2>How to use the sample</h2>

<p>Run the sample and use the drop-down menu at the top to select a rendering rule.</p>

<h2>How it works</h2>

<ol>
  <li> Create an <code>ImageServiceRaster</code> using a URL to an online image service. </li>
  <li> After loading the raster, use <code>imageServiceRaster.getServiceInfo().getRenderingRuleInfos()</code> to get a list of RenderingRuleInfos supported by the service. </li>
  <li> Choose a rendering rule info to apply and use it to create a <code>RenderingRule</code>. </li>
  <li> Create a new <code>ImageServiceRaster</code> using the same URL. </li>
  <li> Apply the rendering rule to the new raster using <code>imageServiceRaster.setRenderingRule(renderingRuleInfo)</code>. </li>
  <li> Create a RasterLayer from the raster for display. </li>
</ol>

<h2>Relevant API</h2>

<ul>
  <li>ImageServiceRaster</li>
  <li>RasterLayer</li>
  <li>RenderingRule</li>
</ul>

<h2>About the data</h2>

<p>This raster image service contains 9 LAS files coveringcovering Charlotte, North Carolina's downtown area. The lidar data was collected in 2007. Four Raster Rules are available for selection: None, RFTAspectColor, RFTHillshade, and RFTShadedReliefElevationColorRamp</p>

<h2>Tags</h2>

<p>ImageServiceRaster, Raster, RasterLayer, RenderingRule, Visualization</p>
