# Open mobile scene package

Opens and displays a scene from a mobile scene package (.mspk).

![Image of open mobile scene package](OpenMobileScenePackage.png)

## Use case

A mobile scene package is an archive containing the data (specifically, basemaps and features), used to display an offline 3D scene.

## How to use the sample

When the sample opens, it will automatically display the Scene in the mobile scene package.

Since this sample works with a local .mspk, you will need to download the file to your device.

## How it works

1. Create a `MobileScenePackage` using the path to the local .mspk file.
2. Call `MobileScenePackage.loadAsync` and check for any errors.
3. When the `MobileScenePackage` is loaded, obtain the first `ArcGISScene` using `mobileScenePackage.getScenes().get(0)`
4. Create a `SceneView` and call `sceneView.setView` to display the scene from the package.

## Relevant API

* MobileScenePackage
* SceneView

## Tags

offline, scene
