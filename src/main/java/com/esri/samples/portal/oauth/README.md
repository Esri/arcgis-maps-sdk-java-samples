<h1>Authenticate with OAuth</h1>

<p>Use OAuth2 to authenticate with ArcGIS Online (or your own portal) to access secured resources.</p>

<p><img src="OAuthSample.png"/></p>

<h2>Use case</h2>

<p>Your app may need to access items that are only shared with authorized users. For example, your organization may host private data layers or feature services that are only accessible to verified users. You may also need to take advantage of premium ArcGIS Online services, such as geocoding or routing services, which require a named user login.</p>

<h2>How to use the sample</h2>

<p>When you run the sample, the app attempt to will load a web map which contains premium content. You will be challenged for an ArcGIS Online login to view the private layers. Enter a user name and password for an ArcGIS Online named user account (such as your ArcGIS for Developers account). If you authenticate successfully, web map will display.</p>

<h2>How it works</h2>

<ol>
   <li> Set the `AuthenticationManager`'s `AuthenticationChallengeHandler` to the `DefaultAuthenticationChallengeHandler`.</li> 
    <li> Create an `OAuthConfiguration` specifying the portal URL, client ID, and redirect URL.</li> 
    <li> Add the OAuth configuration to the authentication manager.</li> 
    <li> Load a map with premium content requiring authentication to automatically invoke the default authentication challenge handler.</li> 
</ol>

<h2>Relevant API</h2>

<ul>
  <li>AuthenticationManager</li>
  <li>AuthenticationChallengeHandler</li>
  <li>OAuthConfiguration</li>
  <li>PortalItem</li>
</ul>

<h2>Additional information</h2>

<p>The workflow presented in this sample works for all SAML based enterprise (IWA, PKI, Okta, etc.) and social (Facebook, Google, etc.) identify providers for ArcGIS Online or Portal. More information on enterprise logins can be found <a href="https://doc.arcgis.com/en/arcgis-online/administer/enterprise-logins.htm">here</a>.</p>

<p>For additional information on using OAuth in your app, see the <a href="https://developers.arcgis.com/documentation/core-concepts/security-and-authentication/mobile-and-native-user-logins/">Mobile and Native Named User Login</a> topic in our guide. To setup and access your own app using this sample (using your own ArcGIS portal), see the wiki for details.</p>

<h2>Tags</h2>

<p>authentication, cloud, credential, portal, security</p>