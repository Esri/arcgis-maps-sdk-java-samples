# Export Vector Tiles

Export tiles from an online vector tile service.

![](ExportVectorTiles.png)

## How to use the sample

First, a dialog will appear prompting for authentication credentials to arcgis.com. When the vector tiled layer 
loads, zoom in to the extent you want to export. The red box shows the extent that will be exported. Click the 
"Export Vector Tiles" button to start the job. A progress indicator will show. The larger the extent, the longer it 
will take to export. An error will show if the extent is larger than the maximum limit allowed. When finished, a 
dialog will show the exported result in a new map view.

## How it works

To export tiles from an `ArcGISVectorTiledLayer`:

1.  Create an `ExportVectorTilesTask`, passing in the `PortalItem` for the vector tiled layer. 
  Since vector tiled layers are premium content, you must first authenticate with the Portal.
2.  Create default `ExportTilesParameters` with `task.createDefaultExportTilesParametersAsync(extent, maxScale)`.
3.  Call `task.exportVectorTilesAsync(defaultParams, vtpkPath, resourcePath)` to create the 
  `ExportVectorTilesJob`. The resource path is required if you want to export the tiles with the style.
4.  Call `job.start()` to start the export job.
5.  When the job is done, use `job.getResult()` to get the resulting 
  `ExportVectorTilesResult`.
6.  You can load the result as a `ArcGISVectorTiledLayer` with `new ArcGISVectorTiledLayer(result.getVectorTileCache(), result.getItemResourceCache())`.


## Relevant API


*   ArcGISVectorTiledLayer
*   ExportVectorTilesJob
*   ExportVectorTilesParamters
*   ExportVectorTilesResult
*   ExportVectorTilesTask
*   ItemResourceCache
*   Portal
*   PortalItem
*   UserCredential
*   VectorTileCache

