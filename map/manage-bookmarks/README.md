#Manage Bookmarks#
This samples demonstrates how to access and add `Bookmark`s to an `ArcGISMap`.

##How to use the sample##
The map in the sample comes pre-populated with a set of bookmarks. You can click on the Bookmarks from the control panel to update the `MapView`. To create a new bookmark, pan and/or zoom to a new location and click on the on the Add Bookmark button. You will be prompted to provide a name for the new bookmark. When you click OK the new bookmark should also show up in the list.

![](ManageBookmarks.png)

##How it works##
To display the ArcGISMap's bookmarks:

- Create an ArcGISMap; it has a property called bookmarks.  
- Create a BookMarkList from the ArcGISMap via `ArcGISMap#getBookmarks()` method.
- Set the map to the view via `MapView` via `MapView#setMap()`. 
- For creating a new bookmark use the `mapView#getCurrentViewpoint` method as the bookmark viewpoint and the name you provide as the bookmark.name
- Finally, add the new bookmark to the list of bookmarks using `bookmarkList#add()`.

##Features##
- ArcGISMap
- MapView
- Bookmark
- BookmarkList
- Viewpoint
