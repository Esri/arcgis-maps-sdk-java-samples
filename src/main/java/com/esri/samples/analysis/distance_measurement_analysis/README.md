<h1>Distance Measurement Analysis</h2>
<p>This sample demonstrates measuring 3D distances between two points in a scene.</p>

<p>The distance measurement analysis allows you to add the same measuring experience found in ArcGIS Pro, City Engine, 
and the ArcGIS API for JavaScript to your app. You can set the unit system of measurement (metric or imperial) and 
have the units automatically switch to one appropriate for the current scale. The rendering is handled internally so 
they do not interfere with other analyses like viewsheds.</p>

<p><img src="DistanceMeasurementAnalysis.png"/></p>

<h2>How to use the sample</h2>
<p>Choose a unit system for the measurement in the UI dropdown. Click any location in the scene to start measuring. 
Move the mouse to an end location, and click to complete the measure. Click a new location to start a new 
measurement.</p>

<h2>How it works</h2>
<p>To measure distances with the <code>LocationDistanceMeasurement</code> analysis:</p>
<ol>
  </li>Create an <code>AnalysisOverlay</code> and add it to your scene view's analysis overlay collection: 
  <code>sceneView.getAnalysisOverlays().add(analysisOverlay)</code>.</li>
  <li>Create a <code>LocationDistanceMeasurement</code>, specifying the <code>startLocation</code> and 
  <code>endLocation</code>. These can be the same point to start with. Add the analysis to the analysis overlay: 
  <code>analysisOverlay.getAnalyses().add(LocationDistanceMeasurement)</code>. The measuring line will be drawn for 
  you between the two points.</li>
  <li>The <code>measurementChanged</code> callback will fire if the distances change. You can get the new values for 
  the <code>directDistance</code>, <code>horizontalDistance</code>, and <code>verticalDistance</code> from the 
  <code>MeasurementChangedEvent</code> returned by the callback. The distance objects contain both the scalar value 
  and unit of measurement.</li>
</ol>

<h2>Features</h2>  
<ul>
<li>AnalysisOverlay</li>
<li>LocationDistanceMeasurement</li>
<li>UnitSystem</li>
</ul>

<h2>Additional information</h2>
<p>The <code>LocationDistanceMeasurement</code> analysis only performs planar distance calculations. This may not be 
appropriate for large distances where the Earth's curvature needs to be taken into account.</p>

<h2>Tags</h2>
<ul>
<li>Analysis</li>
<li>3D</li>
</ul>