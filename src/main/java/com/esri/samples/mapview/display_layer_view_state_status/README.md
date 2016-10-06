<h1>Display Layer View State Status</h1>

<p>Demonstrates how to get view status for layers in a ArcGISMap. </p>

<h2>How to use the sample</h2>

<p>The view status of a layer changes while the layer is loading, like the start of the application. If you pan or zoom the map, the view status of some layers should also change.
    The LayerViewStatus could be:
    - ACTIVE
    - ERROR
    - LOADING
    - NOT<em>VISIBLE
        - OUT</em>OF_SCALE
    - UNKNOWN</p>

<p><img src="DisplayLayerViewStateStatus.png" alt="" title="" /></p>

<h2>How it works</h2>

<p>To use the <code>MapView</code>'s <code>DrawStatus</code>:</p>

<ol>
    <li>Create an <code>ArcGISMap</code>. </li>
    <li>Set the map to the <code>MapView</code>, <code>MapView.setMap()</code>. </li>
    <li>Add the <code>MapView.addLayerViewStateChangedListener()</code> property and listen when the <code>Layer.getLayerViewStatus()</code> changes.</li>
</ol>

<h2>Features</h2>

<ul>
    <li>ArcGISMap</li>
    <li>ArcGISMapImageLayer</li>
    <li>Layer</li>
    <li>LayerViewStatus </li>
    <li>LayerViewStateChangedEvent</li>
    <li>MapView</li>
    <li>Viewpoint</li>
</ul>
