<h1>Create and Save Map</h1>

<p>Create and save a map to your own portal.</p>

<p><img src="CreateAndSaveMap.png"/></p>

<h2>How to use the sample</h2>

<p>This sample requires you to setup your own app on arcgis.com. See the <a href="https://github.com/Esri/arcgis-runtime-samples-java/wiki/OAuth">wiki</a> for details.</p>

<p>Fill in your portal and registered app credentials in the starting dialog to authenticate. Then, choose 
the basemap and layers for your new map. To save the map, choose a title, tags and description (optional), and a folder 
on your portal (you will need to create one in your portal's My Content). Click the Save button to save the map to the 
chosen folder.</p>

<h2>How it works</h2>

<p>To create and save a map to your portal for use in an app:</p>
<ol>
  <li>Create an <code>ArcGISMap</code> with a <code>Basemap</code> and operational layers</li>
  <li>Create an <code>OAuthConfiguration</code> with your portal and app credential.</li>
  <li>Add the configuration to the <code>AuthenticationMangager</code></li>
  <li>Create a <code>Portal</code> and load it. Use a custom <code>AuthenticationChallengeHandler</code> to 
  authenticate with your username and password</li>
  <li>Once authenticated, save the map by calling <code>map.saveMapAsAsync()</code>, passing in the title, tags, 
  description, and portal folder</li>
</ol>

<h2>Relevant API</h2>

<ul>
  <li>AuthenticationChallengeHandler</li>
  <li>ArcGISMap</li>
  <li>ArcGISMapImageLayer</li>
  <li>Basemap</li>
  <li>MapView</li>
  <li>OAuthConfiguration</li>
  <li>Portal</li>
  <li>PortalFolder</li>
  <li>PortalItem</li>
  <li>PortalUserContent</li>
</ul>


<h2>Additional information</h2>

<p>The JavaFX <code>WebEngine</code> used in the `OAuthChallengeHandler` in this sample may not support rendering of some modern web elements returned by the <code>AuthorizationURL</code>. For this reason, we append <code>&display=classic</code> to the authorization URL, to ensure it renders properly.</p>