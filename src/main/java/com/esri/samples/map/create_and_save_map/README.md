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
  <li>Add the configuration to the <code>AuthenticationMangager</code></li>
  <li>Create a <code>Portal</code> and load it. Use a custom <code>AuthenticationChallengeHandler</code> to 
  authenticate with your username and password</li>
  <li>Once authenticated, save the map by calling <code>map.saveMapAsAsync()</code>, passing in the title, tags, 
  description, and portal folder</li>
</ol>

<h2>Authentication</h2>
This sample uses the <a href="https://developers.arcgis.com/authentication/#named-user-login">named user login</a> 
authentication pattern.  As a developer, you will need the following to make use of this pattern:  

<h3>Your apps <b>Client ID</b></h3>
Login to your <a href="http://developers.arcgis.com">ArcGIS Developers site</a> account and <a href="https://developers.arcgis.com/applications/#/new/">Register</a>
 your app. Once registered, select the <b>Authentication</b> tab taking note of your <b>Client ID</b>.

<h3>A custom <b>Redirect URI</b></h3>

While still under the <b>Authentication</b> tab in your account, navigate down the page to the <b>Redirect URIs</b> 
section to setup the redirect URI. 

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
