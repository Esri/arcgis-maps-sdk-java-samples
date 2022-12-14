# Set up location driven Geotriggers

Create a notification every time a given location data source has entered and/or exited a set of features or graphics.

![Geotriggers](SetUpLocationDrivenGeotriggers.png)

## Use case

Geotriggers can be used to notify users when they have entered or exited a geofence by monitoring a given set of features or graphics. They could be used to display contextual information to museum visitors about nearby exhibits, notify hikers when they have wandered off their desired trail, notify dispatchers when service workers arrive at a scene, or more.

## How to use the sample

Observe a virtual walking tour of the Santa Barbara Botanic Garden. Information about the user's current Garden Section, as well as information about nearby points of interest within 10 meters will display or be removed from the UI when the user enters or exits the buffer of each feature.

## How it works

1. Create a `LocationGeotriggerFeed` with a `SimulatedLocationDataSource`.
2. Create `FeatureFenceParameters` from a `ServiceFeatureTable`, and an optional buffer distance at which to monitor each feature.
3. Create a `FenceGeotrigger` with the location geotrigger feed, a `FenceRuleType.ENTER_OR_EXIT`, the feature fence parameters, an Arcade Expression, and a name for the specific geotrigger.
4. Create a `GeotriggerMonitor` with the fence geotrigger and call `.startAsync()` on it to begin listening for events that meet the `FenceRuleType`.
5. When a `GeotriggerMonitorNotificationEvent` emits, capture the `GeotriggerNotificationInfo`.
6. For more information about the feature that triggered the notification, cast the `GeotriggerNotificationInfo` to a `FenceGeotriggerNotificationInfo` and call `fenceGeotriggerNotificationInfo.getFenceGeoElement()` to get the fence feature, casting the geoelement as an `ArcGISFeature`.
7. Depending on the geotrigger notification info's `FenceNotificationType()` display (on notification type `ENTERED`) or hide (on notification type `EXITED`) information about the feature on the UI.

## Relevant API

* ArcadeExpression
* FeatureFenceParameters
* FenceGeotrigger
* FenceGeotriggerNotificationInfo
* FenceNotificationType
* FenceRuleType
* Geotrigger
* GeotriggerFeed
* GeotriggerMonitor
* GeotriggerNotificationInfo
* SimulatedLocationDataSource

## About the data

This sample uses the [Santa Barbara Botanic Garden Geotriggers Sample](https://www.arcgis.com/home/item.html?id=6ab0e91dc39e478cae4f408e1a36a308) ArcGIS Online Web Map which includes a georeferenced map of the garden as well as select polygon and point features to denote garden sections and points of interest. Description text and attachment images in the feature layers were provided by the Santa Barbara Botanic Garden and more information can be found on the [Garden Sections & Displays](https://www.sbbg.org/explore-garden/garden-sections-displays) portion of their website. All assets are used with permission from the Santa Barbara Botanic Garden. For more information, visit the [Santa Barbara Botanic Garden](https://www.sbbg.org/) website.

## Tags

alert, arcade, fence, geofence, geotrigger, location, navigation, notification, notify, routing, trigger
