# Local Server Services

Manage multiple running services in Local Server.

**Note:** Local Server is not supported on MacOS

![](LocalServerServices.png)

## How to use the sample

Choose an option from the dropdown control to filter packages by service
type. Then click the Open button to choose a package. Finally, click the
Start button to start the service. The service’s status will be
displayed in the center log.

To stop a service, select it from the Running Services list and click
the Stop Service button. To go to the service’s URL in your default web
browser, select it and click the Go to URL button.

## How it works

To start a `LocalService`:

1.  Start the local server.
      - `LocalServer.INSTANCE` creates a local server
      - `server.startAsync()` starts the server asynchronously
2.  Wait for server to be in the `LocalServerStatus.STARTED` state.
      - `Server.addStatusChangedListener()` fires whenever the running
        status of the local server changes
3.  Create and run a local service.
      - `new LocalMapService(Url)` creates a local map service with the
        given url path to mpk file
      - `service.startAsync()` starts the service asynchronously
4.  Stop the service with `service.stopAsync().`
