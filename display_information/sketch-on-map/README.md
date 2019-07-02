# Sketch on Map

Use the Sketch Editor to edit, or sketch a new point, line, or polygon geometry on to a map.

![](SketchOnMap.png)

## How to use the sample

Choose which geometry type to sketch from one of the available buttons. Choose from points, multipoints, polylines, polygons, freehand polylines and freehand polgons.

Use the control panel to cancel the sketch, undo or redo changes made to the sketch and to save the sketch to the graphics overlay. There is also the option to select a saved graphic and edit its geometry using the Sketch Editor. The graphics overlay can be cleared using the clear all button.


## How it works


1. Create a `SketchEditor` and set it to the MapView with `mapView.setSketchEditor(sketchEditor)`.

2. Use `SketchEditor.start(SketchCreationMode.chooseGeometryType)` to start sketching. If editing an existing graphic's geometry, use `SketchEditor.start(graphic.getGeometry)`.

3. Check to see if undo and redo are possible during a sketch session using `sketchEditor.canUndo()` and `sketchEditor.canRedo()`. If it's possible, use `sketchEditor.undo()` and `sketchEditor.redo()`.

4. Check if sketch is valid using `sketchEditor.isSketchValid()`, then allow the sketch to be saved to the `Graphics Overlay`.

5. Get the geometry of the sketch using `sketchEditor.getGeometry()`, and create a new `Graphic` from that geometry. Add the graphic to the `Graphics Overlay`.

6. To exit the sketch editor, use `sketchEditor.stop()`.



## Relevant API



*   Geometry
*   Graphic
*   GraphicsOverlay
*   MapView
*   SketchCreationMode
*   SketchEditor



## Tags

draw, edit, Geometry, Graphic, GraphicsOverlay, SketchCreationMode, SketchEditor

