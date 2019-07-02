# Closest Facility

Find a route to the closest facility from a location.

![](ClosestFacility.png)

## How to use sample
Left click near any of the hospitals and a route will be displayed from that clicked location to the nearest hospital.

## How it works

To display a `ClosestFacilityRoute` between an `Incident` and a `Facility`:

1.  Create a `ClosestFacilityTask` using  an Url from an online service.
2.  Get `ClosestFacilityParameters` from task, `task.createDefaultParametersAsync().get()`
3.  Add facilities to parameters, `closestFacilityParameters.setFacilities().addAll(facilities)`.
4.  Add incidents to parameters, `closestFacilityParameters.setIncidents().add(Arrays.asList(new Incident(incidentPoint)))`.
5.  Get `ClosestFacilityResult` from solving task with parameters, `task.solveClosestFacilityAsync(facilityParameters).get()`
6.  Get index list of closet facilities to incident, `facilityResult.getRankedFacilities(0)`
7.  Get index of closest facility, `rankedFacilitiesList.get(0)`
8.  Find closest facility route, `facilityResult.getRoute(closestFacilityIndex, IncidentIndex)`
9.  Display route to `MapView`. 
*   create `Graphic` from route geometry, `new Graphic(route.getRouteGeometry())`
*   add graphic to `GraphicsOverlay` which is attached to the mapview

## Relevant API

*   ClosestFacilityParameters
*   ClosestFacilityResult
*   ClosestFacilityRoute
*   ClosestFacilityTask
*   Facility
*   Graphic
*   GraphicsOverlay
*   Incident
*   MapView

