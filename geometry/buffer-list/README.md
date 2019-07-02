# Buffer list

Combine buffers from multiple points individual buffer distances.

![](BufferList.png)

## How to use the sample

Tap the map to add points. Each point will use the buffer distance entered when it was created. Check the check box if you want the result to union the buffers. Click the "Create Buffer(s)" button to draw the buffer(s). Click the "Clear" button to start over. The envelope shows the area where you can expect reasonable results for planar buffer operations with the North Central Texas State Plane spatial reference.

## How it works

To union multiple buffers:


  1. Call `GeometryEngine.buffer(points, distances, union)` where `points` are the points to buffer around, `distances` are the buffer distances for each point (in meters) and `union` is a boolean for whether the results should be unioned.
  <li>Use the result polygons (if not unioned) or single polygon (if unioned).


## Relevant API


* GeometryEngine


## Additional Information

The properties of the underlying projection determine the accuracy of buffer polygons in a given area. Planar buffers are generally more accurate if made over small areas in a suitable spatial reference, such as the correct UTM zone or state plane system for the area of interest. Note that inaccurate buffers could still be created by buffering points inside the spatial reference's envelope with distances that move it outside the envelope.</p>

<h2 id="tags">Tags</h2>

Analysis, Buffer, GeometryEngine