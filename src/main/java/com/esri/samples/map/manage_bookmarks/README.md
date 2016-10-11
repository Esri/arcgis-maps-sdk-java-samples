<h1>Manage Bookmarks</h1>

<p>Demonstrates how to access and add bookmarks to an ArcGISMap.</p>

<h2>How to use the sample</h2>

<p>The map in the sample comes pre-populated with a set of bookmarks.
  To access a bookmark and move to that location.
  - click on a bookmark's name from the list</p>

<p>To add a bookmark.
  - pan and/or zoom to a new location and click on the on the Add Bookmark button
  - enter a unique name for the bookmark and click ok
  - bookmark will be added to the list</p>

<p><img src="ManageBookmarks.png"/></p>

<h2>How it works</h2>

<p>To display the <code>ArcGISMap</code>'s <code>Bookmark</code>s:</p>

<ol>
  <li>Create an ArcGIS map; it has a property called bookmarks.  </li>
  <li>Create a <code>BookmarkList</code> from the ArcGIS map, <code>ArcGISMap.getBookmarks()</code>.</li>
  <li>Set the map to the <code>MapView</code>, <code>MapView.setMap()</code>. </li>
  <li>To create a new bookmark
    <ul><li>use <code>MapView.getCurrentViewpoint()</code> to set the bookmark's viewpoint </li>
      <li>bookmark.name for the name of the bookmark</li>
      <li>add new bookmark to book mark list, <code>BookmarkList.add()</code></li></ul></li>
</ol>

<h2>Features</h2>

<ul>
  <li>ArcGISMap</li>
  <li>Basemap</li>
  <li>Bookmark</li>
  <li>BookmarkList</li>
  <li>MapView</li>
  <li>Viewpoint</li>
</ul>


