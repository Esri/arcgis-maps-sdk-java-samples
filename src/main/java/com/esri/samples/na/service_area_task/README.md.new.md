# Service Area Task

Find the service area for a point.

A service area shows locations that can be reached from a facility based
off a certain impedance \[such as travel time\]. Barriers can also be
added which can effect the impedance by not letting traffic through or
adding the time is takes to pass that barrier.

![](ServiceAreaTask.gif)

## How to use sample

In order to find any service areas at least one ServiceAreaFaciltiy
needs to be added.

To add a facility, click the facility button, then click anywhere on the
MapView.

To add a barrier, click the barrier button, and click multiple locations
on MapView.

  - Hit the barrier button again to finish drawing barrier.
  - Hitting any other button will also stop the barrier from drawing.

To show service areas around facilities that were added, click show
service areas button.

Reset button, clears all graphics and reset ServiceAreaTask.

## How it works

To display service areas around a certain location:

Create a `ServiceAreaTask` from an online service.

Get ServiceAreaParameters from task,
`task.createDefaultParametersAsync()`

Setting return polygons to true will return all services areas,
`serviceAreaParameters.setReturnPolygons(true)`

Add a `ServiceAreaFacilty` to parameters,
`serviceAreaParameters.setFacilities(serviceAreaFacility)`

Get `ServiceAreaResult` by solving the service area task using
parameters, `task.solveServiceAreaAsync(serviceAreaParameters).get()`

Get any `ServiceAreaPolygon`s that were returned,
`serviceAreaResult.getResultPolygons(facilityIndex)`

  - facilityIndex is the faciltiy from the mapview that you want to get
    the services areas of

Display services areas to MapView,
`graphicsOverlay.getGraphics().add(new
Graphic(serviceAreaPolygon.getGeometry(), fillSymbol))`
