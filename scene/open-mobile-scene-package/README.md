# Open mobile scene package

Open and display a scene from an offline Mobile Scene Package (.mspk).

![](OpenMobileScenePackage.png)

## Use Case

An .mspk file is an archive containing the data (specifically, basemaps and features) used to display an offline 3D scene.

## How to use the sample

The sample loads a mobile scene package. Pan and zoom to explore the scene.

## How it works

1. Create a `MobileScenePackage` with the path to a .mspk file.
2. Call `mobileScenePackage.loadAsync` to load the mobile scene package. When finished, get the `ArcGISScene` objects inside with `mobileScenePackage.getScenes()`.
3. Set the first scene to the scene view.

## Relevant API

* MobileScenePackage

## Tags

offline, scene
