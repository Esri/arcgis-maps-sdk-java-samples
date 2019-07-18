# Authenticate with OAuth

Use OAuth2 to authenticate with ArcGIS Online (or your own portal) to access secured resources.

![](OAuthSample.png)

## Use case

Your app may need to access items that are only shared with authorized users. For example, your organization may host private data layers or feature services that are only accessible to verified users. You may also need to take advantage of premium ArcGIS Online services, such as geocoding or routing services, which require a named user login.

## How to use the sample

When you run the sample, the app attempt to will load a web map which contains premium content. You will be challenged for an ArcGIS Online login to view the private layers. Enter a user name and password for an ArcGIS Online named user account (such as your ArcGIS for Developers account). If you authenticate successfully, web map will display.

## How it works

1.  Set the `AuthenticationManager`'s `AuthenticationChallengeHandler` to the `DefaultAuthenticationChallengeHandler`.
2.  Create an `OAuthConfiguration` specifying the portal URL, client ID, and redirect URL.
3.  Add the OAuth configuration to the authentication manager.
4.  Load a map with premium content requiring authentication to automatically invoke the default authentication challenge handler.

## Relevant API

*   AuthenticationManager
*   AuthenticationChallengeHandler
*   OAuthConfiguration
*   PortalItem

## Additional information

The workflow presented in this sample works for all SAML based enterprise (IWA, PKI, Okta, etc.) and social (Facebook, Google, etc.) identify providers for ArcGIS Online or Portal. More information on enterprise logins can be found [here](https://doc.arcgis.com/en/arcgis-online/administer/enterprise-logins.htm).

For additional information on using OAuth in your app, see the [Mobile and Native Named User Login](https://developers.arcgis.com/documentation/core-concepts/security-and-authentication/mobile-and-native-user-logins/) topic in our guide. To setup and access your own app using this sample (using your own ArcGIS portal), see the wiki for details.

## Tags

authentication, cloud, credential, portal, security