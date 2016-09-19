#Local Server Services
Demonstrates how to start and stop a Local Server and start and stop a Local Map, Feature, and Geoprocessing Service to that server.

##How to use the sample
Local Server Controls (Top Left):
  - Start Local Server -- Starts a Local Server if one is not already running.
  - Stop Local Server --  Stops a Local Server if one is running. 
  
Local Services Controls (Top Right):
  - Combo Box -- Allows for the selection of a Local Map, Feature, or Geoprocessing Service. 
  - Start Service -- Starts the Service that is selected in the combo box.
  - Stop Service --  Stops the Service that is selected in the `List of Running Services`.
  
Text Area (Middle):
  - Displays the running status of the Local Server and any services that are added to that server. 
  
List of Running Services (Bottom):
  - Displays any services that are currently running on the server and allows for one of the services to be selected. 
  - Go to URL -- Opens browser to the service that is selected in the `List of Running Services`. 
  - 
  

##How it works
To start a `Local Server` and start a `Local Service` to it:

1. Create and run a local server.
  - `LocalServer.INSTANCE` creates a local server
  - `Server.startAsync()` starts the server asynchronously
2. Wait for server to be in the  `LocalServerStatus.STARTED` state.
  - `Server.addStatusChangedKistener()` fires whenever the running status of the local server has changed.
3. Create and run local service, example of running a `Local Map Service`.
  - `new LocalMapService(URL)`, creates a local map servie with the given url path of where the local map service can be found
  - `Service.startAsync()`, starts the service asynchronously
  - service will be added to the local server when running status is `LocalServerStatus.STARTED`

To stop a `Local Server` and stop any `Local Service`s that are added to it:

1. Get any services that are currently running on the local server, `Server.getServices()`.
2. Loop through all services and stop any that are currently started.
  - check service is started, `Service.getStatus()` equals `LocalServerStatus.STARTED`
  - `Service.stopAsync()`, stops the service asynchronously
3. Wait for all services to be in the `LocalServerStatus.STOPPED` state.
  - `Service.addStatusChangedKistener()` fires whenever the running status of the local service has changed.
4. Stop the local server, `Server.stopAsync()`.

##Tags
- LocalFeatureService
- LocalGeoprocessingService
- LocalMapService
- LocalServer
- StatusChangedEvent
- LocalServerStatus
- LocalService
