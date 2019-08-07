# Integrated windows authentication

Use Windows credentials to access services hosted on a portal secured with Integrated Windows Authentication (IWA).

![](IntegratedWindowsAuthentication.png)

## Use case

IWA, which is built into Microsoft Internet Information Server (IIS), allows users to easily authenticate for different services using their domain's Windows credentials. This simplifies logging in to services that are provided through an intranet environment, as only a single username and password combination are required for the applications that support IWA.

## How to use the sample

Enter a URL to your IWA-secured portal into the URL field and select "Search IWA Secured Portal" to search for web maps stored there. Authenticate with a username (including domain, such as username@DOMAIN or domain\username), and password. If authentication is successful, portal item results will display in the list. Select a web map item to display it in the map view.

## How it works

1. Set up an `AuthenticationChallengeHandler` that challenges the user for authentication, such as the custom handler created for this sample: `IWAChallengeHandler`.
2. Set the `AuthenticationManager` to use the created challenge handlerusing `.setAuthenticationChallengeHandler(new IWAChallengeHandler());`.
3. Create a new `Portal` from a portal URL and a boolean indicating if it is a secured resource using `new Portal(url, true)`.
4. Load the portal. The authentication challenge handler will prompt the user for credentials. The portal will load successfully with valid credentials.
5. Create `PortalQueryParameters` to define a search query for map items in the portal.
5. Find web map portal items using `Portal.findItemsAsync(portalQueryParameters)`.
6. Create a map using a `PortalItem` result with `new ArcGISMap(portalItem)`.

## Relevant API

* AuthenticationChallenge
* AuthenticationChallengeHandler
* AuthenticationChallengeResponse
* AuthenticationManager
* Portal
* UserCredential

## About the data

Searching a public portal with this sample retrieves publicly available web maps from www.arcgis.com.

## Additional information

More information about IWA and its use with ArcGIS can be found at the following links:

* [Use Integrated Windows Authentication with your portal](http://enterprise.arcgis.com/en/portal/latest/administer/windows/use-integrated-windows-authentication-with-your-portal.htm)
* [IWA - Wikipedia](https://en.wikipedia.org/wiki/Integrated_Windows_Authentication)

## Tags

authentication, IWA, login, portal, security, sign-in