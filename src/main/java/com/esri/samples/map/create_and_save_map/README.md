<h1>Create and Save Map</h1>

<p>How to create and save a map to your own portal.</p>

<p><img src="CreateAndSaveMap.png"/></p>

<h2>How to use the sample</h2>

Fill in your portal and registered app credentials of the starting dialog to authenticate. Then choose 
the basemap and layers for your new map. To save the map, choose a title, tags and description (optional), and a folder 
on your portal. Click the Save button to save the map to the chosen folder.

<h2>How it works</h2>

<p>To create and save a map to your portal for use in an app:</p>
<ol>
  <li>Create an <ccde>ArcGISMap</code> with a <code>Basemap</code> and operational layers</li>
  <li>Create an <code>OAuthConfiguration</code> with your portal and app credential.</li>
  <li>Add the configuration to the <code>AuthenticationMangager</li>
  <li>Create a <code>Portal</code> and load it. Use a custom <code>AuthenticationChallengeHandler</li> to 
  authenticate with your username and password</li>
  <li>Once authenticated, save the map by calling <code>map.saveMapAsAsync()</code>, passing in the title, tags, 
  description, and portal folder</li>
</ol>

<h2>Features</h2>

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
