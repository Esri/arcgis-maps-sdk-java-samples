# Create and save KML file

Construct a KML document and save it as a KMZ file.

![Create And Save KML File Sample](CreateAndSaveKmlFile.png)

## Use case

If you need to create and save data on the fly, you can use KML to create points, lines, and polygons by sketching on the map, customizing the style, and serializing them as KML nodes in a KML Document. Once complete, you can share the KML data with others that are using a KML reading application, such as ArcGIS Earth.

## How to use the sample

Click on one of the buttons in the middle row to start adding a geometry. Click on the map view to place vertices. Click the "Complete Sketch" button to add the geometry to the KML document as a new KML placemark. Use the style interface to edit the style of the placemark. If you do not wish to set a style, click the "Don't Apply Style" button. When you are finished adding KML nodes, click on the "Save KMZ file" button to save the active KML document as a .kmz file on your system. Use the "Reset" button to clear the current KML document and start a new one.

## How it works

1. Create a `KmlDocument`
2. Create a `KmlDataset` using the KML document.
3. Create a `KmlLayer` using the KML dataset and add it to the map as an `OperationalLayer`.
4. Create `Geometry` using `SketchEditor`.
5. Project that geometry to WGS84 using `GeometryEngine.Project`.
6. Create a `KmlGeometry` object using that projected geometry.
7. Create a `KmlPlacemark` using the KML geometry.
8. Add the KML placemark to the KML document.
9. Set the `KmlStyle` for the KML placemark.
10. When finished with adding KML placemark nodes to the KML document, save the KML document to a file using the `saveAsAsync(Path)` method.

## Relevant API

* GeometryEngine.Project
* KmlDataset
* KmlDocument
* KmlGeometry
* KmlLayer
* KmlNode.SaveAsASync
* KmlPlacemark
* KmlStyle
* SketchEditor

## Tags

Keyhole, KML, KMZ, OGC