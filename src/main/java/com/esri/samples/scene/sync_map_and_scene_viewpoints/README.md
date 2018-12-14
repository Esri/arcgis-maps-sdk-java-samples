<h1>Sync Map and Scene Viewpoints</h1>

<p>Synchronize the viewpoints between a <code>MapView</code> and a <code>SceneView</code>.</p>

<p>The two <code>GeoViews</code> share a common <code>ViewPoint</code>. When navigating in one view, the other view is immediately updated to display the same <code>ViewPoint</code>.</p>

<p><img src="SyncMapAndSceneViewpoints.png" /></p>

<h2>How to use the sample</h2>

<p>Interact with the map by panning, zooming or rotating the map or scene view. The other view will update automatically to match your navigation. Note that the resulting maps may not look identical due to the fact the <code>MapView</code> is 2D and the <code>SceneView</code> is 3D: but the centers and scales of each view will be kept the same.</p>

<h2>How it works</h2>

<p><code>MapView</code> and <code>SceneView</code> inherit from the <code>GeoView</code> parent class. When the <code>GeoView</code>'s viewpoint has changed, a listener can be added and allow the viewpoint of the other <code>GeoView</code> to be set and synchronized.


<ol>
<li>Create a <code>MapView</code> and a <code>SceneView</code>.</li>
<li>Add a viewpoint changed listener to each <code>GeoView</code> with <code>geoView.addViewpointChangedListener(viewpointChangedEvent)</code>.</li>
<li>Check if the <code>GeoView</code> is being navigated with <code>geoView.isNavigating()</code>.</li>
<li>Get the current viewpoint of the active <code>GeoView</code> with <code>geoView.getcurrentViewpoint(Viewpoint.Type.CENTER_AND_SCALE)</code>.</li>
<li>Set the viewpoint of the other view to the active view's viewpoint with <code>geoView.setViewpoint(Viewpoint)</code>.</li>
</ol>

<h2>Relevant API</h2>

<ul>
<li>GeoView</li>

<li>Viewpoint</li>

<li>ViewpointChangedEvent</li>

</ul>

<h2>Tags</h2>

<p>2D, 3D, view synchronisation, Viewpoint, Scene, Map</p>

