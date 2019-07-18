# Integrated windows authentication

Use Windows credentials to access services hosted on a portal secured with Integrated Windows Authentication (IWA).

![](IntegratedWindowsAuthentication.png)

## Use case

IWA, which is built into Microsoft Internet Information Server (IIS), allows users to easily authenticate for different services using their domain's Windows credentials. This simplifies logging in to services that are provided through an intranet environment, as only a single username and password combination are required for the applications that support IWA.

## How to use the sample

Select "Search Public Portal" to return all portal results hosted on www.arcgis.com (without requiring IWA authentication) or enter a URL to your IWA-secured portal, and select "Search IWA Secured Portal" to search for web maps stored there. If the latter was chosen, you will be prompted for a username (including domain, such as username@DOMAIN or domain\username), and password. If authentication is successful, portal item results will display in the list. Select a web map item to display it in the map view.

## How it works

1.  Set up an `AuthenticationChallengeHandler` that challenges the user for authentication, such as the custom handler created for this sample: `IWAChallengeHandler`.
2.  Set the `AuthenticationManager` to use the created challenge handler, `.setAuthenticationChallengeHandler(new IWAChallengeHandler());`.
3.  Create a new `Portal` from a portal URL and a boolean indicating if it is a secured resource, `new Portal (url, true)`.
4.  Load the portal, `Portal.loadAsync()`. This will trigger the challenge handler to prompt for a username and password. Depending on whether authentication is successful or not, access to the portal will be granted.
5.  Search the portal for the desired items, `Portal.findItemsAsync(new PortalQueryParameters("search query"))`.
6.  Use the `PortalItem`s to create a new map, `new ArcGISMap(PortalItem)`, and display them to the `MapView` as required.

## Relevant API

*   AuthenticationChallenge
*   AuthenticationChallengeHandler
*   AuthenticationChallengeResponse
*   AuthenticationManager
*   AuthenticationManager.setAuthenticationChallengeHandler
*   Portal
*   UserCredential

## About the data

Searching a public portal with this sample retrieves publicly available web maps from www.arcgis.com.

## Additional information

More information about IWA and its use with ArcGIS can be found at the following links:

*   [Use Integrated Windows Authentication with your portal](http://enterprise.arcgis.com/en/portal/latest/administer/windows/use-integrated-windows-authentication-with-your-portal.htm)
*   [IWA - Wikipedia](https://en.wikipedia.org/wiki/Integrated_Windows_Authentication)

## Tags

authentication, IWA, login, portal, security, sign-in