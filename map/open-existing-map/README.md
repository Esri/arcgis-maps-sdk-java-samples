#Open an Existing Map#
The sample demonstrates how to open an existing web map.

##How to use the sample##
The sample opens with a web map displayed by default. You can select a `ArcGISMap` from the drop-down list. On selection the web map opens up in the `MapView`.

![](MinMaxScale.png)

##How it works##
To open an existing web map:

- Create a `Portal` from the ArcGIS url `http://www.arcgis.com/`
- Crate a `PortalItem` using the Portal and the web map id.
- The last create the ArcGISMap using the PortalItem and set it into the MapView.

##Features##
- ArcGISMap
- MapView
- PortalItem
- Portal