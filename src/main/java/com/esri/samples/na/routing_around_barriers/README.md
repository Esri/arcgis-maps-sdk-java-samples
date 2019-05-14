# Routing around barriers

Generate a route among multiple stops taking into account different barriers.

[](RoutingAroundBarriers.png)

## Use case

## How to use the sample

Click on the map to add any number of stops. Then select the Barriers ToggleButton and click on the map to add buffered polygons as barriers.
 
 The last stop/barrier can be removed by selecting the appropriate ToggleButton and secondary-clicking.
 
 Check the 'Find best sequence' CheckBox to enable targeting stops at the optimum order, and tick either 'Preserve first stop' and/or 'Preserve last stop' if these are desired. Now click on the Determine Route button to generate a route connecting all the stops you added.

Once the route is computed, you should see details about the route, like route length and the travel time.

To view the directions, expand the Route Directions accordion menu. 
 
 You can select each direction to highlight it on the map. If you want to delete a route tap on the trash icon next to the route details. And if you want to delete either stops or barriers, select the right one on the switch and tap on the trash icon in the toolbar.

## How it works

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