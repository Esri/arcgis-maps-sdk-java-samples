# OAuth

Authenticate with OAuth 2.0 to retrieve the user's profile information. 

![](OAuth.png)

## How to use the sample

This sample requires you to setup your own app on arcgis.com. See the <a href="https://github.com/Esri/arcgis-runtime-samples-java/wiki/OAuth">wiki</a> for details.

Enter the details of the application registered on arcgis.com and click sign-in.
This will open a sign-in dialog. After the credentials are entered correctly, the sample
will receive an authorization code from the ArcGIS platform. This authorization code will then be used to obtain an
access token. This access token is used later to access user's profile.

## How it works


  1. Setup an `OAuthConfiguration` with the settings of an application registered in the ArcGIS platform.
  2. Setup an `AuthenticationChallengeHandler` that challenges the user for authentication. You could
  create a custom handler similar to the one created in this sample: `OAuthChallengeHandler`.
  3. On trying to access a secured resource, the authentication challenge in invoked.
  4. `OAuthChallengeHandler` directs the user to a sign-in page (using a `WebView`) from ArcGIS platform.
  5. On successful sign-in, the ArcGIS platform provides an authorization code.
  6. Use the authorization code to create a `OAuthTokenCredentialRequest`. This will be used by the Runtime
  to request an access token. The access token is then used to request a secured resource in the ArcGIS platform.


## Relevant API


*   AuthenticationChallengeHandler
*   OAuthConfiguration
*   OAuthTokenCredential
*   OAuthTokenCredentialRequest
*   Portal
*   PortalUser


## Additional information

The JavaFX `WebEngine` used in the `OAuthChallengeHandler` in this sample may not support rendering of some modern web elements returned by the `AuthorizationURL`. For this reason, we append `&display=classic` to the authorization URL, to ensure it renders properly.