# Simple Line Symbol

Change a line graphic’s color and style.

![](SimpleLineSymbol.png)

## How to use the sample

Change Line Color: - changes the color of the line symbol

Change Line Width: - change the width of the line symbol

Change Line Style: - changes the pattern of the line symbol

## How it works

To display a `SimpleLineSymbol`:

1.  Create a `ArcGISMap`’s with `Basemap`.
2.  Create a `GraphicsOverlay` and add it to the `MapView`,
    `MapView.getGraphicsOverlays().add()`.
3.  Add the map to the view, `MapView.setMap()`.
4.  Create a `Polyline` using a `PointCollection` to indicate the
    boundaries of the `Graphic`.
5.  Create a `SimpleLineSymbol(SimpleLineSymbol.Style, color, width)`.
6.  style, pattern that makes up this symbol
7.  Lately, create a `Graphic(Geometry, Symbol)` and add it to the
    graphics overlay.
