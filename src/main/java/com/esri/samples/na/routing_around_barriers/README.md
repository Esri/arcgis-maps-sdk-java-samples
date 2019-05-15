# Routing around barriers

Generate a route among multiple stops taking into account different barriers.

[](RoutingAroundBarriers.png)

## Use case

When determining a route between several stops, it is important to be able to take into account barriers of which the routing service may not be aware. In some situations, it is further beneficial to find the most efficient route between a larger amount of stops, reordering them to reduce travel time. For example, a delivery service may target a number of drop-off addresses, specifically looking to avoid congested areas or blocked roads (e.g. roadworks), and arranging the stops in the most time-effective order. 

## How to use the sample

Click on the map to add any number of stops. Then select the Barriers ToggleButton and click on the map to add buffered polygons as barriers.
 The last stop/barrier can be removed using secondary-click when the appropriate ToggleButton is selected. Check the 'Find best sequence' CheckBox to enable targeting stops at the optimum order, and tick either 'Preserve first stop' and/or 'Preserve last stop', if these are desired. Now click on the Determine Route button to generate a route connecting all the stops, avoiding barriers. Once the route is computed details about the route, like route length and the travel time are displayed. To view the directions, expand the Route Directions accordion menu. 
 
 (You can select each direction to highlight it on the map.)
 
 Click the reset button to remove all stops, barriers and routes and start over.
 
## How it works

1. Create a `RouteTask` using a URL for an online routing service, and create the default set of parameters: `routeTask.createDefaultParametersAsync()`.
1. Enable the properties to return the Stops and Directions on completion of the routing task: `routeParameters.setReturnStops(true)` and `routeParameters.setReturnDirections(true)`. This is done to later retrieve the list of `DirectionManeuver`s and display the route directions.
1. Create `Stop`s and `PolygonBarrier`s, optionally grouping them in `ArrayList<>`s for ease of use.
1. Add the list of stops and list of barriers to the route parameters: `routeParameters.setStops(stopsList)` and `routeParameters.setPolygonBarriers(barriersList)`.
    * Optional: enable further options in the route parameters, such as finding the best sequence (`routeParameters.setFindBestSequence(true)`), or preserving the first (`routeParameters.setPreserveFirstStop(true)`) or last stop (`routeParameters.setPreserveLastStop(true)`).
1. Solve the routing task: `routeTask.solveRouteAsync(routeParameters)`.
1. Retrieve the `Route` from the task result: `routeResult.getRoutes().get(0)` and get the route geometry: `.getRouteGeometry()`.
1. Construct a `Graphic` using the route geometry, and add it to a `GraphicsOverlay` to display the route on a map.

## Relevant API

   - DirectionManeuver
   - PolygonBarrier
   - Route
   - RouteParameters
   - RouteResult
   - RouteTask
   - Stop

## Tags

barrier, directions, network analysis, routing