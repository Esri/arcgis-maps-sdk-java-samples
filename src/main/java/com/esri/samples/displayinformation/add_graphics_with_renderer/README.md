<h1>Add Graphics with Renderer</h1>

<p>Demonstrates how to add Graphics to a GraphicsOverlay and display those Graphics to a MapView using a UniqueValueRenderer. The UniqueValueRenderer uses the attribute value of a Graphic to assign a specific symbol to a specific graphic. </p>

<p><img src="AddGraphicsWithRenderer.png"/></p>

<h2>How it works</h2>

<p>To add Graphics to a GraphicsOverlay using a UniqueValueRenderer:</p>

<ol>
  <li>Create a <code>GraphicsOverlay</code> and attach it to the <code>MapView</code>.</li>
  <li>Create a <code>Graphic</code> for each bird location.
    <ul><li>each graphic will have a <code>Point</code> with a x,y-coordinate location to display a symbol</li>
      <li>each graphic will have an attribute with a key SEABIRD and a value of the bird's name</li></ul></li>
  <li>Add all graphics to the graphics overlay, <code>GraphicsOverlay.getGraphics().add(graphic)</code>.</li>
  <li>Create an <code>UniqueValueRenderer</code>.
    <ul><li>set field names to SEABIRD <code>UniqueValRenderer.getFieldNames().add("SEABIRD")</code>, this will tell the renderer which key to look for in the graphic's attributes</li></ul></li>
  <li>Create a <code>SimpleMarkerSymbol</code> for each kind of bird.
    <ul><li>each symbol will have a style, color, and size </li></ul></li>
  <li>Create a <code>UniqueValue</code> for each kind of bird.
    <ul><li>each unqiue value will have a label, description, symbol, and list of objects</li>
      <li>the list of object will hold the kind of bird to link the symbol to</li></ul></li>
  <li>Add unique values to the unique value renderer, <code>UniqueValRenderer.getUniqueValues().add(unique value)</code>.</li>
  <li>Set unique value renderer to graphics overlay, <code>GraphicsOverlay.setRenderer(unique value renderer)</code>.</li>
</ol>

<h2>Features</h2>

<ul>
  <li>ArcGISMap</li>
  <li>Graphic</li>
  <li>GraphicsOverlay</li>
  <li>MapView</li>
  <li>Point</li>
  <li>SimpleMarkerSymbol</li>
  <li>UniqueValue</li>
  <li>UniqueValueRenderer</li>
</ul>
