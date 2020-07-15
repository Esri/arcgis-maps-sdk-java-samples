# Routing around barriers

Find a route that reaches all stops without crossing any barriers.

![Image of routing around barriers](RoutingAroundBarriers.png)

## Use case

You can define barriers to avoid unsafe areas, for example flooded roads, when planning the most efficient route to evacuate a hurricane zone. When solving a route, barriers allow you to define portions of the road network that cannot be traversed. You could also use this functionality to plan routes when you know an area will be inaccessible due to a community activity like an organized race or a market night.

In some situations, it is further beneficial to find the most efficient route that reaches all stops, reordering them to reduce travel time. For example, a delivery service may target a number of drop-off addresses, specifically looking to avoid congested areas or closed roads, arranging the stops in the most time-effective order.

## How to use the sample

Use the "Edit Mode" toggle buttons to select whether to add Stops or Barriers to the route. The route will be solved automatically as you add stops and barriers, and information about the length of the route and directions will be shown in the controls area. Select "Find best sequence" to allow stops to be re-ordered in order to find an optimum route. Select "Preserve first stop" to preserve the first stop. Select "Preserve last stop" to preserve the last stop. You can use the "Reset" button to reset the sample.

## How it works

1. Create a `RouteTask` with the URL to a Network Analysis route service.
2. Get the default `RouteParameters` for the service, and create the desired `Stop`s and `PolygonBarrier`s.
4. Add the stops and barriers to the route's parameters, `routeParameters.setStops(routeStops)` and `routeParameters.setPolygonBarriers(routeBarriers)`.
5. Set the `ReturnStops` and `ReturnDirections` to `true`.
6. If the user will accept routes with the stops in any order, set `FindBestSequence` to `true` to find the most optimal route.
7. If the user has a definite start point, set `PreserveFirstStop` to `true`.
8. If the user has a definite final destination, set `PreserveLastStop` to `true`.
9. Call `routeTask.solveRouteAsync(routeParameters)` to get a `RouteResult`.
10. Get the first returned route by calling `routeResult.getRoutes().get(0)`.
11. Get the geometry from the route to display the route to the map.

## Relevant API

* DirectionManeuver
* PolygonBarrier
* Route
* RouteParameters
* RouteResult
* RouteTask
* Stop

## About the data

This sample uses an Esri-hosted sample street network for San Diego.

## Tags

barriers, best sequence, directions, maneuver, network analysis, routing, sequence, stop order, stops
