<h1>Token authentication</h1>

<p>access a map service that is secured with ArcGIS token-based authentication</p>

<p><img src="TokenAuthentication.png"/></p>

<h2>Use case</h2>

<p>Applications often require accessing data from private map services on remote servers. A token authentication system can be used to allow app users who hold a valid username and password to access the remote service. </p>

<h2>How to use the sample</h2>

<p>When starting the sample, the user is presented with an authentication dialog for the ArcGIS Online map server. Upon successful authentication, access to the map server is granted and the data is displayed in the map view.</p>

<h2>How it works</h2>

<ol>
  <li>Create an <code>AuthenticationChallengeHandler</code> using the <code>DefaultAuthenticationChallengeHandler</code> to handle the challenges sent by the protected map service.</li>
  <li>Set the <code>AuthenticationChallengeHandler</code> used by the <code>AuthenticationManager</code>.</li>
  <li>Create a <code>Portal</code> to ArcGIS Online.</li>
  <li>Create a <code>PortalItem</code> for the protected web map using the <code>Portal</code> and Item ID of the protected map service.</li>
  <li>Create a map to display in the <code>MapView</code> using the <code>PortalItem</code>.</li>
  <li>Set the map to display in the <code>MapView</code>.</li>
</ol>

<h2>Relevant API</h2>

<ul>
  <li>AuthenticationManager</li>
  <li>AuthenticationChallengeHandler</li>
  <li>DefaultAuthenticationChallengeHandler</li>
  <li>Portal</li>
  <li>PortalItem</li>
  <li>Map</li>
  <li>MapView</li>
</ul>

<h2>Tags</h2>

<p>authentication, map service, security, token</p>