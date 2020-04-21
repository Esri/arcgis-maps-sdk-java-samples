# Service Area Task

Find the service area within a network from a given point.

![Image of service area task](ServiceAreaTask.gif)

## Use case

A service area shows locations that can be reached from a facility based off a certain impedance, such as travel time or distance. Barriers can increase impedance by either adding to the time it takes to pass through the barrier or by altogether preventing passage.

You might calculate the region around a hospital in which ambulances can service in 30 min or less.

## How to use the sample

In order to find any service areas at least one facility needs to be added to the map view. Add facilities to the map by clicking the 'Add Facilities' button and then clicking anywhere on the map. To add a barrier, click the 'Add Barrier' button, and click multiple locations on map. Clicking any other button will stop the barrier from drawing. To show the service areas around facilities that were added, click 'Show Service Areas' button. The 'Reset' button clears all graphics and resets the service area task.

## How it works

1. Create a new `ServiceAreaTask` from a network service.
2. Create default `ServiceAreaParameters` from the service area task.
3. Set the parameters to return polygons (true) to return all service areas.
4. Add a `ServiceAreaFacility` to the parameters.
5. Get the `ServiceAreaResult` by solving the service area task using the parameters.
6. Get any `ServiceAreaPolygons` that were returned, serviceAreaResult.getResultPolygons(facilityIndex).
7. Display the service area polygons as graphics in a `GraphicsOverlay` on the `MapView`.

## Relevant API

* PolylineBarrier
* ServiceAreaFacility
* ServiceAreaParameters
* ServiceAreaPolygon
* ServiceAreaResult
* ServiceAreaTask

## Tags

barriers, facilities, impedance, logistics, routing
