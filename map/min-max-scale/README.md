#Min Max Scale#
This sample demonstrates how to set the minimum and maximum scale of an `ArcGISMap`.

##How to use the sample##
The `ArcGISMap` has a min and max scales properties which allows define the scale which the layers can be viewed. Then you just need to assign that map to the `MapView`.

![](MinMaxScale.png)

##How it works##
To set the min and max scales on the ArcGISMap:

- Create an ArcGISMap.  
- Set the min and max scales in the `MapView` using the `ArcGISMap#setMaxScale` and `ArcGISMap#setMinScale` methods.
- The last set the ArcGISMap to the `MapView`.

##Features##
- ArcGISMap
- MapView