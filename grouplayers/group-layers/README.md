<h1>Group layers</h1>

<p>Group a collection of layers together and toggle their visibility as a group.</p>

<p><img src="GroupLayers.png"/></p>

<h2>Use case</h2>

<p>Group layers communicate to the user that layers are related and can be managed together.</p>

<p>In a land development project, you might group layers according to the phase of development.</p>

<h2>How to use the sample</h2>

<p>The layers in the map will be displayed in a table of contents. Toggle the checkbox next to a layer's name to change its visibility.</p>

<h2>How it works</h2>

<ol>
<li>Create an empty <code>GroupLayer</code>.</li>

<li>Add a child layer to the group layer's layers collection.</li>

<li>To toggle the visibility of the group, simply change the group layer's visibility property.</li>
</ol>

<h2>Relevant API</h2>

<ul>
<li>GroupLayer</li>
</ul>

<h2>Additional information</h2>

<p>The full extent of a group layer may change when child layers are added/removed. Group layers do not have a spatial reference, but the full extent will have the spatial reference of the first child layer.</p>

<p>Group layers can be saved to web scenes. In web maps, group layers will be flattened in the web map's operational layers.</p>

<p>The implementation shown here makes use of a custom tree cell for displaying layers in a tree view. In the custom 
tree cell, toggling the checkbox of the group layer cell does not also toggle the child cell's checkbox. If you 
desire this behavior, use or extend JavaFX's <code>CheckBoxTreeCell</code> and <code>CheckBoxTreeItem</code>.

<h2 id="tags">Tags</h2>

<p>Layers, group layer</p>