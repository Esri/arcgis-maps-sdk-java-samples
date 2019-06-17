<h1>OAuth</h1>

<p>Authenticate with OAuth 2.0 to retrieve the user's profile information.</p> 

<p><img src="OAuth.png"/></p>

<h2>How to use the sample</h2>

<p>This sample requires you to setup your own app on arcgis.com. See the <a href="https://github.com/Esri/arcgis-runtime-samples-java/wiki/OAuth">wiki</a> for details.</p>

<p>Enter the details of the application registered on arcgis.com and click sign-in.
This will open a sign-in dialog. After the credentials are entered correctly, the sample
will receive an authorization code from the ArcGIS platform. This authorization code will then be used to obtain an
access token. This access token is used later to access user's profile.</p>

<h2>How it works</h2>

<ol>
    <li>Setup an <code>OAuthConfiguration</code> with the settings of an application registered in the ArcGIS platform.</li>
    <li>Setup an <code>AuthenticationChallengeHandler</code> that challenges the user for authentication. You could
    create a custom handler similar to the one created in this sample: <code>OAuthChallengeHandler</code>.</li>
    <li>On trying to access a secured resource, the authentication challenge in invoked.</li>
    <li><code>OAuthChallengeHandler</code> directs the user to a sign-in page (using a <code>WebView</code>) from ArcGIS platform.</li>
    <li>On successful sign-in, the ArcGIS platform provides an authorization code.</li>
    <li>Use the authorization code to create a <code>OAuthTokenCredentialRequest</code>. This will be used by the Runtime
    to request an access token. The access token is then used to request a secured resource in the ArcGIS platform.</li>
</ol>

<h2>Relevant API</h2>

<ul>
    <li>AuthenticationChallengeHandler</li>
    <li>OAuthConfiguration</li>
    <li>OAuthTokenCredential</li>
    <li>OAuthTokenCredentialRequest</li>
    <li>Portal</li>
    <li>PortalUser</li>
</ul>

<h2>Additional Information</h2>

<p>The JavaFX <code>WebEngine</code> used in this sample may not support rendering of some modern web elements returned by the <code>AuthorizationURL</code>. For this reason, we append <code>&display=classic</code> to the authorization URL, to ensure it renders properly.</p>