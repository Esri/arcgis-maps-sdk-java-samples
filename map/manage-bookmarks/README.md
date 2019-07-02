# Manage Bookmarks

Use bookmarks in a map.

<img src="ManageBookmarks.png"/>

## How to use the sample

The map in the sample comes pre-populated with a set of bookmarks.
  To access a bookmark and move to that location.
  - click on a bookmark's name from the list

To add a bookmark.
  - pan and/or zoom to a new location and click on the on the Add Bookmark button
  - enter a unique name for the bookmark and click ok
  - bookmark will be added to the list

## How it works

To display the `ArcGISMap`'s `Bookmark`s:


  1. Create an ArcGIS map; it has a property called bookmarks.
  2. Create a `BookmarkList` from the ArcGIS map, `ArcGISMap.getBookmarks()`.
  3. Set the map to the `MapView`, `MapView.setMap()`.
  4. To create a new bookmark
  * use `MapView.getCurrentViewpoint()` to set the bookmark's viewpoint
  * bookmark.name for the name of the bookmark
  * add new bookmark to book mark list, `BookmarkList.add()`


## Relevant API


  * ArcGISMap
  * Basemap
  * Bookmark
  * BookmarkList
  * MapView
  * Viewpoint



