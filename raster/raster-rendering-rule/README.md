<h1>Raster rendering rule</h1>

<p>Display a raster on a map and apply different rendering rules to that raster.</p>

<p><img src="RasterRenderingRule.png"/></p>

<h2>Use case</h2>

<p>Raster images whose individual pixels represent elevation values can be rendered in a number of different ways, including representation of slope, aspect, hillshade, and shaded relief. Applying these different rendering rules to the same raster allows for a powerful visual analysis of the data. For example, a geologist could interrogate the raster image to map subtle geological features on a landscape, which may become apparent only through comparing the raster when rendered using several different rules.</p>

<h2>How to use the sample</h2>

<p>Run the sample and use the drop-down menu at the top to select a rendering rule.</p>

<h2>How it works</h2>

<ol>
  <li>Create an <code>ImageServiceRaster</code> using a URL to an online image service.</li>
  <li>After loading the raster, use <code>imageServiceRaster.getServiceInfo().getRenderingRuleInfos()</code> to get a list of <code>RenderingRuleInfo</code> supported by the service.</li>
  <li>Choose a rendering rule info to apply and use it to create a <code>RenderingRule</code>.</li>
  <li>Create a new <code>ImageServiceRaster</code> using the same URL.</li>
  <li>Apply the rendering rule to the new raster using <code>imageServiceRaster.setRenderingRule(renderingRuleInfo)</code>.</li>
  <li>Create a <code>RasterLayer</code> from the raster for display.</li>
</ol>

<h2>Relevant API</h2>

<ul>
  <li>ImageServiceRaster</li>
  <li>RasterLayer</li>
  <li>RenderingRule</li>
</ul>

<h2>About the data</h2>

<p>This raster image service contains 9 LAS files covering Charlotte, North Carolina's downtown area. The lidar data was collected in 2007. Four Raster Rules are available for selection: None, RFTAspectColor, RFTHillshade, and RFTShadedReliefElevationColorRamp.</p>

<h2>Additional information</h2>

<p>Image service rasters of any type can have rendering rules applied to them; they need not necessarily be elevation rasters. For a list of raster functions and the syntax for rendering rules, see the ArcGIS REST API documentation: https://developers.arcgis.com/documentation/common-data-types/raster-function-objects.htm.</p>

<h2>Tags</h2>

<p>raster, rendering rules, visualization</p>
