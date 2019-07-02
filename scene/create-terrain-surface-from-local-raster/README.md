# Create Terrain Surface from a Local Raster

Set the terrain surface with elevation described by a raster file.

![](CreateTerrainSurfaceFromLocalRaster.png)

The terrain surface is what the basemap, operational layers, and graphics are draped on. Supported raster formats include:

  * ASRP/USRP
  * CIB1, 5, 10
  * DTED0, 1, 2
  * GeoTIFF
  * HFA
  * HRE
  * IMG
  * JPEG
  * JPEG 2000
  * NITF
  * PNG
  * RPF
  * SRTM1, 2


## How it works

  1. Create an `ArcGISScene` and add it to a `SceneView`.
  2. Create a `RasterElevationSource` with a list of raster file paths.
  3. Add this source to the scene's base surface: `scene.getBaseSurface().getElevationSources().add(rasterElevationSource)`.


## Relevant API

  * RasterElevationSource
  * Surface


<h2 id="tags">Tags</h2>
3D, Raster, Elevation, Surface