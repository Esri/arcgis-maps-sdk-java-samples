# Open mobile scene package

Open and display a scene from an offline Mobile Scene Package (.mspk).

![](OpenMobileScenePackageSample.png)

## Use Case
A .mspk file is an archive containing the data (specifically, basemaps and features) used to display an offline 3D scene.

## How it works
1. Create a `String` pointing to the samples-data/mspk directory which hosts the .mspk data.
1. Use the static method `MobileScenePackage.isDirectReadSupportedAsync(mspkData)` to check whether the package can be read in the archived form (.mspk) or whether it needs to be unpacked.
2. If the mobile scene package requires unpacking, use `MobileScenePackage.unpackAsync(mspkPath, pathToUnpackTo)` and wait for this to complete.
3. Instantiate two `MobileScenePackage` objects, one using the path to the local `.mspk` file and the other to the unpacked directory.
4. Call `mobileScenePackage.loadAsync` and check for any errors.
5. When the mobile scene package is loaded, obtain the first `Scene` in the package using `mobileScenePackage.getScenes().get(0))`.
6. Set the `SceneView` scene to the one obtained in the previous step.

## Relevant API
- MobileScenePackage
- Scene
- SceneView

## Offline data
Read more about how to set up the sample's offline data [here](http://links.esri.com/ArcGISRuntimeQtSamples).

Link | Local Location
---------|-------|
|[Philadelphia MSPK](https://www.arcgis.com/home/item.html?id=7dd2f97bb007466ea939160d0de96a9d)| `<userhome>`/ArcGIS/Runtime/Data/mspk/philadelphia.mspk |

## Additional information
Before loading the `MobileScenePackage`, it is important to first check if direct read is supported. The mobile scene package could contain certain data types that would require the data to be unpacked. For example, scenes containing raster data will need to be unpacked.

## Tags
Offline, Scene, MobileScenePackage