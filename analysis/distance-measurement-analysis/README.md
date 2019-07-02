# Distance Measurement Analysis

Measure distances within a scene.

The distance measurement analysis allows you to add the same measuring experience found in ArcGIS Pro, City Engine, and the ArcGIS API for JavaScript to your app. You can set the unit system of measurement (metric or imperial) and have the units automatically switch to one appropriate for the current scale. The rendering is handled internally so it doesn't interfere with other analyses like viewsheds.

![](DistanceMeasurementAnalysis.png)

## How to use the sample

Choose a unit system for the measurement in the UI dropdown. Click any location in the scene to start measuring. Move the mouse to an end location, and click to complete the measure. Clicking any new location after this will start a new measurement.

## How it works

To measure distances with the `LocationDistanceMeasurement` analysis:

1.  Create an `AnalysisOverlay` and add it to your scene view's analysis overlay collection: `sceneView.getAnalysisOverlays().add(analysisOverlay)`.
2.  Create a `LocationDistanceMeasurement`, specifying the `startLocation` and `endLocation`. These can be the same point to start with. Add the analysis to the analysis overlay: `analysisOverlay.getAnalyses().add(LocationDistanceMeasurement)`. The measuring line will be drawn for you between the two points.
3.  The `measurementChanged` callback will fire if the distances change. You can get the new values for the `directDistance`, `horizontalDistance`, and `verticalDistance` from the `MeasurementChangedEvent` returned by the callback. The distance objects contain both a scalar value and a unit of measurement.

## Relevant API

*   AnalysisOverlay
*   LocationDistanceMeasurement
*   UnitSystem

## Additional information

The `LocationDistanceMeasurement` analysis only performs planar distance calculations. This may not be appropriate for large distances where the Earth's curvature needs to be taken into account.

## Tags

Analysis, 3D
