# Simple Marker Symbol

Show simple markers.

![](SimpleMarkerSymbol.png)

## How to use the sample

For simplicity, the sample starts with a predefined SimpleMarkerSymbol
set as a red circle.

## How it works

To display a `SimpleMarkerSymbol`:

1.  Create a `ArcGISMap`’s with `Basemap`.
2.  Create a `GraphicsOverlay` and add it to the
    `MapView`,`MapView.getGraphicsOverlays().add()`.
3.  Add the map to the view, `MapView.setMap()`.
4.  Create a `SimpleMarkerSymbol(SimpleMarkerSymbol.Style, color,
    size)`.
5.  style, how the symbol will be displayed (circle, square, etc.)
