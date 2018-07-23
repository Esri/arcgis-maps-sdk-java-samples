<h1>Symbolize Shapefile</h1>

<p>Demonstrates how to override symbols of the default renderer for a shapefile.</p>
  
<p><img src="SymbolizeShapefile.png"/></p>

<h2>How to use the sample</h2>

<p>Press the toggle button to switch between red and yellow symbols and the default renderer.</p>

<h2>How it works</h2>

<p>To change the renderer of a shapefile feature layer:</p>

<ol>
  <li>Create a <code>ShapefileFeatureTable</code> passing in the URL of a shapefile.</li>
  <li>Create a <code>FeatureLayer</code> using the <code>ShapefileFeatureTable</code>.</li>
  <li>Create a <code>SimpleLineSymbol</code> and <code>SimpleFillSymbol</code> (uses the line symbol).</li>
  <li>Make a <code>SimpleRenderer</code> with the <code>SimpleFillSymbol</code>.</li>
  <li>To apply the renderer, use <code>featureLayer.setRenderer(renderer)</code>.</li> 
  <li>To go back to the default renderer, use <code>featureLayer.resetRenderer()</code>.</li>
</ol>

<h2>Relevant API</h2>

<ul>
  <li>FeatureLayer</li>
  <li>ShapefileFeatureTable</li>
  <li>SimpleFillSymbol</li>
  <li>SimpleLineSymbol</li>
  <li>SimpleRenderer</li>
</ul>



