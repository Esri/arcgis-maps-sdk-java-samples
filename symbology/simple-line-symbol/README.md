# Simple line symbol

Change a line graphic's color, width and style.

![Image of simple line symbol](SimpleLineSymbol.png)

## Use case

Customize the appearance of a line with a color, width and style scheme suitable for the data. For example, a red line with a dashed style could represent a geological fault mapped on a geological map. 

## How to use the sample

Use the drop down menus in the control panel to change the line color, width and style.

## How it works

1.  Create a `Polyline` using a `PointCollection` to define its geometry.
2.  Create a `SimpleLineSymbol(SimpleLineSymbol.Style, color, width)`.
3.  Set the color, width and style of the simple line symbol object with:
	* `setColor()`;
	* `setWidth()`;
	* `setStyle()`;  
4.  Create a `Graphic` passing in the polyline and simple line symbol as parameters. 
5.  Add the graphic to the graphics overlay with `graphicsOverlay.getGraphics().add(new Graphic(line, simpleLineSymbol))`.

## Relevant API

* Graphic
* GraphicsOverlay
* PointCollection
* Polyline
* SimpleLineSymbol

## Tags

graphic, line, symbol
