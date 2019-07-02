# Service Area Task

Find the service area for a point.

A service area shows locations that can be reached from a facility based off a certain impedance [such as travel 
time]. Barriers can also be added which can effect the impedance by not letting traffic through or adding the time is takes to pass that barrier.

![]("ServiceAreaTask.gif)

## How to use sample
<li>In order to find any service areas at least one ServiceAreaFaciltiy needs to be added.</li>
<li>To add a facility, click the facility button, then click anywhere on the MapView.</li>
<li>To add a barrier, click the barrier button, and click multiple locations on MapView.</li>
* Hit the barrier button again to finish drawing barrier.
* Hitting any other button will also stop the barrier from drawing.
<li>To show service areas around facilities that were added, click show service areas button.</li>
<li>Reset button, clears all graphics and reset ServiceAreaTask.</li>

## How it works

To display service areas around a certain location:


1. Create a `ServiceAreaTask` from an online service.
2. Get ServiceAreaParameters from task, `task.createDefaultParametersAsync()`
3. Setting return polygons to true will return all services areas, `serviceAreaParameters.setReturnPolygons(true)`
4. Add a `ServiceAreaFacilty` to parameters, `serviceAreaParameters.setFacilities(serviceAreaFacility)`
5. Get `ServiceAreaResult` by solving the service area task using parameters, `task.solveServiceAreaAsync(serviceAreaParameters).get()`
6. Get any `ServiceAreaPolygon`s that were returned, `serviceAreaResult.getResultPolygons(facilityIndex)`
* facilityIndex is the faciltiy from the mapview that you want to get the services areas of
7. Display services areas to MapView, `graphicsOverlay.getGraphics().add(new Graphic(serviceAreaPolygon.getGeometry(), fillSymbol))`


## Relevant API

  * ArcGISMap
  * GraphicsOverlay
  * MapView
  * PolylineBarrier
  * ServiceAreaFacility
  * ServiceAreaParameters
  * ServiceAreaPolygon
  * ServiceAreaResult
  * ServiceAreaTask
  * PolylineBuilder


