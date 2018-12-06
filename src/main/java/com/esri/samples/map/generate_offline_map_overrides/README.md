# Generate Offline Map (Overrides)

Take a web map offline with additional options for each layer.

For applications where you just need to take all layers offline, use the standard workflow (using only 
`GenerateOfflineMapParameters`). For a simple example of how you take a map offline, see the "Generate Offline Map" 
sample. For more fine-grained control over the data you are taking offline, use overrides to adjust the settings for
 each layer (`GenerateOfflineMapParameters` in conjunction with `GenerateOfflineMapParameterOverrides`). Some example
  use cases for the overrides approach could be when you need to:

- adjust the extent for one or more layers to be different to the rest of the map.
- reduce the amount of data (for example tile data) downloaded for one or more layers in the map.
- filter features to be taken offline.
- take features with no geometry offline.

![Image](GenerateOfflineMapOverrides.png)

## How to use the sample
Sign in with a free developer account when prompted for credentials (taking web maps offline requires an account).

Use the min/max scale spinners to adjust the levelIds to be taken offline for the Streets basemap.

Use the extent buffer distance spinner to set the buffer radius for the streets basemap. 

Check the checkboxes for the feature operational layers you want to include in the offline map.
 
Use the min hydrant flow rate spinner to only download features with a flow rate higher than this value.

Select the "Water Pipes" checkbox if you want to crop the water pipe features to the extent of the map.

Click the "Generate offline map" button to start the download. A progress bar will display. Click the "Cancel" button
 if you want to stop the download. When the download is complete, the view will display the offline map. Pan around 
 to see that it is cropped to the download area's extent.

# How it works
1. Load a web map from a `PortalItem`. Authenticate with the portal if required.
2. Create an `OfflineMapTask` with the map.
3. Generate default task parameters for the extent to download with `offlineMapTask.
.createDefaultGenerateOfflineMapParametersAsync(extent)`.
4. Generate override parameters using the default parameters with `offlineMapTask
.createGenerateOfflineMapParameterOverridesAsync(parameters)`.

## Relevant API
- OfflineMapTask
- GenerateGeodatabaseParameters
- GenerateOfflineMapParameters
- GenerateOfflineMapParameterOverrides
- GenerateOfflineMapJob
- GenerateOfflineMapResult
- ExportTileCacheParameters

## Tags
Offline