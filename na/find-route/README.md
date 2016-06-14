#Find Route#
This sample demonstrates how to get a route between two `Stop`s locations.

##How to use the sample##
For simplicity, the sample comes loaded with a start and end stop. You can click on the Find route button to get a route between these stops. Once the route is generated, the `DirectionMessage`s are shown step by step directions in the directions list.

![](FindRoute.png)

##How it works##
To display a route using a RouteTask:

- Create an ArcGISMap 
- Add the map to the view via `MapView` via `MapView#setMap()`. 
- Create a `RouteTask` using an URL from an online service. Online route task solves a route from an online route service.
- Set the `RouteParameters` to find a route between the Stops. 
- Get the `Route` using the  `RouteTask#solveAsync(routeParameters)`.
- Display the route by adding it to a `GraphicsOverlay`.
- Show the step by step `DirectionMessage`s using the `Route#getDirectionManeuvers()` method.

##Features##
- ArcGISMap
- MapView
- GraphicsOverlay
- Route
- RouteTask
- RouteParameters
- RouteResult
- DirectionManeuver
- DirectionMessage
