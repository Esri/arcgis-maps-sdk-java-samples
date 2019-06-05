<h1>Routing around barriers</h1>

<p>Find a route that reaches all stops without crossing any barriers.</p>

<p><img src="RoutingAroundBarriers.png"/></p>

<h2>Use case</h2>

<p>You can define barriers to avoid unsafe areas, for example flooded roads, when planning the most efficient route to evacuate a hurricane zone. When solving a route, barriers allow you to define portions of the road network that cannot be traversed. You could also use this functionality to plan routes when you know an area will be inaccessible due to a community activity like an organized race or a market night.
   
 In some situations, it is further beneficial to find the most efficient route that reaches all stops, reordering them to reduce travel time. For example, a delivery service may target a number of drop-off addresses, specifically looking to avoid congested areas or closed roads, arranging the stops in the most time-effective order.</p>

<h2>How to use the sample</h2>

<p>Use the Edit Mode toggle buttons to select whether to add Stops or Barriers to the route. Click 'Determine Route' to find the route and display it. Select 'Allow stops to be re-ordered' to find the best sequence. Select 'Preserve first stop' if there is a known start point, and 'Preserve last stop' if there is a known final destination.</p>

<h2>How it works</h2>

<ol>
  <li>Create the route task by calling <code>RouteTask.CreateAsync(serviceUrl)</code> with the URL to a Network Analysis route service.</li>
  <li>Get the default route parameters for the service by calling <code>routeTask.CreateDefaultParametersAsync</code>.</li>

  <li>When the user adds a stop, add it to the route parameters.
    <ol>
      <li>Normalize the geometry; otherwise the route job would fail if the user included any stops over the 180th degree meridian.</li>
      <li>Get the name of the stop by counting the existing stops - <code>stepsOverlay.Graphics.Count + 1</code>.</li>
      <li>Create a composite symbol for the stop. This sample uses a pushpin marker and a text symbol.</li>
      <li>Create the graphic from the geometry and the symbol.</li>
      <li>Add the graphic to the stops graphics overlay.</li>
    </ol>
  </li>

  <li>When the user adds a barrier, create a polygon barrier and add it to the route parameters.
    <ol>
      <li>Normalize the geometry (see <strong>3i</strong> above).</li>
      <li>Buffer the geometry to create a larger barrier from the tapped point by calling <code>GeometryEngine.BufferGeodetic(mapLocation, 500, LinearUnits.Meters)</code>.</li>
      <li>Create the graphic from the geometry and the symbol.</li>
      <li>Add the graphic to the barriers overlay.</li>
    </ol>
  </li>

  <li>When ready to find the route, configure the route parameters.
    <ol>
      <li>Set the <code>ReturnStops</code> and <code>ReturnDirections</code> to <code>true</code>.</li>
      <li>Create a <code>Stop</code> for each graphic in the stops graphics overlay. Add that stop to a list, then call <code>routeParameters.SetStops(routeStops)</code>.</li>
      <li>Create a <code>PolygonBarrier</code> for each graphic in the barriers graphics overlay. Add that barrier to a list, then call <code>routeParameters.SetPolygonBarriers(routeBarriers)</code>.</li>
      <li>If the user will accept routes with the stops in any order, set <code>FindBestSequence</code> to <code>true</code> to find the most optimal route.</li>
      <li>If the user has a definite start point, set <code>PreserveFirstStop</code> to <code>true</code>.</li>
      <li>If the user has a definite final destination, set <code>PreserveLastStop</code> to <code>true</code>.</li>
    </ol>
  </li>

  <li>Calculate and display the route.
    <ol>
      <li>Call <code>routeTask.SolveRouteAsync(routeParameters)</code> to get a <code>RouteResult</code>.</li>
      <li>Get the first returned route by calling <code>calculatedRoute.Routes.First()</code>.</li>
      <li>Get the geometry from the route as a polyline by accessing the <code>firstResult.RouteGeometry</code> property.</li>
      <li>Create a graphic from the polyline and a simple line symbol.</li>
      <li>Display the steps on the route, available from <code>firstResult.DirectionManeuvers</code>.</li>
    </ol>
  </li>
</ol>

<h2>Relevant API</h2>

<ul>
  <li>DirectionManeuver</li>
  <li>PolygonBarrier</li>
  <li>Route</li>
  <li>Route.DirectionManeuver</li>
  <li>Route.RouteGeometry</li>
  <li>RouteParameters.ClearPolygonBarriers</li>
  <li>RouteParameters.FindBestSequence</li>
  <li>RouteParameters.PreserveFirstStop</li>
  <li>RouteParameters.PreserveLastStop</li>
  <li>RouteParameters.ReturnDirections</li>
  <li>RouteParameters.ReturnStops</li>
  <li>RouteParameters.SetPolygonBarriers</li>
  <li>RouteResult</li>
  <li>RouteResult.Routes</li>
  <li>RouteTask</li>
  <li>Stop</li>
</ul>

<h2>About the data</h2>

<p>This sample uses an Esri-hosted sample street network for San Diego.</p>

<h2>Tags</h2>

<p>barriers, best sequence, directions, maneuver, network analysis, routing, sequence, stop order, stops</p>