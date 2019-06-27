<h1>Display Layer View State</h1>

<p>Determine if a layer is currently visible.</p>

<p><img src="DisplayLayerViewState.png"/></p>

<h2>How to use the sample</h2>

<p>The view state of a layer changes while the layer is loading, like the start of the application. If you pan or zoom the map, the view state of some layers should also change.
    The LayerViewStatus could be:
    <ul>
    <li>ACTIVE</li>
    <li>ERROR</li>
    <li>LOADING</li>
    <li>NOT_VISIBLE</li>
    <li>OUT_OF_SCALE</li>
    <li>UNKNOWN</li>
    </ul>
    </p>

<h2>How it works</h2>

<p>To get a layer's view state:</p>

<ol>
    <li>Create an <code>ArcGISMap</code>. </li>
    <li>Set the map to the <code>MapView</code>, <code>MapView.setMap()</code>. </li>
    <li>Add the <code>MapView.addLayerViewStateChangedListener()</code> property and listen when the <code>Layer.getLayerViewStatus()</code> changes.</li>
</ol>

<h2>Relevant API</h2>

<ul>
    <li>ArcGISMap</li>
    <li>ArcGISMapImageLayer</li>
    <li>Layer</li>
    <li>LayerViewStatus </li>
    <li>LayerViewStateChangedEvent</li>
    <li>MapView</li>
    <li>Viewpoint</li>
</ul>
