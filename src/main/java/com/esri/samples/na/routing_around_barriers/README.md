<h1>Routing around barriers</h1>

<p>Generate a route among multiple stops taking into account different barriers.</p>

<p><img src="RoutingAroundBarriers.png"/></p>

<h2>Use case</h2>

<p>When determining a route between several stops, it is important to be able to take into account barriers of which the routing service may not be aware. In some situations, it is further beneficial to find the most efficient route between a larger amount of stops, reordering them to reduce travel time. For example, a delivery service may target a number of drop-off addresses, specifically looking to avoid congested areas or blocked roads (e.g. roadworks), and arranging the stops in the most time-effective order. </p>

<h2>How to use the sample</h2>

<p>Click on the map to add any number of stops. Then select the Barriers ToggleButton and click on the map to add buffered polygons as barriers.
 The last stop/barrier can be removed at any time using secondary-click when the appropriate ToggleButton is selected. Check the 'Find best sequence' CheckBox to enable targeting stops at the optimum order, and tick either 'Preserve first stop' and/or 'Preserve last stop', if these are desired. Now click on the Determine Route button to generate a route connecting all the stops, avoiding barriers. Once the route is computed, details about the route such route length and the travel time are displayed. To view the directions, expand the Route Directions accordion menu. Click the reset button to remove all stops, barriers and routes and start over.</p>

<h2>How it works</h2>

<ol>
  <li>Create a <code>RouteTask</code> using a URL for an online routing service, and create the default set of parameters: <code>routeTask.createDefaultParametersAsync()</code>.</li>
  <li>Enable the properties to return the Stops and Directions on completion of the routing task: <code>routeParameters.setReturnStops(true)</code> and <code>routeParameters.setReturnDirections(true)</code>. This is done to later retrieve the list of <code>DirectionManeuver</code>s and display the route directions.</li>
  <li>Create <code>Stop</code>s and <code>PolygonBarrier</code>s, optionally grouping them in <code>ArrayList&lt;&gt;</code>s for ease of use.</li>
  <li>Add the list of stops and list of barriers to the route parameters: <code>routeParameters.setStops(stopsList)</code> and <code>routeParameters.setPolygonBarriers(barriersList)</code>.
  <ul>
    <li>Optional: enable further options in the route parameters, such as finding the best sequence (<code>routeParameters.setFindBestSequence(true)</code>), or preserving the first (<code>routeParameters.setPreserveFirstStop(true)</code>) or last stop (<code>routeParameters.setPreserveLastStop(true)</code>).</li>
  </ul>
  </li>
  <li>Solve the routing task: <code>routeTask.solveRouteAsync(routeParameters)</code>.</li>
  <li>Retrieve the <code>Route</code> from the task result: <code>routeResult.getRoutes().get(0)</code> and get the route geometry: <code>.getRouteGeometry()</code>.</li>
  <li>Construct a <code>Graphic</code> using the route geometry, and add it to a <code>GraphicsOverlay</code> to display the route on a map.</li>
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

<h2>Tags</h2>

<p>barrier, directions, network analysis, routing</p>