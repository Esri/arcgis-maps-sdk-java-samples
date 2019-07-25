# Dictionary renderer with graphics overlay

Create graphics using a local mil2525d style file and an XML file with key/value pairs for each graphic.

![Dictionary Renderer Graphics Overlay Sample](dictionary-renderer-graphics-overlay.png)

## Use case

Use a dictionary renderer on a graphics overlay to display more transient data, such as military messages coming through a local tactical network.

## How to use the sample

When the sample is started, a dictionary renderer is applied and contents of a symbol style file are shown.

## How it works

1. Create a new `DictionarySymbolStyle` from a stylx file.
2. Create a new `DictionaryRenderer` from the dictionary symbol style.
3. Create a new `GraphicsOverlay` and set the  dictionary renderer to the graphics overlay.
4. Parse through the local XML file creating a map of key/value pairs for each block of attributes.
5. Create a `Graphic` for each attribute.
6. Use the _`wkid` key to get the geometry's spatial reference.
7. Use the `_control_points` key to get the geometry's shape.
8. Add the graphic to the graphics overlay.

## Relevant API

* DictionaryRenderer
* DictionarySymbolStyle
* GraphicsOverlay

## Tags

Visualization
