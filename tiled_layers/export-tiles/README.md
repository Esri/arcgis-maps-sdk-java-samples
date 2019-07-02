# Export Tiles

Export tiles from an online tile service.

![]("ExportTiles.png)

## How it works

To export tiles from an `ArcGISTiledLayer`:

  1. Create an `ExportTileCacheTask`, passing in the URI of the tiled layer
  2. Create default `ExportTileCacheParameters` with `task.createDefaultExportTileCacheParametersAsync
  (extent, minScale, maxScale)`
  3. Call `task.exportTileCacheAsync(defaultParams, downloadFile)` to create the 
  `ExportTileCacheJob`
  4. Call `job.start()` to start the job
  5. When the job is done, use `job.getResult()` to get the resulting `TileCache`


## Relevant API


  * ArcGISMap
  * ArcGISTiledLayer
  * Basemap
  * ExportTileCacheJob
  * ExportTileCacheParamters
  * ExportTileCacheTask
  * MapView
  * TileCache

