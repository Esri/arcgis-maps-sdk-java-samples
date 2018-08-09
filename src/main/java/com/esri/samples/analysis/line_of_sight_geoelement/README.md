<h1>Line of Sight GeoElement</h1>

<p>Shows how to display a line of sight between two (possibly moving) graphics.</p>

<p>To determine if an observer can see a target, you can show a line of sight between them. The line will be green until it is obstructed, in which case it will turn red. By using the GeoElement variant of the line of sight, the line will automatically update when either GeoElement moves.</p>

<p><img src="LineOfSightGeoElement.gif"/></p>

<h2>How to use the sample</h2>

<p>A line of sight will display between a point on the Empire State Building (observer) and a taxi (target). The taxi will drive around a block and the line of sight should automatically update. The taxi will be highlighted when it is visibile. You can change the observer height with the slider to see how it affects the target's visibility.</p>

<h2>How it works</h2>

<p>To show a line of sight between two graphics:</p>

<ol>
    <li>Create an <code>AnalysisOverlay</code> and add it to the <code>SceneView</code>'s analysis overlays collection.</li>
    <li>Create a <code>LineOfSightGeoElement</code>, passing in observer and target <code>GeoElement</code>s (feautures or graphics). Add the line of sight to the analysis overlay's analyses collection</code>.</li>
    <li>To get the target visibility when it changes, add a <code>TargetVisibilityChangedListener</code> to the line of sight. The changed event will give the <code>TargetVisibility</code>.</li>
</ol>

<h2>Relevant API</h2>

<ul>
    <li>AnalysisOverlay</li>
    <li>LineOfSightGeoElement</li>
    <li>LineOfSight.TargetVisibility</li>
</ul>

<h2>Tags</h2>

<p>Analysis</p>