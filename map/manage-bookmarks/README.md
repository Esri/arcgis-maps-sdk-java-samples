# Manage bookmarks

Access and create bookmarks on a map.

![](ManageBookmarks.png)

## Use case

Bookmarks are saved extents for a map. A tour app may save a bookmark for each site in the tour. An app may also allow users to add their own favorite places as bookmarks.

## How to use the sample

The map in the sample comes pre-populated with a set of bookmarks. To access a bookmark and move to that location, click on a bookmark's name from the list. To add a bookmark, pan and/or zoom to a new location and click on the 'Add Bookmark' button. Enter a unique name for the bookmark and click ok, and the bookmark will be added to the list.

## How it works

1. Instantiate a new `ArcGISMap` object and create a `BookmarkList` with `ArcGISMap.getBookmarks()`.
2. To create a new bookmark and add it to the bookmark list:
    * Instantiate a new `Bookmark` object passing in text (the name of the bookmark) and a `Viewpoint` as parameters.
    * Add the new bookmark to the book mark list with `BookmarkList.add(bookmark)`.

## Relevant API

* Bookmark
* BookmarkList
* Viewpoint

## Tags

bookmark, extent, location, zoom
