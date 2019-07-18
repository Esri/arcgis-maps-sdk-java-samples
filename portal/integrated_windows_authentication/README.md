<h1 id="integratedwindowsauthentication">Integrated windows authentication</h1>

<p>Use Windows credentials to access services hosted on a portal secured with Integrated Windows Authentication (IWA).</p>

<p><img src="IntegratedWindowsAuthentication.png" alt="" /></p>

<h2>Use case</h2>

<p>IWA, which is built into Microsoft Internet Information Server (IIS), allows users to easily authenticate for different services using their domain's Windows credentials. This simplifies logging in to services that are provided through an intranet environment, as only a single username and password combination are required for the applications that support IWA.</p>

<h2>How to use the sample</h2>

<p>Select "Search Public Portal" to return all portal results hosted on www.arcgis.com (without requiring IWA authentication) or enter a URL to your IWA-secured portal, and select "Search IWA Secured Portal" to search for web maps stored there. If the latter was chosen, you will be prompted for a username (including domain, such as username@DOMAIN or domain\username), and password. If authentication is successful, portal item results will display in the list. Select a web map item to display it in the map view.</p>

<h2>How it works</h2>

<ol>
  <li>Set up an <code>AuthenticationChallengeHandler</code> that challenges the user for authentication, such as the custom handler created for this sample: <code>IWAChallengeHandler</code>.</li>
  <li>Set the <code>AuthenticationManager</code> to use the created challenge handler, <code>.setAuthenticationChallengeHandler(new IWAChallengeHandler());</code>.</li>
  <li>Create a new <code>Portal</code> from a portal URL and a boolean indicating if it is a secured resource, <code>new Portal (url, true)</code>.</li>
  <li>Load the portal, <code>Portal.loadAsync()</code>. This will trigger the challenge handler to prompt for a username and password. Depending on whether authentication is successful or not, access to the portal will be granted.</li>
  <li>Search the portal for the desired items, <code>Portal.findItemsAsync(new PortalQueryParameters("search query"))</code>.</li>
  <li>Use the <code>PortalItem</code>s to create a new map, <code>new ArcGISMap(PortalItem)</code>, and display them to the <code>MapView</code> as required.</li>
</ol>

<h2>Relevant API</h2>

<ul>
  <li>AuthenticationChallenge</li>
  <li>AuthenticationChallengeHandler</li>
  <li>AuthenticationChallengeResponse</li>
  <li>AuthenticationManager</li>
  <li>AuthenticationManager.setAuthenticationChallengeHandler</li>
  <li>Portal</li>
  <li>UserCredential</li>
</ul>

<h2>About the data</h2>

<p>Searching a public portal with this sample retrieves publicly available web maps from www.arcgis.com.</p>

<h2>Additional information</h2>

<p>More information about IWA and its use with ArcGIS can be found at the following links:</p>

<ul>
  <li><a href="http://enterprise.arcgis.com/en/portal/latest/administer/windows/use-integrated-windows-authentication-with-your-portal.htm">Use Integrated Windows Authentication with your portal</a></li>
  <li><a href="https://en.wikipedia.org/wiki/Integrated_Windows_Authentication">IWA - Wikipedia</a></li>
</ul>

<h2>Tags</h2>

<p>authentication, IWA, login, portal, security, sign-in</p>