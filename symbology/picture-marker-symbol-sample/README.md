#Picture Marker Symbol#
This sample shows how to create `PictureMarkerSymbol`s from the different types of picture resources; an URL, locally on the device or in the app.

##How to use the sample##
The picture marker symbols that you see in the app are all constructed from different types of resources and then added to a `GraphicsOverlay`. The campsite icon is constructed from a URL, the blue pin with a star is stored in the resource folder that comes with the application and the orange pin is created from a file path on disk (which is written to disk when the app starts and cleaned up when the app closes).

![](PictureMarkerSymbol.png)

##How it works##
 To show picture marker symbols in your app:

- Create the `ArcGISMap`'s basemap
- Create the GraphicsOverlay and add it to the `MapView` using `MapView#getGraphicsOverlays` method.
- Add the map to the view via `MapView` via `MapView#setMap()`. 
- Use the constructor `PictureMarkerSymbol(String uri)` to create a symbol from a specified URI (URL, a web page, or from an absolute path to a file that is stored locally).
- Use the constructor `PictureMarkerSymbol(Image)` to creates a symbol from a local JavaFX `Image`. 
- Lastly once a symbol is created it will need to be added to a `Graphic`. Set the graphic into the GraphicsOverlay using the `GraphicsOverlay#getGraphics` method indicating the location `Point`.

##Features##
- ArcGISMap
- MapView
- Graphic
- GraphicsOverlay
- PictureMarkerSymbol
 