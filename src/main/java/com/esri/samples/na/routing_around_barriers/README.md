<h1>Routing around barriers</h1>

<p>Find a route with multiple stops and barriers.</p>

<p><img src="RoutingAroundBarriers.png"/></p>

<h2>Use case</h2>

<p>Delivery trucks need to find efficient routes to multiple stops, taking into account barriers such as traffic accidents or blocked roads. The route may be optimized to reduce overall travel time or travel distance. If stops are added to an existing route, it may be more efficient to reorder the stops to achieve the most efficient route.</p>

<h2>How to use the sample</h2>

<p>Use the Edit Mode toggle buttons to select whether to add Stops or Barriers to the route. Click 'Determine Route' to find the route and display it. Select 'Find best sequence' to allow stops to be re-ordered in order to find an optimum route. Select 'Preserve first stop' to preserve the first stop. Select 'Preserve last stop' to preserve the last stop.</p>

<h2>How it works</h2>

<ol>
  <li>Create the route task by calling <code>RouteTask.CreateAsync(serviceUrl)</code> with the URL to a Network Analysis route service.</li>
  <li>Get the default <code>RouteParameters</code> for the service by calling <code>routeTask.CreateDefaultParametersAsync</code>.</li>
  <li>Create the desired <code>Stop</code>s and <code>PolygonBarrier</code>`s, and add a graphics for these to the graphics overlay.</li>
  <li>Add the stops and barriers to the route's parameters, <code>routeParameters.SetStops(routeStops)</code> and <code>routeParameters.SetPolygonBarriers(routeBarriers)</code>.
  <li>Set the <code>ReturnStops</code> and <code>ReturnDirections</code> to <code>true</code>.</li>
  <li>If the user will accept routes with the stops in any order, set <code>FindBestSequence</code> to <code>true</code> to find the most optimal route.</li>
  <li>If the user has a definite start point, set <code>PreserveFirstStop</code> to <code>true</code>.</li>
  <li>If the user has a definite final destination, set <code>PreserveLastStop</code> to <code>true</code>.</li>
  <li>Call <code>routeTask.SolveRouteAsync(routeParameters)</code> to get a <code>RouteResult</code>.</li>
  <li>Get the first returned route by calling <code>calculatedRoute.Routes.First()</code>.</li>
  <li>Get the geometry from the route as a polyline by accessing the <code>firstResult.RouteGeometry</code> property, and use the geometry to create a graphic display the route to the map.</li>
</ol>

<h2>Relevant API</h2>

<ul>
  <li>DirectionManeuver</li>
  <li>PolygonBarrier</li>
  <li>Route</li>
  <li>RouteParameters</li>
  <li>RouteResult</li>
  <li>RouteTask</li>
  <li>Stop</li>
</ul>

<h2>About the data</h2>

<p>This sample uses an Esri-hosted sample street network for San Diego.</p>

<h2>Tags</h2>

<p>barriers, best sequence, directions, maneuver, network analysis, routing, sequence, stop order, stops</p>