<h1>Display KML Network Links</h1>

<p>Display KML with a network link and show network messages as the data is automatically refreshed.</p>

<p><img src="DisplayKMLNetworkLinks.png"/></p>

<h2>How to use the sample</h2>

<p>The data shown should refresh automatically every few seconds. Network messages will be displayed in the list view as they come in.</p>

<h2>How it works</h2>

<p>To show KML with network links and display network messages:</p>

<ol>
    <li>Create a <code>KmlNetworkDataset</code> from a KML source which has network links.</li>
    <li>Construct a <code>KmlLayer</code> with the dataset and add the layer as an operational layer.</li>
    <li>To listen for network messages, add a <code>KmlNetworkLinkMessageReceivedListener</code> on the dataset.</li>
</ol>

<h2>Relevant API</h2>

<ul>
<li>KmlDataset</li>
<li>KmlLayer</li>
<li>KmlNetworkLinkMessageReceivedEvent</li>
</ul>

<h2>About the data</h2>

<p>This map shows the current air traffic in parts of Europe with heading, altitude, and ground speed. Additionally, noise levels from ground monitoring stations are shown.</p>

<h2>Tags</h2>

<p>KML, KMZ, OGC, Keyhole, Network Link, Network Link Control</p>