# Display KML Network Links

Display KML that automatically refreshes every few seconds.

![](DisplayKMLNetworkLinks.png)

## How to use the sample

The data shown should refresh automatically every few seconds. A network
message will be displayed when the dataset is loaded.

## How it works

To show KML with network links and display network messages:

1.  Create a `KmlDataset` from a KML source which has network links.
2.  Construct a `KmlLayer` with the dataset and add the layer as an
    operational layer.
3.  To listen for network messages, add a
    `KmlNetworkLinkMessageReceivedListener` on the dataset.

## About the data

This map shows the current air traffic in parts of Europe with heading,
altitude, and ground speed. Additionally, noise levels from ground
monitoring stations are shown.
