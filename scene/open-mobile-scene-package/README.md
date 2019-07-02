# Open mobile scene package

Open and display a scene from an offline Mobile Scene Package (.mspk).

![](OpenMobileScenePackage.png)

## Use Case

A .mspk file is an archive containing the data (specifically, basemaps and features) used to display an offline 3D scene.

## How it works


1. Use the static method `MobileScenePackage.isDirectReadSupportedAsync(mspkData)` to check whether the package can be read in the archived form (.mspk) or whether it needs to be unpacked.

2. If direct read is supported, use `isDirectReadSupported.get()` and instantiate a `MobileScenePackage` with the path to the .mspk file.

3. If the mobile scene package requires unpacking, use `MobileScenePackage.unpackAsync(mspkPath, pathToUnpackTo)` and instantiate a `MobileScenePackage` with the path to the unpacked .mspk file.

4. Call `mobileScenePackage.loadAsync` to load the mobile scene package. When finished, get the `ArcGISScene` objects inside with `mobileScenePackage.getScenes()`.

5. Set the first scene in the object collection on the scene view with `sceneView.setArcGISScene(scene)`.


## Relevant API


* MobileScenePackage


## Additional information

Before loading the `MobileScenePackage`, it is important to first check if direct read is supported. The mobile scene package could contain certain data types that would require the data to be unpacked. For example, scenes containing raster data will need to be unpacked.

## Tags

Offline, Scene, MobileScenePackage