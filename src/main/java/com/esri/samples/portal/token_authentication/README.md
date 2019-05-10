# Token authentication

This sample demonstrates how to access a map service that is secured with ArcGIS token-based authentication.

![](TokenAuthentication.png)

## Use case

Applications often require accessing data from private map services on secured remote servers. A token authentication system can be used to allow app users who hold a valid username and password to access the remote service. 

## How to use the sample

When starting the sample, the user is presented with an authentication dialog for the ArcGIS Online map server. Upon successful authentication, access to the map server is granted and the data is displayed in the map view.

## How it works

1. Create an `AuthenticationChallengeHandler`, and use this to set the handler for the `AuthenticationManager`: `.setAuthenticationChallengeHandler(authenticationChallengeHandler)`.

2. Create a `Portal` to ArcGIS Online. Use the `Portal` and the id of a protected map service to create a new `PortalItem`

3. Create a new `ArcGISMap` with the `PortalItem`, and display the map in the `MapView`.

## Relevant API

- Portal
- PortalItem
- AuthenticationChallengeHandler
- AuthenticationManager

## Tags

authentication, map service, token