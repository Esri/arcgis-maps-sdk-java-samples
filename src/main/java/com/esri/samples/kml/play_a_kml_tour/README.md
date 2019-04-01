<h1>Play a KML Tour</h1>

<p>Play tours in KML files.</p>

<p><img src="PlayAKMLTour.png"/></p>

<h2>Use case</h2>

<p>KML, the file format used by Google Earth, supports creating tours, which can control the viewpoint of the scene, hide and show content, and play audio. Tours allow you to easily share tours of geographic locations, which can be augmented with rich multimedia.</p>

<h2 id="howtousethesample">How to use the sample</h2>

<p>Click the play button to play the tour. Press again to pause the tour. Click the reset button to start again from 
the beginning.</p>

<h2>How it works</h2>

<ol>
<li>Create a <code>KmlDataset</code> with the path to a local KML file with a KML tour.</li>
<li>Create and load <code>KmlLayer</code> with the dataset.</li>
<li>When the layer has loaded, search its <code>KmlNode</code>s by recursing through <code>kmlLayer.getRootNodes()
</code> to find a <code>KmlTour</code> node.</li>
<li>Create a <code>KmlTourController</code> and set the tour with <code>kmlTourController.setTour(kmlTour)</code>.</li>
<li>Use <code>kmltourController.play()</code>, <code>kmltourController.pause()</code>, and <code>kmltourController.reset
()</code> to control the tour.</li>

<h2>Relevant API</h2>

<ul>
<li>KmlDataset</li>
<li>KmlTour</li>
<li>KmlTourController</li>
<li>KmlTourStatus</li>
</ul>

<h2>About the data</h2>

<p>This sample uses a custom tour of Esri's offices created by a member of the ArcGIS Runtime SDK samples team. 
Contains audio.</p>

<h2>Additional information</h2>

<p>See <a href="https://developers.google.com/kml/documentation/touring">Google's documentation</a> for information about authoring KML tours.</p>

<h2>Tags</h2>

<p>KML, tour, story, interactive, narration, play, pause, animation</p>