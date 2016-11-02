<h1>Find Route</h1>

<p>Demonstrates how to get a route between two stops.</p>

<h2>How to use the sample</h2>

<p>For simplicity, the sample comes loaded with a start and end stop. You can click on the Find route button to get a route between these stops. Once the route is generated, the DirectionMessages show step by step directions in the directions list.</p>

<p><img src="FindRoute.png"/></p>

<h2>How it works</h2>

<p>To display a <code>Route</code> using a <code>RouteTask</code>:</p>

<ol>
  <li>Create an <code>ArcGISMap</code>.</li>
  <li>Add the map to the <code>MapView</code>, <code>MapView.setMap()</code>. </li>
  <li>Create a route task using an URL from an online service.
    <ul><li>online route task solves a route from an online route service</li></ul></li>
  <li>Set the <code>RouteParameters</code> to find a route between the <code>Stop</code>s. </li>
  <li>Get the route using the  <code>RouteTask.solveAsync(routeParameters)</code>.</li>
  <li>Display the route by adding it to a <code>GraphicsOverlay.getGraphics().add()</code>.</li>
  <li>Show the step by step <code>DirectionMessage</code>s using the <code>Route.getDirectionManeuvers()</code> method.</li>
</ol>

<h2>Features</h2>

<ul>
  <li>ArcGISMap</li>
  <li>DirectionManeuver</li>
  <li>DirectionMessage</li>
  <li>Graphic</li>
  <li>GraphicsOverlay</li>
  <li>MapView</li>
  <li>Route</li>
  <li>RouteTask</li>
  <li>RouteParameters</li>
  <li>RouteResult</li>
  <li>Stop</li>
</ul>