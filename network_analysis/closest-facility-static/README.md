<h1>Closest Facility (Static)</h1>

<p>Find routes from several locations to the respective closest facility.</p>

<p><img src="ClosestFacilityStatic.png"/></p>

<h2>Use case</h2>

<p>Quickly and accurately determining the most efficient route between a location and a facility is a frequently encountered task (e.g. emergency services).</p>

<h2>How to use the sample</h2>

<p>Click the 'Solve Routes' button to determine and display the route from each incident (fire) to the nearest facility (fire station).</p>

<h2>How it works</h2>

<p>To display a <code>ClosestFacilityRoute</code> between several incidents and facilities:</p>

<ol>
  <li>Create a <code>ClosestFacilityTask</code> using a Url from an online service.</li>
  <li>Get the default set of <code>ClosestFacilityParameters</code> from the task: <code>closestFacilityTask.createDefaultParametersAsync().get()</code>.</li>
  <li>Build a list of all Facilities and Incidents:
    <ul>
      <li>Create a <code>FeatureTable</code> using <code>ServiceFeatureTable(Uri)</code>.</li>
      <li>Query the <code>FeatureTable</code> for all <code>Feature</code>s using <code>.queryFeaturesAsync(queryParameters)</code>.</li>
      <li>Iterate over the result and add each <code>Feature</code> to a <code>List</code>, instantiating the feature as a <code>Facility</code> or <code>Incident</code>.</li>
    </ul>
  </li>
  <li>Add a list of all facilities to the task parameters: <code>closestFacilityParameters.setFacilities(facilitiesList)</code>.</li>
  <li>Add a list of all incidents to the task parameters: <code>closestFacilityParameters.setIncidents(incidentsList)</code>.</li>
  <li>Get <code>ClosestFacilityResult</code> from solving the task with the provided parameters: <code>closestFacilityTask.solveClosestFacilityAsync(closestFacilityParameters)</code>.</li>
  <li>Find the closest facility for each incident by iterating over the previously created <code>incidentsList</code>:
    <ul>
      <li>Get index list of closet facilities to the incident, <code>closestFacilityResult.getRankedFacilityIndexes(indexOfIncident).get(0)</code>.</li>
      <li>Find closest facility route, <code>closestFacilityResult.getRoute(closestFacilityIndex, indexOfIncident)</code>.</li>
    </ul>
  </li> 
  <li>Display the route:
    <ul>
      <li>create a <code>Graphic</code> from route geometry, with <code>new Graphic(closestFacilityRoute.getRouteGeometry())</code>.</li>
      <li>add graphic to <code>GraphicsOverlay</code> and set it to the mapview.</li>
    </ul>
  </li>
</ol>

<h2>Relevant API</h2>

<ul>
  <li>ClosestFacilityParameters</li>
  <li>ClosestFacilityResult</li>
  <li>ClosestFacilityRoute</li>
  <li>ClosestFacilityTask</li>
  <li>Facility</li>
  <li>Graphic</li>
  <li>GraphicsOverlay</li>
  <li>Incident</li>
</ul>

<h2>Tags</h2>

<p>facility, incident, network analysis, route, search</p>
