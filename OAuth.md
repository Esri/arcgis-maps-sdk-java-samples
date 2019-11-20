Some samples require you to configure your own portal for authentication. This page describes the OAuth pattern.

### Register an App
Login to your [ArcGIS Developers site](http://developers.arcgis.com) account and [register](https://developers.arcgis.com/applications/#/new/) an app.

### Portal URL
For an app registered and hosted on ArcGIS.com, use `http://www.arcgis.com/` as the portal URL.

### Client ID
Once registered, select the Authentication tab to get your **Client ID**.

### Redirect URL
In the Authentication tab, go to the Redirect URIs section and add `urn:ietf:wg:oauth:2.0:oob`.
