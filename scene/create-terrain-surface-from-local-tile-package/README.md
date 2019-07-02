# Create Terrain from a Local Tile Package

Set the terrain surface with elevation described by a local tile package.

The terrain surface is what the basemap, operational layers, and graphics are draped on. The tile package must be a LERC (limited error raster compression) encoded TPK. Details on creating these are in the <a href="https://pro.arcgis.com/en/pro-app/help/sharing/overview/tile-package.htm">ArcGIS Pro documentation</a>.

![]("CreateTerrainSurfaceFromLocalTilePackage.png)

## How it works

  1. Create an `ArcGISScene` and add it to a `SceneView`.
  2. Create an `ArcGISTiledElevationSource` with the path to the local tile package.
  3. Add this source to the scene's base surface: `scene.getBaseSurface().getElevationSources().add(tiledElevationSource)`.


## Relevant API

  * ArcGISTiledElevationSource
  * Surface


<h2 id="tags">Tags</h2>
3D, Tile Cache, Elevation, Surface
