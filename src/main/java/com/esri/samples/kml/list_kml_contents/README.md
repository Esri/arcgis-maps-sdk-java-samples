<h1>List KML Contents</h1>

<p>Show KML nodes in their nested hierarchy.</p>

<p><img src="ListKMLContents.png"/></p>

<h2>How to use the sample</h2>

<p>When the scene and KML layer loads, the KML node tree will be shown in the tree view. Click on a node to zoom to its extent (if it has one).</p>

<h2>How it works</h2>

<p>To list the nodes in a KML file:</p>

<ol>
  <li>Create a <code>KmlDataset</code> pointing to the KML file.</li>
  <li>Start with a list of the rood nodes with <code>kmlDataset.getRootNodes()</code>.</li>
  <li>For each node, check if it is a <code>KmlContainer</code> or <code>KmlNetworkLink</code>. These types can have child nodes. If it is one of these, cast to the appropriate type and call <code>getChildNodes()</code>.</li>
  <li>Recursively search these child nodes for more nodes.</li>
</ol>

<h2>Relevant API</h2>

<ul>
  <li>KmlContainer</li>
  <li>KmlDataset</li>
  <li>KmlDocument</li>
  <li>KmlFolder</li>
  <li>KmlGroundOverlay</li>
  <li>KmlLayer</li>
  <li>KmlNetworkLink</li>
  <li>KmlNode</li>
  <li>KmlPlacemark</li>
  <li>KmlScreenOverlay</li>
</ul>

<h2>About the data</h2>

<p>This is an example KML file meant to demonstrate how Runtime supports several common features.</p>

<h2>Tags</h2>

<p>KML, KMZ, OGC, Keyhole</p>
