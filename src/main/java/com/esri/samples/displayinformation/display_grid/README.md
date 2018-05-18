<h1>Display Grid</h1>

<p>Demonstrates how to display and style different types of grids over a map.</p>

<p><img src="DisplayGrid.png"/></p>

<h2>How to use the sample</h2>

<p>Select the different grid style options and click "Update" to set the grid.</p>

<h2>How it works</h2>

<p>To show and style a grid:</p>

<ol>
  <li>Create an instance of one of the <code>Grid</code> types.</li>
  <li>Grid lines and labels can be styled per grid level with <code>grid.setLineSymbol(gridLevel, lineSymbol)</code> 
  and <code>grid.setTextSymbol(gridLevel, textSymbol)</code>.</li>
  <li>The label position can be set with <code>grid.setLabelPosition(labelPosition)</code>.</li>
  <li>For the <code>LatitudeLongitudeGrid</code> type, you can specify a label format of <code>DECIMAL_DEGREES</code>
   or <code>DEGREES_MINUTES_SECONDS</code>.</li>
   <li>To set the grid, use <code>mapView.setGrid(grid)</code>.</li>
</ol>

<h2>Features</h2>

<ul>
  <li>Grid</li>
  <li>LatitudeLongitudeGrid</li>
  <li>LineSymbol</li>
  <li>MapView</li>
  <li>MgrsGrid</li>
  <li>SimpleLineSymbol</li>
  <li>TextSymbol</li>
  <li>UsngGrid</li>
  <li>UtmGrid</li>
</ul>
